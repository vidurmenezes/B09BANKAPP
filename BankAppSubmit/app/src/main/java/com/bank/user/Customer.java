package com.bank.user;

import com.bank.database.DatabaseSelectHelper;
import com.bank.account.Account;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

  private String address;
  private int roleId;
  private List<Account> accounts = new ArrayList<>();

  /**
   * create a Customer object under User
   * 
   * @param id of customer
   * @param name of customer
   * @param age of customer
   * @param address of customer
   */
  public Customer(int id, String name, int age, String address, Context context) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, context);
  }

  /**
   * customer with authentication
   * 
   * @param id of customer
   * @param name of customer
   * @param age of customer
   * @param address of customer
   * @param authenticated if the customer is authenticated or not
   */
  public Customer(int id, String name, int age, String address, boolean authenticated,
                  Context context) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, context);
  }

  @Override
  public String getAddress() {
    // the address of customer
    return this.address;
  }

  @Override
  public int getRoleId() {
    // the role id of the customer
    return this.roleId;
  }

  /**
   * Get a list all the account of this customer
   * 
   * @return list all the account of this customer
   */
  public List<Account> getAccounts() {
    // list of accounts under the customer
    return this.accounts;
  }

  /**
   * Add an account for the customer
   * 
   * @param account the account to be added
   */
  public void addAccount(Account account) {
    // add account for the customer
    this.accounts.add(account);
  }

}
