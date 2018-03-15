package com.bank;

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
import java.util.List;

import static com.example.vidur.bankapp.R.id.mainListView;

public class CurrentCustomers extends AppCompatActivity {
  private ListView mainListView;
  private ArrayAdapter<String> listAdapter;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_current_customers);
    context = this.getApplicationContext();
    EnumMapRoles roleMap = new EnumMapRoles(context);
    mainListView = (ListView) findViewById(R.id.mainListViewCustomer);

    int ctr = 1;
    boolean loop = true;
    List<String> customerUsers = new ArrayList<>();
    // Get user details from database and store in list
    try {
      while (loop) {
        User user = DatabaseSelectHelper.getUserDetails(ctr, context);
        if (user.getRoleId() == roleMap.get(Roles.CUSTOMER)) {
          customerUsers.add(user.getName());
        }
        ctr++;
      }
    } catch (Exception e) {
    }

    ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, customerUsers);

    mainListView.setAdapter(listAdapter);

    Button exit = (Button) findViewById(R.id.Exit1);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(CurrentCustomers.this, Admin.class);
        startActivity(i);
      }
    });
  }
}
