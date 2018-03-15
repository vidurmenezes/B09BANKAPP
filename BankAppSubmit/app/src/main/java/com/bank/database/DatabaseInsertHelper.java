package com.bank.database;


import android.content.Context;
import android.widget.Toast;

import com.bank.exceptions.InvalidNameException;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

public class DatabaseInsertHelper {

  /**
   * Insert a new account into account table.
   *
   * @param name    the name of the account.
   * @param balance the balance currently in account.
   * @param typeId  the id of the type of the account.
   * @param context is the context received from the activity
   * @return accountId of inserted account, -1 otherwise
   * @throws InvalidNameException if the name is null
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId, Context context)
      throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if the name entered is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    // convert the balance into a value with two decimal places
    BigDecimal newBalance = new BigDecimal(balance.toPlainString());
    newBalance = newBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    // check if given type id is in the database
    List<Integer> listA = new ArrayList<>();
    listA = DatabaseSelectHelper.getAccountTypesIds(context);
    boolean exists = false;
    for (int i = 0; i < listA.size(); i++) {
      if ((typeId == listA.get(i))) {
        exists = true;
      }
    }
    if (exists == false) {
      return -1;
    }
    // insert the account
    int accountId = 0;
    accountId = (int) mydb.insertAccount(name, newBalance, typeId);
    mydb.close();
    try {
      mydb.getWritableDatabase();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return accountId;
  }

  /**
   * insert an accountType into the accountType table.
   *
   * @param name         the name of the type of account.
   * @param interestRate the interest rate for this type of account.
   * @param context      is the context received from the activity
   * @return true if successful, false otherwise.
   * @throws InvalidNameException name is null
   */
  public static int insertAccountType(String name, BigDecimal interestRate, Context context)
      throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if name is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    // check if interest rate is between 0 and 1
    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("0.0");
    if (!((interestRate.compareTo(b) == 1) && (interestRate.compareTo(a) == -1))) {
      return -1;
    }
    // insert the account type
    int success = 0;
    success = (int) mydb.insertAccountType(name, interestRate);
    mydb.close();
//    try {
//      mydb.getWritableDatabase();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
    return success;
  }

  /**
   * inserts a new user into the users table
   *
   * @param name     the name of the user
   * @param age      the age of the user
   * @param address  the address of the user
   * @param roleId   the roleId of the user
   * @param password the password of the user
   * @param context  is the context received from the activity
   * @return Id if successful, -1 otherwise
   * @throws InvalidNameException if the name is null
   */
  public static int insertNewUser(String name, int age, String address, int roleId, String password,
                                  Context context) throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if role id is in the database
    List<Integer> listA = new ArrayList<>();
    listA = DatabaseSelectHelper.getRoles(context);
    boolean exists = false;
    for (int i = 0; i < listA.size(); i++) {
      if (!(roleId == listA.get(i))) {
        exists = true;
      }
    }
    if (exists == false) {
      return -1;
    }
    // insert new user
    int id = 0;
    id = (int) mydb.insertNewUser(name, age, address, roleId, password);
    mydb.close();
    return id;
  }

  /**
   * Use this to insert new roles into the database.
   *
   * @param role    the new role to be added.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   * @throws InvalidNameException if the string is null
   */
  public static int insertRole(String role, Context context) throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if name is null
    if (role.equals(null)) {
      throw new InvalidNameException();
    }
    // insert the role
    int success = 0;
    success = (int) mydb.insertRole(role);
    mydb.close();
    return success;
  }

  /**
   * insert a user and account relationship.
   *
   * @param userId    the id of the user.
   * @param accountId the id of the account.
   * @param context   is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static int insertUserAccount(int userId, int accountId, Context context) {
    // Set boolean return value to 0
    int retVal = 0;
    // Adds user account with user account information into database if userid and account id
    // are both greater than 0
    if (userId > 0 && accountId > 0) {
      // create a an instance of a database
      DatabaseDriverA mydb = new DatabaseDriverA(context);
      // Set return value to the id if user account is inserted into database
      retVal = (int) mydb.insertUserAccount(userId, accountId);
      mydb.close();
    }
    // Return boolean return value
    return retVal;
  }

  /**
   * Insert a new message into the database.
   *
   * @param userId  the id of the user whom the message is for.
   * @param message the message to be left (max 512 characters).
   * @param context is the context received from the activity
   * @return the id of the inserted message.
   */
  public static int insertMessage(int userId, String message, Context context) {
    int messageId = -1;
    if ((userId > 0) && (message.length() <= 512)) {
      // create a an instance of a database
      DatabaseDriverA mydb = new DatabaseDriverA(context);
      messageId = (int) mydb.insertMessage(userId, message);
      mydb.close();
    }
    return messageId;
  }
}
