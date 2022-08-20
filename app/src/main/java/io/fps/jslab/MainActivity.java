package io.fps.jslab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;
import java.util.UUID;

import io.fps.jslab.ui.main.SectionsPagerAdapter;
import io.fps.jslab.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    protected SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        Log.d("tabs", String.format("reading tabs %d", pref.getInt("number_of_tabs", 1)));
        // sectionsPagerAdapter.setCount(pref.getInt("number_of_tabs", 1));
        Gson gson = new Gson();
        List<String> uuids = gson.fromJson(pref.getString("uuids", "[]"), new TypeToken<List<String>>() {}.getType());

        for (int index = 0; index < uuids.size(); ++index) {
            sectionsPagerAdapter.addItem(uuids.get(index));
        }

        ViewPager viewPager = binding.viewPager;
        viewPager.setOffscreenPageLimit(50);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sectionsPagerAdapter.setCount(sectionsPagerAdapter.getCount()+1);
                sectionsPagerAdapter.addItem(UUID.randomUUID().toString());
                sectionsPagerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("tabs", String.format("saving tabs %d", sectionsPagerAdapter.getCount()));
        SharedPreferences pref = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String uuids = gson.toJson(sectionsPagerAdapter.getUUIDs());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("uuids", uuids);
        edit.putInt("number_of_tabs", sectionsPagerAdapter.getCount());
        edit.apply();

        super.onPause();
    }
}