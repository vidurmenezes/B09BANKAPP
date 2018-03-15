package com.bank.database;

import android.content.Context;

import com.bank.exceptions.InvalidNameException;
import com.bank.generics.EnumMapAccountTypes;
import com.bank.generics.AccountTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class DatabaseUpdateHelper {

  /**
   * Update the role name of a given role in the role table.
   *
   * @param name    the new name of the role.
   * @param id      the current ID of the role.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   * @throws InvalidNameException if the given name is null
   */
  public static boolean updateRoleName(String name, int id, Context context) throws InvalidNameException {
    // check if name is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // update the name
    boolean complete = mydb.updateRoleName(name, id);
    mydb.close();
    return complete;
  }

  /**
   * Use this to update the user's name.
   *
   * @param name    the new name
   * @param id      the current id
   * @param context is the context received from the activity
   * @return true if it works, false otherwise.
   * @throws InvalidNameException is the inputed name is null.
   */
  public static boolean updateUserName(String name, int id, Context context) throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if inputed name is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    // run helper to update the user name
    boolean complete = mydb.updateUserName(name, id);
    mydb.close();
    return complete;
  }

  /**
   * Use this to update the user's age.
   *
   * @param age     the new age.
   * @param id      the current id
   * @param context is the context received from the activity
   * @return true if it succeeds, false otherwise.
   */
  public static boolean updateUserAge(int age, int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if inputed age is less than 0
    if (age < 0) {
      return false;
    }
    // run the helper to update the age of the user
    boolean complete = mydb.updateUserAge(age, id);
    mydb.close();
    return complete;
  }

  /**
   * update the role of the user.
   *
   * @param roleId  the new role.
   * @param id      the current id.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static boolean updateUserRole(int roleId, int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // get the list of roles from the database
    List<Integer> listA = new ArrayList<>();
    listA = DatabaseSelectHelper.getRoles(context);
    boolean exists1 = false;
    // if the inputed roleId is not in the list
    for (int i = 0; i < listA.size(); i++) {
      if ((roleId == listA.get(i))) {
        exists1 = true;
      }
    }
    if (exists1 == false) {
      return false;
    }
    // update the user role
    boolean complete = mydb.updateUserRole(roleId, id);
    mydb.close();
    return complete;
  }

  /**
   * Use this to update user's address.
   *
   * @param address the new address.
   * @param id      the current id.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static boolean updateUserAddress(String address, int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if address is within 100 character limit
    if (!(address.length() <= 100)) {
      return false;
    }
    boolean complete = mydb.updateUserAddress(address, id);
    mydb.close();
    return complete;
  }

  /**
   * update the name of the account.
   *
   * @param name    the new name for the account.
   * @param id      the id of the account to be changed.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   * @throws InvalidNameException if name is null.
   */
  public static boolean updateAccountName(String name, int id, Context context) throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if name is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    boolean complete = mydb.updateAccountName(name, id);
    mydb.close();
    return complete;
  }

  /**
   * update the account balance.
   *
   * @param balance the new balance for the account.
   * @param id      the id of the account.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id, Context context) {
    // Create a map of account types and allow the balance to be negative if it is balanceowing
    EnumMapAccountTypes accountsMap = new EnumMapAccountTypes(context);
    boolean isBalanceOwing = DatabaseSelectHelper.getAccountDetails(id, context).getType() ==
        accountsMap.get(AccountTypes.BALANCEOWING);
    if (balance.compareTo(BigDecimal.ZERO) >= 0 || isBalanceOwing) {
      BigDecimal roundedBalance = new BigDecimal(balance.toPlainString());
      roundedBalance = roundedBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
      // create a an instance of a database
      DatabaseDriverA mydb = new DatabaseDriverA(context);
      // Sets and returns boolean value to true if balance is updated
      boolean complete = mydb.updateAccountBalance(roundedBalance, id);
      mydb.close();
      return complete;
    }
    // Returns false if balance is negative
    return false;
  }

  /**
   * update the type of the account.
   *
   * @param typeId  the new type for the account.
   * @param id      the id of the account to be updated.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountType(int typeId, int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    List<Integer> listA = new ArrayList<>();
    // get a list of ids of all account types
    listA = DatabaseSelectHelper.getAccountTypesIds(context);
    boolean exists = false;
    // check if the given typeId is in the list
    for (int i = 0; i < listA.size(); i++) {
      if ((typeId == listA.get(i))) {
        exists = true;
      }
    }
    if (exists == false) {
      return false;
    }
    // update the account type
    boolean complete = mydb.updateAccountType(typeId, id);
    mydb.close();
    return complete;
  }

  /**
   * update the name of an accountType.
   *
   * @param name    the new name to be given.
   * @param id      the id of the accountType.
   * @param context is the context received from the activity
   * @return true if successful, false otherwise.
   * @throws InvalidNameException if name is null
   */
  public static boolean updateAccountTypeName(String name, int id, Context context)
      throws InvalidNameException {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if name is null
    if (name.equals(null)) {
      throw new InvalidNameException();
    }
    boolean complete = mydb.updateAccountTypeName(name, id);
    mydb.close();
    return complete;
  }

  /**
   * update the interest rate for this account type.
   *
   * @param interestRate the interest rate to be updated to.
   * @param id           the id of the accountType.
   * @param context      is the context received from the activity
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id,
                                                      Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    // check if interest rate is between 0 and 1
    BigDecimal a = new BigDecimal("1.0");
    BigDecimal b = new BigDecimal("0.0");
    if (!((interestRate.compareTo(b) == 1) && (interestRate.compareTo(a) == -1))) {
      return false;
    }
    boolean complete = mydb.updateAccountTypeInterestRate(interestRate, id);
    mydb.close();
    return complete;
  }

  /**
   * Updates a users password in the database.
   *
   * @param id       the id of the user.
   * @param password the HASHED password of the user (not plain text!).
   * @param context  is the context received from the activity
   * @return true if update succeeded, false otherwise.
   * @throws InvalidNameException if password is null
   */
  public static boolean updatePassword(String password, int id, Context context)
      throws InvalidNameException {
    // check if name is null
    if (password.equals(null)) {
      throw new InvalidNameException();
    }
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    boolean complete = mydb.updateUserPassword(password, id);
    mydb.close();
    return complete;
  }

  /**
   * Update the state of the user message to viewed.
   *
   * @param id      the id of the message that has been viewed.
   * @param context is the context received from the activity
   * @return true if successful, false o/w.
   */
  public static boolean updateUserMessageState(int id, Context context) {
    // create a an instance of a database
    DatabaseDriverA mydb = new DatabaseDriverA(context);
    boolean changed = mydb.updateUserMessageState(id);
    mydb.close();
    return changed;
  }
}
