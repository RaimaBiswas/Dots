package com.dots.games.pratick.dots;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.dots.games.pratick.dots.Designers.Misc;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.Enums.GRID_PURPOSE;
import com.dots.games.pratick.dots.Enums.MAXIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.Enums.PURPOSE;
import com.dots.games.pratick.dots.Fragments.GridFragment;
import com.dots.games.pratick.dots.Fragments.ScoreBarSmallFragment;
import com.dots.games.pratick.dots.Lists.GridStyleListFragment;
import com.dots.games.pratick.dots.Lists.PlayerListFragment;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.gameDetails;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pratick on 02-06-2015.
 * Makes the Fragments
 */
public class FragmentsUI
{
    private final DotsUI activity;
    private final Dimensions activityDimensions;
    private static final int fragmentBackgroundResourceId=R.id.menu_list_container;

    private GridFragment gridFragment;
    private ScoreBarSmallFragment scoreBarSmallFragment;
    private GridStyleListFragment gridStyleListFragment;
    private PlayerListFragment playerListFragment;

    public FragmentsUI(DotsUI activity, Dimensions activityDimensions)
    {
        this.activity=activity;
        this.activityDimensions=activityDimensions;
    }

    public FrameLayout.LayoutParams getLayoutParams(double widthFactor,double heightFactor,int gravity)
    {
        FrameLayout.LayoutParams rootLayoutParams=new FrameLayout.LayoutParams((int) (activityDimensions.width * widthFactor), (int) (activityDimensions.height *heightFactor));
        rootLayoutParams.gravity= gravity;
        return rootLayoutParams;
    }
    public void createDialog(MAXIMIZED_LIST_VIEW_ITEM selectedMaximizedItem)
    {
        switch (selectedMaximizedItem)
        {
            case SINGLE_PLAYER__START:
            {
                gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(1d,9d/10d,Gravity.TOP);
                gridFragment.purpose= GRID_PURPOSE.GAME;
                addFragment(gridFragment);

                scoreBarSmallFragment=new ScoreBarSmallFragment();
                scoreBarSmallFragment.setArguments(activity.getIntent().getExtras());
                scoreBarSmallFragment.rootLayoutParams=getLayoutParams(1d,1d/10d,Gravity.BOTTOM);
                addFragment(scoreBarSmallFragment);

                DotsUI.exitDetails.setGame(gridFragment, scoreBarSmallFragment);

                break;
            }
            case SINGLE_PLAYER__AI_LEVEL:
            {

                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.AI_LEVEL_CHOOSER;
                addFragment(playerListFragment);

                if(gameDetails.getPlayerType(0)!=PLAYER_TYPE.HUMAN)
                {
                    addOnRemovalListener(playerListFragment, PLAYER_TYPE.getPlayerTypeString(gameDetails.getPlayerType(0))+" AI Selected");
                }
                else
                {
                    addOnRemovalListener(playerListFragment, PLAYER_TYPE.getPlayerTypeString(gameDetails.getPlayerType(1))+" AI Selected");
                }


                break;
            }
            case SINGLE_PLAYER__GRID_SIZE:
            {
                gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                gridFragment.purpose= GRID_PURPOSE.GRID_SIZE_PREVIEW;
                addFragment(gridFragment);
                (activity.findViewById(fragmentBackgroundResourceId)).setBackgroundResource(R.color.BlackAlpha50);
                DotsUI.exitDetails.setMaximizedMenuItem(gridFragment, "Grid Size : " + gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount());
                break;
            }
            case SINGLE_PLAYER__STYLE:
            {
                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.STYLE;
                addFragment(playerListFragment);
                addOnRemovalListener(playerListFragment, "Game Style Settings Saved");
                break;
            }
            case MULTI_PLAYER__START:
            {
                gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(1d,9d/10d,Gravity.TOP);
                gridFragment.purpose= GRID_PURPOSE.GAME;
                addFragment(gridFragment);

                scoreBarSmallFragment=new ScoreBarSmallFragment();
                scoreBarSmallFragment.setArguments(activity.getIntent().getExtras());
                scoreBarSmallFragment.rootLayoutParams=getLayoutParams(1d,1d/10d,Gravity.BOTTOM);
                addFragment(scoreBarSmallFragment);

                DotsUI.exitDetails.setGame(gridFragment, scoreBarSmallFragment);

                break;
            }
            case MULTI_PLAYER__NO_OF_PLAYERS:
            {

                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.MULTI_PLAYER_OPPONENTS;
                addFragment(playerListFragment);
                addOnRemovalListener(playerListFragment, "Number of Players : " + gameDetails.getNumberOfPlayers());
                break;
            }
            case MULTI_PLAYER__GRID_SIZE:
            {
                gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                gridFragment.purpose= GRID_PURPOSE.GRID_SIZE_PREVIEW;
                addFragment(gridFragment);
                (activity.findViewById(fragmentBackgroundResourceId)).setBackgroundResource(R.color.BlackAlpha50);
                DotsUI.exitDetails.setMaximizedMenuItem(gridFragment, "Grid Size : "+gameDetails.getDotRowCount()+" x "+gameDetails.getDotColCount());
                break;
            }
            case MULTI_PLAYER__STYLE:
            {
                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.STYLE;
                addFragment(playerListFragment);
                addOnRemovalListener(playerListFragment, "Game Style Settings Saved");
                break;
            }
            case MIXED__START:
            {
               gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(1d,9d/10d,Gravity.TOP);
                gridFragment.purpose= GRID_PURPOSE.GAME;
                addFragment(gridFragment);

                scoreBarSmallFragment=new ScoreBarSmallFragment();
                scoreBarSmallFragment.setArguments(activity.getIntent().getExtras());
                scoreBarSmallFragment.rootLayoutParams=getLayoutParams(1d,1d/10d,Gravity.BOTTOM);
                addFragment(scoreBarSmallFragment);

                DotsUI.exitDetails.setGame(gridFragment, scoreBarSmallFragment);

                break;
            }
            case MIXED__OPPONENTS:
            {
                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.MIXED_OPPONENTS;
                addFragment(playerListFragment);
                addOnRemovalListener(playerListFragment, Misc.getAllPlayersNameAndType());
                break;
            }
            case MIXED__GRID_SIZE:
            {
                gridFragment=new GridFragment();
                gridFragment.setArguments(activity.getIntent().getExtras());
                gridFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                gridFragment.purpose= GRID_PURPOSE.GRID_SIZE_PREVIEW;
                addFragment(gridFragment);
                (activity.findViewById(fragmentBackgroundResourceId)).setBackgroundResource(R.color.BlackAlpha50);
                DotsUI.exitDetails.setMaximizedMenuItem(gridFragment, "Grid Size : " + gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount());
                break;
            }
            case MIXED__STYLE:
            {
                if (playerListFragment == null)
                {
                    playerListFragment = new PlayerListFragment();
                    playerListFragment.setArguments(activity.getIntent().getExtras());
                    playerListFragment.rootLayoutParams=getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
                }
                playerListFragment.playerListFragmentPurpose= PURPOSE.STYLE;
                addFragment(playerListFragment);
                addOnRemovalListener(playerListFragment, "Game Style Settings Saved");
                break;
            }
            case ACHIEVEMENT__SIGN_IN:
            {
                activity.googleApiServices.onConnectedAction=true;
                activity.googleApiServices.signInOut();
                break;
            }
            case ACHIEVEMENT__SHOW_TROPHY:
            {
                activity.googleApiServices.showAchievement();
                break;
            }
            case ACHIEVEMENT__SHOW_LEADER_BOARD:
            {
                activity.googleApiServices.showLeaderboardDialog();
                break;
            }
            case ACHIEVEMENT__SHOW_OTHERS1:
            {
                Toasts.appPresetToast(activity, Toasts.ToastMessages.waitForUpdate);
                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                break;
            }

        }
    }
    public void addOnRemovalListener(final Fragment activeMaximizedItemFragment, final String removalToastMessage)
    {
        DotsUI.exitDetails.setMaximizedMenuItem(activeMaximizedItemFragment, removalToastMessage);
        (activity.findViewById(fragmentBackgroundResourceId)).setBackgroundResource(R.color.BlackAlpha50);
        (activity.findViewById(fragmentBackgroundResourceId)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toasts.appMonoSpacedToast(activity, removalToastMessage).show();
                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                removeMaximizedItemFragment(activeMaximizedItemFragment, activity);
            }

        });
    }
    public void addFragment(Fragment activeMaximizedItemFragment)
    {

        activity.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.flip_left_in, -1)
                .add(R.id.menu_list_container, activeMaximizedItemFragment)
                .addToBackStack(null).commit();
    }
    public static void removeMaximizedItemFragment(Fragment activeMaximizedItemFragment,DotsUI activity)
    {
        activity.getFragmentManager()
                .beginTransaction().
                setCustomAnimations(-1,R.animator.flip_left_out).
                remove(activeMaximizedItemFragment).addToBackStack(null).commit();
        (activity.findViewById(fragmentBackgroundResourceId)).setBackgroundResource(R.color.Transparent);
        (activity.findViewById(fragmentBackgroundResourceId)).setClickable(false);
    }
    public void updateScoreBarSmallFragment(AtomicInteger currentPlayer)
    {
        scoreBarSmallFragment.changeCurrentPlayerFragment(currentPlayer);
    }
    public void callStyleFragment(int selectedPlayerPosition)
    {

        removeMaximizedItemFragment(playerListFragment, activity);

        gridStyleListFragment=new GridStyleListFragment();
        gridStyleListFragment.setArguments(activity.getIntent().getExtras());
        gridStyleListFragment.rootLayoutParams = getLayoutParams(8d/10d,8d/10d,Gravity.CENTER);
        gridStyleListFragment.selectedPlayer=selectedPlayerPosition;
        addFragment(gridStyleListFragment);
        addOnRemovalListener(gridStyleListFragment, "Game Style Settings Saved");
    }
    public void backToMenu()
    {
        if(gridFragment!=null && gridFragment.isAdded())
        {
            removeMaximizedItemFragment(gridFragment, activity);

        }
        if(scoreBarSmallFragment!=null && scoreBarSmallFragment.isAdded())
        {
            removeMaximizedItemFragment(scoreBarSmallFragment, activity);

        }
        if(playerListFragment!=null && playerListFragment.isAdded())
        {
            removeMaximizedItemFragment(playerListFragment, activity);
        }
        DotsUI.exitDetails.setMainMenu();
        //
    }



}
