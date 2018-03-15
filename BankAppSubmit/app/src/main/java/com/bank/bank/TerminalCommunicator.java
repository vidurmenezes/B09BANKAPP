package com.bank.bank;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.bank.exceptions.InvalidNameException;

public class TerminalCommunicator {

  private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Runs terminal and allows user to choose either TELLER or ATM interfaces, or exit
   */
  public static void runTerminal(Context context) throws IOException, NumberFormatException, InvalidNameException {
    boolean rePrompt = true;
    while (rePrompt == true) {
      // Show context menu and receive input
      System.out.println("Please enter the right number to chose an option");
      System.out.println("{1} TELLER");
      System.out.println("{2} ATM");
      System.out.println("{0} Exit");
      String userInput = input.readLine();
      // Run the TELLER interface for an input of 1
      if (userInput.equals("1")) {
        TellerCommunicator.runTeller(context);
        // Run the ATM interface for an input of 2
      } else if (userInput.equals("2")) {
        AtmCommunicator.runAtm(context);
        // Exit the program for an input of 0
      } else if (userInput.equals("0")) {
        // Print exit statements
        System.out.println("Program exiting...");
        System.out.println("Exited");
        System.exit(0);
      } else {
        rePrompt = true;
      }
    }
  }
}
