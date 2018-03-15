package com.bank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.EnumMapRoles;
import com.bank.generics.Roles;
import com.bank.user.User;
import com.example.vidur.bankapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrentAdmins extends Activity {
  private ListView mainListView;
  private ArrayAdapter<String> listAdapter;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_current_admins);

    context = this.getApplicationContext();
    EnumMapRoles roleMap = new EnumMapRoles(context);

    mainListView = findViewById(R.id.mainListView);
    int ctr = 1;
    boolean loop = true;
    List<String> adminUsers = new ArrayList<>();
    // Get user details from database and store in list
    try {
      while (loop) {
        User user = DatabaseSelectHelper.getUserDetails(ctr, context);
        if (user.getRoleId() == roleMap.get(Roles.ADMIN)) {
          adminUsers.add(user.getName());
        }
        ctr++;
      }
    } catch (Exception e) {
    }

    listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, adminUsers);

    mainListView.setAdapter(listAdapter);

    Button exit = (Button) findViewById(R.id.Exit1);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(CurrentAdmins.this, Admin.class);
        startActivity(i);
      }
    });

  }
}
