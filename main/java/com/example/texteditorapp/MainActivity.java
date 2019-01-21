package com.example.texteditorapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int INTENT_REQUEST_OPEN_DOCUMENT = 0;
    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 0;
    private static final int INTENT_REQUEST_FILE_EXPLORER = 1;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout navDrawer;
    private TabLayout tabLayout;
    private ContentFragmentPagerAdapter adapter;
    private ViewPager viewPager;
    private boolean storageAvailable = false;
    private boolean storagePermission = false;
    private ArrayList<ContentFragment> fragmentList = new ArrayList<>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        navDrawer = findViewById(R.id.drawer_main);
        NavigationView navigationView = findViewById(R.id.nav_main);
        drawerToggle = new ActionBarDrawerToggle(this,
                navDrawer,
                toolbar,
                R.string.app_drawer_openCD,
                R.string.app_drawer_closeCD) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        navDrawer.addDrawerListener(drawerToggle);

        viewPager = findViewById(R.id.pager_main);
        adapter = new ContentFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        adapter.addTab();

//        EditText etContent = findViewById(R.id.editText_main_content);

        tabLayout = findViewById(R.id.tabLayout_main);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStoragePermission();
        storageAvailable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean returnResult = drawerToggle.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_toolbar_new: {
                adapter.addTab();
                viewPager.setCurrentItem(adapter.getCount() - 1);
                returnResult = true;
                break;
            }
            case R.id.action_toolbar_open: {
                // TODO: Use ACTION_GET_CONTENT for API < 19
                Intent filePickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                        .setType("text/*")
                        .addCategory(Intent.CATEGORY_OPENABLE);
                if (getPackageManager().resolveActivity(filePickIntent, 0) != null
                        && storageAvailable) {
                    startActivityForResult(filePickIntent, INTENT_REQUEST_OPEN_DOCUMENT);
                }
                else {
                    Toast.makeText(this, "Couldn't access storage.", Toast.LENGTH_SHORT).show();
                }
                returnResult = true;
                break;
            }
            case R.id.action_toolbar_save: {
                if (storageAvailable && storagePermission) {
                    adapter.saveCurrentFile(viewPager.getCurrentItem());
                }
                else {
                    Toast.makeText(this, "Couldn't access storage or permission is denied.", Toast.LENGTH_SHORT).show();
                }
                returnResult = true;
                break;
            }
            case R.id.action_toolbar_saveAs: {
                Intent fileExplorerIntent = new Intent(MainActivity.this, FileExplorerActivity.class);
                startActivityForResult(fileExplorerIntent, INTENT_REQUEST_FILE_EXPLORER);
                break;
            }
            case R.id.action_toolbar_close: {
                adapter.removeFragment(viewPager.getCurrentItem()); // Necessary for proper removal.
                adapter = new ContentFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
                viewPager.setAdapter(adapter);
                break;
            }
        }
        /* Recommended to pass this event to the toggle.*/
        return returnResult || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_OPEN_DOCUMENT) {
            if (resultCode == RESULT_OK) {
                adapter.addTab(data.getData());
                viewPager.setCurrentItem(adapter.getCount() - 1);
            }
        }
        else if (requestCode == INTENT_REQUEST_FILE_EXPLORER) {
            if (resultCode == RESULT_OK) {
                adapter.saveCurrentFileAs(viewPager.getCurrentItem(), data.getData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                storagePermission = true;
            }
            else {
                Toast.makeText(this, "Storage permission is required to save files.", Toast.LENGTH_SHORT).show();
                storagePermission = false;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = navDrawer.isDrawerOpen(GravityCompat.START);
        menu.setGroupVisible(R.id.group_toolbar_all, !drawerOpen);
        return true;
    }

    private void checkStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_STORAGE);
        }
        else {
            storagePermission = true;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();   // Sync indicator state if events haven't been sent to the toggle for a while.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig); // Recommended to pass these changes to the toggle
    }
}
