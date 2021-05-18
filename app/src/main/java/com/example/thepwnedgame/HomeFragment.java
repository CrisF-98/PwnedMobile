package com.example.thepwnedgame;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    int tutorialPage = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Utilities.insertFragment((AppCompatActivity) getActivity(), new TitleFragment(), "Title Fragment", R.id.home_frag_content);
        FloatingActionButton tutorialfab = view.findViewById(R.id.fab_tutorial);

        tutorialfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (inflater != null) {
                    View popupView = inflater.inflate(R.layout.tutorial_layout_one, null);
                /*VideoView tutorial = popupView.findViewById(R.id.tutorialVideoView);
                String videoPath = "android.resource.//" + getActivity().getPackageName() + "/" + R.raw.tutorial;
                Uri uri = Uri.parse(videoPath);*/
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true;
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupWindow.setAnimationStyle(R.style.popup_animation);
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                    /*tutorial.setVideoURI(uri);
                    MediaController controller = new MediaController(getContext());
                    tutorial.setMediaController(controller);
                    controller.setAnchorView(tutorial);*/
                        //dismiss when touched
                    /*popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });*/
                    popupView.findViewById(R.id.nextTutorial).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            View popupViewTwo = inflater.inflate(R.layout.tutorial_layout_two, null);
                            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                            boolean focusable = true;
                            final PopupWindow popupWindowTwo = new PopupWindow(popupViewTwo, width, height, focusable);
                            popupWindowTwo.setAnimationStyle(R.style.popup_animation);
                            popupWindowTwo.showAtLocation(view, Gravity.CENTER, 0, 0);
                            popupViewTwo.findViewById(R.id.tutorial_button).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindowTwo.dismiss();
                                }
                            });
                        }
                    });
                    popupView.findViewById(R.id.tutorial_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });
                }
            }
        });
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
