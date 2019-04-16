package com.monday2105.smarttank;

import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.method.LinkMovementMethod;
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
    TextView latitude;
    TextView longitude;
    TextView lock;
    TextView pump;

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
        latitude = (TextView) view.findViewById(R.id.latitudeText);
        longitude = (TextView) view.findViewById(R.id.longitudeText);
        lock = (TextView) view.findViewById(R.id.lockText);
        pump = (TextView) view.findViewById(R.id.findPump);
        pump.setMovementMethod(LinkMovementMethod.getInstance());

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
                String splitLine[] = text.split("\n");
                for(int i=1;i<splitLine.length-1;i++){
                    String spltColon[] = splitLine[i].split(":");
                    try {
                        if(spltColon[0].contains("Name")) {
                            String name = spltColon[1].replaceAll("\\s+","");
                            if(!name.equals(value)) {
                                Toast.makeText(getActivity().getApplicationContext(),"Tank name doesn't match in the sms",Toast.LENGTH_LONG).show();
                                Log.d("name not match",name+" "+value);
                                return;
                            }
                        }
                        if(spltColon[0].contains("Fuel")) {
                            String strSQL = "UPDATE " + TABLE_NAME + " SET fuel = '"+ spltColon[1].replaceAll("\\s+","") +"' WHERE name = \"" + value + "\"";
                            db.execSQL(strSQL);
                            fuel.setText(spltColon[1]);
                        }
                        else if(spltColon[0].contains("Latitude")){
                            String strSQL = "UPDATE " + TABLE_NAME + " SET lat = '"+ spltColon[1].replaceAll("\\s+","") +"' WHERE name = \"" + value + "\"";
                            db.execSQL(strSQL);
                            latitude.setText(spltColon[1]);
                        }
                        else if(spltColon[0].contains("Longitude")){
                            String strSQL = "UPDATE " + TABLE_NAME + " SET long = '"+ spltColon[1].replaceAll("\\s+","") +"' WHERE name = \"" + value + "\"";
                            db.execSQL(strSQL);
                            longitude.setText(spltColon[1]);
                        }

                        else if(spltColon[0].contains("Lock")){
                            String strSQL = "UPDATE " + TABLE_NAME + " SET lock = '"+ spltColon[1].replaceAll("\\s+","") +"' WHERE name = \"" + value + "\"";
                            db.execSQL(strSQL);
                            lock.setText(spltColon[1]);
                        }

                        Log.d("Splited", spltColon[0]+":"+spltColon[1]);
                    }
                    catch (Exception e){
                        Log.d("split exception",e.getMessage());
                    }
                }
                updateFromDb();
            }
        });

        updateFromDb();

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

    public void updateFromDb() {
        try{
            Cursor cursorDb = db.rawQuery("SELECT lock,fuel,lat,long FROM " + TABLE_NAME + " WHERE name=\""+value+"\"", null);
            if(cursorDb.moveToFirst()) {
                lock.setText(cursorDb.getString(0));
                fuel.setText(cursorDb.getString(1));
                latitude.setText(cursorDb.getString(2));
                longitude.setText(cursorDb.getString(3));

                Log.d("db",cursorDb.getString(0)+" "+cursorDb.getString(1)+" "+cursorDb.getString(2)+" "+cursorDb.getString(3));

            }
        }
        catch (Exception e){
        Log.d("SQL get vehcstats",e.getMessage());
        }

        String lat = latitude.getText().toString();
        String longi = longitude.getText().toString();
        lat = lat.replaceAll("\\s+","");
        longi = longi.replaceAll("\\s+","");
        String link = "https://www.google.co.in/maps/search/petrol+station/@"+lat+","+longi+"z/data=!3m1!4b1?hl=en&authuser=0";

        pump.setText(link);
    }
}