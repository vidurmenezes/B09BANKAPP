package com.bank.user;

import com.bank.security.PasswordHelpers;

import java.io.Serializable;

import com.bank.database.DatabaseSelectHelper;
import android.content.Context;

/**
 * creates a abstract User
 * 
 * @author vidur
 *
 */
public abstract class User implements Authenticate, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -8096008970864520415L;
  // initialize variables
  private int id;
  private String name;
  private String address;
  private int age;
  private int roleId;
  private boolean authenticated;

  /**
   * Sets the id of the user.
   * 
   * @param id the id to be set.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the id of the user.
   * 
   * @return the id of the user.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets the name of the user.
   * 
   * @param name the name to be set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the user.
   * 
   * @return the name of the user.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the age of the user.
   * 
   * @param age the age to be set.
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Gets the age of the user.
   * 
   * @return the age of the user.
   */
  public int getAge() {
    return this.age;
  }

  /**
   * Gets the roleID of the user.
   * 
   * @return the roleID of the user.
   */
  public int getRoleId() {
    return this.roleId;
  }

  /**
   * Authenticates the given password with the one in the database
   * 
   * @param password the password given by the user
   * @param context is the context received from the activity
   * @return if the passwords match
   */
  public final boolean authenticate(String password, Context context) {
    // get the password for this user from the database
    String pass = DatabaseSelectHelper.getPassword(this.getId(), context);
    // compares the two passwords
    this.authenticated = PasswordHelpers.comparePassword(pass, password);
    return this.authenticated;
  }

  public String getAddress() {
    return this.address;
  }
}
