package com.bank.user;

import com.bank.database.DatabaseSelectHelper;
import android.content.Context;

/**
 * creates a teller object from user
 *
 */
public class Teller extends User {

  private String address;
  private int roleId;

  /**
   * create a Teller object under User
   * 
   * @param id of the Teller
   * @param name of the teller
   * @param age of the teller
   * @param address of the teller
   */
  public Teller(int id, String name, int age, String address, Context context) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, context);
  }

  /**
   * teller object with authentication
   * 
   * @param id of the teller
   * @param name of the teller
   * @param age of the teller
   * @param address of teller
   * @param authenticated if the password matches
   */
  public Teller(int id, String name, int age, String address, boolean authenticated, Context con) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.address = address;
    this.roleId = DatabaseSelectHelper.getUserRole(id, con);
  }

  @Override
  public int getRoleId() {
    // return the role id of teller
    return this.roleId;
  }

  @Override
  public String getAddress() {
    // return the address in string format
    return this.address;
  }
}
