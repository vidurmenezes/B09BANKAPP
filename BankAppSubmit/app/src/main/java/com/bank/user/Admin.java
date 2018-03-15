package com.bank.user;

import com.bank.database.DatabaseSelectHelper;
import android.content.Context;

/**
 * Admin object that extends User
 * 
 */
public class Admin extends User {

  private String address;
  private int roleId;

  /**
   * create a Admin.
   * 
   * @param id which is the integer given
   * @param name of the admin
   * @param age of admin
   * @param address of admin
   */
  public Admin(int id, String name, int age, String address, Context context) {
    // set id,name,address and get role id.
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, context);
  }

  /**
   * already authenticated new admin.
   * 
   * @param id which is the id of admin
   * @param name of admin
   * @param age of admin
   * @param address of admin
   * @param authenticated of admin
   */
  public Admin(int id, String name, int age, String address, boolean authenticated,
               Context context) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, context);
  }

  @Override
  public int getRoleId() {
    // return the role id number
    return this.roleId;
  }

  @Override
  public String getAddress() {
    // return the string which is the address
    return this.address;
  }
}
