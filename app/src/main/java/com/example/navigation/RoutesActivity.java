package com.example.navigation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.navigation.database.AppDatabase;
import com.example.navigation.utils.DatabaseInitializer;
import com.example.navigation.utils.DatabaseListner;
import com.example.navigation.utils.RouteInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoutesActivity extends AppCompatActivity  {


    DatabaseInitializer database;

    ArrayAdapter<String> adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText txtUrl = new EditText(RoutesActivity.this);

                txtUrl.setHint("Home");
                showInputDialouge(txtUrl);
            }
        });

        database = new DatabaseInitializer(AppDatabase.getAppDatabase(this));


        listView = (ListView) findViewById(R.id.routelist);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;
                //in normal case
                stringText= ((TextView)view).getText().toString();

                //show selected
                Intent addRoutesPoint = new Intent(RoutesActivity.this,MapsActivity.class);
                addRoutesPoint.putExtra("routename",stringText);
                startActivity(addRoutesPoint);
            }
        });
        updaterouteList();

    }

    private void updaterouteList(){
        ArrayList<String> routes = database.getAllRouteName();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, routes);
        listView.setAdapter(adapter);
    }

    private void showInputDialouge(final EditText editText){

        new AlertDialog.Builder(this)
                .setTitle("Route Name")
                .setMessage("Please enter route name here")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = editText.getText().toString();
                        Toast.makeText(RoutesActivity.this, name, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RoutesActivity.this,MapsActivity.class);
                        intent.putExtra("routename",name);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updaterouteList();
    }
}
