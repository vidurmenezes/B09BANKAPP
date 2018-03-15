package com.bank.account;

import java.math.BigDecimal;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;

import android.content.Context;

public class SavingsAccount extends Account {

  private BigDecimal interestRate;
  private int type;

  public SavingsAccount(int id, String name, BigDecimal balance, Context context) {
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
      if (typename.equals("SAVING")) {
        this.type = listId.get(i);
      }
    }
    // get interest rate and set it
    BigDecimal interestrate = DatabaseSelectHelper.getInterestRate(this.type, context);
    this.interestRate = interestrate;
  }

  @Override
  public void addInterest() {
    BigDecimal interestrate = this.getBalance().multiply(interestRate);
    this.setBalance(this.getBalance().add(interestrate));
  }

  @Override
  public int getType() {
    return this.type;
  }
}
