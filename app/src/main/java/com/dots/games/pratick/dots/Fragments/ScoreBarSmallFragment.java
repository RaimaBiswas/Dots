package com.dots.games.pratick.dots.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.games.pratick.dots.Designers.Misc;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Player;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pratick on 03-06-2015.
 *
 */
public class ScoreBarSmallFragment extends Fragment
{
    private View rootLayout;
    public FrameLayout.LayoutParams rootLayoutParams;
    ScoreBarSmallFragmentInterface mCallback;
    private Animation anim_in;

    private ImageView activePlayerColor;
    private TextView  activePlayerName;
    private TextView  activePlayerScore;

    AtomicInteger previousPlayer=new AtomicInteger(-1);

    public interface ScoreBarSmallFragmentInterface
    {
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (ScoreBarSmallFragmentInterface) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnViewScreenSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootLayout=inflater.inflate(R.layout.score_bar_small_ui, container, false);
        rootLayout.setLayoutParams(rootLayoutParams);

        rootLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setTouch();
            }
        });

        Typeface ItemTextTypeface = Typeface.createFromAsset(getActivity().getAssets(), "font/RobotoCondensed-BoldItalic.ttf");

        anim_in  = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        anim_in.setDuration(250);

        activePlayerColor = (ImageView)rootLayout.findViewById(R.id.active_player_image);
        activePlayerName  = (TextView)rootLayout.findViewById(R.id.active_player_name);
        activePlayerScore  = (TextView)rootLayout.findViewById(R.id.active_player_score);

        activePlayerName.setTypeface(ItemTextTypeface);
        activePlayerScore.setTypeface(ItemTextTypeface);

        activePlayerColor.getLayoutParams().height=(rootLayoutParams.height/2);
        activePlayerColor.setPadding(rootLayoutParams.height / 6, 0, rootLayoutParams.height / 6, 0);
        activePlayerColor.setPadding(10, 10, 10, 10);
        activePlayerName.setTextSize(rootLayoutParams.height / 5);
        activePlayerScore.setTextSize(rootLayoutParams.height / 5);
        changeCurrentPlayerFragment(new AtomicInteger(0));

        return rootLayout;
    }

    SuperActivityToast superActivityToast;
    public void changeCurrentPlayerFragment(final AtomicInteger currentPlayer)
    {
        Log.d("Previous",previousPlayer.get()+"");
        Log.d("Current",currentPlayer.get()+"");

        if(currentPlayer.get()==previousPlayer.get())
        {
            anim_in  = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
            if(superActivityToast!=null && superActivityToast.isShowing())
            {
                superActivityToast.dismiss();
                setTouch();
            }
            activePlayerScore.startAnimation(anim_in);
            activePlayerScore.setText(gameDetails.getPlayerScore(currentPlayer.get()) + "");
        }
        else
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    anim_in  = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
                    final ColorDrawable barColor=new ColorDrawable(gameDetails.getPlayerBarColor(currentPlayer.get()));
                    final ColorDrawable boxColor=new ColorDrawable(gameDetails.getPlayerBoxColor(currentPlayer.get()));
                    final String playerName = gameDetails.getPlayerName(currentPlayer.get());
                    final String playerScore= gameDetails.getPlayerScore(currentPlayer.get())+"";

                    previousPlayer.set(currentPlayer.get());

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            activePlayerColor.setImageDrawable(boxColor);

                            activePlayerColor.setBackgroundDrawable(barColor);
                            activePlayerName.setText (playerName);
                            activePlayerScore.setText(playerScore);

                            activePlayerColor.startAnimation(anim_in);
                            activePlayerName.startAnimation(anim_in);
                            activePlayerScore.startAnimation(anim_in);
                        }
                    });
                }

            }).start();

            Log.d(" New Previous",previousPlayer.get()+"");
        }
    }


    public void setTouch()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                superActivityToast= Toasts.appCustomToast(getActivity(), Misc.getAllPlayersNameAndScore(), Toasts.ToastDurations.showScoreDuration, Toasts.ToastStyles.greenFlying, gameDetails.getMonoTypeface());
                superActivityToast.setTouchToDismiss(true);

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        superActivityToast.show();
                        Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                    }
                });

            }
        }).start();

    }

}
