package com.monday2105.smarttank;

import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class vehiclestats extends Fragment implements  View.OnClickListener{

    TextView fuel;
    TextView position;
    TextView lock;

    Button fuelB;
    Button positionB;
    Button lockB;

    SQLiteDatabase db;
    public static final String DATABASE_NAME = "SmartTank";
    public static final String TABLE_NAME = "TankNumber";
    public static String SERVICE_NUMBER = "";
    String value;

    private SmsBroadcastReceiver smsBroadcastReceiver;
    private static String SERVICE_CONDITION = "-----SmartTank-----";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.vehiclestats,container,false);
        fuel = (TextView) view.findViewById(R.id.fuelText);
        position = (TextView) view.findViewById(R.id.positionText);
        lock = (TextView) view.findViewById(R.id.lockText);

        fuelB = (Button) view.findViewById(R.id.fuelRefresh);
        positionB = (Button) view.findViewById(R.id.positionRefresh);
        lockB = (Button) view.findViewById(R.id.lockRefresh);

        fuelB.setOnClickListener(this);
        positionB.setOnClickListener(this);
        lockB.setOnClickListener(this);

        value = ((Navigation) getActivity()).getSupportActionBar().getTitle().toString();

        db = getActivity().openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        try {
            Cursor cursorDb = db.rawQuery("SELECT number FROM " + TABLE_NAME + " WHERE name=\""+value+"\"", null);
            if(cursorDb.moveToFirst()) {
                SERVICE_NUMBER = cursorDb.getString(0);

            }
        }
        catch (Exception e){
            Log.d("SQL get no. vehcstats",e.getMessage());
        }

        smsBroadcastReceiver = new SmsBroadcastReceiver(SERVICE_NUMBER, SERVICE_CONDITION, getActivity().getApplicationContext());
        getActivity().registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String text) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fuelRefresh:
                SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----\nqwer", null, null);
                Toast.makeText(getActivity().getApplicationContext(),"Message sent to refresh fuel",Toast.LENGTH_LONG).show();
                break;

            case R.id.positionRefresh:
                SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----\nhjkl", null, null);
                Toast.makeText(getActivity().getApplicationContext(),"Message sent to refresh position",Toast.LENGTH_LONG).show();
                break;

            case R.id.lockRefresh:
                SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----\nuiop", null, null);
                Toast.makeText(getActivity().getApplicationContext(),"Message sent to refresh lock status",Toast.LENGTH_LONG).show();
                break;
        }
    }
}