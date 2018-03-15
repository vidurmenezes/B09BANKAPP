
package com.bank.terminals;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import com.bank.account.Account;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NoAccessToAccountException;

/**
 * basic interfaces that allow the terminal to work.
 *
 * @author vidur
 */
public interface GeneralTasksInterface {
  /**
   * authenticate account.
   *
   * @param userId   which is the id of user
   * @param password which is given from user
   * @return boolean if the password is matching in database
   */
  public boolean authenticate(int userId, String password, Context context);

  /**
   * list the accounts.
   *
   * @return List<Account> which is the list of all accounts in database
   */
  public List<Account> listAccounts(Context context) throws NoAccessToAccountException;

  /**
   * make deposit into account.
   *
   * @param amount    is the amount wanting to put in
   * @param accountId is the id of account
   * @return boolean if the transaction went thru
   */
  public boolean makeDeposit(BigDecimal amount, int accountId, Context context)
      throws NoAccessToAccountException;

  /**
   * check the balance in account
   *
   * @param accountId which is the account id.
   * @return BigDecimal which is the amount in the account
   */
  public BigDecimal checkBalance(int accountId, Context context) throws NoAccessToAccountException;

  /**
   * make a withdrawal from account
   *
   * @param amount    is the amount wanting to take from account
   * @param accountId is the account id
   * @return boolean if transaction went thru.
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws InsuffiecintFundsException, NoAccessToAccountException;

}
