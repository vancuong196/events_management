package com.example.clay.event_manager.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.clay.event_manager.fragments.EventManagementFragment;
import com.example.clay.event_manager.fragments.UserManagementFragment;
import com.example.clay.event_manager.R;
import com.google.firebase.FirebaseApp;

public class RootActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        //Init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //open event management as default.
        openEventManagementFragment();

        setMenuItemChecked(R.id.nav_event);
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

    private void setMenuItemChecked(int menuItemID) {
        MenuItem menuItem = navigationView.getMenu().findItem(menuItemID);
        menuItem.setChecked(true);
    }

    public void ReplaceFragment(Fragment newFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.root_content, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openEventManagementFragment() {

        // Replace root view to default fragment
        toolbar.setTitle("Sự kiện");
        Fragment newFragment = new EventManagementFragment();
        ReplaceFragment(newFragment);
    }

    private void openUserManagementFragment() {
        // Replace root view to default fragment
        toolbar.setTitle("Nhân viên");
        Fragment newFragment = new UserManagementFragment();
        ReplaceFragment(newFragment);
    }

    private void openNotficationManagementFragment() {
        // Replace root view to default fragment
        toolbar.setTitle("Thông báo");
        Fragment newFragment = new EventManagementFragment();
        ReplaceFragment(newFragment);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_event) {
            openEventManagementFragment();
        } else if (id == R.id.nav_employee) {
            openUserManagementFragment();
        } else if (id == R.id.nav_notification) {
            openNotficationManagementFragment();
        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_changelog) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class EventDetailsActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_event);
        }
    }
}
