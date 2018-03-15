package com.bank.user;

import android.content.Context;

public interface Authenticate {
  /**
   * Authenticating the user
   *
   * @param password that should be correct
   * @return authenticated
   */
  public boolean authenticate(String password, Context context);

}
