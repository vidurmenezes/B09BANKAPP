package com.bank.account;

import java.math.BigDecimal;

import com.bank.database.DatabaseSelectHelper;

import android.content.Context;

import java.util.List;

public class ChequingAccount extends Account {

  private BigDecimal interestRate;
  private int type;

  public ChequingAccount(int id, String name, BigDecimal balance, Context context) {
    super();
    this.setName(name);
    this.setId(id);
    this.setBalance(balance);
    this.type = DatabaseSelectHelper.getAccountType(id, context);
  }

  @Override
  public void findAndSetInterestRate(Context context) {
    // get ids of all account types
    List<Integer> listId = DatabaseSelectHelper.getAccountTypesIds(context);
    for (int i = 0; i < listId.size(); i++) {
      // get the type id
      String typename = DatabaseSelectHelper.getAccountTypeName(listId.get(i), context);
      if (typename.equals("CHEQUING")) {
        this.type = listId.get(i);
      }
    }
    // get interest rate and set it
    BigDecimal interestRate = DatabaseSelectHelper.getInterestRate(this.type, context);
    this.interestRate = interestRate;
  }

  @Override
  public void addInterest() {
    BigDecimal interest = this.getBalance().multiply(interestRate);
    this.setBalance(this.getBalance().add(interest));
  }

  @Override
  public int getType() {
    return this.type;
  }
}
