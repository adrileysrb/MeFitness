package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mefitness.R;
import com.example.mefitness.viewmodel.FragmentAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static java.lang.Thread.sleep;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{



    TabLayout tabLayout;
    ViewPager2 viewPager2;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treinos);

        //init();
        try{
            Bundle bundle = getIntent().getExtras();
            if(bundle.getInt("from")==2){
                SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name"+ FirebaseAuth.getInstance().getUid(), bundle.getString("name"));
                editor.putString("email"+ FirebaseAuth.getInstance().getUid(), bundle.getString("email"));
                editor.apply();
            }
        } catch (Exception e){
            e.printStackTrace();
        }



        SharedPreferences sharedPreferences = getSharedPreferences("AppSettingPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean isNightMode = sharedPreferences.getBoolean("NightMode", false);

        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        drawerLayout = findViewById(R.id.main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getString(R.string.app_name));
        NavigationView navigationView = findViewById(R.id.navigationView);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.userimage);
        RequestOptions options = new RequestOptions()

                .circleCrop();
        Glide.with(this).load(R.drawable.ic_baseline_run_circle_24).apply(options).into(imageView);

        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Treinos Personalizados"));
        tabLayout.addTab(tabLayout.newTab().setText("Ferramentas"));

        viewPager2 = findViewById(R.id.viewpager2);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        SharedPreferences sharedPreferences2 = getSharedPreferences("AppSettingPref", 0);

      userImage = navigationView.getHeaderView(0).findViewById(R.id.userimage);
      userImage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MyAccountActivity.class)));
        RequestOptions options2 = new RequestOptions()
                .error(R.drawable.ic_baseline_person_24)
                .circleCrop();
        Glide.with(this).load(sharedPreferences.getString("image"+FirebaseAuth.getInstance().getUid(), "1")).apply(options2).into(userImage);

        TextView nameUser = navigationView.getHeaderView(0).findViewById(R.id.name_user);
        nameUser.setText(sharedPreferences2.getString("name"+FirebaseAuth.getInstance().getUid(), "Usuário"));
        TextView emailUser = navigationView.getHeaderView(0).findViewById(R.id.email_user);
        emailUser.setText(sharedPreferences2.getString("email"+FirebaseAuth.getInstance().getUid(), "usuario@email.com"));
    }


    ImageView userImage;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.op1:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch (item.getItemId()){
           /* case R.id.optionNav1:{
                startActivity(new Intent(MainActivity.this, Configuration.class));
                break;
            }*/
            case R.id.optionNav2:{
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            }
            case R.id.optionNavMyAccount:{
                startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
                finish();
                break;
            }
            case R.id.optionNav3:{
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            }
        }
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }




}
