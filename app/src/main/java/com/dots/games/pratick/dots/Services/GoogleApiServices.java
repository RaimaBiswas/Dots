package com.dots.games.pratick.dots.Services;

import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dots.games.pratick.dots.Designers.*;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.ACHIEVEMENT_TYPE;
import com.dots.games.pratick.dots.Enums.GAME_TYPE;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.R;

import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.*;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;

/**
 * Created by Pratick on 23-06-2015.
 */
public class GoogleApiServices
{
    DotsUI activity;
    public boolean onConnectedAction=false;
    public GoogleApiServices()
    {
        activity=null;
    }

    public GoogleApiServices(DotsUI activity)
    {
        this.activity=activity;
    }

    public void signInOut()
    {
        if(activity.getGoogleApiClient().isConnected())
        {
            activity.callSignOut();

            activity.signInOutText.setText(R.string.achievement_sign_in_taunt);
            new MaterialDialog.Builder(activity)
                    .title("Signed Out!")
                    .content("You have been signed out from Google Play")
                    .positiveText("OK")
                    .show();
        }
        else
        {
            activity.callBeginUserInitiatedSignIn();
        }
    }
    public void showAchievement()
    {
        if((activity.getGoogleApiClient().isConnected()))
        {
            activity.startActivityForResult(Games.Achievements.getAchievementsIntent(activity.getGoogleApiClient()), 1);
        }
        else
        {
            Toasts.appPresetToast(activity, Toasts.ToastMessages.networkConnectionError);
        }
    }

    PLAYER_TYPE leaderboardType=null;
    public void showLeaderboardDialog()
    {
        final Dialog chooseLeaderboard=new Dialog(activity);
        chooseLeaderboard.setContentView(R.layout.choose_leaderboard_dialog_ui);

        TextView title =(TextView)chooseLeaderboard.findViewById(R.id.title);

        title.setText("Choose Leaderboard : ");
        title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

        final RadioButton radioButton1 =(RadioButton)chooseLeaderboard.findViewById(R.id.radioButton1);
        final RadioButton radioButton2 =(RadioButton)chooseLeaderboard.findViewById(R.id.radioButton2);
        final RadioButton radioButton3 =(RadioButton)chooseLeaderboard.findViewById(R.id.radioButton3);
        final RadioButton radioButton4 =(RadioButton)chooseLeaderboard.findViewById(R.id.radioButton4);

        MDButton negative=(MDButton)chooseLeaderboard.findViewById(R.id.buttonDefaultNegative);
        MDButton positive=(MDButton)chooseLeaderboard.findViewById(R.id.buttonDefaultPositive);

        positive.setText("OK");
        positive.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
        negative.setText("CANCEL");
        negative.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));


        positive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(radioButton1.isChecked())
                {
                    leaderboardType= PLAYER_TYPE.EASY;
                }
                if(radioButton2.isChecked())
                {
                    leaderboardType= PLAYER_TYPE.MEDIUM;
                }
                if(radioButton3.isChecked())
                {
                    leaderboardType= PLAYER_TYPE.HARD;
                }
                if(radioButton4.isChecked())
                {
                    leaderboardType= PLAYER_TYPE.TITAN;
                }
                com.dots.games.pratick.dots.Designers.Players.playShortSound(com.dots.games.pratick.dots.Designers.Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                showLeaderBoard();
                chooseLeaderboard.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                leaderboardType=null;
                com.dots.games.pratick.dots.Designers.Players.playShortSound(com.dots.games.pratick.dots.Designers.Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                chooseLeaderboard.dismiss();
            }
        });
        chooseLeaderboard.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        chooseLeaderboard.show();
    }

    public void showLeaderBoard()
    {
        if(leaderboardType==null)
        {
            return;
        }

        if((activity.getGoogleApiClient().isConnected()))
        {
            switch (leaderboardType)
            {
                case EASY:
                {
                    activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(activity.getGoogleApiClient(), activity.getString(R.string.leaderboard_the_kyth_easy_ai)), 2);
                    break;
                }
                case MEDIUM:
                {
                    activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(activity.getGoogleApiClient(), activity.getString(R.string.leaderboard_enigma_medium_ai)), 2);
                    break;
                }
                case HARD:
                {
                    activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(activity.getGoogleApiClient(), activity.getString(R.string.leaderboard_lisa_hard_ai)), 2);
                    break;
                }
                case TITAN:
                {
                    activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(activity.getGoogleApiClient(), activity.getString(R.string.leaderboard_l_titan_ai)), 2);
                    break;
                }
            }

        }
        else
        {
            Toasts.appPresetToast(activity, Toasts.ToastMessages.networkConnectionError);
        }
        leaderboardType=null;

    }

    public void unlockAchievement(int id,int increment)
    {
        if(increment==0)
        {
            Games.Achievements.unlock(activity.getGoogleApiClient(),activity.getResources().getString(id));
        }
        else
        {
            Games.Achievements.increment(activity.getGoogleApiClient(), activity.getString(id), increment);
        }


    }
    public void setAchievement(ACHIEVEMENT_TYPE achievementType)
    {

        if(achievementType==ACHIEVEMENT_TYPE.FINISH)
        {
            if(gameDetails.getGameType()== GAME_TYPE.SINGLE_PLAYER)
            {
                if
                (
                    (gameDetails.getDotRowCount()==2 && gameDetails.getDotColCount()==3)||
                    (gameDetails.getDotRowCount()==3 && gameDetails.getDotColCount()==2)
                )
                {
                    unlockAchievement(R.string.achievement_guerrilla,0);
                }
            }
        }

        if(gameDetails.getDotColCount()<5 && gameDetails.getDotColCount()<5)
        {
            return;
        }

        if(activity.getGoogleApiClient().isConnected())
        {
            switch (achievementType)
            {
                case START:
                {
                    switch (gameDetails.getGameType())
                    {
                        case SINGLE_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_initiation,0);
                            break;
                        }
                        case MULTI_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_the_draft,0);
                            break;
                        }
                        case MIXED:
                        {
                            unlockAchievement(R.string.achievement_the_smoking_gun,0);
                            break;
                        }
                    }
                    break;
                }
                case FINISH:
                {
                    switch (gameDetails.getGameType())
                    {
                        case SINGLE_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_dust_to_dust,0);

                            if(gameDetails.getDotRowCount()>=10 && gameDetails.getDotColCount()>=10)
                            {
                                unlockAchievement(R.string.achievement_the_grand_bargain,0);
                            }

                            break;
                        }
                        case MULTI_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_baptism_of_fire,0);
                            break;
                        }
                        case MIXED:
                        {
                            unlockAchievement(R.string.achievement_d__day,0);
                            break;
                        }
                    }
                    break;
                }
                case WIN:
                {
                    switch (gameDetails.getGameType())
                    {
                        case SINGLE_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_judgement_day,0);
                            int i=0;
                            if(gameDetails.getPlayerType(1)!= PLAYER_TYPE.HUMAN)
                            {
                                i=1;
                            }
                            switch (gameDetails.getPlayerType(i))
                            {
                                case EASY:
                                {
                                    unlockAchievement(R.string.achievement_noob_no_more,0);
                                    break;
                                }
                                case MEDIUM:
                                {
                                    unlockAchievement(R.string.achievement_the_imitation_game,0);
                                    break;
                                }
                                case HARD:
                                {
                                    unlockAchievement(R.string.achievement_apples_and_oranges,0);
                                    break;
                                }
                                case TITAN:
                                {
                                    unlockAchievement(R.string.achievement_lollipoped,0);

                                    unlockAchievement(R.string.achievement_limitanei,1);
                                    unlockAchievement(R.string.achievement_hastati,1);
                                    unlockAchievement(R.string.achievement_triarii,1);
                                    unlockAchievement(R.string.achievement_the_x_legion,1);
                                    unlockAchievement(R.string.achievement_the_pretorian_cohorts,1);
                                    break;
                                }
                            }

                            final String firstAchievement[]=
                            {
                                activity.getResources().getString(R.string.achievement_noob_no_more),
                                activity.getResources().getString(R.string.achievement_the_imitation_game),
                                activity.getResources().getString(R.string.achievement_apples_and_oranges),
                                activity.getResources().getString(R.string.achievement_lollipoped)
                            };

                            if(gameDetails.getDotRowCount()>=10 && gameDetails.getDotColCount()>=10)
                            {
                                unlockAchievement(R.string.achievement_champion,0);
                            }

                            unlockAchievement(R.string.achievement_deputy,1);
                            unlockAchievement(R.string.achievement_questor,1);
                            unlockAchievement(R.string.achievement_magistrate,1);
                            unlockAchievement(R.string.achievement_decurion,1);
                            unlockAchievement(R.string.achievement_centurion,1);
                            unlockAchievement(R.string.achievement_caesar,1);

                            break;
                        }
                        case MULTI_PLAYER:
                        {
                            unlockAchievement(R.string.achievement_red_badge_of_fire,0);
                            unlockAchievement(R.string.achievement_principality,1);
                            unlockAchievement(R.string.achievement_kingdom,1);
                            unlockAchievement(R.string.achievement_empire,1);
                            unlockAchievement(R.string.achievement_industrial_power,1);
                            unlockAchievement(R.string.achievement_superpower,1);

                            break;
                        }
                        case MIXED:
                        {
                            unlockAchievement(R.string.achievement_finest_hour,0);
                            unlockAchievement(R.string.achievement_the_conqueror,1);
                            unlockAchievement(R.string.achievement_the_annihilator,1);
                            unlockAchievement(R.string.achievement_the_exalted,1);
                            unlockAchievement(R.string.achievement_the_great,1);

                            int count=0;
                            for (int i=0;i<gameDetails.getNumberOfPlayers();i++)
                            {
                                if(gameDetails.getPlayerType(i)==PLAYER_TYPE.TITAN)
                                {
                                    count++;
                                }
                            }
                            if(count>=4)
                            {
                                unlockAchievement(R.string.achievement_oneman_army,0);
                            }
                            break;
                        }
                    }
                    break;
                }
                case DRAW:
                {
                    unlockAchievement(R.string.achievement_loc,1);
                    break;
                }
                case LOSE:
                {
                    unlockAchievement(R.string.achievement_failure_is_the_pillar_of_err_failure, 1);
                    break;
                }
                case SPECTATOR:
                {
                    switch (gameDetails.getGameType())
                    {
                        case MIXED:
                        {
                            unlockAchievement(R.string.achievement_subterfuge,0);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        else
        {
            Toasts.appPresetToast(activity, Toasts.ToastMessages.networkConnectionError);
        }
    }

    public void setNewScore(final int score, final PLAYER_TYPE playerType)
    {
        int leader_board_id=0;
        switch (playerType)
        {
            case EASY:
            {
                leader_board_id=R.string.leaderboard_the_kyth_easy_ai;
                break;
            }
            case MEDIUM:
            {
                leader_board_id=R.string.leaderboard_enigma_medium_ai;
                break;
            }
            case HARD:
            {
                leader_board_id=R.string.leaderboard_lisa_hard_ai;
                break;
            }
            case TITAN:
            {
                leader_board_id=R.string.leaderboard_l_titan_ai;
                break;
            }
        }
        if((activity.getGoogleApiClient().isConnected()))
        {
            Games.Leaderboards.submitScore(activity.getGoogleApiClient(), activity.getString(leader_board_id), score);
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(activity.getGoogleApiClient(), activity.getString(leader_board_id), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>()
            {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult)
                {
                    if (isScoreResultValid(scoreResult))
                    {
                        if (score > scoreResult.getScore().getRawScore())
                        {
                            leaderboardType=playerType;
                            showLeaderBoard();
                        }
                    }
                    else
                    {
                        leaderboardType=playerType;
                        showLeaderBoard();
                    }
                }
            });
        } else {
            Toasts.appPresetToast(activity, Toasts.ToastMessages.networkConnectionError);
        }
    }

    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult)
    {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
    }

    public Player getUserAccount()
    {
        if((activity.getGoogleApiClient().isConnected()))
        {
            return Games.Players.getCurrentPlayer(activity.getGoogleApiClient());
        }
        return null;
    }
}
