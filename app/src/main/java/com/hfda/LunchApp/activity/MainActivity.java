package com.hfda.LunchApp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.hfda.LunchApp.fragment.CoffeeFragment;
import com.hfda.LunchApp.fragment.HomeFragment;
import com.hfda.LunchApp.fragment.MenuFragment;
import com.hfda.LunchApp.fragment.SettingsFragment;
import com.hfda.LunchApp.fragment.SpecialFragment;
import com.hfda.LunchApp.helper.LunchDBhelper;
import com.hfda.LunchApp.helper.SessionManager;

import android.view.MenuItem;

import com.hfda.LunchApp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //Må være i første acitivity. Camera
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    ListView lv;

    private ActionBarDrawerToggle abDrawerToggle;
    private DrawerLayout drawerLayout;

    private SessionManager session;
    private LunchDBhelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lv = (ListView) findViewById(R.id.drawerleft);
        lv.setOnItemClickListener(this);


        // find the drawer layout from view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Enable the drawer indicator:
        setupDrawer();

        //icon in the Action Bar:
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout.addDrawerListener(abDrawerToggle);
        checkRequestPermission();

        //SQLite
        session = new SessionManager(getApplicationContext());

        //Creates database handler
        db = new LunchDBhelper(getApplicationContext());

        Fragment fragment;//Oppretter fragment
        fragment = new HomeFragment();//Legger til objekt av HomeFragment
        fragmentTransaction(0, fragment);//Sender plassering og variabel til fragmentTransaction()
    }


    //Helper method:
    private void setupDrawer() {
        //Drawer Toggle:
        abDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
            @Override//Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override//Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
                super.onDrawerClosed(view);
            }
        };

        abDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(abDrawerToggle);//FROM set... TO add...
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        return abDrawerToggle.onOptionsItemSelected(item) || abDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    //Case for alle mulighter man kan trykke på i navigation drawer:
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new MenuFragment();
                fragmentTransaction(position, fragment);
                break;
            case 2:
                fragment = new SpecialFragment();
                fragmentTransaction(position, fragment);
                break;
            case 3:
                fragment = new CoffeeFragment();
                fragmentTransaction(position, fragment);
                break;
            case 4:
                fragment = new SettingsFragment();
                fragmentTransaction(position, fragment);
                break;
            case 5:
                logoutUser();
                break;
            default:
                fragment = new HomeFragment();
                fragmentTransaction(position, fragment);
        }
    }

    //Endrer tittel og fragment i forhold til knappetrykk samt henter variabler fra onItemClick():
    public void fragmentTransaction(int position, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_content, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        selectItem(position);
    }

    //Lukker naviagation drawer når noe har blitt valgt:
    public void selectItem(int position) {
        drawerLayout.closeDrawer(Gravity.START);
    }

    //sjekker kameratillatelser, HVIS kameraet ikke er tillat vil den spørre om det:
    private boolean checkRequestPermission() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }



    @Override
    public void onBackPressed() {
        FrameLayout fl = (FrameLayout) findViewById(R.id.main_content);
        if (fl.getChildCount() == 1) {
            super.onBackPressed();
            if (fl.getChildCount() == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Close App?")
                        .setMessage("Do you really want to close this app?")
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Fragment fragment;
                                        fragment = new HomeFragment();
                                        fragmentTransaction(0, fragment);
                                    }
                                }).show();
            }
        } else {
            super.onBackPressed();
        }
    }




    //Logg Out:
    public void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}