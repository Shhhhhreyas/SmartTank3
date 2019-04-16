package com.monday2105.smarttank;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.sqlite.*;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //defining views
    private Button goToTank;
    private EditText tankNumber ;
    private EditText tankName;

    SQLiteDatabase db;

    public static final String DATABASE_NAME = "SmartTank";
    public static final String TABLE_NAME = "TankNumber";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        String SERVICE_NUMBER = "";

        try {
            Cursor cursorDb = db.rawQuery("SELECT number FROM " + TABLE_NAME, null);
            if(cursorDb.moveToFirst()) {
                //Toast.makeText(this,"Inside",Toast.LENGTH_LONG).show();
                    SERVICE_NUMBER = cursorDb.getString(0);
            }
        }
        catch (Exception e){
            Log.d("SQL get number",e.getMessage());
        }

        //Toast.makeText(this,SERVICE_NUMBER,Toast.LENGTH_LONG).show();

        if(!SERVICE_NUMBER.isEmpty()){
            finish();
            startActivity(new Intent(this, Navigation.class).putExtra("SERVICE_NUMBER",SERVICE_NUMBER) );
        }

        //initializing views
        tankNumber = (EditText) findViewById(R.id.input_tankContact);
        tankName = (EditText) findViewById(R.id.input_tankName);
        goToTank = (Button) findViewById(R.id.btn_login);


        //attaching click listener
        goToTank.setOnClickListener(this);

    }

    //method for user login
    private void userLogin() {
        String SERVICE_NUMBER = tankNumber.getText().toString().trim();
        String tank_Name = tankName.getText().toString().trim();


        if (TextUtils.isEmpty(SERVICE_NUMBER)) {
            Toast.makeText(this, "Please enter tank's contact number", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(tank_Name)) {
            Toast.makeText(this, "Please enter tank's Name", Toast.LENGTH_LONG).show();
            return;
        }

        if(!android.util.Patterns.PHONE.matcher(SERVICE_NUMBER).matches()) {
            Toast.makeText(this, "Please enter a valid contact number", Toast.LENGTH_LONG).show();
            return;
        }

        else {
            String temp = SERVICE_NUMBER.substring(0,3);
            if(!temp.equals("+91")) SERVICE_NUMBER = "+91"+SERVICE_NUMBER;
            try {
                try{
                    db.rawQuery("DROP TABLE "+TABLE_NAME, null);
                }
                catch (Exception e){
                    Log.d("SQL drop table",e.getMessage());
                }
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(\n" +
                        "number varchar(13) NOT NULL," +
                        "name varchar(25) NOT NULL," +
                        "lock INTEGER NOT NULL," +
                        "fuel varchar NOT NULL," +
                        "lat varchar NOT NULL," +
                        "long varchar NOT NULL," +
                        "sno INTEGER PRIMARY KEY);");


                String insertSQL = "INSERT INTO " + TABLE_NAME + "\n" +
                        "(number, name, lock, fuel, lat, long,sno)\n" +
                        "VALUES \n" +
                        "(?,?,?,?,?,?,?);";
                db.execSQL(insertSQL, new String[]{SERVICE_NUMBER,tank_Name,"1","0","0","0","1"});
            }
            catch (Exception e){
                Log.d("SQL make table",e.getMessage());
            }


            finish();
            startActivity(new Intent(this, Navigation.class).putExtra("SERVICE_NUMBER", SERVICE_NUMBER));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == goToTank){
            userLogin();
        }
    }
}
