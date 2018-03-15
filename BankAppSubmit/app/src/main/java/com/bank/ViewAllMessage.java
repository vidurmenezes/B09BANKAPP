package com.bank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.example.vidur.bankapp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewAllMessage extends AppCompatActivity {
  private ListView mainListView;
  private ArrayAdapter<String> listAdapter;
  Context context;
  int adminId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_all_message);
    context = this.getApplicationContext();
    mainListView = (ListView) findViewById(R.id.mainListView);
    Intent i = getIntent();
    int id = Integer.valueOf(i.getStringExtra("id"));
    adminId = Integer.valueOf(i.getStringExtra("name"));
    List<String> messages = new ArrayList<>();
    // check if the id inputed is the id of the current logged in admin
    if (id == adminId) {
      // get a list of all of this user's message ids
      List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(id, context);
      messages.add("This is a list of messages for your own account: ");
      for (Integer iD : messageIds) {
        messages.add(DatabaseSelectHelper.getSpecificMessage(iD, context));
        DatabaseUpdateHelper.updateUserMessageState(iD, context);
      }
      Toast.makeText(getApplicationContext(), "Each message's status has been changed", Toast.LENGTH_LONG).show();
    } else {
      // get a list of all of this user's message ids
      List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(id, context);
      messages.add("This is a list of messages for this user: ");
      for (Integer iD : messageIds) {
        messages.add(DatabaseSelectHelper.getSpecificMessage(iD, context));
      }
      Toast.makeText(getApplicationContext(), "No message status was changed", Toast.LENGTH_LONG).show();
    }
    listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, messages);

    mainListView.setAdapter(listAdapter);

    Button Exit = (Button) findViewById(R.id.Exit1);
    Exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {

        Intent i = new Intent(ViewAllMessage.this, MessageOptions.class);
        i.putExtra("name", String.valueOf(adminId));
        startActivity(i);
      }
    });
  }
}
