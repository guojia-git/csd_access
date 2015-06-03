package com.cs211.csdaccess;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends ActionBarActivity {

    Button btn_change, btn_backup;
    TextView tv_mac;
    EditText et_mac;

    private String true_mac = "";
    private String authenticate_mac = "a4:99:47:f1:f6:68";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initCheckMac();
    }

    private void initCheckMac(){
        Util.init(getApplicationContext());
        //tv_mac.setText(Util.BUSYBOX_PATH);
        //File busybox = null;
        File busybox = new File(Util.BUSYBOX_PATH);
        Util.getassetsfile(this, "busybox", busybox);
        Util.runCommand("/system/bin/chmod 777 " + Util.BUSYBOX_PATH);
        String mac = Util.getMac(this);
        if (mac != null) {
            tv_mac.setText(mac);
            true_mac = mac;
        } else {
            tv_mac.setText("Error");
        }

    }
    private void initComponents() {
        // Init layout
        btn_change = (Button) findViewById(R.id.btn_change);
        btn_backup = (Button) findViewById(R.id.btn_backup);
        tv_mac = (TextView) findViewById(R.id.tv_mac);
        et_mac = (EditText) findViewById(R.id.et_mac);

        // Set default mac
        et_mac.setText(authenticate_mac);

        // Onclick listener
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mac = et_mac.getText().toString();
                if (mac.length() != 17) {
                    Toast.makeText(getApplicationContext(), "MAC Address Invalid!", Toast.LENGTH_LONG).show();
                    return;
                }
                Util.setMac(mac);
                mac = Util.getMac(getApplicationContext());
                tv_mac.setText(mac);
            }
        });
        btn_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true_mac.length() == 17) {
                    Util.setMac(true_mac);
                }
                String mac = Util.getMac(getApplicationContext());
                tv_mac.setText(mac);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
