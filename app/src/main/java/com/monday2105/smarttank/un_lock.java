package com.monday2105.smarttank;

import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.content.Context.MODE_PRIVATE;

public class un_lock extends Fragment implements View.OnClickListener{

    boolean locked = false;
    ImageView imageView;

    SQLiteDatabase db;
    public static final String DATABASE_NAME = "SmartTank";
    public static final String TABLE_NAME = "TankNumber";
    public static String SERVICE_NUMBER = "";
    String value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.un_lock, container, false);

        imageView = (ImageView)  view.findViewById(R.id.lock_unlock);
        imageView.setOnClickListener(this);

        value = ((Navigation) getActivity()).getSupportActionBar().getTitle().toString();

        db = getActivity().openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        String lockedString = "";

        try {
            Cursor cursorDb = db.rawQuery("SELECT lock FROM " + TABLE_NAME + " WHERE name=\""+value+"\"", null);
            if(cursorDb.moveToFirst()) {
                    lockedString = cursorDb.getString(0);
            }
            cursorDb = db.rawQuery("SELECT number FROM " + TABLE_NAME + " WHERE name=\""+value+"\"", null);
            if(cursorDb.moveToFirst()) {
                SERVICE_NUMBER = cursorDb.getString(0);

            }
        }
        catch (Exception e){
            Log.d("SQL get lock",e.getMessage());
        }

        if(lockedString.equals("1")) {
            imageView.setImageResource(R.drawable.locked);
            locked = true;
        }
        else imageView.setImageResource(R.drawable.unlocked);


        return  view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lock_unlock:
                if (locked) {
                    imageView.setImageResource(R.drawable.unlocked);
                    locked = false;
                    String strSQL = "UPDATE " + TABLE_NAME + " SET lock = '0' WHERE name = \"" + value + "\"";
                    db.execSQL(strSQL);
                    SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----\nOPEN", null, null);
                } else {
                    imageView.setImageResource(R.drawable.locked);
                    locked = true;
                    String strSQL = "UPDATE " + TABLE_NAME + " SET lock = '1' WHERE name = \"" + value + "\"";
                    db.execSQL(strSQL);
                    SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----\nCLOSE", null, null);
                }

                break;
        }
    }
}