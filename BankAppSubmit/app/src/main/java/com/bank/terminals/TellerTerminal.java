package com.bank.terminals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.InvalidIdException;
import com.bank.exceptions.InvalidNameException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.user.Customer;
import com.bank.user.Teller;
import com.bank.account.Account;
import com.bank.generics.AccountTypes;
import com.bank.security.PasswordHelpers;

import android.content.Context;
import android.widget.Toast;

public class TellerTerminal implements GeneralTasksInterface, Serializable {

  private Teller currentUser;
  private boolean currentUserAuthenticated;
  private Customer currentCustomer;
  private boolean currentCustomerAuthenticated;

  /**
   * starts the teller terminal.
   *
   * @param tellerId which is the id of the teller
   * @param password which should match the teller password in db
   */
  public TellerTerminal(int tellerId, String password, Context context) {
    super();
    // get the user details of the Teller user
    this.currentUser = (Teller) DatabaseSelectHelper.getUserDetails(tellerId, context);
  }

  /**
   * authenticate the teller with their user and password.
   *
   * @param userId   is the users id
   * @param password is the password that should match the db
   * @return the boolean if authenticated
   */
  public boolean authenticateTeller(int userId, String password, Context context) {
    // get the password
    String pass = DatabaseSelectHelper.getPassword(userId, context);
    // compare the password
    this.currentUserAuthenticated = PasswordHelpers.comparePassword(pass, password);
    // check if true
    if (this.currentUserAuthenticated) {
      // get the details of this user
      Teller user = (Teller) DatabaseSelectHelper.getUserDetails(userId, context);
      System.out.println("Teller logged in:");
      System.out.println("Name: " + user.getName());
      System.out.println("Address: " + user.getAddress() + "\n");
    }
    return this.currentUserAuthenticated;
  }

  /**
   * Make a new account with the given values.
   *
   * @param name    of the account
   * @param balance amount to be added to account
   * @param type    the type id of account
   * @return true if successful ,false otherwise
   */
  public boolean makeNewAccount(String name, BigDecimal balance, int type, Context context) {
    // check of both are authenticated
    if ((this.currentCustomerAuthenticated) && (this.currentUserAuthenticated)) {
      try {
        // get account id of the account
        int accId = DatabaseInsertHelper.insertAccount(name, balance, type, context);
        // insert a user-account relationship
        DatabaseInsertHelper.insertUserAccount(this.currentCustomer.getId(), accId, context);
      } catch (InvalidNameException e) {
        e.printStackTrace();
      }
      return true;
    } else {
      Toast.makeText(context, "Account not made", Toast.LENGTH_LONG).show();
      System.out.println("Customer not authenticated");
      System.out.println("Account not made.");
      return false;
    }
  }

  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
  }

  /**
   * Authenticated the customer.
   *
   * @param password the password for the customer
   */
  public void authenticateCurrentCustomer(String password, Context context) {
    // get the id for the customer.
    int id = this.currentCustomer.getId();
    // try to authenticate
    boolean success = this.authenticate(id, password, context);
    if (success) {
      System.out.println("Authentication successfull");
    } else {
      System.out.println("Wrong password");
    }
  }

  /**
   * Make a new user
   *
   * @param name     the name of the user.
   * @param age      the age of the user.
   * @param address  the address of the user
   * @param password the password of the user
   */
  public void makeNewUser(String name, int age, String address, String password, Context context) {
    // check if already authenticated as a teller.
    if (this.currentUserAuthenticated) {
      try {
        // insert the customer in to the database
        int id = DatabaseInsertHelper.insertNewUser(name, age, address, 3, password, context);
        System.out.println("The user ID is: " + id);
      } catch (InvalidNameException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Give interest to the account.
   *
   * @param accountId the id of the account.
   */
  public void giveInterest(int accountId, Context context) {
    boolean success = false;
    // authentication is true for customer and teller
    if ((this.currentCustomerAuthenticated) && (this.currentUserAuthenticated)) {
      // list all account ids
      List<Integer> listAccIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId(),
          context);
      // get interest rate,balance and set the money given
      if (listAccIds.contains(accountId)) {
        int accTypeId = DatabaseSelectHelper.getAccountType(accountId, context);
        BigDecimal interest = DatabaseSelectHelper.getInterestRate(accTypeId, context);
        BigDecimal balance = DatabaseSelectHelper.getBalance(accountId, context);
        BigDecimal moneyGiven = interest.multiply(balance);
        try {
          success = this.makeDeposit(moneyGiven, accountId, context);
        } catch (NoAccessToAccountException e) {
          e.printStackTrace();
        }
      }
    }
    // if the deposit was successful
    if (success) {
      System.out.println("Interest was given");
      String message = "An interest was given to your account";
      int messageId = DatabaseInsertHelper.insertMessage(this.currentCustomer.getId(), message,
          context);
      System.out.println("Message inserted, the message Id is: " + messageId);
    } else {
      System.out.println("Interest was not given");
    }
  }

  /**
   * De-authenticate the customer.
   */
  public void deAuthenticateCustomer() {
    // reset current customer values
    this.currentCustomerAuthenticated = false;
    this.currentCustomer = null;
  }

  public Customer getCurrentCustomer() {
    Customer customer = null;
    if ((this.currentCustomerAuthenticated) && (this.currentUserAuthenticated)) {
      customer = this.currentCustomer;
    }
    return customer;
  }

  @Override
  /**
   * authenticate the user
   */
  public boolean authenticate(int userId, String password, Context context) {
    // the password and compare if it same
    String pass = DatabaseSelectHelper.getPassword(userId, context);
    this.currentCustomerAuthenticated = PasswordHelpers.comparePassword(pass, password);
    if (this.currentCustomerAuthenticated) {
      // print out basic information of the current customer
      Customer user = (Customer) DatabaseSelectHelper.getUserDetails(userId, context);
      System.out.println("Customer logged in:");
      System.out.println("Name: " + user.getName());
      System.out.println("Address: " + user.getAddress() + "\n");
      // create a string of all the accounts
      String accounts = "";
      List<Account> listAcc;
      try {
        // all the accounts under the user print the name, balance and type
        listAcc = this.listAccounts(context);
        for (int i = 0; i < listAcc.size(); i++) {
          int type = listAcc.get(i).getType();
          String typeName = DatabaseSelectHelper.getAccountTypeName(type, context);
          accounts += "Account Name: " + listAcc.get(i).getName();
          accounts += " , Balance: " + listAcc.get(i).getBalance().toPlainString();
          accounts += " , Account Type: " + typeName;
          accounts += "\n";
        }
        System.out.println(accounts);
      } catch (NoAccessToAccountException e) {
        e.printStackTrace();
      }
    }
    return this.currentCustomerAuthenticated;
  }

  @Override
  public List<Account> listAccounts(Context context) throws NoAccessToAccountException {
    // create a new list of Account types
    List<Account> listAcc = new ArrayList<>();
    Account account = null;
    // check if customer is authenticated and user authenticated.
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      // get the accountIds for the customer
      List<Integer> listAccId = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId(),
          context);
      for (int i = 0; i < listAccId.size(); i++) {
        account = DatabaseSelectHelper.getAccountDetails(listAccId.get(i), context);
        listAcc.add(account);
      }
    } else {
      throw new NoAccessToAccountException();
    }
    return listAcc;
  }

  @Override
  public boolean makeDeposit(BigDecimal balance, int accountId, Context context)
      throws NoAccessToAccountException {
    boolean success = false;
    // new list of accounts
    List<Account> listAcc = this.listAccounts(context);
    BigDecimal currentMoney = null;
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      Account account = DatabaseSelectHelper.getAccountDetails(accountId, context);
      // current balance
      currentMoney = DatabaseSelectHelper.getBalance(accountId, context);
      for (int i = 0; i < listAcc.size(); i++) {
        if (listAcc.get(i).getId() == account.getId()) {
          // add two funds together
          balance = balance.add(currentMoney);
          // update the account balance
          success = DatabaseUpdateHelper.updateAccountBalance(balance, accountId, context);
        }
      }
      if (!success) {
        throw new NoAccessToAccountException();
      }
    } else {
      throw new NoAccessToAccountException();
    }
    return success;
  }

  @Override
  public BigDecimal checkBalance(int accountId, Context context) throws NoAccessToAccountException {
    BigDecimal balance = null;
    List<Account> listAcc = this.listAccounts(context);
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      Account account = DatabaseSelectHelper.getAccountDetails(accountId, context);
      for (int i = 0; i < listAcc.size(); i++) {
        // get the id
        if (listAcc.get(i).getId() == account.getId()) {
          // get balance
          balance = DatabaseSelectHelper.getBalance(accountId, context);
        }
      }
      if (balance == null) {
        throw new NoAccessToAccountException();
      }
    } else {
      throw new NoAccessToAccountException();
    }
    return balance;
  }

  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws InsuffiecintFundsException, NoAccessToAccountException {
    BigDecimal currentMoney = null;
    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    // convert the balance into a value with two decimal places
    BigDecimal newAmount = new BigDecimal(amount.toPlainString());
    amount = newAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      // get the balance
      currentMoney = DatabaseSelectHelper.getBalance(accountId, context);
      // If the account balance owing account allow a negative balance
      if (currentMoney.compareTo(amount) == -1 && DatabaseSelectHelper.getAccountDetails(accountId,
          context)
          .getType() != accountsMap.get(AccountTypes.BALANCEOWING)) {
        throw new InsuffiecintFundsException();
      }
    } else {
      throw new NoAccessToAccountException();
    }
    // subtract the amount
    currentMoney = currentMoney.subtract(amount);
    // make it true if balance updated
    boolean success = DatabaseUpdateHelper.updateAccountBalance(currentMoney, accountId, context);
    return success;
  }

  public boolean updateName(String name, Context context) throws InvalidNameException {
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      DatabaseUpdateHelper.updateUserName(name, this.currentCustomer.getId(), context);
      return true;
    }
    return false;
  }

  public boolean updateAddress(String address, Context context) throws InvalidNameException {
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      DatabaseUpdateHelper.updateUserAddress(address, this.currentCustomer.getId(), context);
      return true;
    }
    return false;
  }

  public boolean updatePassword(String password, Context context) throws InvalidNameException {
    // check if authenticated
    if ((this.currentCustomerAuthenticated == true) && (this.currentUserAuthenticated == true)) {
      DatabaseUpdateHelper.updatePassword(password, this.currentCustomer.getId(), context);
      return true;
    }
    return false;
  }
}
