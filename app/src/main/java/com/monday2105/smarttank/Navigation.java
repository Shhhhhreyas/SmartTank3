package com.monday2105.smarttank;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    SQLiteDatabase db;

    public static final String DATABASE_NAME = "SmartTank";
    public static final String TABLE_NAME = "TankNumber";
    private static int READ_SMS_PERMISSION_CODE = 1;
    private static int SEND_SMS_PERMISSION_CODE = 2;
    private static int RECEIVE_SMS_PERMISSION_CODE = 3;
    private static int RECEIVE_BOOT_PERMISSION_CODE = 4;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private static String SERVICE_NUMBER = "";
    private static String SERVICE_CONDITION = "-----SmartTank-----";

    String dbTankName;
    String dbTankNumber;
    boolean addCancel=false;
    int sno = 0;
    String nName="";
    //TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        db = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);

        if (!isReadSmsPermissionGranted()) requestReadSmsPermission();
        if (!isSendSmsPermissionGranted()) requestSendSmsPermission();
        if (!isReceiveSmsPermissionGranted()) requestReceiveSmsPermission();
        if (!isBootCompletePermissionGranted()) requestBootCompletePermission();

        Intent intent = getIntent();
        SERVICE_NUMBER = intent.getStringExtra("SERVICE_NUMBER");
        smsBroadcastReceiver = new SmsBroadcastReceiver(SERVICE_NUMBER, SERVICE_CONDITION, this);
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        /*smsBroadcastReceiver.setListener(new SmsBroadcastReceiver.Listener() {
            @Override
            public void onTextReceived(String text) {
                tv1.setText(text);
            }
        });*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {

        //if(view == tv1){
            //tv1.setText("ok");
            //SmsManager.getDefault().sendTextMessage(SERVICE_NUMBER, null, "-----SmartTank-----", null, null);
       // }
    }

    /**
     * Check SMS permission
     */
    public boolean isReadSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean isSendSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean isReceiveSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    public boolean isBootCompletePermissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request SMS permission
     */
    private void requestReadSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_CODE);
    }
    private void requestSendSmsPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_CODE);
    }
    private void requestReceiveSmsPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_CODE);
    }
    private void requestBootCompletePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, RECEIVE_BOOT_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case 1:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                    showRequestPermissionsInfoAlertDialog(true,requestCode);
                }
                break;
            case 2:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                    showRequestPermissionsInfoAlertDialog(true,requestCode);
                }
                break;
            case 3:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                    showRequestPermissionsInfoAlertDialog(true,requestCode);
                }
                break;
        }
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest, final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required");
        builder.setMessage("This app communicates with the SmartTank chip using SMS, hence" +
                "please provide permissions for the same.");

        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Display system runtime permission request?
                if (requestCode==1) {
                    requestReadSmsPermission();
                }
                if (requestCode==2) {
                    requestSendSmsPermission();
                }
                if (requestCode==3) {
                    requestReceiveSmsPermission();
                }
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                System.exit(1);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    public void createMenu(){
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final List<Vehicle> vehicleList = new ArrayList<>();

        try {
            Cursor cursorDb = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            if(cursorDb.moveToFirst()) {
                do {
                    Vehicle vehicle = new Vehicle(cursorDb.getString(1));
                    vehicleList.add(vehicle);
                } while(cursorDb.moveToNext());
            }
        }
        catch (Exception e){
            Log.d("SQL",e.getMessage());
        }

        final Menu menu = navigationView.getMenu();
        menu.removeItem(1);
        final SubMenu vehicleMenu = menu.addSubMenu(Menu.NONE,1,1,"My Vehicles");
        for(int i=0;i<vehicleList.size();i++) {
            vehicleMenu.add(vehicleList.get(i).getName()).setIcon(R.drawable.ic_directions_car_black_24dp)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            onNavigationItemSelected(item);
                            return true;
                        }
                    });
        }
        navigationView.setNavigationItemSelectedListener(this);
        Cursor cursorDb;
        try {
            cursorDb = db.rawQuery("SELECT MAX(sno) FROM " + TABLE_NAME, null);
            if (cursorDb.moveToFirst()) {
                do {
                    sno = cursorDb.getInt(0);
                } while (cursorDb.moveToNext());
            }
        }
        catch (Exception e){
            Log.d("SQL: max(sno): ",e.getMessage());
        }
        sno = sno+1;

    }

    @Override
    protected void onStart() {
        super.onStart();
        createMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_vehicle) {
            final String vName = getSupportActionBar().getTitle().toString();
            if(vName.equals("Navigation")){
                Toast.makeText(this,"Select a vehicle first",Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("The selected(currently active) vehicle and all it's data will be deleted" +
                        " from database. This CAN'T be undone");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String strSQL = "DELETE FROM " + TABLE_NAME + " WHERE name = \"" + vName +"\"";
                        try{
                            db.execSQL(strSQL);
                        }
                        catch (Exception e){
                            Log.d("SQL: ",e.getMessage());
                        }
                        unregisterReceiver(smsBroadcastReceiver);
                        finish();
                        startActivity(new Intent(Navigation.this,Navigation.class));
                        Toast.makeText(Navigation.this,"Vehicle "+vName+" deleted",Toast.LENGTH_SHORT);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setCancelable(false);
                builder.show();
            }
            return true;
        }

        if (id == R.id.rename_vehicle) {
            final String vName = getSupportActionBar().getTitle().toString();
            if(vName.equals("Navigation")){
                Toast.makeText(this,"Select a vehicle first",Toast.LENGTH_SHORT).show();
                return false;
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter new Name");
                final EditText tankName = new EditText(this);
                tankName.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(tankName);

                builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nName = tankName.getText().toString();
                        if(nName.isEmpty()){
                            Toast.makeText(Navigation.this,"Name Can not be empty. It will be set to tank"+Integer.toString(sno),Toast.LENGTH_LONG).show();
                            nName = "tank"+Integer.toString(sno);

                        }
                        String strSQL = "UPDATE " + TABLE_NAME + " SET name = '"+nName+"' WHERE name = \"" + vName + "\"";
                        try{
                            db.execSQL(strSQL);
                            createMenu();

                        }catch (Exception e){
                            Log.d("SQL Rename: ",e.getMessage());
                            Toast.makeText(Navigation.this,"Error in renaming. "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.add_vehicle) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Input Tank Number");
            final EditText tankNumber = new EditText(this);
            tankNumber.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(tankNumber);

            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbTankNumber = tankNumber.getText().toString();
                    if(!android.util.Patterns.PHONE.matcher(dbTankNumber).matches()) {
                        Toast.makeText(Navigation.this, "Please enter a valid contact number", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        return;
                    }
                    String temp = dbTankNumber.substring(0,3);
                    if(!temp.equals("+91")) dbTankNumber = "+91"+dbTankNumber;

                    String strSQL = "INSERT INTO "+TABLE_NAME+" VALUES('"+dbTankNumber+"','"+dbTankName+"',\"1\",\""+Integer.toString(sno)+"\")";
                    try{
                        db.execSQL(strSQL);
                        createMenu();
                    }
                    catch (Exception e){
                        Log.d("SQL CreationError: ",e.getMessage());
                        Toast.makeText(Navigation.this,"Error in adding vehicle: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Input Tank Name");
            final EditText tankName = new EditText(this);
            tankName.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(tankName);

            builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbTankName = tankName.getText().toString();
                    if(dbTankName.isEmpty()){
                        Toast.makeText(Navigation.this,"Name Can not be empty. It will be set to tank"+Integer.toString(sno),Toast.LENGTH_LONG).show();
                        dbTankName = "tank"+Integer.toString(sno);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addCancel = true;
                    dialog.cancel();
                }
            });

            builder.show();


            return true;
        }

        else {

            if (item != null) {
                String VEHICLE_NAME = item.getTitle().toString();
                Bundle bundle = new Bundle();
                bundle.putString("com.monday2105.smarttank", VEHICLE_NAME);

                fragment = new Activity();

                if (bundle != null) {
                    fragment.setArguments(bundle);
                }
            }

        }

        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.screen_area,fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
