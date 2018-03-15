package com.bank.generics;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;

public class EnumMapAccountTypes extends EnumMap<AccountTypes, Integer> {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor that makes the EnumMap and also updates it to the database
   */
  public EnumMapAccountTypes(Context context) {
    super(AccountTypes.class);
    this.EnumMapUpdate(context);
  }

  /**
   * maps all of enum for accountTypes.
   */
  public void EnumMapUpdate(Context context) {
    // Get a list of all account type ids in the database
    List<Integer> accountTypeIds = new ArrayList<>();
    try {
      accountTypeIds = DatabaseSelectHelper.getAccountTypesIds(context);
    } catch (NullPointerException e) {
      Toast.makeText(context, "NULL", Toast.LENGTH_LONG).show();
    }
    List<String> accountTypeNames = new ArrayList<>();
    // In the same order, get all of the account type names
    for (int i = 0; i < accountTypeIds.size(); i++) {
      accountTypeNames.add(DatabaseSelectHelper.getAccountTypeName(accountTypeIds.get(i), context));
    }
    for (int i = 0; i < accountTypeNames.size(); i++) {
      this.put(AccountTypes.valueOf(accountTypeNames.get(i)), i + 1);
    }
  }
}
