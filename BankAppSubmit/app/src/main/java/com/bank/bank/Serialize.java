package com.bank.bank;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;
import com.bank.user.User;
import com.bank.account.Account;
import com.bank.generics.EnumMapRoles;

public class Serialize {

  List<User> users = new ArrayList<>();
  List<Account> accounts = new ArrayList<>();
  List<List<Integer>> userAccount = new ArrayList<>();
  List<String> passwords = new ArrayList<>();
  List<String> accountTypeNames = new ArrayList<>();
  List<String> roleNames = new ArrayList<>();
  List<String> interestRates = new ArrayList<>();
  List<List<String>> messages = new ArrayList<>();
  List<List<Integer>> messageStatuses = new ArrayList<>();

  private void getUsers(Context context) {
    int ctr = 1;
    boolean loop = true;
    // Get user details from database and store in list
    try {
      while (loop) {
        User user = DatabaseSelectHelper.getUserDetails(ctr, context);
        user.getRoleId();
        String pass = DatabaseSelectHelper.getPassword(ctr, context);
        this.users.add(user);
        this.passwords.add(pass);
        ctr++;
      }
    } catch (Exception e) {
    }
  }

  private void getAccountTypeInfo(Context context) {
    // get the list of account type ids from database
    List<Integer> idsList = DatabaseSelectHelper.getAccountTypesIds(context);
    for (Integer id : idsList) {
      // add account type names and their associated interest rates to lists
      this.accountTypeNames.add(DatabaseSelectHelper.getAccountTypeName(id, context));
      this.interestRates.add(DatabaseSelectHelper.getInterestRate(id, context).toPlainString());
    }
  }

  private void getRoleNames(Context context) {
    // get the list of role ids from database
    List<Integer> idsList = DatabaseSelectHelper.getRoles(context);
    for (Integer id : idsList) {
      // add role names to the list
      roleNames.add(DatabaseSelectHelper.getRole(id, context));
    }
  }

  private void getAccounts(Context context) {
    int ctr = 1;
    boolean loop = true;
    try {
      while (loop) {
        // get all accounts in the database and store in a list
        Account account = DatabaseSelectHelper.getAccountDetails(ctr, context);
        account.getType();
        this.accounts.add(account);
        ctr++;
      }
    } catch (Exception e) {
    }
  }

  private void getUserAccount(Context context) {
    int ctr = 1;
    boolean loop = true;
    try {
      while (loop) {
        // get each user in the database
        User user = DatabaseSelectHelper.getUserDetails(ctr, context);
        // get a list of accounts for each user and add it to a list of lists
        List<Integer> accList = DatabaseSelectHelper.getAccountIds(user.getId(), context);
        this.userAccount.add(accList);
        ctr++;
      }
    } catch (Exception e) {
    }
  }

  private void getMessages(Context context) {
    int ctr = 1;
    boolean loop = true;
    try {
      while (loop) {
        // get each user in the database
        User user = DatabaseSelectHelper.getUserDetails(ctr, context);
        // get a list of messages for each user and add it to a list of lists
        List<String> messageList = DatabaseSelectHelper.getAllMessages(user.getId(), context);
        this.messages.add(messageList);
        ctr++;
      }

    } catch (Exception e) {
    }
  }

  private void getMessageStatuses(Context context) {
    int ctr = 1;
    this.getMessages(context);

    while ((ctr - 1) < this.messages.size()) {

      // get a list of message statuses
      List<Integer> messageStatusList = DatabaseSelectHelper.getAllMessageStatuses(ctr, context);
      this.messageStatuses.add(messageStatusList);
      ctr++;
    }
  }

  public void serializeData(Context context) {
    // open an output stream and serialize all the lists
    try {
      this.getUsers(context);
      this.getAccountTypeInfo(context);
      this.getRoleNames(context);
      this.getAccounts(context);
      this.getUserAccount(context);
      this.getMessageStatuses(context);
      File file = new File(context.getFilesDir(),"database_copy.ser");
      FileOutputStream stream = new FileOutputStream(file);
      ObjectOutputStream out = new ObjectOutputStream(stream);
      out.writeObject(users);
      out.writeObject(passwords);
      out.writeObject(roleNames);
      out.writeObject(accountTypeNames);
      out.writeObject(interestRates);
      out.writeObject(accounts);
      out.writeObject(userAccount);
      out.writeObject(messages);
      out.writeObject(messageStatuses);
      out.close();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
