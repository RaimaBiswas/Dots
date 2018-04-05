package com.dots.games.pratick.dots;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidsx.rateme.RateMeDialog;
import com.androidsx.rateme.RateMeDialogTimer;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.Enums.GAME_TYPE;
import com.dots.games.pratick.dots.Enums.MINIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.Fragments.GridFragment;
import com.dots.games.pratick.dots.Fragments.ScoreBarSmallFragment;
import com.dots.games.pratick.dots.Intro.IntroActivity;
import com.dots.games.pratick.dots.Lists.GridStyleListFragment;
import com.dots.games.pratick.dots.Lists.MaximizedListFragment;
import com.dots.games.pratick.dots.Lists.MinimizedListFragment;
import com.dots.games.pratick.dots.Lists.PlayerListFragment;
import com.dots.games.pratick.dots.Services.GoogleApiServices;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.concurrent.atomic.AtomicInteger;


public class DotsUI

        extends

        BaseGameActivity

        implements

        MinimizedListFragment.MinimizedListCommunicationInterface,
        MaximizedListFragment.MaximizedListCommunicationInterface,
        GridFragment.GridFragmentInterface,
        ScoreBarSmallFragment.ScoreBarSmallFragmentInterface,
        GridStyleListFragment.GridStyleListCommunicationInterface,
        PlayerListFragment.PlayerListCommunicationInterface


{


    Dimensions activityDimensions = new Dimensions();
    private MenuUI menuUI;
    private FragmentsUI fragmentsUI;

    public GoogleApiServices googleApiServices;

    public boolean endFireWorks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_ui);
        gameDetails.setGameType(GAME_TYPE.SINGLE_PLAYER);

        DotsUIConstructor();
        menuUI.createMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Players.pauseLongMusic(Players.PLAYER_TYPE.background1);
        Players.pauseLongMusic(Players.PLAYER_TYPE.victory);
        Players.pauseLongMusic(Players.PLAYER_TYPE.defeat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Players.backgroundMusic1 == null) {
            Players.initializeDots(this);
        } else {
            Players.resumeLongMusic(Players.PLAYER_TYPE.background1);
        }
        Players.resumeLongMusic(Players.PLAYER_TYPE.background1);
        Players.resumeLongMusic(Players.PLAYER_TYPE.victory);
        Players.resumeLongMusic(Players.PLAYER_TYPE.defeat);
    }

    @Override
    public void onBackPressed() {
        Log.d("Override", exitDetails.overrideBackButton + "");
        //if(exitDetails.overrideBackButton)
        {
            callExit();
        }
        /*else
        {
            exitDetails.overrideBackButton=true;
            super.onBackPressed();
        }*/

    }

    private void DotsUIConstructor() {
        setAppCharacteristics();
        initialiseUI();
        initiateServices();
    }

    private void initiateServices() {
        //RateMeDialogTimer.onStart(this);
        if (RateMeDialogTimer.shouldShowRateDialog(this, 7, 3)) {
            createRateMeDialog();
        }

        googleApiServices = new GoogleApiServices(this);
    }

    private void initialiseUI() {
        menuUI = new MenuUI(this, activityDimensions);
        fragmentsUI = new FragmentsUI(this, activityDimensions);
    }

    private void setAppCharacteristics() {
        exitDetails.setMainMenu();
        if (Players.backgroundMusic1 == null) {
            Players.initializeDots(this);
        }
        gameDetails.initialize(this);
        getWindowManager().getDefaultDisplay().getMetrics(activityDimensions);
        activityDimensions.width = activityDimensions.widthPixels;
        activityDimensions.height = activityDimensions.heightPixels;
    }

    @Override
    public void sendOnMinimizedListItemClick(MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem) {
        menuUI.changeData(activeMinimizedListItem);
    }

    @Override
    public void sendOnMaximizedListItemClick(int activeMaximizedListItemPosition) {
        fragmentsUI.createDialog(menuUI.getSelectedMaximizedItem(activeMaximizedListItemPosition));
    }

    @Override
    public void updateScoreBarSmallFragment(AtomicInteger currentPlayer) {
        fragmentsUI.updateScoreBarSmallFragment(currentPlayer);
    }

    @Override
    public void backToMenu() {
        fragmentsUI.backToMenu();
    }

    @Override
    public void callStyleFragment(int selectedPlayerPosition) {
        fragmentsUI.callStyleFragment(selectedPlayerPosition);
    }

    public enum ACTIVE_MENU {
        MAIN_MENU, GAME, MAXIMIZED_ITEM_MENU
    }

    public static class exitDetails {
        private static boolean overrideBackButton = true;
        private static ACTIVE_MENU backButtonController;
        private static Fragment[] activeFragmentList = new Fragment[2];
        private static String onRemovalMessage;

        public static void setMainMenu() {
            backButtonController = ACTIVE_MENU.MAIN_MENU;
            onRemovalMessage = "";
        }

        public static void setMaximizedMenuItem(Fragment activeMenuFragment, String removalMessage) {
            backButtonController = ACTIVE_MENU.MAXIMIZED_ITEM_MENU;
            activeFragmentList[0] = activeMenuFragment;
            onRemovalMessage = removalMessage;
        }

        public static void setGame(Fragment gameFragment, Fragment scoreFragment) {
            backButtonController = ACTIVE_MENU.GAME;
            activeFragmentList[0] = gameFragment;
            activeFragmentList[1] = scoreFragment;
            onRemovalMessage = "Game Abondoned";
        }

    }

    public boolean exited = false;

    public void callExit() {
        switch (exitDetails.backButtonController) {
            case MAIN_MENU: {
                exitDetails.overrideBackButton = false;

                new MaterialDialog.Builder(this)
                        .title("Exit to Android?")
                        .content("All Unsaved Progress will be lost")
                        .positiveText("Yes, I hate fun!")
                        .negativeText("No, Lets Rumble")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                exitDetails.overrideBackButton = true;
                                Players.playShortSound(Players.PLAYER_TYPE.appExit);
                                dialog.dismiss();
                                gameDetails.destroy();
                                finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                exitDetails.overrideBackButton = true;
                                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                dialog.dismiss();
                            }
                        }).show();

                break;
            }
            case GAME: {
                exitDetails.overrideBackButton = false;

                if (gameDetails.isGameOver(-1)) {
                    exitDetails.overrideBackButton = true;
                    gameDetails.reset();
                    Players.playShortSound(Players.PLAYER_TYPE.gameExit);

                    endFireWorks = true;
                    Players.stopLongMusic(Players.PLAYER_TYPE.victory);
                    Players.stopLongMusic(Players.PLAYER_TYPE.defeat);

                    getFragmentManager()
                            .beginTransaction().
                            setCustomAnimations(-1, R.animator.flip_left_out).
                            remove(exitDetails.activeFragmentList[0]).addToBackStack(null).commit();
                    (findViewById(R.id.menu_list_container)).setBackgroundResource(R.color.Transparent);
                    (findViewById(R.id.menu_list_container)).setClickable(false);

                    getFragmentManager()
                            .beginTransaction().
                            setCustomAnimations(-1, R.animator.flip_left_out).
                            remove(exitDetails.activeFragmentList[1]).addToBackStack(null).commit();
                    (findViewById(R.id.menu_list_container)).setBackgroundResource(R.color.Transparent);
                    (findViewById(R.id.menu_list_container)).setClickable(false);

                    exitDetails.setMainMenu();
                    return;
                }

                final DotsUI activity = this;
                new MaterialDialog.Builder(activity)
                        .title("Return to Menu?")
                        .content("You will lose the game")
                        .positiveText("Yes, Im a loser")
                        .negativeText("No, Victory shall be mine!")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                exited = true;
                                exitDetails.overrideBackButton = true;
                                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                                gameDetails.reset();
                                Players.playShortSound(Players.PLAYER_TYPE.gameExit);
                                Toasts.appMonoSpacedToast(activity, exitDetails.onRemovalMessage).show();
                                dialog.dismiss();

                                getFragmentManager()
                                        .beginTransaction().
                                        setCustomAnimations(-1, R.animator.flip_left_out).
                                        remove(exitDetails.activeFragmentList[0]).addToBackStack(null).commit();
                                (findViewById(R.id.menu_list_container)).setBackgroundResource(R.color.Transparent);
                                (findViewById(R.id.menu_list_container)).setClickable(false);

                                getFragmentManager()
                                        .beginTransaction().
                                        setCustomAnimations(-1, R.animator.flip_left_out).
                                        remove(exitDetails.activeFragmentList[1]).addToBackStack(null).commit();
                                (findViewById(R.id.menu_list_container)).setBackgroundResource(R.color.Transparent);
                                (findViewById(R.id.menu_list_container)).setClickable(false);

                                exitDetails.setMainMenu();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                exitDetails.overrideBackButton = true;
                                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                dialog.dismiss();
                            }
                        }).show();
                break;
            }
            case MAXIMIZED_ITEM_MENU: {
                SuperActivityToast.cancelAllSuperActivityToasts();
                Toasts.appMonoSpacedToast(this, exitDetails.onRemovalMessage).show();

                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);

                getFragmentManager()
                        .beginTransaction().
                        setCustomAnimations(-1, R.animator.flip_left_out).
                        remove(exitDetails.activeFragmentList[0]).addToBackStack(null).commit();
                (findViewById(R.id.menu_list_container)).setBackgroundResource(R.color.Transparent);
                (findViewById(R.id.menu_list_container)).setClickable(false);
                exitDetails.setMainMenu();
                break;
            }
        }
    }


    public void callBeginUserInitiatedSignIn() {
        beginUserInitiatedSignIn();
    }

    public void callSignOut() {
        signOut();
    }

    public TextView signInOutText;

    @Override
    public void onSignInSucceeded() {
        if (googleApiServices.onConnectedAction) {
            signInOutText.setText(R.string.achievement_sign_out_taunt);
            signInOutText = null;
            googleApiServices.onConnectedAction = false;
        }
    }


    @Override
    public void onSignInFailed() {
        //googleApiServices.onSignInFailed();
    }

    public GoogleApiClient getGoogleApiClient() {
        return getApiClient();
    }

    public void createRateMeDialog() {
        if (!RateMeDialogTimer.wasRated(this)) {
            new RateMeDialog.Builder(getPackageName())

                    .setBodyBackgroundColor(getResources().getColor(R.color.White))
                    .setBodyTextColor(getResources().getColor(R.color.Black))
                    .setShowShareButton(true)
                    .enableFeedbackByEmail("extnds@gmail.com")
                    .showAppIcon(R.drawable.icon)
                    .build().show(getFragmentManager(), "plain-dialog");
        }


    }

    public void openTutorial() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        Intent myIntent = new Intent(this, IntroActivity.class);
        startActivity(myIntent);
    }

}
