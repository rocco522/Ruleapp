package com.tecnosoluciones.ruleapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.OutputStream;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BluetoothControl extends ActionBarActivity {

    /**
     * Constant of the extra address
     */
    public static String EXTRA_ADDRESS = "device_address";

    /**
     * Variable to call the button widget in the layout
     */
    Button btnPaired;

    /**
     * Variable to call the listView widget in the layout
     */
    ListView lvDeviceList;

    /**
     * The bluetooth adapter
     */
    private BluetoothAdapter myBluetooth = null;

    /**
     * The set of paired bluetooth devices
     */
    private Set<BluetoothDevice> pairedDevices;

    /**
     * The output stream
     */
    private OutputStream outStream = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_control);

        btnPaired = (Button)findViewById(R.id.btnPaired);
        lvDeviceList = (ListView)findViewById(R.id.lvPaired);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth==null){

            //Message saying that the device doesnt have bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if(myBluetooth.isEnabled()){}
            else{

                //Ask to the user turn the bluetooth on
                Intent turnOnBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOnBT,1);
            }
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });
    }

    /**
     * Shows the paired devices
     */
    private void pairedDevicesList() {

        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if(pairedDevices.size()>0){
            for(BluetoothDevice bd : pairedDevices){
                list.add(bd.getName() + "/n" + bd.getAddress());
            }
        }else{
            Toast.makeText(getApplicationContext(), "No paired Bluetooth Devices Found.",
                    Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        lvDeviceList.setAdapter(adapter);
        lvDeviceList.setOnItemClickListener(myListClickListener);
    }

    /**
     * Adapts the event of the list view
     */
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent i = new Intent(BluetoothControl.this, LightControl.class);

            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at LightControl (class) Activity
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
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
