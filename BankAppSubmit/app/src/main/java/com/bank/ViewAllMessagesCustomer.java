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
import com.bank.terminals.ATM;
import com.example.vidur.bankapp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewAllMessagesCustomer extends AppCompatActivity {

    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    Context context;
    int id;
    ATM atm = null;
    ArrayList<String> accountBalance = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_messages_customer);

        context = this.getApplicationContext();
        mainListView = (ListView) findViewById(R.id.mainListView);
        id = getIntent().getIntExtra("customerId", id);
        atm = (ATM) getIntent().getSerializableExtra("atm");
        accountBalance = getIntent().getStringArrayListExtra("balances");

        List<String> messages = new ArrayList<>();
        // get a list of all of this user's message ids
        List<Integer> messageIds = DatabaseSelectHelper.getAllMessageIds(id, context);
        messages.add("This is a list of messages for you: ");
        String currMessage;
        for (Integer iD : messageIds) {
            currMessage = DatabaseSelectHelper.getSpecificMessage(iD, context);
            messages.add(iD + ":" + currMessage);
            if (!currMessage.substring(2, 6).equals("@#$%")) {
                DatabaseUpdateHelper.updateUserMessageState(iD, context);
            }
        }
        Toast.makeText(getApplicationContext(), "Each message's status has been changed", Toast.LENGTH_LONG).show();

        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, messages);

        mainListView.setAdapter(listAdapter);

        Button Exit = (Button) findViewById(R.id.Exit1);
        Exit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent i = new Intent(ViewAllMessagesCustomer.this, Customerlogin.class);
                i.putExtra("atm", atm);
                i.putExtra("customerId", id);
                i.putStringArrayListExtra("balances", accountBalance);
                startActivity(i);
            }
        });
    }

    public static class ViewTellerMessages extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_teller_messages);
        }
    }
}
