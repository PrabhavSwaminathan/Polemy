package com.e.polemyiteration1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SymptomsPage extends AppCompatActivity {
    ExpandableListView expandableListView;
    BottomNavigationView bottomNavigationView;
    private int lastIndex;
    List<Fragment> Fragments;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_symptoms);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        initialFragment2();
        initialBottomNavigation2();
        expandableListView = findViewById(R.id.expand_menu);
        expandableListView.setAdapter(new expandListAdapter());
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int count = new expandListAdapter().getGroupCount();
                for(int i = 0; i < count; i++){
                    if (i!=groupPosition){
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
    }
    private void initialFragment2(){
        Fragments = new ArrayList<>();
        Fragments.add(new PreventiveMeasure());
    }
    private void initialBottomNavigation2(){
        bottomNavigationView = findViewById(R.id.navigation2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.symptoms:
                        Intent intent2 = new Intent(SymptomsPage.this, SymptomsPage.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.hidden,R.anim.show);
                        finish();
                        break;
                    case R.id.preventive:
                        setFragmentPosition(0);
                        break;
                }
                return true;
            }
        });
    }
    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = Fragments.get(position);
        Fragment lastFragment = Fragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.DisplayScreen2, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }
}