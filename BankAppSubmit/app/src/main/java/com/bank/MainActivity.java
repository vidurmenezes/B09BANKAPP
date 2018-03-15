package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.InvalidNameException;
import com.example.vidur.bankapp.R;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    context = this.getApplicationContext();
    if (DatabaseSelectHelper.getRoles(context).size() < 1) {
      try {
        DatabaseInsertHelper.insertRole("ADMIN", context);
        DatabaseInsertHelper.insertRole("TELLER", context);
        DatabaseInsertHelper.insertRole("CUSTOMER", context);
        DatabaseInsertHelper.insertAccountType("CHEQUING", BigDecimal.valueOf(0.2), context);
        DatabaseInsertHelper.insertAccountType("SAVING", BigDecimal.valueOf(0.2), context);
        DatabaseInsertHelper.insertAccountType("TFSA", BigDecimal.valueOf(0.2), context);
        DatabaseInsertHelper.insertAccountType("RESTRICTEDSAVING", BigDecimal.valueOf(0.2), context);
        DatabaseInsertHelper.insertAccountType("BALANCEOWING", BigDecimal.valueOf(0.2), context);
        int id = DatabaseInsertHelper.insertNewUser("GaganAdmin", 19, "123street", 1, "123", context);
        int id3 = DatabaseInsertHelper.insertNewUser("KarenCustomer", 19, "123street", 3, "123", context);
        int id4 = DatabaseInsertHelper.insertAccount("KarenCheq", BigDecimal.valueOf(100000), 1, context);
        int id5 = DatabaseInsertHelper.insertNewUser("GaganTeller", 19, "123street", 2, "123", context);
        int done2 = DatabaseInsertHelper.insertUserAccount(id3, id4, context);
        Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_LONG).show();
      } catch (InvalidNameException e) {
        Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG).show();
      }
    }
    Button button = (Button) findViewById(R.id.ADMIN);

    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, SecondActivity.class);

        startActivity(i);

      }
    });
    Button teller = (Button) findViewById(R.id.TELLER);

    teller.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, TellerLogin.class);
        startActivity(i);
      }
    });

    Button Customer = (Button) findViewById(R.id.CUSTOMER);

    Customer.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, Customerlogin.class);
        startActivity(i);
      }
    });

  }
   /* public boolean useridauthen1(){
        EditText etUserName = (EditText) findViewById(R.id.editText5);
        String strUserName = etUserName.getText().toString();
        EditText etUserName2 = (EditText) findViewById(R.id.editText4);
        String strUserName2 = etUserName2.getText().toString();
        boolean useridauthen = true;
        boolean passwordauthen=true;
        if(TextUtils.isEmpty(strUserName)) {
            etUserName.setError("No UserId");
            useridauthen = false;
        }
        if(TextUtils.isEmpty(strUserName2)) {
            etUserName2.setError("No password");
            passwordauthen = false;
        }

        return (useridauthen && passwordauthen);
    }
*/
}
