package com.dots.games.pratick.dots.Designers;

import android.app.Activity;
import android.media.MediaPlayer;

import com.dots.games.pratick.dots.R;

/**
 * Created by Pratick on 08-06-2015.
 *
 */
public class Players
{

    public enum PLAYER_TYPE
    {

        nonGameSuccessfulTouch,nonGameUnsuccessfulTouch, playerBarFill, playerBoxFill,preview,gameInvalidClick,
        gameExit,appExit,

        background1,background2,victory,defeat
    }

    public static MediaPlayer backgroundMusic1;
    public static MediaPlayer backgroundMusic2;
    public static MediaPlayer[] victoryMusic=new MediaPlayer[2];
    public static MediaPlayer[] defeatMusic=new MediaPlayer[2];

    public static Activity Activity;

    public static void initializeDots(Activity activity)
    {
        Activity=activity;
        playLongMusic(PLAYER_TYPE.background1);
    }
    public static void initializeIntro(Activity activity)
    {
        Activity=activity;
        playLongMusic(PLAYER_TYPE.background2);

    }

    public static void playLongMusic(PLAYER_TYPE player_type)
    {
        switch (player_type)
        {
            case background1:
            {
                backgroundMusic1 = MediaPlayer.create(Activity, R.raw.background_normal);
                backgroundMusic1.setLooping(true);
                backgroundMusic1.start();
                break;
            }
            case background2:
            {
                backgroundMusic2 = MediaPlayer.create(Activity, R.raw.background_normal);
                backgroundMusic2.setLooping(true);
                backgroundMusic2.start();
                break;
            }
            case victory:
            {
                victoryMusic[0] = MediaPlayer.create(Activity, R.raw.background_victory);
                victoryMusic[0].setLooping(true);
                victoryMusic[0].start();

                victoryMusic[1] = MediaPlayer.create(Activity, R.raw.applause);
                victoryMusic[1].start();
                victoryMusic[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        if (victoryMusic[1] != null)
                        {
                            victoryMusic[1].release();
                        }
                        victoryMusic[1] = null;
                    }
                });

                break;
            }
            case defeat:
            {
                defeatMusic[0] = MediaPlayer.create(Activity, R.raw.background_defeat);
                defeatMusic[0].setLooping(true);
                defeatMusic[0].start();

                defeatMusic[1] = MediaPlayer.create(Activity, R.raw.boo);
                defeatMusic[1].start();
                defeatMusic[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        if (defeatMusic[1] != null)
                        {
                            defeatMusic[1].release();
                        }
                        defeatMusic[1] = null;
                    }
                });

                break;
            }
        }
    }
    public static void resumeLongMusic(PLAYER_TYPE player_type)
    {
        switch (player_type)
        {
            case background1:
            {
                if (backgroundMusic1 !=null)
                {
                    backgroundMusic1.start();
                }

                break;
            }
            case background2:
            {
                if (backgroundMusic2 !=null)
                {
                    backgroundMusic2.start();
                }

                break;
            }
            case victory:
            {
                if (victoryMusic[0]!=null)
                {
                    victoryMusic[0].start();
                }
                if (victoryMusic[1]!=null)
                {
                    victoryMusic[1].start();
                }
                break;
            }
            case defeat:
            {
                if (defeatMusic[0]!=null)
                {
                    defeatMusic[0].start();
                }
                if (defeatMusic[1]!=null)
                {
                    defeatMusic[1].start();
                }
                break;
            }
        }
    }
    public static void pauseLongMusic(PLAYER_TYPE player_type)
    {
        switch (player_type)
        {
            case background1:
            {
                if(backgroundMusic1 !=null )
                {
                    backgroundMusic1.pause();
                }
                break;
            }
            case background2:
            {
                if(backgroundMusic2 !=null )
                {
                    backgroundMusic2.pause();
                }
                break;
            }
            case victory:
            {
                if(victoryMusic[0]!=null )
                {
                    victoryMusic[0].pause();
                }
                if(victoryMusic[1]!=null )
                {
                    victoryMusic[1].pause();
                }
                break;
            }
            case defeat:
            {
                if(defeatMusic[0]!=null )
                {
                    defeatMusic[0].pause();
                }
                if(defeatMusic[1]!=null )
                {
                    defeatMusic[1].pause();
                }
                break;
            }
        }
    }
    public static void stopLongMusic(PLAYER_TYPE player_type)
    {
        switch (player_type)
        {
            case background1:
            {
                backgroundMusic1.reset();
                backgroundMusic1.release();
                backgroundMusic1 =null;
                break;
            }
            case background2:
            {
                backgroundMusic2.reset();
                backgroundMusic2.release();
                backgroundMusic2 =null;
                break;
            }
            case victory:
            {
                if(victoryMusic[0]!=null)
                {
                    victoryMusic[0].release();
                    victoryMusic[0]=null;
                }
                if(victoryMusic[1]!=null)
                {
                    victoryMusic[1].release();
                    victoryMusic[1]=null;
                }
                break;
            }
            case defeat:
            {
                if(defeatMusic[0]!=null)
                {
                    defeatMusic[0].release();
                    defeatMusic[0]=null;
                }
                if(defeatMusic[1]!=null)
                {
                    defeatMusic[1].release();
                    defeatMusic[1]=null;
                }
                break;
            }
        }
    }

    public static void playShortSound(PLAYER_TYPE player_type)
    {
        MediaPlayer shortAudioPlayer = null;
        switch (player_type)
        {
            case nonGameSuccessfulTouch:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.non_game_successful_touch);
                break;
            }
            case nonGameUnsuccessfulTouch:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.non_game_unsuccessful_touch);
                break;
            }
            case playerBarFill:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.player_bar_fill);
                break;
            }
            case playerBoxFill:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.player_box_fill);
                break;
            }
            case preview:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.preview);
                break;
            }
            case gameInvalidClick:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.game_invalid_click);
                break;
            }
            case gameExit:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.game_exit);
                break;
            }
            case appExit:
            {
                shortAudioPlayer = MediaPlayer.create(Activity, R.raw.app_exit);
                break;
            }


        }
        shortAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mp.start();
            }
        });
        shortAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer mp)
            {
                mp.release();
            }
        });
    }
}
