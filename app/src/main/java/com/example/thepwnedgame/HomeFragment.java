package com.example.thepwnedgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.insertFragment((AppCompatActivity) getActivity(), new TitleFragment(), "Title Fragment", R.id.home_frag_content);
        //TODO: handle TABS
       TabLayout tabLayout = view.findViewById(R.id.home_tab_layout);
       tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:{
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new TitleFragment(), "title fragment", R.id.home_frag_content);
                        break;
                    }
                    case 1:{
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new LeaderBoardsFragment(), "leaderboard fragment", R.id.home_frag_content);
                        break;
                    }
                    case 2:{
                        Utilities.insertFragment((AppCompatActivity) getActivity(), new StatisticFragment(), "statistic fragment", R.id.home_frag_content);
                        break;
                    }
                }
           }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }

           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });

    }
}
