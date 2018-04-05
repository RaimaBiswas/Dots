package com.dots.games.pratick.dots.Intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pratick on 01-07-2015.
 */
public class IntroActivity extends AppIntro {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<IntroSlideFragment> introSlideFragments = getSlideFragmentsList();
        for(IntroSlideFragment introSlideFragment : introSlideFragments) {
            addSlide(introSlideFragment);
        }

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    private List<IntroSlideFragment> getSlideFragmentsList() {

        final IntroSlidesEnum[] introSlidesEnumArray = IntroSlidesEnum.values();
        final List<IntroSlideFragment> introSlideFragments = new ArrayList<>();
        for (IntroSlidesEnum introSlideEnum : introSlidesEnumArray) {
            introSlideFragments.add(introSlideEnum.introSlideFragment);
        }
        return introSlideFragments;
    }

    @Override
    public void onSkipPressed()
    {
        Intent myIntent = new Intent(this, DotsUI.class);
        this.startActivity(myIntent);
        finish();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Players.pauseLongMusic(Players.PLAYER_TYPE.background2);
    }
    boolean exit=false;
    //Object obj=new Object();
    @Override
    protected void onStart()
    {
        /*new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (obj)
                {
                    resolveOnFirstCall();
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        resolveOnFirstCall();
        if(!exit)
        {
            if(Players.backgroundMusic2 ==null)
            {
                Players.initializeIntro(this);
            }
            else
            {
                Players.resumeLongMusic(Players.PLAYER_TYPE.background2);
            }
        }
        super.onStart();

    }

    @Override
    public void onDonePressed()
    {
        Intent myIntent = new Intent(this, DotsUI.class);
        this.startActivity(myIntent);
        finish();

    }

    public void resolveOnFirstCall()
    {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true))
        {
            settings.edit().putBoolean("my_first_time", false).commit();
        }
        else
        {
            if(gameDetails.getGameType()==null)
            {
                exit=true;
                Intent myIntent = new Intent(this, DotsUI.class);
                this.startActivity(myIntent);
                finish();
            }
        }

        //obj.notify();
    }

    public enum Slide {


    }
    public static class SlideIntro extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
             View v= inflater.inflate(R.layout.intro_main, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSinglePlayer extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_single_player_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSinglePlayerStart extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_single_player_start_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideGame extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_game, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideGameEnd extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_game_end, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSinglePlayerAi extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_single_player_ai_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSinglePlayerSize extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_single_player_size_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSize extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_size, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideSinglePlayerStyle extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_single_player_style_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideStylePlayer extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_single_player_style_player_click, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }
    public static class SlideStylePlayerColor extends Fragment
    {
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
                {
                    View v= inflater.inflate(R.layout.intro_single_player_style_player_color_click, container, false);
                    ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                    return v;
                }
            }
    public static class SlideStylePlayerName extends Fragment
    {
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
                {
                    View v= inflater.inflate(R.layout.intro_single_player_style_player_name_click, container, false);
                    ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                    return v;
                }
            }
    public static class SlideMultiPlayer extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_multi_player_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideMultiPlayerPlayers extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_multi_player_players_click, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }
    public static class SlidePlayersHuman extends Fragment
    {
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
                {
                    View v= inflater.inflate(R.layout.intro_multi_player_players, container, false);
                    ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                    return v;
                }
            }
    public static class SlideMixedPlayer extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_mixed_player_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideMixedPlayerPlayers extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_mixed_player_players_click, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }
    public static class PlayersMixed extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_mixed_player_players, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideAchievements extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v= inflater.inflate(R.layout.intro_achievements_click, container, false);
            ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
            return v;
        }
    }
    public static class SlideAchievementsSignInOut extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_achievements_click_sign_in_out, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }
    public static class SlideAchievementsSignShowAchievement extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_achievements_click_show_achievements, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }
    public static class SlideAchievementsSignShowLeaderboard extends Fragment
    {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                View v= inflater.inflate(R.layout.intro_achievements_click_show_leaderboards, container, false);
                ((TextView)(v.findViewById(R.id.text))).setMovementMethod(new ScrollingMovementMethod());
                return v;
            }
        }



}