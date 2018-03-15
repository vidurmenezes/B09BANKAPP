package com.bank.account;

import java.io.Serializable;
import java.math.BigDecimal;

import android.content.Context;

public abstract class Account implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 7214670321541741647L;
  // initialize variables
  private int id;
  private String name;
  private BigDecimal balance;
  private int type;

  /**
   * Sets the id of the account.
   *
   * @param id the id to be set.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the id of the account.
   *
   * @return the id of the account.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Sets the name of the account.
   *
   * @param name the name to be set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the account.
   *
   * @return the name of the account.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the balance of the account.
   *
   * @param balance the balance to be set.
   */
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  /**
   * Gets the balance of the account.
   *
   * @return the balance of the account.
   */
  public BigDecimal getBalance() {
    return this.balance;
  }

  /**
   * Gets the type of the account.
   */
  public abstract int getType();

  /**
   * Set the interest rate of this type of account.
   */
  public abstract void findAndSetInterestRate(Context context);

  /**
   * add the interest to the current balance of this account
   */
  public abstract void addInterest();
}
