package com.example.navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import com.example.navigation.database.AppDatabase;
import com.example.navigation.utils.DatabaseInitializer;
import com.example.navigation.utils.SocketHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;
import com.vikramezhil.droidspeech.OnDSPermissionsListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RoutesActivity extends AppCompatActivity {


    DatabaseInitializer database;

    ArrayAdapter<String> adapter;
    ListView listView;
    Socket mSocket;
    private Boolean isConnected = true;
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
                addRoutesPoint.putExtra("viewmode",true);
                startActivity(addRoutesPoint);
            }
        });
        updaterouteList();
//        SocketHelper app = new SocketHelper();
//        mSocket = app.getSocket();
//        mSocket.on(Socket.EVENT_CONNECT,onConnect);
//        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.on("distance", onNewMessage);

        //mSocket.connect();



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
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        mSocket.emit("add user", "android apps");
                        Toast.makeText(RoutesActivity.this,
                                "connect", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("ANDROID", "diconnected");
                    isConnected = false;
                    Toast.makeText(RoutesActivity.this,
                            "disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("ANDROID", "Error connecting");
                    Toast.makeText(RoutesActivity.this,
                            "eroor connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    double distance;
                    try {
                        distance = data.getDouble("distance");
                        Toast.makeText(RoutesActivity.this, String.valueOf(distance), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.e("Message", e.getMessage());
                        return;
                    }
                }
            });
        }
    };
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        mSocket.disconnect();
//
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.off("new message", onNewMessage);
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        mSocket.disconnect();
//
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
//        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
//        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
//        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
//        mSocket.off("new message", onNewMessage);
//    }


}
