package com.example.t2m.moneytracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.t2m.moneytracker.dataaccess.IWalletsDAO;
import com.example.t2m.moneytracker.setting.Setting;

import com.example.t2m.moneytracker.dataaccess.WalletsDAOImpl;

import com.example.t2m.moneytracker.transaction.TransactionTabFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtEmail,txtUsername;
    FirebaseAuth mAuth;
    public static final int REQUEST_SETTING_CODE = 1;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_transaction));

        IWalletsDAO iWalletsDAO = new WalletsDAOImpl(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        iWalletsDAO.getAllWalletByUser(user.getUid());

        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txtEmail);
        txtUsername = navigationView.getHeaderView(0).findViewById(R.id.txtUsername);

        if (user != null){
            if (txtUsername != null && user.getDisplayName() != null){
                txtUsername.setText(user.getDisplayName());
            }
            if(txtEmail != null) txtEmail.setText(user.getEmail());
        }


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
        // Inflate the menu_save; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivityForResult(intent,REQUEST_SETTING_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //int checkedId = navigationView.getCheckedItem().getItemId();
        //if(checkedId == id) ;
        Fragment fragment;
        Class fragmentClass = null;
        if (id == R.id.nav_transaction) {
            Log.d(MainActivity.class.getSimpleName(),"Start Transaction Activity");
            fragmentClass = TransactionTabFragment.class;

        } else if (id == R.id.nav_chart) {

        } else if (id == R.id.nav_plan) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivity(intent);
            finish();
            return true;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        try {
            if(fragmentClass != null) {
                fragment = (Fragment) fragmentClass.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,fragment).addToBackStack(null).commit();
                // Highlight the selected item has been done by NavigationView
                item.setChecked(true);
                // Set action bar title
                setTitle(item.getTitle());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static void recreateAppMain(AppCompatActivity appCompatActivity){
        appCompatActivity.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_CODE && resultCode == RESULT_OK){
            MainActivity.recreateAppMain(this);
        }
    }
}
