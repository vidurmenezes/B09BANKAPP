package com.bank.bank;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.InvalidNameException;
import com.bank.user.User;
import com.bank.account.Account;
import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseUpdateHelper;

public class Deserialize {

  @SuppressWarnings("unchecked")
  public void deserializeData(Context context) throws InvalidNameException {

    // initialize empty lists
    ArrayList<User> newUsers = new ArrayList<>();
    ArrayList<Account> newAccounts = new ArrayList<>();
    ArrayList<String> newPasswords = new ArrayList<>();
    ArrayList<String> newRoleNames = new ArrayList<>();
    ArrayList<String> newAccountTypeNames = new ArrayList<>();
    ArrayList<String> newInterestRates = new ArrayList<>();
    ArrayList<List<Integer>> newUserAccounts = new ArrayList<>();
    ArrayList<List<String>> newMessages = new ArrayList<>();
    ArrayList<List<Integer>> newMessageStatuses = new ArrayList<>();

    // open input stream to receive info from a serialized file
    try {
      File file = new File(context.getFilesDir(),"database_copy.ser");
      FileInputStream stream = new FileInputStream(file);
      ObjectInputStream in = new ObjectInputStream(stream);
      newUsers = (ArrayList<User>) in.readObject();
      newPasswords = (ArrayList<String>) in.readObject();
      newRoleNames = (ArrayList<String>) in.readObject();
      newAccountTypeNames = (ArrayList<String>) in.readObject();
      newInterestRates = (ArrayList<String>) in.readObject();
      newAccounts = (ArrayList<Account>) in.readObject();
      newUserAccounts = (ArrayList<List<Integer>>) in.readObject();
      newMessages = (ArrayList<List<String>>) in.readObject();
      newMessageStatuses = (ArrayList<List<Integer>>) in.readObject();
      in.close();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }


    // insert the roles info into the database
    for (int i = 0; i < newRoleNames.size(); i++) {
      DatabaseInsertHelper.insertRole(newRoleNames.get(i), context);
    }

    // insert the account types info into the database
    for (int i = 0; i < newAccountTypeNames.size(); i++) {
      String name = newAccountTypeNames.get(i);
      BigDecimal rate = new BigDecimal(newInterestRates.get(i));
      DatabaseInsertHelper.insertAccountType(name, rate, context);
    }

    // insert all the new users into the new database
    for (int i = 0; i < newUsers.size(); i++) {
      String name = newUsers.get(i).getName();
      int age = newUsers.get(i).getAge();
      int roleId = newUsers.get(i).getRoleId();
      String address = newUsers.get(i).getAddress();
      String password = newPasswords.get(i);
      int id = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password, context);
      DatabaseUpdateHelper.updatePassword(password, id, context);
    }

    // insert all the new accounts into the database
    for (int i = 0; i < newAccounts.size(); i++) {
      String name = newAccounts.get(i).getName();
      BigDecimal balance = newAccounts.get(i).getBalance();
      int typeId = newAccounts.get(i).getType();
      DatabaseInsertHelper.insertAccount(name, balance, typeId, context);
    }

    // insert all user-account relationships to the database
    for (int i = 0; i < newUserAccounts.size(); i++) {
      for (int x = 0; x < newUserAccounts.get(i).size(); x++) {
        DatabaseInsertHelper.insertUserAccount(i + 1, newUserAccounts.get(i).get(x), context);
      }
    }

    // insert all the messages into the database
    for (int i = 0; i < newMessages.size(); i++) {
      for (int x = 0; x < newMessages.get(i).size(); x++) {
        DatabaseInsertHelper.insertMessage(i + 1, newMessages.get(i).get(x), context);
      }
    }

    //update the statuses of the messages
    int ctr = 1;
    for (int i = 0; i < newMessageStatuses.size(); i++) {
      for (int x = 0; x < newMessageStatuses.get(i).size(); x++) {
        if (newMessageStatuses.get(i).get(x) == 1) {
          DatabaseUpdateHelper.updateUserMessageState(ctr, context);
        }
        ctr++;
      }
    }
  }
}
