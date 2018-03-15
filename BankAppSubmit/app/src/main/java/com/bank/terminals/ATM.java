package com.bank.terminals;

import java.io.Serializable;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.bank.generics.EnumMapAccountTypes;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NoAccessToAccountException;
import com.bank.generics.AccountTypes;
import com.bank.security.PasswordHelpers;
import com.bank.account.Account;
import com.bank.user.Customer;

import android.content.Context;
import android.widget.Toast;

public class ATM implements GeneralTasksInterface, Serializable {

  private Customer currentCustomer;
  private boolean authenticated;

  /**
   * constructor for ATM which takes in customerId and password.
   *
   * @param customerId which is the id of customer
   * @param password   password which will be checked
   */
  public ATM(int customerId, String password, Context context) {
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId, context);
    this.authenticated = this.authenticate(customerId, password, context);
  }

  public ATM(int customerId, Context context) {
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId, context);
    this.authenticated = true;
  }

  protected ATM() {

  }

  public Customer getCurrentCustomer() {
    Customer customer = null;
    if (this.authenticated == true) {
      customer = this.currentCustomer;
    }
    return customer;
  }

  /**
   * Authenticates the user from the password.
   *
   * @param userId   the id of the user
   * @param password the password of the user
   * @return boolean saying if authenticated or not
   */
  public boolean authenticate(int userId, String password, Context context) {
    // get password from the database
    String pass = DatabaseSelectHelper.getPassword(userId, context);
    // compare the passwords
    this.authenticated = PasswordHelpers.comparePassword(pass, password);
    if (this.authenticated == true) {
      Customer user = (Customer) DatabaseSelectHelper.getUserDetails(userId, context);
      System.out.println("User logged in:");
      System.out.println("Name: " + user.getName());
      System.out.println("Address: " + user.getAddress() + "\n");

      String accounts = "";
      List<Account> listAcc;
      try {
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
    return this.authenticated;
  }

  /**
   * Get a list of accounts of the current customer;
   *
   * @return list of accounts of the current customer.
   * @throws NoAccessToAccountException if current customer is not authenticated.
   */
  public List<Account> listAccounts(Context context) throws NoAccessToAccountException {
    List<Account> listAcc = new ArrayList<>();
    if (this.authenticated == true) {
      // get all account ids associated with current customer
      List<Integer> listAccId = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId(),
          context);
      for (int i = 0; i < listAccId.size(); i++) {
        // add each account object to the list
        Account account = DatabaseSelectHelper.getAccountDetails(listAccId.get(i), context);
        listAcc.add(account);
      }
    } else {
      throw new NoAccessToAccountException();
    }
    return listAcc;
  }

  /**
   * Add money to the current balance in the account.
   *
   * @param balance   the money to be added.
   * @param accountId the id of the account
   * @return true if successful, false otherwise
   * @throws NoAccessToAccountException if not authenticated.
   */
  public boolean makeDeposit(BigDecimal balance, int accountId, Context context)
      throws NoAccessToAccountException {
    boolean success = false;
    // get a list of all the accounts
    List<Account> listAcc = this.listAccounts(context);

    // check authentication
    BigDecimal currentMoney = null;
    if (this.authenticated == true) {
      // get current balance
      Account account = DatabaseSelectHelper.getAccountDetails(accountId, context);
      currentMoney = DatabaseSelectHelper.getBalance(accountId, context);
      for (int i = 0; i < listAcc.size(); i++) {
        if (listAcc.get(i).getId() == account.getId()) {
          // add two funds together
          balance = balance.add(currentMoney);
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

  /**
   * Get the current balance of the account.
   *
   * @param accountId the id of the account
   * @return the current balance of the account
   * @throws NoAccessToAccountException if not authenticated
   */
  public BigDecimal checkBalance(int accountId, Context context) throws NoAccessToAccountException {
    BigDecimal balance = null;
    // get a list of all accounts
    List<Account> listAcc = this.listAccounts(context);
    if (this.authenticated == true) {
      Account a = DatabaseSelectHelper.getAccountDetails(accountId, context);
      for (int i = 0; i < listAcc.size(); i++) {
        if (listAcc.get(i).getId() == a.getId()) {
          // get the current balance if accountId is valid
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

  /**
   * Take out money from the current balance.
   *
   * @param amount    the amount to be withdrawn.
   * @param accountId the id of the account
   * @return true if successful, false otherwise
   * @throws InsuffiecintFundsException if goes to negative balance.
   * @throws NoAccessToAccountException if not authenticated
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws InsuffiecintFundsException, NoAccessToAccountException {
    BigDecimal currentMoney = null;
    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    // convert the balance into a value with two decimal places
    BigDecimal presentamount = new BigDecimal(amount.toPlainString());
    amount = presentamount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    if (this.authenticated == true) {
      currentMoney = DatabaseSelectHelper.getBalance(accountId, context);
      // check if balance is greater than amount to be taken out unless balance owing account
      if (currentMoney.compareTo(amount) == -1 && DatabaseSelectHelper.getAccountDetails(accountId,
          context).getType() != accountsMap.get(AccountTypes.BALANCEOWING)) {
        throw new InsuffiecintFundsException();
      }
    } else {
      throw new NoAccessToAccountException();
    }
    // subtract the amount
    currentMoney = currentMoney.subtract(amount);
    // update new balance in database
    boolean success = DatabaseUpdateHelper.updateAccountBalance(currentMoney, accountId, context);
    return success;
  }
}
