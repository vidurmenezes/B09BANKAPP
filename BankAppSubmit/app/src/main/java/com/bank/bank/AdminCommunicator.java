package com.bank.bank;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.InvalidNameException;
import com.bank.user.Admin;
import com.bank.user.Teller;
import com.bank.user.User;
import com.bank.account.Account;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;

public class AdminCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Allows ADMIN to run admin interface if user input it validated
   */
  public static void runAdmin(Context context) throws IOException {
    boolean authenticated = false;
    boolean quit = false;
    int id = 0;
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    // Run ADMIN mode
    while ((authenticated != true) && (quit != true)) {
      // Keep running until user inputs -1
      if (id != -1) {
        System.out.println("Enter the Admin Id (Enter -1 to exit)");
        try {
          id = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        // Check if inputed id is valid
        User user = DatabaseSelectHelper.getUserDetails(id, context);
        if ((user != null) && (user.getRoleId() == roleMap.get(Roles.ADMIN))) {
          // Authenticate inputed password
          System.out.println("Enter the Admin password");
          String pass = input.readLine();
          Admin admin = (Admin) DatabaseSelectHelper.getUserDetails(id, context);
          boolean passed = admin.authenticate(pass, context);
          if (passed == true) {
            authenticated = true;
            // Allow ADMIN to run the admin interface
            quit = true;
            runAdminInterface(admin.getId(), context);
          } else {
            System.out.println("Invalid password");
          }
        } else {
          if (id != -1) {
            System.out.println("This is not a valid admin id");
          }
        }
      } else {
        quit = true;
        System.out.println("Interface closed");
      }
    }
  }

  /**
   * Allows admin to choose and run an option from the display list
   *
   * @param adminId the id of the logged in admin.
   */
  protected static void runAdminInterface(int adminId, Context context) throws IOException {
    String userInput = "";
    // Create a map of all roles in the database
    EnumMapRoles roleMap = new EnumMapRoles(context);
    while (!(userInput.equals("13"))) {
      // Display all options
      System.out.println("Welcome admin, choose an option.");
      System.out.println("{1} Create a new Admin");
      System.out.println("{2} Create a new Teller");
      System.out.println("{3} View all current Admins");
      System.out.println("{4} View all current Tellers");
      System.out.println("{5} View all current Customers");
      System.out.println("{6} View total balance of all accounts of given user");
      System.out.println("{7} View balances of all accounts");
      System.out.println("{8} Serialize Database");
      System.out.println("{9} Deserialize Database");
      System.out.println("{10} Promote Teller to Admin");
      System.out.println("{11} View Messages");
      System.out.println("{12} Leave Message");
      System.out.println("{13} Logout");
      // Read user input
      userInput = input.readLine();
      // If user input is 1, enter info and create new admin
      if (userInput.equals("1")) {
        System.out.println("You will now create a new Admin.");
        System.out.println("Enter the name.");
        String name = input.readLine();
        System.out.println("Enter age");
        int age = -1;
        try {
          age = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        System.out.println("Enter address");
        String address = input.readLine();
        System.out.println("Enter new password");
        String password = input.readLine();

        int success = -1;
        // Insert info into database
        try {
          success = DatabaseInsertHelper.insertNewUser(name, age, address, 1, password, context);
        } catch (InvalidNameException e) {
          e.printStackTrace();
        }
        // If insert failed, print statement
        if (success == -1) {
          System.out.println("Insert failed");
          // If insert is a success, print statement and admin id
          // number
        } else {
          System.out.println("Admin created.");
          System.out.println("This Admin's id is " + success);
          System.out.println(" ");
        }
        // If user input is 2, enter info and create new teller
      } else if (userInput.equals("2")) {
        System.out.println("You will now create a new teller.");
        System.out.println("Enter the name.");
        String name = input.readLine();
        System.out.println("Enter age");
        int age = -1;
        try {
          age = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        System.out.println("Enter address");
        String address = input.readLine();
        System.out.println("Enter new password");
        String password = input.readLine();
        int success = -1;
        // Insert info into database
        try {
          success = DatabaseInsertHelper.insertNewUser(name, age, address, 2, password, context);
        } catch (InvalidNameException e) {
          e.printStackTrace();
        }
        // If insert failed, print statement
        if (success == -1) {
          System.out.println("Insert failed");
          // If insert is a success, print statement and teller id
          // number
        } else {
          System.out.println("Teller created.");
          System.out.println("This teller's id is " + success);
          System.out.println(" ");
        }
        // If user input is 3, view all current admins
      } else if (userInput.equals("3")) {
        int ctr = 1;
        boolean loop = true;
        String output = "";
        List<User> adminUsers = new ArrayList<>();
        // Get user details from database and store in list
        try {
          while (loop) {
            User user = DatabaseSelectHelper.getUserDetails(ctr, context);
            if (user.getRoleId() == roleMap.get(Roles.ADMIN)) {
              adminUsers.add(user);
            }
            ctr++;
          }
        } catch (Exception e) {
          output += "All current admins are : \n";
          for (int i = 0; i < adminUsers.size(); i++) {
            output += adminUsers.get(i).getName() + "\n";
          }
        }
        // Print all admins names
        System.out.println(output);
        // If user input is 4, view all current tellers
      } else if (userInput.equals("4")) {
        int ctr = 1;
        boolean loop = true;
        String output = "";
        List<User> tellerUsers = new ArrayList<>();
        // Get user details from database and store in list
        try {
          while (loop) {
            User user = DatabaseSelectHelper.getUserDetails(ctr, context);
            if (user.getRoleId() == roleMap.get(Roles.TELLER)) {
              tellerUsers.add(user);
            }
            ctr++;
          }
        } catch (Exception e) {
          output += "All current tellers are : \n";
          for (int i = 0; i < tellerUsers.size(); i++) {
            output += tellerUsers.get(i).getName() + "\n";
          }
        }
        // Print all tellers names
        System.out.println(output);
        // If user input is 5, view all current customers
      } else if (userInput.equals("5")) {
        int ctr = 1;
        boolean loop = true;
        String output = "";
        List<User> customerUsers = new ArrayList<>();
        // Get user details from database and store in list
        try {
          while (loop) {
            User user = DatabaseSelectHelper.getUserDetails(ctr, context);
            if (user.getRoleId() == roleMap.get(Roles.CUSTOMER)) {
              customerUsers.add(user);
            }
            ctr++;
          }
        } catch (Exception e) {
          output += "All current customers are : \n";
          for (int i = 0; i < customerUsers.size(); i++) {
            output += customerUsers.get(i).getName() + "\n";
          }
        }
        // Print all customers names
        System.out.println(output);
      } else if (userInput.equals("6")) {
        getTotalUserBalance(context);
      } else if (userInput.equals("7")) {
        int ctr = 1;
        boolean loop = true;
        String output = "";
        List<Account> accounts = new ArrayList<>();
        Account account = null;
        // Get account details from database and store in list
        while (loop) {
          account = DatabaseSelectHelper.getAccountDetails(ctr, context);
          if (account == null) {
            break;
          }
          accounts.add(account);
          ctr++;
        }
        output += "All accounts and their balances are : \n";
        for (Iterator<Account> i = accounts.iterator(); i.hasNext(); ) {
          account = i.next();
          output += account.getName() + " : $" + account.getBalance() + "\n";
        }
        // Print the accounts and balances
        System.out.println(output);
      } else if (userInput.equals("8")) {
        System.out.println("Serialization started");
        Serialize saveData = new Serialize();
        saveData.serializeData(context);
        System.out.println("Serialization finished: New file 'database_copy.ser' created.");
      } else if (userInput.equals("9")) {
        System.out.println("Deserialization started");
        Deserialize getData = new Deserialize();
        try {
          getData.deserializeData(context);
        } catch (InvalidNameException e) {
          e.printStackTrace();
        }
        System.out.println("Deserialization finished: new database 'bank.db' created.");
      } else if (userInput.equals("10")) {
        int tellerId = 0;
        boolean done = false;
        System.out.println(
            "Please enter the id of the teller you want to promote " + "(enter -1 to exit):");
        while (!done) {
          try {
            tellerId = Integer.parseInt(input.readLine());
            @SuppressWarnings("unused")
            User teller = (Teller) DatabaseSelectHelper.getUserDetails(tellerId, context);
            DatabaseUpdateHelper.updateUserRole(roleMap.get(Roles.ADMIN), tellerId, context);
            System.out.println("Teller promoted");
            String message = "You were promoted from Teller to Admin";
            int messageId = DatabaseInsertHelper.insertMessage(tellerId, message, context);
            System.out.println("Message inserted, the message Id is: " + messageId);
            done = true;
          } catch (NumberFormatException e) {
            System.out.println("Please enter a number");
          } catch (ClassCastException e) {
            System.out.println("User is not a teller. Did not promote to administrator.");
            if (tellerId == -1) {
              done = true;
            }
          }
        }
      } else if (userInput.equals("11")) {
        int id = -1;
        String output = "";
        System.out.println("Enter the UserId of the user whose messages you would like to view");
        // get the id
        try {
          id = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        // check if the id inputed is the id of the current logged in admin
        if (id == adminId) {
          // get a list of all of this user's message ids
          List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(id, context);
          output += "This is a list of messages for your own account: " + "\n";
          for (Integer iD : messageIds) {
            output += DatabaseSelectHelper.getSpecificMessage(iD, context) + "\n";
            DatabaseUpdateHelper.updateUserMessageState(iD, context);
          }
          output += "Each message's status has been changed";
        } else {
          // get a list of all of this user's message ids
          List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(id, context);
          output += "This is a list of messages for this user: " + "\n";
          for (Integer iD : messageIds) {
            output += DatabaseSelectHelper.getSpecificMessage(iD, context) + "\n";
          }
          output += "No message status was changed";
        }
        System.out.println(output);

      } else if (userInput.equals("12")) {
        int id = -1;
        System.out
            .println("Enter the UserId of the user who you would like to leave a message for.");
        // get the id
        try {
          id = Integer.parseInt(input.readLine());
        } catch (NumberFormatException e) {
          System.out.println("Please enter a number");
        }
        System.out.println("Please enter the message.");
        String message = input.readLine();
        int messageId = DatabaseInsertHelper.insertMessage(id, message, context);
        System.out.println("Message inserted, the message Id is: " + messageId);
      } else {
        runAdmin(context);
      }
    }
  }


  /**
   * Prints the total balance of all accounts from given user
   */
  public static void getTotalUserBalance(Context context) throws IOException {
    int id = -1;
    do {
      System.out.println(
          "Please enter the user id that you want to check the balance for (enter 0 to exit):");
      try {
        String userId = input.readLine();
        id = Integer.parseInt(userId);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a number");
      }
      User customer = DatabaseSelectHelper.getUserDetails(id, context);
      EnumMapRoles roleMap = new EnumMapRoles(context);
      if (customer == null && id != 0) {
        System.out.println("Please enter a valid id");
      } else if (customer.getRoleId() == roleMap.get(Roles.CUSTOMER)) { // If the id is of a
        // customer
        BigDecimal total = BigDecimal.ZERO;
        List<Integer> accIds = DatabaseSelectHelper.getAccountIds(id, context);
        Account account = null;
        // Add the balance to total from each account
        for (Iterator<Integer> i = accIds.iterator(); i.hasNext(); ) {
          id = i.next();
          account = DatabaseSelectHelper.getAccountDetails(id, context);
          total = total.add(account.getBalance());
        }
        System.out.println("Total balance for " + customer.getName() + " is " + total);
        id = 0;
      } else {
        System.out.println("The id does not belong to a customer");
      }
    } while (id != 0);
  }
}
