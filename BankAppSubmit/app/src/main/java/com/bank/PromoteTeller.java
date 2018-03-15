package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.Teller;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

public class PromoteTeller extends AppCompatActivity {

  Context context;
  int id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_promote_teller);
    context = this.getApplicationContext();
    Button button = (Button) findViewById(R.id.promotetellersubmit);
    button.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        if (checkEmpty(context)) {
          Intent i = new Intent(PromoteTeller.this, Admin.class);
          startActivity(i);
        }
      }
    });
    Button exit = (Button) findViewById(R.id.Exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(PromoteTeller.this, Admin.class);
        startActivity(i);
      }
    });
  }

  private boolean checkEmpty(Context context) {
    EditText etId = (EditText) findViewById(R.id.telleridtopromote);
    id = Integer.valueOf(etId.getText().toString());
    boolean idAuthen = true;
    if (TextUtils.isEmpty(String.valueOf(id))) {
      etId.setError("No age");
      idAuthen = false;
    }
    boolean done = false;
    if (idAuthen) {
      done = runPromote(context);
    }
    return (done);
  }

  private boolean runPromote(Context context) {
    int tellerId = 0;
    boolean done = false;
    boolean success = false;
    EnumMapRoles roleMap = new EnumMapRoles(context);
    while (!done) {
      try {
        User teller = (Teller) DatabaseSelectHelper.getUserDetails(id, context);
        success = DatabaseUpdateHelper.updateUserRole(roleMap.get(Roles.ADMIN), id, context);
        if (success) {
          Toast.makeText(getApplicationContext(), "Teller Promoted", Toast.LENGTH_LONG)
              .show();
          String message = "You were promoted from Teller to Admin";
          int messageId = DatabaseInsertHelper.insertMessage(id, message, context);
          Toast.makeText(getApplicationContext(), "Message inserted, the message Id is: "
              + String.valueOf(messageId), Toast.LENGTH_LONG).show();
          done = true;
        }
      } catch (ClassCastException e) {
        Toast.makeText(getApplicationContext(), "User is not a teller. Did not promote to" +
            " administrator.", Toast.LENGTH_LONG).show();
        done = true;
      }
    }
    return success;
  }

}
