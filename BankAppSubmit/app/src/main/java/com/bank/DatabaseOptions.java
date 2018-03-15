package com.bank;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bank.bank.Deserialize;
import com.bank.bank.Serialize;
import com.bank.database.DatabaseDriverA;
import com.bank.exceptions.InvalidNameException;
import com.example.vidur.bankapp.R;

public class DatabaseOptions extends AppCompatActivity {

  Context context;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_database_options);
    context = this.getApplicationContext();

    Button exit = (Button) findViewById(R.id.exit);
    exit.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Intent i = new Intent(DatabaseOptions.this, Admin.class);
        startActivity(i);
      }
    });

    Button serialize = (Button) findViewById(R.id.serializeDatabase);
    serialize.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "Serialization started", Toast.LENGTH_LONG).show();
        Serialize saveData = new Serialize();
        saveData.serializeData(context);
        Toast.makeText(getApplicationContext(), "Serialization finished: New file " +
                "'database_copy.ser' created.", Toast.LENGTH_LONG).show();
      }
    });

    Button deserialize = (Button) findViewById(R.id.deserializeDatabase);
    deserialize.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        // erase old database and initialize new one
        DatabaseDriverA mydb = new DatabaseDriverA(context);
        mydb.onUpgrade(mydb.getWritableDatabase(), 1, 2);

        Toast.makeText(getApplicationContext(), "Deserialization started", Toast.LENGTH_LONG).show();
        Deserialize getData = new Deserialize();
        try {
          getData.deserializeData(context);
        } catch (InvalidNameException e) {
          e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Deserialization finished: new database 'bank.db'" +
                " created.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(DatabaseOptions.this, MainActivity.class);
        startActivity(i);
      }
    });
  }
}
