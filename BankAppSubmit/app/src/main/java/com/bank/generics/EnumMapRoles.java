package com.bank.generics;

import android.content.Context;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.bank.database.DatabaseSelectHelper;

public class EnumMapRoles extends EnumMap<Roles, Integer> {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor that makes the EnumMap and also updates it to the database
   */
  public EnumMapRoles(Context context) {
    super(Roles.class);
    this.EnumMapUpdate(context);
  }

  /**
   * maps the enumerator to each corresponding number
   */
  public void EnumMapUpdate(Context context) {
    // Get a list of all account type ids in the database
    List<Integer> roleIds = DatabaseSelectHelper.getRoles(context);
    List<String> roles = new ArrayList<>();
    // In the same order, get all of the account type names
    for (int i = 0; i < roleIds.size(); i++) {
      roles.add(DatabaseSelectHelper.getRole(roleIds.get(i), context));
    }
    for (int i = 0; i < roles.size(); i++) {
      this.put(Roles.valueOf(roles.get(i)), i + 1);
    }
  }
}
