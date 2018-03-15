package com.bank.database;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.account.BalanceOwingAccount;
import com.bank.account.ChequingAccount;
import com.bank.account.RestrictedSavingsAccount;
import com.bank.account.SavingsAccount;
import com.bank.account.Tfsa;
import com.bank.user.Admin;
import com.bank.user.Customer;
import com.bank.user.Teller;
import com.bank.user.User;
import com.bank.account.Account;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class DatabaseSelectHelper {

  /**
   * Gets the role with id
   *
   * @param id      the id of the role
   * @param context is the context received from the activity
   * @return the string containing the role
   */
  public static String getRole(int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    return mydb.getRole(id);
  }

  /**
   * get the hashed version of the password.
   *
   * @param userId  the user's id.
   * @param context is the context received from the activity
   * @return the hashed password to be checked against given password.
   */
  public static String getPassword(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    String pass = mydb.getPassword(userId);
    mydb.close();
    return pass;
  }

  /**
   * Get a User object from the userId.
   *
   * @param userId  the id of the user
   * @param context is the context received from the activity
   * @return a User object with details about the user from the database
   */
  public static User getUserDetails(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    User user = null;
    // get the result set based on the userId
    Cursor results = mydb.getUserDetails(userId);
    // for each result get the user details
    while (results.moveToNext()) {
      // get the role name
      int roleId = results.getInt(results.getColumnIndex("ROLEID"));
      String role = mydb.getRole(roleId);
      // check which user it is and create the users
      if (role.equals("ADMIN")) {
        user = new Admin(userId, results.getString(results.getColumnIndex("NAME")),
            results.getInt(results.getColumnIndex("AGE")),
            results.getString(results.getColumnIndex("ADDRESS")), context);
      } else if (role.equals("TELLER")) {
        user = new Teller(userId, results.getString(results.getColumnIndex("NAME")),
            results.getInt(results.getColumnIndex("AGE")),
            results.getString(results.getColumnIndex("ADDRESS")), context);
      } else if (role.equals("CUSTOMER")) {
        user = new Customer(userId, results.getString(results.getColumnIndex("NAME")),
            results.getInt(results.getColumnIndex("AGE")),
            results.getString(results.getColumnIndex("ADDRESS")), context);
      }
    }
    results.close();
    mydb.close();
    return user;
  }

  /**
   * Get a list of accountIds associated with the userID
   *
   * @param userId  the id of the user
   * @param context is the context received from the activity
   * @return list of accountIds associated with the userID
   */
  public static List<Integer> getAccountIds(int userId, Context context) {
    List<Integer> accountList = new ArrayList<Integer>();
    // Uses userId to get account id and adds account ids to account list if userId is greater than
    // 0
    if (userId > 0) {
      // create a an instance of a database
      DatabaseDriverA mydb = new DatabaseDriverA(context);
      Cursor results;
      results = mydb.getAccountIds(userId);
      while (results.moveToNext()) {
        // System.out.println(results.getInt("ID"));
        accountList.add(results.getInt(results.getColumnIndex("ACCOUNTID")));
      }
      results.close();
      mydb.close();
    }
    // Return list of accounts
    return accountList;
  }

  /**
   * . Get an Account object from the account Id
   *
   * @param accountId the id of the account
   * @param context   is the context received from the activity
   * @return an Account object created with the details of the account.
   */
  public static Account getAccountDetails(int accountId, Context context) {
    Account account = null;
    if (accountId > 0) {
      // create a an instance of a database
      DatabaseDriverA mydb = new DatabaseDriverA(context);
      Cursor results;
      results = mydb.getAccountDetails(accountId);
      while (results.moveToNext()) {
        // Get account type from database
        String accountTypeName = mydb.getAccountTypeName(accountId);
        // If account type is CHEQUING, set account to chequing with corresponding account details
        if ("CHEQUING".equals(accountTypeName)) {
          account = new ChequingAccount(results.getInt(results.getColumnIndex("ID")),
              results.getString(results.getColumnIndex("NAME")), new BigDecimal
              (results.getString(results.getColumnIndex("BALANCE"))), context);
          // If account type is SAVING, set account to saving with corresponding account details
        } else if ("SAVING".equals(accountTypeName)) {
          account = new SavingsAccount(results.getInt(results.getColumnIndex("ID")),
              results.getString(results.getColumnIndex("NAME")), new BigDecimal
              (results.getString(results.getColumnIndex("BALANCE"))), context);
          // If account type is TFSA, set account to tfsa with corresponding account details
        } else if ("TFSA".equals(accountTypeName)) {
          account = new Tfsa(results.getInt(results.getColumnIndex("ID")), results.getString
              (results.getColumnIndex("NAME")), new BigDecimal(results.getString
              (results.getColumnIndex("BALANCE"))), context);
          // If account type is RESTRICTEDSAVING, set account to saving with corresponding
          // account details
        } else if ("RESTRICTEDSAVING".equals(accountTypeName)) {
          account = new RestrictedSavingsAccount(results.getInt(results.getColumnIndex("ID")),
              results.getString(results.getColumnIndex("NAME")), new BigDecimal
              (results.getString(results.getColumnIndex("BALANCE"))), context);
          // If account type is BALANCEOWING, set account to saving with corresponding
          // account details
        } else if ("BALANCEOWING".equals(accountTypeName)) {
          account = new BalanceOwingAccount(results.getInt(results.getColumnIndex("ID")),
              results.getString(results.getColumnIndex("NAME")), new BigDecimal
              (results.getString(results.getColumnIndex("BALANCE"))), context);
        }
      }
      results.close();
      mydb.close();
      // Return account with account details
      return account;
    }
    return null;
  }

  /**
   * Gets the current balance in the account.
   *
   * @param accountId is the id of the account
   * @param context   is the context received from the activity
   * @return the current balance of the account in the database
   */
  public static BigDecimal getBalance(int accountId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    BigDecimal balance = null;
    // try to get the balance of the account from the database, otherwise catch exception
    balance = mydb.getBalance(accountId);
    mydb.close();
    return balance;
  }

  /**
   * Get the current interest associated with the accountType.
   *
   * @param accountType the id of the accountType
   * @param context     is the context received from the activity
   * @return the current interest associated with the accountType.
   */
  public static BigDecimal getInterestRate(int accountType, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    BigDecimal interestRate = null;
    interestRate = mydb.getInterestRate(accountType);
    mydb.close();
    return interestRate;
  }

  /**
   * Get a list of ids of account types.
   *
   * @param context is the context received from the activity
   * @return a list of ids of account types.
   */
  public static List<Integer> getAccountTypesIds(Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    Cursor results = null;
    List<Integer> ids = new ArrayList<>();
    // try to get the result set of all account types
    results = mydb.getAccountTypesId();
    while (results.moveToNext()) {
      // add each id from the result set to the list
      ids.add(results.getInt(results.getColumnIndex("ID")));
    }
    results.close();
    mydb.close();
    return ids;
  }

  /**
   * Get the name of the account type associated with the id
   *
   * @param accountTypeId the id of the account type
   * @param context       is the context received from the activity
   * @return the name of the account type associated with the id
   */
  public static String getAccountTypeName(int accountTypeId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    String name = null;
    // try to get the name of the account type, otherwise catch an exception
    name = mydb.getAccountTypeName(accountTypeId);
    mydb.close();
    return name;
  }

  /**
   * Get a list of roles.
   *
   * @param context is the context received from the activity
   * @return a list of roles.
   */
  public static List<Integer> getRoles(Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    Cursor results = null;
    List<Integer> ids = new ArrayList<>();
    // try to get the result set of all the roles
    results = mydb.getRoles();
    while (results.moveToNext()) {
      // add each id into the list of id's
      // System.out.println(results.getInt("ID"));
      ids.add(results.getInt(results.getColumnIndex("ID")));
    }
    results.close();
    mydb.close();
    return ids;
  }

  /**
   * Get the account type Id associated with that account.
   *
   * @param accountId the id of the account.
   * @param context   is the context received from the activity
   * @return the account type Id associated with that account.
   */
  public static int getAccountType(int accountId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    int type1 = -1;
    // try and get the type id of the account, otherwise catch an exception

    type1 = mydb.getAccountType(accountId);
    mydb.close();
    return type1;
  }

  /**
   * Get the roleId of the user.
   *
   * @param userId  the id of the user.
   * @param context is the context received from the activity
   * @return the roleId of the user.
   */
  public static int getUserRole(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    int role = -1;
    // try and get the roleId of the user, otherwise catch an exception
    role = mydb.getUserRole(userId);
    mydb.close();
    return role;
  }

  /**
   * Get all messageIds currently available to a user.
   *
   * @param userId  the user whose messages are being retrieved.
   * @param context is the context received from the activity
   * @return a list of IDs of the messages.
   */
  public static List<Integer> getAllMessageIds(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    List<Integer> messageIdList = new ArrayList<>();
    Cursor result = mydb.getAllMessages(userId);
    while (result.moveToNext()) {
      messageIdList.add(result.getInt(result.getColumnIndex("ID")));
    }
    result.close();
    mydb.close();
    return messageIdList;
  }

  /**
   * Get all messages currently available to a user.
   *
   * @param userId  the user whose messages are being retrieved.
   * @param context is the context received from the activity
   * @return a list of messages currently in the database.
   */
  public static List<String> getAllMessages(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    List<String> messageList = new ArrayList<>();
    Cursor result = mydb.getAllMessages(userId);
    while (result.moveToNext()) {
      messageList.add(result.getString(result.getColumnIndex("MESSAGE")));
    }
    result.close();
    mydb.close();
    return messageList;
  }

  /**
   * Get all message statuses.
   *
   * @param userId  the user whose message statuses are being retrieved.
   * @param context is the context received from the activity
   * @return a list of message statuses.
   */
  public static List<Integer> getAllMessageStatuses(int userId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    List<Integer> messageStatusList = new ArrayList<>();
    Cursor result = mydb.getAllMessages(userId);
    while (result.moveToNext()) {
      messageStatusList.add(result.getInt(result.getColumnIndex("VIEWED")));
    }
    result.close();
    mydb.close();
    return messageStatusList;
  }

  /**
   * Get a specific message from the database.
   *
   * @param messageId the id of the message.
   * @param context   is the context received from the activity
   * @return the message from the database as a string.
   */
  public static String getSpecificMessage(int messageId, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    String message = "";
    message = mydb.getSpecificMessage(messageId);
    mydb.close();
    return message;
  }
}