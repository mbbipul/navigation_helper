package com.example.navigation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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

public class RoutesActivity extends AppCompatActivity {


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

                txtUrl.setHint("biplku");
                showInputDialouge(txtUrl);
            }
        });


        String[] routes = {"home","work","home","work","home","work"};
        ListView listView = (ListView) findViewById(R.id.routelist);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;
                //in normal case
                stringText= ((TextView)view).getText().toString();

                //show selected
                Intent addRoutesPoint = new Intent(RoutesActivity.this,AddRoutePointsActivity.class);
                addRoutesPoint.putExtra("routeName",stringText);
                startActivity(addRoutesPoint);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
                        String url = editText.getText().toString();
                        Toast.makeText(RoutesActivity.this, url, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

}
