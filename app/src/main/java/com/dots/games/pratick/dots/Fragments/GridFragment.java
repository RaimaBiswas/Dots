package com.dots.games.pratick.dots.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dots.games.pratick.dots.Designers.Misc;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.ACHIEVEMENT_TYPE;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.Enums.GAME_TYPE;
import com.dots.games.pratick.dots.Enums.GRID_PURPOSE;
import com.dots.games.pratick.dots.Enums.GRID_TYPE;

import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.INTELLIGENT_UI_CONSTANTS;
import com.dots.games.pratick.dots.Structures.gridDimensions;


import com.dots.games.pratick.dots.Structures.GridElement;
import com.dots.games.pratick.dots.Structures.cluster;
import com.dots.games.pratick.dots.Structures.gameDetails;


import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.google.android.gms.games.Player;
import com.melnykov.fab.FloatingActionButton;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Pratick on 02-06-2015.
 * Creates the Grid Fragment
 */

public class GridFragment extends Fragment
{
    private View rootLayout;
    GridFragmentInterface mCallback;

    public FrameLayout.LayoutParams rootLayoutParams;
    public GRID_PURPOSE purpose;

    GridLayout gridLayout;
    GridElement[][] Grid;

    int lowerI;
    int lowerJ;

    int upperI;
    int upperJ;

    int barI;
    int barJ;

    int dotRowCountCopy=gameDetails.getDotRowCount();
    int dotColCountCopy=gameDetails.getDotColCount();

    Random rand = new Random();

    int filledCountRow[];
    int filledCountCol[];

    ImageView done;
    ImageView colRem;
    ImageView colAdd;
    ImageView rowRem;
    ImageView rowAdd;

    public interface GridFragmentInterface
    {
        void updateScoreBarSmallFragment(AtomicInteger currentPlayer);
        void backToMenu();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (GridFragmentInterface) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnViewScreenSelectedListener");
        }
    }

    ColorDrawable previewDrawable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ((DotsUI)getActivity()).exited=false;
        rootLayout=inflater.inflate(R.layout.grid_fragment_ui, container, false);
        gridLayout = (GridLayout)rootLayout.findViewById(R.id.game_ui_grid);
        gridLayout.setLayoutParams(rootLayoutParams);
        previewDrawable = new ColorDrawable(rootLayout.getResources().getColor(R.color.Orange400));

        if(purpose!=GRID_PURPOSE.GAME)
        {
            gridLayout.setBackgroundResource(R.drawable.grid_fragment_drop_shadow);

            gridLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.invalidMove1);
                }
            });
            done = (ImageView)rootLayout.findViewById(R.id.done);
            colRem = (ImageView)rootLayout.findViewById(R.id.column_remove);
            colAdd = (ImageView)rootLayout.findViewById(R.id.column_add);
            rowRem = (ImageView)rootLayout.findViewById(R.id.row_remove);
            rowAdd = (ImageView)rootLayout.findViewById(R.id.row_add);


            animator(done);
            animator(colRem);
            animator(colAdd);
            animator(rowRem);
            animator(rowAdd);

        }
        else
        {
            GridElement.reset();
            gridLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.invalidMove1);
                    Players.playShortSound(Players.PLAYER_TYPE.gameInvalidClick);
                }
            });
        }
        gridUI(purpose);
        return rootLayout;
    }

    public void animator(View v)
    {
        AnimatorSet animatorSet=new AnimatorSet();

        ObjectAnimator rotation = ObjectAnimator.ofFloat(v, View.ROTATION, 0, 360);
        ObjectAnimator riseUp = ObjectAnimator.ofFloat(v, View.TRANSLATION_Y, 100, 0);
        ObjectAnimator fadeIn   = ObjectAnimator.ofFloat(v, View.ALPHA, 0f, 1f);

        animatorSet.playTogether(fadeIn,riseUp,rotation);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(3000);
        animatorSet.start();
    }

    public void gridUI(GRID_PURPOSE purpose)
    {
        gridLayout.removeAllViews();

        Dimensions gridContainer =new Dimensions();
        gridContainer.width=rootLayoutParams.width;
        gridContainer.height=rootLayoutParams.height;

        gridDimensions barCount = new gridDimensions(gameDetails.getDotRowCount() - 1, gameDetails.getDotColCount() - 1);
        gridDimensions gridElementCount = new gridDimensions(gameDetails.getDotRowCount() + barCount.row, gameDetails.getDotColCount() + barCount.col);
        gridDimensions effectiveGridElementCount = new gridDimensions
        (
            (int) (gameDetails.getDotRowCount() + INTELLIGENT_UI_CONSTANTS.BAR_TO_DOT_RATIO * barCount.row + INTELLIGENT_UI_CONSTANTS.MARGIN_TO_DOT_RATIO * INTELLIGENT_UI_CONSTANTS.VERTICAL_MARGIN_COUNT  ),
            (int) (gameDetails.getDotColCount() + INTELLIGENT_UI_CONSTANTS.BAR_TO_DOT_RATIO * barCount.col + INTELLIGENT_UI_CONSTANTS.MARGIN_TO_DOT_RATIO * INTELLIGENT_UI_CONSTANTS.HORIZONTAL_MARGIN_COUNT)
        );

        GridElement.safeCount=(barCount.row*gameDetails.getDotColCount())+(barCount.col*gameDetails.getDotRowCount());

        filledCountRow=new int[gridElementCount.row];
        filledCountCol=new int[gridElementCount.col];

        Dimensions dot = new Dimensions();
        dot.width = dot.height =(gridContainer.width / effectiveGridElementCount.col)<(gridContainer.height / effectiveGridElementCount.row)?(gridContainer.width / effectiveGridElementCount.col) :(gridContainer.height / effectiveGridElementCount.row);

        Dimensions bar = new Dimensions();
        bar.width = bar.height = (int) (dot.width * (INTELLIGENT_UI_CONSTANTS.BAR_TO_DOT_RATIO));

        Dimensions grid = new Dimensions();
        grid.leftPadding = grid.rightPadding = (int) (dot.width * (INTELLIGENT_UI_CONSTANTS.MARGIN_TO_DOT_RATIO)) + ((gridContainer.width) - (dot.width * effectiveGridElementCount.col)) / 2;
        grid.topPadding = grid.bottomPadding = (int) (dot.height * (INTELLIGENT_UI_CONSTANTS.MARGIN_TO_DOT_RATIO)) + ((gridContainer.height) - (dot.height * effectiveGridElementCount.row)) / 2;

        lowerI=1;
        lowerJ=1;

        upperI=gridElementCount.row - 1;
        upperJ=gridElementCount.col - 1;

        Grid = new GridElement[gridElementCount.row][gridElementCount.col];

        gridLayout.setRowCount(gridElementCount.row);
        gridLayout.setColumnCount(gridElementCount.col);
        gridLayout.setPadding(grid.leftPadding, grid.topPadding, grid.rightPadding, grid.bottomPadding);

        for (int i = 0; i < gridElementCount.row; i++)
        {
            for (int j = 0; j < gridElementCount.col; j++)
            {
                Grid[i][j] = new GridElement(((DotsUI)getActivity()));

                if ((i % 2 == 0 && j % 2 == 0))
                {
                    Grid[i][j].gridFrontEnd.setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
                    Grid[i][j].gridFrontEnd.setLayoutParams(new FrameLayout.LayoutParams(dot.width, dot.height));
                    if (purpose == GRID_PURPOSE.GAME)
                    {
                        Grid[i][j].gridBackEnd = GRID_TYPE.DOT;
                        try
                        {
                            touchHandler(i, j,GRID_TYPE.DOT);
                        }
                        catch (Exception ignored)
                        {

                        }
                    }
                }
                else
                {
                    if ((i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0))
                    {
                        Grid[i][j].gridFrontEnd.setImageDrawable(new ColorDrawable(gameDetails.getUnfilledBarColor()));

                        //Grid[i][j].gridFrontEnd.setImageDrawable(DynamicDrawables.playerOneBoxDrawable);


                        if (purpose == GRID_PURPOSE.GAME)
                        {
                            if (i % 2 == 0)
                            {
                                Grid[i][j].gridFrontEnd.setLayoutParams(new FrameLayout.LayoutParams(bar.width, dot.height));
                                //Grid[i][j].gridFrontEnd.setImageDrawable(DynamicDrawables.playerTwoBarDrawable);
                                if (i == 0)
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_TOP_SAFE;
                                }
                                else if (i == gridElementCount.row - 1)
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_BOTTOM_SAFE;
                                }
                                else
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_NORMAL_SAFE;
                                }
                            }
                            else
                            {
                                Grid[i][j].gridFrontEnd.setLayoutParams(new FrameLayout.LayoutParams(dot.width, bar.height));


                                if (j == 0)
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_LEFT_SAFE;
                                }
                                else if (j == gridElementCount.col - 1)
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_RIGHT_SAFE;
                                }
                                else
                                {
                                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_NORMAL_SAFE;
                                }
                            }

                            try
                            {
                                touchHandler(i, j,GRID_TYPE.BAR);
                            }
                            catch (Exception ignored)
                            {

                            }
                        }
                    }
                    else
                    {
                        Grid[i][j].gridFrontEnd.setImageDrawable(new ColorDrawable(gameDetails.getUnfilledBoxColor()));
                                               //Grid[i][j].gridFrontEnd.setImageDrawable(getResources().getDrawable(R.drawable.unfilled_box));
                        Grid[i][j].gridFrontEnd.setLayoutParams(new FrameLayout.LayoutParams(bar.width, bar.height));
                        //Grid[i][j].gridFrontEnd.setImageDrawable(DynamicDrawables.playerOneBarDrawable);
                        if (purpose == GRID_PURPOSE.GAME)
                        {
                            Grid[i][j].gridBackEnd = GRID_TYPE.BOX_SAFE;
                            try
                            {
                                touchHandler(i, j,GRID_TYPE.BOX);
                            }
                            catch (Exception ignored)
                            {

                            }

                        }
                    }
                }
                gridLayout.addView(Grid[i][j].gridFrontEnd);
            }
        }
        if(purpose==GRID_PURPOSE.GRID_SIZE_PREVIEW)
        {
            gridSizeClickHandler();
        }
        else if(purpose==GRID_PURPOSE.GAME)
        {
            Player humanPlayer=((DotsUI)getActivity()).googleApiServices.getUserAccount();
            for (int i=0;i<gameDetails.getNumberOfPlayers();i++)
            {
                if(gameDetails.getPlayerType(i)==PLAYER_TYPE.HUMAN)
                {
                    if(humanPlayer!=null)
                    {
                        gameDetails.updatePlayerName(humanPlayer.getDisplayName(),i);
                    }
                    break;
                }
            }
            unlockAchievement(ACHIEVEMENT_TYPE.START);
            callAI(0,1);
        }
    }


    public void callAI(final int i, final int j)
    {
        if(!((DotsUI)getActivity()).exited)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (gameDetails.getPlayerType(cP.get())!=PLAYER_TYPE.HUMAN)
                    {


                        final Object Obj=new Object();
                        synchronized (lock)
                        {

                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    //pause(lock);
                                    //AI(gameDetails.playerType[cP.get()], i, j);
                                    //resume(lock);
                                }
                            }).start();
                            AI(gameDetails.getPlayerType(cP.get()), i, j);
                            pause(lock);
                            //resume(lock);

                            //resume(lock);

                            Log.d("VISITED", cP.get() + "");
                            printLog("Player " + (cP.get() + 1) + ":", Grid);
                            try
                            {
                                getActivity().findViewById(R.id.grid_ui_screen).setClickable(false);
                            }
                            catch (Exception e)
                            {
                                Log.d("ERROR",e.getMessage());
                            }

                            if (gameOver)
                            {
                                break;
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public void gridSizeClickHandler()
    {
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DotsUI.exitDetails.setMainMenu();
                SuperActivityToast.cancelAllSuperActivityToasts();
                Toasts.appMonoSpacedToast(getActivity(), "Grid Size : " + gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount()).show();
                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);

                mCallback.backToMenu();
            }
        });

        rootLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                gameDetails.setDotRowCount(dotRowCountCopy);
                gameDetails.setDotColCount(dotColCountCopy);
                SuperActivityToast.cancelAllSuperActivityToasts();
                Toasts.appMonoSpacedToast(getActivity(), "Grid Size : " + gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount()).show();
                mCallback.backToMenu();
            }
        });




        colRem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if((gameDetails.getDotColCount()==3 && gameDetails.getDotRowCount()==2)||(gameDetails.getDotColCount()==2))
                {
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.invalidMove2);
                }
                else
                {
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                    gameDetails.setDotColCount(gameDetails.getDotColCount() - 1);;
                    Toasts.appCustomToast(getActivity(), gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount(), Toasts.ToastDurations.verySmallDuration, Toasts.ToastStyles.greenFlying, gameDetails.getMonoTypeface()).show();
                    gridUI(GRID_PURPOSE.GRID_SIZE_PREVIEW);
                }
            }
        });
        colAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                gameDetails.setDotColCount(gameDetails.getDotColCount()+1);
                Toasts.appCustomToast(getActivity(), gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount(), Toasts.ToastDurations.verySmallDuration, Toasts.ToastStyles.greenFlying, gameDetails.getMonoTypeface()).show();
                gridUI(GRID_PURPOSE.GRID_SIZE_PREVIEW);
            }
        });

        rowRem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if((gameDetails.getDotRowCount() ==3 && gameDetails.getDotColCount() ==2)||(gameDetails.getDotRowCount() ==2))
                {
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.invalidMove2);
                }
                else
                {
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                    gameDetails.setDotRowCount(gameDetails.getDotRowCount() - 1);
                    Toasts.appCustomToast(getActivity(), gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount(), Toasts.ToastDurations.verySmallDuration, Toasts.ToastStyles.greenFlying, gameDetails.getMonoTypeface()).show();
                    gridUI(GRID_PURPOSE.GRID_SIZE_PREVIEW);
                }
            }
        });
        rowAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                gameDetails.setDotRowCount(gameDetails.getDotRowCount() + 1);
                Toasts.appCustomToast(getActivity(), gameDetails.getDotRowCount() + " x " + gameDetails.getDotColCount(), Toasts.ToastDurations.verySmallDuration, Toasts.ToastStyles.greenFlying, gameDetails.getMonoTypeface()).show();
                gridUI(GRID_PURPOSE.GRID_SIZE_PREVIEW);
            }
        });

    }

    private int focusedI=-1;
    private int focusedJ=-1;

    private boolean changed=false;
    private boolean cancel=false;
    private Drawable last;

    public void onBoxTouchFixer(View v,MotionEvent event)
    {

        int boxTop=v.getTop();
        int boxBottom=v.getBottom();
        int boxLeft=v.getLeft();
        int boxRight=v.getRight();

        int touchX=(int)(event.getRawX());
        int touchY=(int)(event.getRawY());

        //touchY -= getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android")) / 2;
        //touchY -= (int) ((getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize})).getDimension(0, 0));



        //(event.getRawY()-(int) ((getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize})).getDimension(0, 0)));

        int[] near=new int[4];
        near[0] = touchY-boxTop;
        near[1] = boxBottom-touchY;
        near[2] = touchX-boxLeft;
        near[3] = boxRight-touchX;

        int minPos=0;
        int min=near[0];
        for(int i=0;i<4;i++)
        {
            if(min>near[i])
            {
                min=near[i];
                minPos=i;
            }
        }

        switch (minPos)
        {
            case 0:
            {
                focusedI--;
                break;
            }
            case 1:
            {
                focusedI++;
                break;
            }
            case 2:
            {
                focusedJ--;
                break;
            }
            case 3:
            {
                focusedJ++;
                break;
            }
        }

        //Log.d("view top",boxTop+"");
        //Log.d("view bottom",boxBottom+"");
        //Log.d("view left",boxLeft+"");
        //Log.d("view right",boxRight+"");

        //Log.d("event x",touchX+"");
        //Log.d("event y", touchY+"");

        //Log.d("bar I",focusedI+"");
        //Log.d("bar J",focusedJ+"");

    }

    public void onDotTouchFixer(View v,MotionEvent event)
    {
        if(focusedI==0)
        {
            focusedI++;
        }
        else if(focusedI==Grid.length-1)
        {
            focusedI--;
        }
        else if(focusedJ==0)
        {
            focusedJ++;
        }
        else if(focusedJ==Grid[0].length-1)
        {
            focusedJ--;
        }
        else
        {
            int toss=(new Random()).nextInt(4);

            switch (toss)
            {
                case 0:
                {
                    focusedI++;
                    break;
                }
                case 1:
                {
                    focusedI--;
                    break;
                }
                case 2:
                {
                    focusedJ++;
                    break;
                }
                case 3:
                {
                    focusedJ--;
                    break;
                }

            }


        }
    }
    

    public void touchHandler(final int i,final int j, final GRID_TYPE grid_type)
    {
        Grid[i][j].gridFrontEnd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d("TouchDown","("+i+","+j+")");
                if (focusedI == -1 && focusedJ == -1) {
                    focusedI = i;
                    focusedJ = j;
                    if (grid_type == GRID_TYPE.BOX) {
                        onBoxTouchFixer(v, event);
                    }
                    if (grid_type == GRID_TYPE.DOT) {
                        onDotTouchFixer(v, event);
                    }
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Players.playShortSound(Players.PLAYER_TYPE.preview);

                        last = (Grid[focusedI][focusedJ].gridFrontEnd).getDrawable();

                        (Grid[focusedI][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                        if (GRID_TYPE.isBarHorizontal(Grid[focusedI][focusedJ].gridBackEnd)) {
                            (Grid[focusedI][focusedJ - 1].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                            (Grid[focusedI][focusedJ + 1].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                        } else {
                            (Grid[focusedI - 1][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                            //DynamicDrawables.setPlayerPreviewDrawable();
                            //previewDrawable.invalidateSelf();
                            (Grid[focusedI + 1][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                        }

                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (cancel) {
                            break;
                        }

                        //int actualY = (int) event.getRawY();// - (int) ((getActivity().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize})).getDimension(0, 0));
                        //Toast.makeText(getApplication(),(int) ((getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize})).getDimension(0, 0))+"",Toast.LENGTH_SHORT).show();
                        ////Log.d("B4 Move", "(" + focusedI + "," + focusedJ + ")");

                        if (event.getRawX() < Grid[focusedI][focusedJ].gridFrontEnd.getLeft() || event.getRawX() > Grid[focusedI][focusedJ].gridFrontEnd.getRight() || event.getRawY() < Grid[focusedI][focusedJ].gridFrontEnd.getTop() || event.getRawY() > Grid[focusedI][focusedJ].gridFrontEnd.getBottom()) {

                            if (GRID_TYPE.isBarHorizontal(Grid[focusedI][focusedJ].gridBackEnd)) {
                                //Log.d("Moved 1 ", "Mouse ( X : " + event.getRawX() + ", Y : " + event.getRawY() + " )" + ", Left : " + Grid[focusedI][focusedJ].gridFrontEnd.getLeft() + ", Right : " + Grid[focusedI][focusedJ].gridFrontEnd.getRight() + ", Top : " + Grid[focusedI][focusedJ].gridFrontEnd.getTop() + ", Bottom : " + Grid[focusedI][focusedJ].gridFrontEnd.getBottom());
                                if (event.getRawX() < Grid[focusedI][focusedJ].gridFrontEnd.getLeft()) {


                                    clear();
                                    focusedJ--;
                                    horizontalBarFixI(event);
                                    changed = true;


                                } else if (event.getRawX() > Grid[focusedI][focusedJ].gridFrontEnd.getRight()) {

                                    clear();
                                    focusedJ++;
                                    horizontalBarFixI(event);
                                    changed = true;


                                } else if (event.getRawY() > (Grid[focusedI][focusedJ].gridFrontEnd.getBottom() + (2d / 4d) * (Grid[focusedI][focusedJ].gridFrontEnd.getRight() - Grid[focusedI][focusedJ].gridFrontEnd.getLeft()))) {
                                    clear();
                                    focusedI += 2;
                                    changed = true;
                                } else if (event.getRawY() < (Grid[focusedI][focusedJ].gridFrontEnd.getTop() - (2d / 4d) * (Grid[focusedI][focusedJ].gridFrontEnd.getRight() - Grid[focusedI][focusedJ].gridFrontEnd.getLeft()))) {
                                    clear();
                                    focusedI -= 2;
                                    changed = true;
                                }


                            } else {
                                //Log.d("Moved 2 ", "Mouse ( X : " + event.getRawX()+", Y : "+event.getRawY()+" )" + ", Left : " + Grid[focusedI][focusedJ].gridFrontEnd.getLeft()+ ", Right : " + Grid[focusedI][focusedJ].gridFrontEnd.getRight()+ ", Top : " + Grid[focusedI][focusedJ].gridFrontEnd.getTop()+ ", Bottom : " + Grid[focusedI][focusedJ].gridFrontEnd.getBottom());
                                if (event.getRawY() < Grid[focusedI][focusedJ].gridFrontEnd.getTop()) {

                                    clear();
                                    focusedI--;
                                    verticalBarFixJ(event);
                                    changed = true;


                                } else if (event.getRawY() > Grid[focusedI][focusedJ].gridFrontEnd.getBottom()) {

                                    clear();
                                    focusedI++;
                                    verticalBarFixJ(event);
                                    changed = true;


                                } else if (event.getRawX() > (Grid[focusedI][focusedJ].gridFrontEnd.getRight() + (2d / 4d) * (Grid[focusedI][focusedJ].gridFrontEnd.getBottom() - Grid[focusedI][focusedJ].gridFrontEnd.getTop()))) {
                                    ////Log.d("Data","X : "+event.getRawX()+", Bar End X : "+Grid[focusedI][focusedJ].gridFrontEnd.getRight()+", Box width : "+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop())+", Theoretical Barrier X : "+(Grid[focusedI][focusedJ].gridFrontEnd.getRight()+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop()))+" , Real Barrier X : "+(3d/4d)*(Grid[focusedI][focusedJ].gridFrontEnd.getRight()+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop())));
                                    clear();
                                    focusedJ += 2;
                                    changed = true;
                                } else if (event.getRawX() < (Grid[focusedI][focusedJ].gridFrontEnd.getLeft() - (2d / 4d) * (Grid[focusedI][focusedJ].gridFrontEnd.getBottom() - Grid[focusedI][focusedJ].gridFrontEnd.getTop()))) {
                                    ////Log.d("Data","X : "+event.getRawX()+", Bar End X : "+Grid[focusedI][focusedJ].gridFrontEnd.getRight()+", Box width : "+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop())+", Theoretical Barrier X : "+(Grid[focusedI][focusedJ].gridFrontEnd.getRight()+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop()))+" , Real Barrier X : "+(3d/4d)*(Grid[focusedI][focusedJ].gridFrontEnd.getRight()+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop())));
                                    clear();
                                    focusedJ -= 2;
                                    changed = true;
                                }

                            }

                            if ((focusedI < 0 || focusedI >= Grid.length) || (focusedJ < 0 || focusedJ >= Grid[0].length)) {
                                ////Log.d("H","H");
                                cancel = true;
                                changed = false;
                                break;
                            }


                            if (changed) {
                                Players.playShortSound(Players.PLAYER_TYPE.preview);
                                last = (Grid[focusedI][focusedJ].gridFrontEnd).getDrawable();


                                //Grid[i][j].gridFrontEnd.setBackgroundColor(0xFFffffff);
                                (Grid[focusedI][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                                //(Grid[focusedI][focusedJ].gridFrontEnd).setBackgroundColor(gameDetails.getPreviewBarColor());
                                if (GRID_TYPE.isBarHorizontal(Grid[focusedI][focusedJ].gridBackEnd)) {
                                    (Grid[focusedI][focusedJ - 1].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                                    (Grid[focusedI][focusedJ + 1].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                                } else {
                                    (Grid[focusedI - 1][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                                    //previewDrawable.invalidateSelf();
                                    (Grid[focusedI + 1][focusedJ].gridFrontEnd).setImageDrawable(new ColorDrawable(gameDetails.getPreviewBarColor()));
                                }

                                changed = false;
                            }


                            ////Log.d("AF Move", "(" + focusedI + "," + focusedJ + ")");

                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        //boxToBarFinished=false;
                        if (!cancel) {
                            onHumanPlayerBarClick(focusedI, focusedJ);
                            if (GRID_TYPE.isBarHorizontal(Grid[focusedI][focusedJ].gridBackEnd)) {
                                (Grid[focusedI][focusedJ - 1].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
                                (Grid[focusedI][focusedJ + 1].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
                            } else {
                                (Grid[focusedI - 1][focusedJ].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
                                (Grid[focusedI + 1][focusedJ].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
                            }
                        } else {
                            cancel = false;
                        }

                        focusedI = -1;
                        focusedJ = -1;

                    }
                    case MotionEvent.ACTION_CANCEL: {
                        focusedI = -1;
                        focusedJ = -1;

                    }
                }
                return true;
            }
        });
    }
    public void clear()
    {
        (Grid[focusedI][focusedJ].gridFrontEnd).setImageDrawable(last);

        if(GRID_TYPE.isBarHorizontal(Grid[focusedI][focusedJ].gridBackEnd))
        {
            (Grid[focusedI][focusedJ-1].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
            (Grid[focusedI][focusedJ+1].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
        }
        else
        {
            (Grid[focusedI-1][focusedJ].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
            (Grid[focusedI+1][focusedJ].gridFrontEnd).setImageDrawable(getResources().getDrawable(R.drawable.single_player_minimized_menu_app_icon));
        }
    }

    public void horizontalBarFixI(MotionEvent event)
    {
        //int actualY=(int)event.getRawY()-150;
        if(GRID_TYPE.isBarTopHorizontal(Grid[focusedI][focusedJ].gridBackEnd))
        {
            focusedI++;
        }
        else if (GRID_TYPE.isBarBottomHorizontal(Grid[focusedI][focusedJ].gridBackEnd))
        {
            focusedI--;
        }
        else
        {
            if(event.getRawY()>(Grid[focusedI][focusedJ].gridFrontEnd.getTop()+(Grid[focusedI][focusedJ].gridFrontEnd.getBottom()-Grid[focusedI][focusedJ].gridFrontEnd.getTop())/2))
            {
                focusedI++;
            }
            else
            {
                focusedI--;
            }
        }
    }

    public void verticalBarFixJ(MotionEvent event)
    {
        if(GRID_TYPE.isBarLeftVertical(Grid[focusedI][focusedJ].gridBackEnd))
        {
            focusedJ++;
        }
        else if (GRID_TYPE.isBarRightVertical(Grid[focusedI][focusedJ].gridBackEnd))
        {
            focusedJ--;
        }
        else
        {
            if(event.getRawX()>(Grid[focusedI][focusedJ].gridFrontEnd.getLeft()+(Grid[focusedI][focusedJ].gridFrontEnd.getRight()-Grid[focusedI][focusedJ].gridFrontEnd.getLeft())/2))
            {
                focusedJ++;
            }
            else
            {
                focusedJ--;
            }
        }
    }




    public void victory(final int victoriousPlayerPosition)
    {
        Players.playLongMusic(Players.PLAYER_TYPE.victory);

        final FrameLayout frameLayout=(FrameLayout)getActivity().findViewById(R.id.animator_screen);
        final FloatingActionButton floatingActionButton=(FloatingActionButton)getActivity().findViewById(R.id.back);
        final ObjectAnimator fadeInButton=ObjectAnimator.ofFloat(getActivity().findViewById(R.id.back),View.ALPHA,0f,1f).setDuration(2000);
        final TextView resultContainer=(TextView)getActivity().findViewById(R.id.conclusion_image);
        final AnimatorSet animatorSet=new AnimatorSet();
        final Dimensions textPadding = new Dimensions();

        {
            animatorSet.setDuration(2000);
            animatorSet.setInterpolator(new OvershootInterpolator());
            animatorSet.playTogether
            (
                ObjectAnimator.ofFloat(resultContainer, View.ROTATION_Y, 720),
                ObjectAnimator.ofFloat(resultContainer, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(resultContainer, View.SCALE_X, 0f, 1f)
            );
        }
        {
            textPadding.leftPadding=(int)(28d/100d*rootLayoutParams.width);
            textPadding.topPadding=(int)(25d/100d*resultContainer.getHeight());
            textPadding.width=resultContainer.getWidth()-2*textPadding.leftPadding;
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Players.stopLongMusic(Players.PLAYER_TYPE.victory);
                ((DotsUI)getActivity()).endFireWorks=true;
                mCallback.backToMenu();
                gameDetails.reset();
            }
        });


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frameLayout.setBackgroundResource(R.color.BlackAlpha50);
                frameLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                fadeInButton.start();
                resultContainer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
                resultContainer.setPadding(textPadding.leftPadding, textPadding.topPadding, 0, 0);
                resultContainer.setTextColor(Color.rgb(255, 192, 0));
                resultContainer.setBackgroundResource(R.drawable.banner);
                resultContainer.setText("   "+gameDetails.getPlayerName(victoriousPlayerPosition) + " Wins !   ");
                Misc.setTextSizeForWidth(resultContainer, textPadding.width);
                animatorSet.start();
            }
        });

        final Object locked=new Object();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final int[] fireworksDrawableId={R.drawable.star_pink,R.drawable.star_white,R.drawable.confeti2,R.drawable.confeti3};
                final int fireworksMaximumCount=50;
                final int fireworksPerSecond=50;
                final int fireworksDuration=1000;

                synchronized (locked)
                {

                    while (true)
                    {
                        if(((DotsUI)getActivity()).endFireWorks)
                        {
                            ((DotsUI)getActivity()).endFireWorks=false;
                            break;
                        }

                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                new ParticleSystem(getActivity(), rand.nextInt(fireworksMaximumCount), fireworksDrawableId[rand.nextInt(4)], fireworksDuration, R.id.animator_screen)
                                        .setRotationSpeed(rand.nextInt(360))
                                        .setSpeedRange(0, rand.nextFloat())
                                        .oneShot(Grid[rand.nextInt(Grid.length - 1)][rand.nextInt(Grid[0].length - 1)].gridFrontEnd, rand.nextInt(fireworksPerSecond));
                            }
                        });

                        try
                        {
                            locked.wait(300);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    public void draw()
    {
        final FrameLayout frameLayout=(FrameLayout)getActivity().findViewById(R.id.animator_screen);
        final FloatingActionButton floatingActionButton=(FloatingActionButton)getActivity().findViewById(R.id.back);
        final ObjectAnimator fadeInButton=ObjectAnimator.ofFloat(getActivity().findViewById(R.id.back),View.ALPHA,0f,1f).setDuration(2000);
        final TextView resultContainer=(TextView)getActivity().findViewById(R.id.conclusion_image);
        final AnimatorSet animatorSet=new AnimatorSet();
        final Dimensions textPadding = new Dimensions();
        {
            animatorSet.setDuration(2000);
            animatorSet.setInterpolator(new OvershootInterpolator());
            animatorSet.playTogether
            (
                ObjectAnimator.ofFloat(resultContainer, View.ROTATION_Y, 720),
                ObjectAnimator.ofFloat(resultContainer, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(resultContainer, View.SCALE_X, 0f, 1f)
            );
        }
        {
            textPadding.leftPadding=(int)(28d/100d*rootLayoutParams.width);
            textPadding.topPadding=(int)(25d/100d*resultContainer.getHeight());
            textPadding.width=resultContainer.getWidth()-2*textPadding.leftPadding;
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.backToMenu();
                gameDetails.reset();
            }
        });


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frameLayout.setBackgroundResource(R.color.BlackAlpha50);
                frameLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                fadeInButton.start();
                resultContainer.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
                resultContainer.setPadding(textPadding.leftPadding, textPadding.topPadding, 0, 0);
                resultContainer.setTextColor(Color.rgb(255, 192, 0));
                resultContainer.setBackgroundResource(R.drawable.banner);
                resultContainer.setText(" No Clear Winner !!");
                Misc.setTextSizeForWidth(resultContainer, textPadding.width);
                animatorSet.start();
            }
        });
    }

    public void defeat()
    {
        Players.playLongMusic(Players.PLAYER_TYPE.defeat);

        final FrameLayout frameLayout=(FrameLayout)getActivity().findViewById(R.id.animator_screen);
        final TextView resultContainer=(TextView)getActivity().findViewById(R.id.conclusion_image);
        final FloatingActionButton floatingActionButton=(FloatingActionButton)getActivity().findViewById(R.id.back);
        final ObjectAnimator fadeInButton=ObjectAnimator.ofFloat(floatingActionButton, View.ALPHA, 0f, 1f).setDuration(2000);
        final AnimatorSet animatorSet=new AnimatorSet();
        {
            animatorSet.playTogether
                    (
                            ObjectAnimator.ofFloat(resultContainer,View.ROTATION_Y,720),
                            ObjectAnimator.ofFloat(resultContainer,View.ALPHA,0f,1f),
                            ObjectAnimator.ofFloat(resultContainer, View.SCALE_X, 0f, 1f)
                    );
            animatorSet.setDuration(2000);
            animatorSet.setInterpolator(new OvershootInterpolator());
        }


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frameLayout.setBackgroundResource(R.color.BlackAlpha50);
                frameLayout.setAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                fadeInButton.start();
                animatorSet.start();

                resultContainer.setBackgroundResource(R.drawable.defeat);

                new ParticleSystem(getActivity(), 180, R.drawable.rain, 10000, R.id.animator_screen)
                        .setScaleRange(0.1f, 0.5f)
                        .setSpeedModuleAndAngleRange(0f, 0f, 0, 0)
                        .setAcceleration(0.00005f, 90)
                        .emitWithGravity(getActivity().findViewById(R.id.cloud), Gravity.BOTTOM, 20);

            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Players.stopLongMusic(Players.PLAYER_TYPE.defeat);
                mCallback.backToMenu();
                gameDetails.reset();
            }
        });
    }

    private void unlockAchievement(final ACHIEVEMENT_TYPE achievement_type)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((DotsUI) getActivity()).googleApiServices.setAchievement(achievement_type);
            }
        });
    }

    public void updateLeaderboard(final PLAYER_TYPE playerType,final int score)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((DotsUI) getActivity()).googleApiServices.setNewScore(score, playerType);
            }
        });
    }

    private void onGameFinished()
    {
        unlockAchievement(ACHIEVEMENT_TYPE.FINISH);
        switch (gameDetails.getGameType())
        {
            case SINGLE_PLAYER:
            {
                if(gameDetails.getPlayerType(0)==PLAYER_TYPE.HUMAN)
                {
                    if(gameDetails.getPlayerScore(0)>gameDetails.getPlayerScore(1))
                    {
                        victory(0);
                        updateLeaderboard(gameDetails.getPlayerType(1),gameDetails.getPlayerScore(0));
                        unlockAchievement(ACHIEVEMENT_TYPE.WIN);
                    }
                    else if(gameDetails.getPlayerScore(1)>gameDetails.getPlayerScore(0))
                    {
                        defeat();
                        unlockAchievement(ACHIEVEMENT_TYPE.LOSE);
                    }
                    else
                    {
                        draw();
                        unlockAchievement(ACHIEVEMENT_TYPE.DRAW);
                    }
                }
                else
                {
                    if(gameDetails.getPlayerScore(1)>gameDetails.getPlayerScore(0))
                    {
                        victory(1);
                        updateLeaderboard(gameDetails.getPlayerType(0),gameDetails.getPlayerScore(1));
                        unlockAchievement(ACHIEVEMENT_TYPE.WIN);
                    }
                    else if(gameDetails.getPlayerScore(0)>gameDetails.getPlayerScore(1))
                    {
                        defeat();
                        unlockAchievement(ACHIEVEMENT_TYPE.LOSE);
                    }
                    else
                    {
                        draw();
                        unlockAchievement(ACHIEVEMENT_TYPE.DRAW);
                    }
                }
                break;
            }
            case MULTI_PLAYER:
            {
                int victoriousPlayerPos=0;
                int victoriousPlayerScore=gameDetails.getPlayerScore(0);
                for(int i=1;i<gameDetails.getNumberOfPlayers();i++)
                {
                    if(gameDetails.getPlayerScore(i)==victoriousPlayerScore)
                    {
                        draw();
                        unlockAchievement(ACHIEVEMENT_TYPE.DRAW);
                        return;
                    }

                    if(gameDetails.getPlayerScore(i)>victoriousPlayerScore)
                    {
                        victoriousPlayerScore=gameDetails.getPlayerScore(i);
                        victoriousPlayerPos=i;
                    }

                }
                victory(victoriousPlayerPos);
                if (victoriousPlayerPos==0)
                {
                    unlockAchievement(ACHIEVEMENT_TYPE.WIN);
                }

                break;
            }
            case MIXED:
            {
                int victoriousPlayerPos=0;
                int victoriousPlayerScore=gameDetails.getPlayerScore(0);

                for(int i=1;i<gameDetails.getNumberOfPlayers();i++)
                {
                    if(gameDetails.getPlayerScore(i)>victoriousPlayerScore)
                    {
                        victoriousPlayerScore=gameDetails.getPlayerScore(i);
                        victoriousPlayerPos=i;
                    }
                }
                if(gameDetails.getPlayerType(victoriousPlayerPos)==PLAYER_TYPE.HUMAN)
                {
                    int c=0;
                    for(int i=0;i<gameDetails.getNumberOfPlayers();i++)
                    {
                        if(gameDetails.getPlayerScore(i)==victoriousPlayerScore)
                        {
                            c++;
                        }
                    }
                    if(c==2)
                    {
                        draw();
                        unlockAchievement(ACHIEVEMENT_TYPE.DRAW);
                        return;
                    }
                    victory(victoriousPlayerPos);
                    unlockAchievement(ACHIEVEMENT_TYPE.WIN);
                }
                else
                {
                    for(int i=0;i<gameDetails.getNumberOfPlayers();i++)
                    {
                        if(gameDetails.getPlayerType(0)==PLAYER_TYPE.HUMAN)
                        {
                            defeat();
                            unlockAchievement(ACHIEVEMENT_TYPE.LOSE);
                        }
                    }
                    victory(victoriousPlayerPos);
                    unlockAchievement(ACHIEVEMENT_TYPE.SPECTATOR);
                    return;
                }
                break;
            }
        }
    }

    private synchronized void updateCurrentPlayer()
    {
        if(cP.get()==gameDetails.getNumberOfPlayers()-1)
        {
            cP.set(0);
            //currentPlayer=0;
        }
        else
        {
            cP.getAndIncrement();
            //currentPlayer++;
        }
        resume(lock);
    }

    
    public AtomicInteger cP=new AtomicInteger(0);
    
    private void onHumanPlayerBarClick(final int i,final int j)
    {
        int previousScore=gameDetails.getPlayerScore(cP.get());

        if (GRID_TYPE.isBarFilled(Grid[i][j].gridBackEnd))
        {
            Players.playShortSound(Players.PLAYER_TYPE.gameInvalidClick);
            (Grid[focusedI][focusedJ].gridFrontEnd).setImageDrawable(last);
            Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.invalidMove1);
            return;
        }
        else
        {
            if(GRID_TYPE.isBarSafe(Grid[i][j].gridBackEnd))
            {
                GridElement.safeCount--;
            }
            if(GRID_TYPE.isBarRipe(Grid[i][j].gridBackEnd))
            {
                if(GridElement.ripeIOne==i && GridElement.ripeJOne==j)
                {
                    GridElement.ripeIOne=-1;
                    GridElement.ripeJOne=-1;
                }
                if(GridElement.ripeITwo==i && GridElement.ripeJTwo==j)
                {
                    GridElement.ripeITwo=-1;
                    GridElement.ripeJTwo=-1;
                }
            }

            ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(gameDetails.getPlayerBarColor(cP.get())));

            if (GRID_TYPE.isBarHorizontal(Grid[i][j].gridBackEnd))
            {
                if (GRID_TYPE.isBarBottomHorizontal(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_BOTTOM_FILLED;
                    humanPlayerFillBox(i - 1, j);
                }
                else if (GRID_TYPE.isBarTopHorizontal(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_TOP_FILLED;
                    humanPlayerFillBox(i + 1, j);
                }
                else
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_NORMAL_FILLED;
                    humanPlayerFillBox(i - 1, j);
                    humanPlayerFillBox(i + 1, j);
                }
            }

            if (GRID_TYPE.isBarVertical(Grid[i][j].gridBackEnd))
            {

                if (GRID_TYPE.isBarLeftVertical(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_LEFT_FILLED;
                    humanPlayerFillBox(i, j + 1);
                }
                else if (GRID_TYPE.isBarRightVertical(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_RIGHT_FILLED;
                    humanPlayerFillBox(i, j - 1);
                }
                else
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_NORMAL_FILLED;
                    humanPlayerFillBox(i, j + 1);
                    humanPlayerFillBox(i, j - 1);
                }
            }

        }

        if (previousScore == gameDetails.getPlayerScore(cP.get()))
        {
            Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
            Log.d(" Human fill Bar CP ", cP.get() + "");

            updateCurrentPlayer();
            mCallback.updateScoreBarSmallFragment(cP);
            if(gameDetails.getPlayerType(cP.get())!=PLAYER_TYPE.HUMAN)
            {
                getActivity().findViewById(R.id.grid_ui_screen).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Players.playShortSound(Players.PLAYER_TYPE.gameInvalidClick);
                        Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.waitForSometime);
                        //Toasts.appCustomToast(getActivity(), Toasts.ToastMessages.waitForSometime, Toasts.ToastDurations.smallDuration, Toasts.ToastStyles.blueFlying, gameDetails.getMonoTypeface()).show();
                    }
                });

                callAI(i,j);
            }
        } 
        else 
        {
            Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);

            if(gameDetails.isGameOver(-1))
            {
                onGameFinished();
            }
        }
    }
    public void humanPlayerFillBox(int i,int j)
    {
        if
        (
            GRID_TYPE.isBarFilled(Grid[i - 1][j].gridBackEnd) &&
            GRID_TYPE.isBarFilled(Grid[i + 1][j].gridBackEnd) &&
            GRID_TYPE.isBarFilled(Grid[i][j - 1].gridBackEnd) &&
            GRID_TYPE.isBarFilled(Grid[i][j + 1].gridBackEnd) &&
            Grid[i][j].gridBackEnd!=GRID_TYPE.BOX_FILLED
        )
        {
            gameDetails.updatePlayerScore(cP.get());
            mCallback.updateScoreBarSmallFragment(cP);
            ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(gameDetails.getPlayerBoxColor(cP.get())));
            Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
            Grid[i][j].gridBackEnd = GRID_TYPE.BOX_FILLED;
            updateRange(i, j);
        }
    }



    {/*   private boolean playerOneTurn=true;

    private void onBarClick(final int i, final int j)
    {
        int previousScore;
        if(gameDetails.gameType==GAME_TYPE.SINGLE_PLAYER)
        {
            previousScore = gameDetails.playerOneScore;
        }
        else
        {
            if(playerOneTurn)
            {
                previousScore = gameDetails.playerOneScore;
            }
            else
            {
                previousScore = gameDetails.playerTwoScore;
            }
        }

        if (GRID_TYPE.isBarFilled(Grid[i][j].gridBackEnd))
        {
            Players.playShortSound(Players.PLAYER_TYPE.gameInvalidClick);

            (Grid[focusedI][focusedJ].gridFrontEnd).setImageDrawable(last);

            SuperActivityToast.create(getActivity(), "Invalid Move", 500, Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN)).show();
            return;
        }
        else
        {
            if(GRID_TYPE.isBarSafe(Grid[i][j].gridBackEnd))
            {
                GridElement.safeCount--;
            }
            if(GRID_TYPE.isBarRipe(Grid[i][j].gridBackEnd))
            {
                if(GridElement.ripeIOne==i && GridElement.ripeJOne==j)
                {
                    GridElement.ripeIOne=-1;
                    GridElement.ripeJOne=-1;
                }
                if(GridElement.ripeITwo==i && GridElement.ripeJTwo==j)
                {
                    GridElement.ripeITwo=-1;
                    GridElement.ripeJTwo=-1;
                }
            }
            if(gameDetails.gameType==GAME_TYPE.SINGLE_PLAYER)
            {
                ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerOneBarColor));
                Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
            }
            else
            {
                if(playerOneTurn)
                {
                    ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerOneBarColor));
                    Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                }
                else
                {
                    ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerOneBarColor));
                    Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                }
            }

            if (GRID_TYPE.isBarHorizontal(Grid[i][j].gridBackEnd))
            {
                if (GRID_TYPE.isBarBottomHorizontal(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_BOTTOM_FILLED;
                    humanBoxFill(i - 1, j);
                }
                else if (GRID_TYPE.isBarTopHorizontal(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_TOP_FILLED;
                    humanBoxFill(i + 1, j);
                }
                else
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_HORIZONTAL_NORMAL_FILLED;
                    humanBoxFill(i - 1, j);
                    humanBoxFill(i + 1, j);
                }
            }

            if (GRID_TYPE.isBarVertical(Grid[i][j].gridBackEnd))
            {

                if (GRID_TYPE.isBarLeftVertical(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_LEFT_FILLED;
                    humanBoxFill(i, j + 1);
                }
                else if (GRID_TYPE.isBarRightVertical(Grid[i][j].gridBackEnd))
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_RIGHT_FILLED;
                    humanBoxFill(i, j - 1);
                }
                else
                {
                    Grid[i][j].gridBackEnd = GRID_TYPE.BAR_VERTICAL_NORMAL_FILLED;
                    humanBoxFill(i, j + 1);
                    humanBoxFill(i, j - 1);
                }
            }
        }

        if(gameDetails.gameType==GAME_TYPE.SINGLE_PLAYER)
        {
            if (previousScore == gameDetails.playerOneScore)
            {
                Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                mCallback.updateScoreBarSmallFragment(1);

                    getActivity().findViewById(R.id.grid_ui_screen).setOnClickListener(new View.OnClickListener() 
                    {
                        @Override
                        public void onClick(View v) 
                        {
                            Players.playShortSound(Players.PLAYER_TYPE.gameInvalidClick);
                            //Toast.makeText(getActivity(),"Wait !!",Toast.LENGTH_SHORT).show();
                            SuperActivityToast.create(getActivity(), "Wait !!", 500, Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN)).show();
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lock) {
                                AI(gameDetails.ai_mode, i, j);
                                printLog("AI", Grid);
                                getActivity().findViewById(R.id.grid_ui_screen).setClickable(false);
                            }
                        }
                    }).start();


            } else {
                Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                mCallback.updateScoreBarSmallFragment(2);

                if(gameDetails.playerOneScore+gameDetails.playerTwoScore==((gameDetails.dotRowCount-1)*(gameDetails.dotColCount-1)))
                {
                    if(gameDetails.playerOneScore>gameDetails.playerTwoScore)
                        victory();
                    else
                        defeat();
                }
            }
        }
        else
        {
            if(playerOneTurn)
            {
                if (previousScore == gameDetails.playerOneScore)
                {
                    Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                    mCallback.updateScoreBarSmallFragment(1);
                    playerOneTurn=false;

                }
                else
                {
                    Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                    mCallback.updateScoreBarSmallFragment(2);

                    if(gameDetails.playerOneScore+gameDetails.playerTwoScore==((gameDetails.dotRowCount-1)*(gameDetails.dotColCount-1)))
                    {
                        if(gameDetails.playerOneScore>gameDetails.playerTwoScore)
                            victory();
                        else
                            defeat();
                    }
                }
            }
            else
            {
                if (previousScore == gameDetails.playerTwoScore)
                {
                    Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                    mCallback.updateScoreBarSmallFragment(3);
                    playerOneTurn=true;

                } else
                {
                    Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                    mCallback.updateScoreBarSmallFragment(4);

                    if(gameDetails.playerOneScore+gameDetails.playerTwoScore==((gameDetails.dotRowCount-1)*(gameDetails.dotColCount-1)))
                    {
                        if(gameDetails.playerOneScore>gameDetails.playerTwoScore)
                            victory();
                        else
                            defeat();
                    }
                }
            }
        }



    }




    private void humanBoxFill(int i, int j)
    {
        if
                (
                GRID_TYPE.isBarFilled(Grid[i - 1][j].gridBackEnd) &&
                        GRID_TYPE.isBarFilled(Grid[i + 1][j].gridBackEnd) &&
                        GRID_TYPE.isBarFilled(Grid[i][j - 1].gridBackEnd) &&
                        GRID_TYPE.isBarFilled(Grid[i][j + 1].gridBackEnd) &&
                        Grid[i][j].gridBackEnd!=GRID_TYPE.BOX_FILLED
                )
        {
            if(gameDetails.gameType==GAME_TYPE.SINGLE_PLAYER)
            {
                ++gameDetails.playerOneScore;
                ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerOneBoxColor));
                Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
            }
            else
            {
                if(playerOneTurn)
                {
                    ++gameDetails.playerOneScore;
                    ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerOneBoxColor));
                    Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                }
                else
                {
                    ++gameDetails.playerTwoScore;
                    ImageViewAnimatedChange(getActivity(), Grid[i][j].gridFrontEnd, new ColorDrawable(DynamicDrawables.playerTwoBoxColor));
                    Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                }
            }



            Grid[i][j].gridBackEnd = GRID_TYPE.BOX_FILLED;
            updateRange(i, j);
        }
    }*/}

    private Object lock=new Object();
    public void AI(PLAYER_TYPE mode, final int humanI, final int humanJ)
    {
        switch (mode)
        {
            case EASY:
            {
                easyAI();
                break;
            }
            case MEDIUM:
            {
                mediumAI(humanI, humanJ);
                break;
            }
            case HARD:
            {
                hardAI(humanI, humanJ);
                break;
            }
            case TITAN:
            {
                titanAI(humanI, humanJ);
                break;
            }
        }
    }

    {
    /*

    private GRID_TYPE reactor(int barI, int barJ) {
        int latestUserBarType = getBarType(barI, barJ);

        GRID_TYPE boxOneType = GRID_TYPE.OUT_OF_BOUNDS;
        GRID_TYPE boxTwoType = GRID_TYPE.OUT_OF_BOUNDS;

        int boxOneI = -1;
        int boxOneJ = -1;

        int boxTwoI = -1;
        int boxTwoJ = -1;

        switch (latestUserBarType) {
            case HORIZONTAL_BAR_UPPER_EDGE: {
                boxOneI = barI + 1;
                boxOneJ = barJ;
                break;
            }
            case HORIZONTAL_BAR_LOWER_EDGE: {
                boxTwoI = barI - 1;
                boxTwoJ = barJ;
                break;
            }
            case HORIZONTAL_BAR_NORMAL: {
                boxOneI = barI + 1;
                boxOneJ = barJ;
                boxTwoI = barI - 1;
                boxTwoJ = barJ;
                break;
            }
            case VERTICAL_BAR_LEFT_EDGE: {
                boxOneI = barI;
                boxOneJ = barJ + 1;
                break;
            }
            case VERTICAL_BAR_RIGHT_EDGE: {
                boxTwoI = barI;
                boxTwoJ = barJ - 1;
                break;
            }
            case VERTICAL_BAR_NORMAL: {
                boxOneI = barI;
                boxOneJ = barJ - 1;
                boxTwoI = barI;
                boxTwoJ = barJ + 1;
                break;
            }
        }

        if (boxOneI != -1 && boxOneJ != -1) {
            boxOneType = getBoxType(boxOneI, boxOneJ);
        }
        if (boxTwoI != -1 && boxTwoJ != -1) {
            boxTwoType = getBoxType(boxTwoI, boxTwoJ);
        }

        for (int i = 1; i <= 2; i++) {
            GRID_TYPE TempBoxOne = i == 1 ? boxOneType : boxTwoType;
            GRID_TYPE TempBoxTwo = i == 1 ? boxTwoType : boxOneType;

            if (TempBoxOne == GRID_TYPE.BOX_UNSAFE) {
                if (TempBoxTwo == GRID_TYPE.BOX_UNSAFE || TempBoxTwo == GRID_TYPE.OUT_OF_BOUNDS) {
                    return GRID_TYPE.BOX_UNSAFE;
                } else if (TempBoxTwo == GRID_TYPE.BOX_SAFE) {
                    return GRID_TYPE.BOX_SAFE_UNSAFE;
                }
            }
            if (GRID_TYPE.isRipe(TempBoxOne)) {
                randomI = i == 1 ? boxOneI : boxTwoI;
                randomJ = i == 1 ? boxOneJ : boxTwoJ;
                boxI = i == 1 ? boxOneI : boxTwoI;
                boxJ = i == 1 ? boxOneJ : boxTwoJ;

                switch (gridBackEnd[boxOneI][boxOneJ]) {
                    case BOX_RIPE_TOP: {
                        randomI--;
                        break;
                    }
                    case BOX_RIPE_BOTTOM: {
                        randomI++;
                        break;
                    }
                    case BOX_RIPE_LEFT: {
                        randomJ--;
                        break;
                    }
                    case BOX_RIPE_RIGHT: {
                        randomJ++;
                        break;
                    }
                }

                return GRID_TYPE.BOX_RIPE;

            }

        }

        return GRID_TYPE.BOX_SAFE;

         if(GRID_TYPE.isRipe(boxOneType))
        {
            randomI=boxOneI;
            randomJ=boxOneJ;
            boxI=boxOneI;
            boxJ=boxOneJ;
            switch (gridBackEnd[boxOneI][boxOneJ])
            {
                case BOX_RIPE_TOP:
                {
                    randomI--;
                    break;
                }
                case BOX_RIPE_BOTTOM:
                {
                    randomI++;
                    break;
                }
                case BOX_RIPE_LEFT:
                {
                    randomJ--;
                    break;
                }
                case BOX_RIPE_RIGHT:
                {
                    randomJ++;
                    break;
                }
            }
            //fillBox(boxOneI,boxOneJ,0);
            return NOTIFICATION.RIPE;
        }
        if(GRID_TYPE.isRipe(boxTwoType))
        {
            randomI=boxTwoI;
            randomJ=boxTwoJ;
            boxI=boxTwoI;
            boxJ=boxTwoJ;
            switch (gridBackEnd[boxTwoI][boxTwoJ])
            {
                case BOX_RIPE_TOP:
                {
                    randomI--;
                    break;
                }
                case BOX_RIPE_BOTTOM:
                {
                    randomI++;
                    break;
                }
                case BOX_RIPE_LEFT:
                {
                    randomJ--;
                    break;
                }
                case BOX_RIPE_RIGHT:
                {
                    randomJ++;
                    break;
                }
            }
            //Toast.makeText(this,"2ripe",Toast.LENGTH_SHORT).show();
            //fillBox(boxTwoI,boxTwoJ,0);
            return NOTIFICATION.RIPE;
        }

        */

            /*if(boxOneType==GRID_TYPE.BOX_UNSAFE)
            {
                if(boxTwoType==GRID_TYPE.BOX_UNSAFE || boxTwoType==GRID_TYPE.OUT_OF_BOUNDS)
                {
                    return NOTIFICATION.UNSAFE;
                }
                else if(boxTwoType==GRID_TYPE.BOX_SAFE)
                {
                    return NOTIFICATION.SAFE_UNSAFE;
                }
            }
            if (boxOneType == GRID_TYPE.BOX_UNSAFE && boxTwoType == GRID_TYPE.BOX_UNSAFE)
            {
                return NOTIFICATION.UNSAFE;
            }
            if ((boxOneType == GRID_TYPE.BOX_SAFE && boxTwoType == GRID_TYPE.BOX_UNSAFE) || (boxOneType == GRID_TYPE.BOX_UNSAFE && boxTwoType == GRID_TYPE.BOX_SAFE))
            {
                //gridBackEnd[barI][barJ]=BAR_SAFE_UNSAFE;
                return BOX_SAFE_UNSAFE;
            }
            if(boxOneType!=GRID_TYPE.OUT_OF_BOUNDS && boxTwoType!=GRID_TYPE.OUT_OF_BOUNDS)
            {
                if (boxOneType == BOX_UNSAFE && boxTwoType == BOX_UNSAFE)
                {
                    return BOX_UNSAFE;
                }
                if ((boxOneType == BOX_SAFE && boxTwoType == BOX_UNSAFE) || (boxOneType == BOX_UNSAFE && boxTwoType == BOX_SAFE))
                {
                    //gridBackEnd[barI][barJ]=BAR_SAFE_UNSAFE;
                    return BOX_SAFE_UNSAFE;
                }
            }
            else
            {
                if (boxOneType == BOX_UNSAFE)
                {
                    return BOX_UNSAFE;
                }
                if (boxTwoType == BOX_UNSAFE)
                {
                    return BOX_UNSAFE;
                }
            }*/
    }

    public void easyAI()
    {
        Log.d("EASY","EASY");

        int previousPlayerScore;
        do
        {
            if(gameOver)
            {
                break;
            }
            previousPlayerScore = gameDetails.getPlayerScore(cP.get());
            do
            {
                getBar(Grid);
                if(gameOver)
                {
                    break;
                }
            }while (GRID_TYPE.isBarFilled(Grid[barI][barJ].gridBackEnd));

            if(!gameOver)
            {
                aiFillBoxGeneral(barI,barJ,Grid);
            }

        }while (gameDetails.getPlayerScore(cP.get())!=previousPlayerScore);

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(" AI fill Bar CP ", cP.get() + "");
                updateCurrentPlayer();
                mCallback.updateScoreBarSmallFragment(cP);
                Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
            }
        });

        if(gameOver)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    onGameFinished();
                }
            });
            Log.d("SAVED","Consider your asses saved");
        }
    }

    boolean gameOver=false;
    public void mediumAI(int lastI, int lastJ)
    {
        while (true)
        {
            probe(lastI, lastJ,Grid);
            if(!GridElement.ripeI.isEmpty())
            {
                lastI=GridElement.ripeI.remove(0);
                lastJ=GridElement.ripeJ.remove(0);
                aiFillBoxGeneral(lastI, lastJ, Grid);
            }
            else
            {
                break;
            }

        }



        if (GridElement.safeCount>0)
        {
            do {
                getBar(Grid);
                if (GridElement.safeCount<=0)
                {
                    do
                    {

                        getBar(Grid);
                        if(gameOver)
                            break;


                    } while (!GRID_TYPE.isBarUnsafe(Grid[barI][barJ].gridBackEnd));
                    break;
                }
            } while (!GRID_TYPE.isBarSafe(Grid[barI][barJ].gridBackEnd));
        }
        else
        {
            do
            {
                getBar(Grid);
                if(gameOver)
                    break;

            } while (!GRID_TYPE.isBarUnsafe(Grid[barI][barJ].gridBackEnd));

        }
        if(!gameOver)
        {
            aiFillBar(barI, barJ, Grid,0);
        }
        else
        {
            onGameFinished();
        }

    }

    ArrayList<cluster> openClusters;
    public void hardAI(int lastI, int lastJ)
    {
        while (true)
        {
            probe(lastI, lastJ,Grid);
            if(!GridElement.ripeI.isEmpty())
            {
                //Log.d("Ripe ", "("+GridElement.ripeI.get(0)+","+GridElement.ripeJ.get(0)+")");
                lastI=GridElement.ripeI.remove(0);
                lastJ=GridElement.ripeJ.remove(0);
                aiFillBoxGeneral(lastI, lastJ,Grid);
            }
            else
            {
                break;
            }

        }





        if (GridElement.safeCount>0)
        {
            do
            {
                getBar(Grid);
                if (GridElement.safeCount<=0)
                {
                    createAICluster(Grid);
                    getClusterBar();
                    break;
                }
            } while (!GRID_TYPE.isBarSafe(Grid[barI][barJ].gridBackEnd));
        }
        else
        {
            if (GridElement.safeCount<=0)
            {

                createAICluster(Grid);
                if(!gameOver)
                {
                    getClusterBar();
                }
                else
                {
                    onGameFinished();
                    Log.d("SAVED","Consider your asses saved");
                    return;
                }
            }

        }
        aiFillBar(barI, barJ,Grid,0);

    }

    int safeI=-1;
    int safeJ=-1;
    int penultimoSafeI=-1;
    int penultimoSafeJ=-1;

    public void titanAI(int lastI,int lastJ)
    {
        boolean restrain=false;
        //Toast.makeText(this,"titan",Toast.LENGTH_SHORT).show();
        if(GridElement.safeCount<=0 && !GridElement.ripeI.isEmpty())
        {
            createAIRipeCluster(lastI,lastJ);
        }
        while (true)
        {
            probe(lastI, lastJ, Grid);
            if(!GridElement.ripeI.isEmpty())
            {
                lastI=GridElement.ripeI.remove(0);
                lastJ=GridElement.ripeJ.remove(0);
                if(lastI==penultimoSafeI && lastJ==penultimoSafeJ)
                {
                    restrain=true;
                }
                else
                {
                    aiFillBoxGeneral(lastI, lastJ, Grid);
                }
            }
            else
            {
                break;
            }

        }

        if(restrain==true)
        {
            aiFillBar(safeI, safeJ, Grid, 0);
            setRipeBar(penultimoSafeI,penultimoSafeJ);
            return;
        }



        if (GridElement.safeCount>0)
        {
            do
            {
                getBar(Grid);
                if (GridElement.safeCount<=0)
                {
                    createAICluster(Grid);
                    getClusterBar();
                    break;
                }
            } while (!GRID_TYPE.isBarSafe(Grid[barI][barJ].gridBackEnd));
        }
        else
        {
            if (GridElement.safeCount<=0)
            {

                createAICluster(Grid);
                if(!gameOver)
                {
                    getClusterBar();
                }
                else
                {
                    onGameFinished();
                    Log.d("SAVED","Consider your asses saved");
                    return;
                }


            }

        }
        aiFillBar(barI, barJ,Grid,0);

    }

    public void createAIRipeCluster(int barI,int barJ)
    {

        ArrayList<Integer> ripeICopy=new ArrayList();
        ArrayList<Integer> ripeJCopy=new ArrayList();

        for(int i=0;i<GridElement.ripeI.size();i++)
        {
            ripeICopy.add(GridElement.ripeI.get(i));
            ripeJCopy.add(GridElement.ripeJ.get(i));
        }

        int playerScoreCopy=gameDetails.getPlayerScore(cP.get());
        int totalScoreCopy=gameDetails.getTotalScore();

        int lowerICopy=lowerI;
        int lowerJCopy=lowerJ;

        int upperICopy=upperI;
        int upperJCopy=upperJ;

        int filledCountRowCopy[]=new int[filledCountRow.length];
        int filledCountColCopy[]=new int[filledCountCol.length];

        safeI=-1;
        safeJ=-1;

        penultimoSafeI=-1;
        penultimoSafeJ=-1;

        GridElement[][] tempGrid = new GridElement[Grid.length][Grid[0].length];
        for(int i=0;i<tempGrid.length;i++)
        {
            for(int j=0;j<tempGrid[0].length;j++)
            {
                tempGrid[i][j]=new GridElement();
                tempGrid[i][j].gridBackEnd=Grid[i][j].gridBackEnd;
            }
        }
        tempGrid[0][0].gridBackEnd=GRID_TYPE.TEMP_2;
        printLog("BEFORE RIPE CLUSTER TEMP", tempGrid);

        while (true)
        {
            probe(barI, barJ, tempGrid);
            if(!GridElement.ripeI.isEmpty())
            {
                Log.d("Ripe ", "("+GridElement.ripeI.get(0)+","+GridElement.ripeJ.get(0)+")");
                barI=GridElement.ripeI.remove(0);
                barJ=GridElement.ripeJ.remove(0);
                aiFillBoxGeneral(barI, barJ,tempGrid);
                printLog("RIPE CLUSTER", tempGrid);
            }
            else
            {
                Log.d("Broken","Broken");
                break;
            }

        }
        createAICluster(tempGrid);

        Log.d("total score", gameDetails.getTotalScore() + "");

        if(safeI!=-1 && safeJ!=-1)
        {
            if(!gameDetails.isGameOver(-1))
            {
                Log.d(" leave","("+penultimoSafeI+","+penultimoSafeJ+")");
                Log.d(" fill","("+safeI+","+safeJ+")");
            }
            else
            {
                safeI=-1;
                safeJ=-1;

                penultimoSafeI=-1;
                penultimoSafeJ=-1;
            }
        }
        else
        {
            safeI=-1;
            safeJ=-1;

            penultimoSafeI=-1;
            penultimoSafeJ=-1;
        }


        gameDetails.updatePlayerScore(playerScoreCopy,cP.get());
        gameDetails.setTotalScore(totalScoreCopy);

        lowerI = lowerICopy;
        lowerJ = lowerJCopy;

        upperI=upperICopy;
        upperJ=upperJCopy;

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRow[i]=filledCountRowCopy[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountCol[i]=filledCountColCopy[i];
        }

        GridElement.ripeI.clear();
        GridElement.ripeJ.clear();

        for(int i=0;i<ripeICopy.size();i++)
        {
            GridElement.ripeI.add(ripeICopy.get(i));
            GridElement.ripeJ.add(ripeJCopy.get(i));
        }
        //Log.d("Done","Done");

    }
    
    {/*
    public void createRipeCluster(int barI,int barJ)
    {

        ArrayList<Integer> ripeICopy=new ArrayList();
        ArrayList<Integer> ripeJCopy=new ArrayList();

        for(int i=0;i<GridElement.ripeI.size();i++)
        {
            ripeICopy.add(GridElement.ripeI.get(i));
            ripeJCopy.add(GridElement.ripeJ.get(i));
        }
        
        int playerTwoScoreCopy=gameDetails.playerTwoScore;

        int lowerICopy=lowerI;
        int lowerJCopy=lowerJ;

        int upperICopy=upperI;
        int upperJCopy=upperJ;

        int filledCountRowCopy[]=new int[filledCountRow.length];
        int filledCountColCopy[]=new int[filledCountCol.length];

        safeI=-1;
        safeJ=-1;

        penultimoSafeI=-1;
        penultimoSafeJ=-1;

        GridElement[][] tempGrid = new GridElement[Grid.length][Grid[0].length];
        for(int i=0;i<tempGrid.length;i++)
        {
            for(int j=0;j<tempGrid[0].length;j++)
            {
                tempGrid[i][j]=new GridElement();
                tempGrid[i][j].gridBackEnd=Grid[i][j].gridBackEnd;

            }
        }
        tempGrid[0][0].gridBackEnd=GRID_TYPE.TEMP_2;
        printLog("BEFORE RIPE CLUSTER TEMP", tempGrid);

        while (true)
        {
            probe(barI, barJ, tempGrid);
            if(!GridElement.ripeI.isEmpty())
            {
                Log.d("Ripe ", "("+GridElement.ripeI.get(0)+","+GridElement.ripeJ.get(0)+")");
                barI=GridElement.ripeI.remove(0);
                barJ=GridElement.ripeJ.remove(0);
                aiFillBoxGeneral(barI, barJ,tempGrid);
                printLog("RIPE CLUSTER", tempGrid);
            }
            else
            {
                Log.d("Broken","Broken");
                break;
            }

        }
        createAICluster(tempGrid);

        Log.d("total score", gameDetails.playerOneScore + gameDetails.playerTwoScore + "");

        //Log.d("min clusters count", getMinClusterCount() + "");


        if(safeI!=-1 && safeJ!=-1)
        {
            //try
            {
                if(gameDetails.playerOneScore+gameDetails.playerTwoScore<((gameDetails.dotRowCount-1)*(gameDetails.dotColCount-1)) && getMinClusterCount()>2)
                {
                    Log.d(" leave","("+penultimoSafeI+","+penultimoSafeJ+")");
                    Log.d(" fill","("+safeI+","+safeJ+")");
                }
                else
                {
                    safeI=-1;
                    safeJ=-1;

                    penultimoSafeI=-1;
                    penultimoSafeJ=-1;
                }
            }
            //catch (Exception e)
            {
                //return;
            }

        }
        else
        {
            safeI=-1;
            safeJ=-1;

            penultimoSafeI=-1;
            penultimoSafeJ=-1;
        }


        gameDetails.playerTwoScore=playerTwoScoreCopy;

        lowerI = lowerICopy;
        lowerJ = lowerJCopy;

        upperI=upperICopy;
        upperJ=upperJCopy;

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRow[i]=filledCountRowCopy[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountCol[i]=filledCountColCopy[i];
        }

        GridElement.ripeI.clear();
        GridElement.ripeJ.clear();

        for(int i=0;i<ripeICopy.size();i++)
        {
            GridElement.ripeI.add(ripeICopy.get(i));
            GridElement.ripeJ.add(ripeJCopy.get(i));
        }
        //Log.d("Done","Done");

    }*/}
    
    public void getClusterBar()
    {
        cluster minCluster=openClusters.get(0);
        int minPos=0;
        for(int i=0;i<openClusters.size();i++)
        {
            if(openClusters.get(i).count< minCluster.count)
            {
                minCluster=openClusters.get(i);
                minPos=i;
            }
        }
        barI=minCluster.i;
        barJ=minCluster.j;
    }
    
    public int getMinClusterCount()
    {
        cluster minCluster=openClusters.get(0);
        for(int i=1;i<openClusters.size();i++)
        {
            if(openClusters.get(i).count< minCluster.count)
            {
                minCluster=openClusters.get(i);
            }
        }
        return minCluster.count;
    }

    public void createAICluster(GridElement[][] Grid)
    {
        int lowerICopy=lowerI;
        int lowerJCopy=lowerJ;

        int upperICopy=upperI;
        int upperJCopy=upperJ;

        int filledCountRowCopy[]=new int[filledCountRow.length];
        int filledCountColCopy[]=new int[filledCountCol.length];

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRowCopy[i]=filledCountRow[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountColCopy[i]=filledCountCol[i];
        }

        int playerScoreCopy=gameDetails.getPlayerScore(cP.get());
        int totalScoreCopy=gameDetails.getTotalScore();

        int safeUnsafeCount=0;

        GridElement[][] tempGrid = new GridElement[Grid.length][Grid[0].length];

        for(int i=0;i<tempGrid.length;i++)
        {
            for(int j=0;j<tempGrid[0].length;j++)
            {
                tempGrid[i][j]=new GridElement();
                tempGrid[i][j].gridBackEnd=Grid[i][j].gridBackEnd;
                if(Grid[i][j].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    safeUnsafeCount++;
                }
            }
        }
        tempGrid[0][0].gridBackEnd=GRID_TYPE.TEMP;
        openClusters=new ArrayList();


        String cluster="x\n";


        do
        {
            do
            {
                getBar(tempGrid);
                if(gameOver)
                {
                    return;
                }

            } while (!GRID_TYPE.isBarUnsafe(tempGrid[barI][barJ].gridBackEnd));

            aiFillBar(barI, barJ, tempGrid,0);

            openClusters.add(new cluster());
            openClusters.get(openClusters.size()-1).i=barI;
            openClusters.get(openClusters.size()-1).j=barJ;

            while (true)
            {
                probe(barI, barJ, tempGrid);
                if(!GridElement.ripeI.isEmpty())
                {
                    barI=GridElement.ripeI.remove(0);
                    barJ=GridElement.ripeJ.remove(0);
                    aiFillBoxGeneral(barI, barJ,tempGrid);
                    openClusters.get(openClusters.size()-1).count++;
                }
                else
                {
                    break;
                }

            }

            if(openClusters.get(openClusters.size()-1).count==0)
            {
                openClusters.remove(openClusters.size() - 1);
            }

            while (GridElement.safeBoxI.size()!=0)
            {
                int safeBoxI=GridElement.safeBoxI.remove(0);
                int safeBoxJ=GridElement.safeBoxJ.remove(0);
                if(tempGrid[safeBoxI][safeBoxJ].gridBackEnd==GRID_TYPE.BOX_RIPE)
                {
                    openClusters.remove(openClusters.size()-1);
                }
                {
                    tempGrid[safeBoxI][safeBoxJ].gridBackEnd=Grid[safeBoxI][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI+1][safeBoxJ].gridBackEnd=Grid[safeBoxI+1][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI-1][safeBoxJ].gridBackEnd=Grid[safeBoxI-1][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI][safeBoxJ+1].gridBackEnd=Grid[safeBoxI][safeBoxJ+1].gridBackEnd;
                    tempGrid[safeBoxI][safeBoxJ-1].gridBackEnd=Grid[safeBoxI][safeBoxJ-1].gridBackEnd;
                }
            }

        }while (!gameDetails.isGameOver(safeUnsafeCount));

        Log.d("cluster Size", openClusters.size()+"");

        for(int i=0;i<openClusters.size();i++)
        {
            cluster += "Cluster "+(i+1)+" :\n Cluster Count = " + openClusters.get(i).count+"\ni = "+openClusters.get(i).i+"\nj = "+openClusters.get(i).j+"\n";
        }
        Log.d("cluster", cluster);

        gameDetails.updatePlayerScore(playerScoreCopy, cP.get());
        gameDetails.setTotalScore(totalScoreCopy);

        lowerI = lowerICopy;
        lowerJ = lowerJCopy;

        upperI=upperICopy;
        upperJ=upperJCopy;

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRow[i]=filledCountRowCopy[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountCol[i]=filledCountColCopy[i];
        }
    }
    {/*
    public void createAICluster(GridElement[][] Grid)
    {
        int lowerICopy=lowerI;
        int lowerJCopy=lowerJ;

        int upperICopy=upperI;
        int upperJCopy=upperJ;

        int filledCountRowCopy[]=new int[filledCountRow.length];
        int filledCountColCopy[]=new int[filledCountCol.length];

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRowCopy[i]=filledCountRow[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountColCopy[i]=filledCountCol[i];
        }

        int playerTwoScoreCopy=gameDetails.playerTwoScore;

        int safeUnsafeCount=0;

        GridElement[][] tempGrid = new GridElement[Grid.length][Grid[0].length];

        for(int i=0;i<tempGrid.length;i++)
        {
            for(int j=0;j<tempGrid[0].length;j++)
            {
                tempGrid[i][j]=new GridElement();
                tempGrid[i][j].gridBackEnd=Grid[i][j].gridBackEnd;
                if(Grid[i][j].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    safeUnsafeCount++;
                }
                //tempGrid[i][j].gridFrontEnd.setImageDrawable(Grid[i][j].gridFrontEnd.getDrawable());
            }
        }
        tempGrid[0][0].gridBackEnd=GRID_TYPE.TEMP;
        openClusters=new ArrayList();

        String cluster="x\n";

        do
        {
            do
            {
                //try
                {
                    getBar(tempGrid);
                }
                //catch (Exception e)
                {
                    //return;
                }

                if(gameOver)
                {
                    //printLog("Its Over ", tempGrid);
                    return;
                }

                //Log.d("LOWER AI","("+lowerI+","+lowerJ+")");
                //Log.d("HIGHR AI","("+upperI+","+upperJ+")");
                //Log.d("Clustered","("+barI+","+barJ+")");

            } while (!GRID_TYPE.isBarUnsafe(tempGrid[barI][barJ].gridBackEnd));

            //Log.d("Unsafe Bar", (barI + "," + barJ));

            aiFillBar(barI, barJ, tempGrid,0);
            //printLog("Cluster Bar Fill ", tempGrid);

            openClusters.add(new cluster());
            openClusters.get(openClusters.size()-1).i=barI;
            openClusters.get(openClusters.size()-1).j=barJ;

            for(int i=0;i<GridElement.ripeI.size();i++)
            {
                //Log.d("Ripe Preview ", "("+GridElement.ripeI.get(i)+","+GridElement.ripeJ.get(i)+")");
            }
            while (true)
            {
                probe(barI, barJ, tempGrid);
                if(!GridElement.ripeI.isEmpty())
                {
                    //Log.d("Ripe ", "("+GridElement.ripeI.get(0)+","+GridElement.ripeJ.get(0)+")");
                    barI=GridElement.ripeI.remove(0);
                    barJ=GridElement.ripeJ.remove(0);
                    aiFillBoxGeneral(barI, barJ,tempGrid);
                    openClusters.get(openClusters.size()-1).count++;
                    //print//Log("Cluster Box Fill ",tempGrid);

                }
                else
                {
                    break;
                }

            }

            if(openClusters.get(openClusters.size()-1).count==0)
            {
                openClusters.remove(openClusters.size() - 1);
            }

            while (GridElement.safeBoxI.size()!=0)
            {
                int safeBoxI=GridElement.safeBoxI.remove(0);
                int safeBoxJ=GridElement.safeBoxJ.remove(0);
                if(tempGrid[safeBoxI][safeBoxJ].gridBackEnd==GRID_TYPE.BOX_RIPE)
                {
                    openClusters.remove(openClusters.size()-1);
                }


                {
                    tempGrid[safeBoxI][safeBoxJ].gridBackEnd=Grid[safeBoxI][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI+1][safeBoxJ].gridBackEnd=Grid[safeBoxI+1][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI-1][safeBoxJ].gridBackEnd=Grid[safeBoxI-1][safeBoxJ].gridBackEnd;
                    tempGrid[safeBoxI][safeBoxJ+1].gridBackEnd=Grid[safeBoxI][safeBoxJ+1].gridBackEnd;
                    tempGrid[safeBoxI][safeBoxJ-1].gridBackEnd=Grid[safeBoxI][safeBoxJ-1].gridBackEnd;
                }
            }

            //cluster += "Cluster "+(openClusters.size()-1+1)+" :\n Cluster Count = " + openClusters.get(openClusters.size()-1).count+"\ni = "+openClusters.get(openClusters.size()-1).i+"\nj = "+openClusters.get(openClusters.size()-1).j+"\n";
            ////Log.d("cluster",cluster);


        }while (gameDetails.playerOneScore+gameDetails.playerTwoScore+safeUnsafeCount<((gameDetails.dotRowCount-1)*(gameDetails.dotColCount-1)));

        Log.d("cluster Size", openClusters.size()+"");

        for(int i=0;i<openClusters.size();i++)
        {
            cluster += "Cluster "+(i+1)+" :\n Cluster Count = " + openClusters.get(i).count+"\ni = "+openClusters.get(i).i+"\nj = "+openClusters.get(i).j+"\n";
        }
        Log.d("cluster", cluster);


        gameDetails.playerTwoScore=playerTwoScoreCopy;

        lowerI = lowerICopy;
        lowerJ = lowerJCopy;

        upperI=upperICopy;
        upperJ=upperJCopy;

        for(int i=0;i<filledCountRow.length;i++)
        {
            filledCountRow[i]=filledCountRowCopy[i];
        }
        for(int i=0;i<filledCountCol.length;i++)
        {
            filledCountCol[i]=filledCountColCopy[i];
        }


    }*/}

    public boolean isSurroundingSafe(int boxI,int boxJ,GridElement[][] Grid)
    {
        int filledCount=0;
        if(boxI==1)
        {
            if(boxJ==1)
            {
                if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            else if(boxJ==Grid[0].length-2)
            {
                if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            else
            {
                if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
                if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            if(Grid[boxI+2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
        }
        else if(boxI==Grid.length-2)
        {
            if(boxJ==1)
            {
                if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            else if(boxJ==Grid[0].length-2)
            {
                if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            else
            {
                if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
                if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    filledCount++;
                }
            }
            if(Grid[boxI-2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
        }
        else if(boxJ==1)
        {
            if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI-2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI+2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
        }
        else if(boxJ==Grid[0].length-2)
        {
            if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI-2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI+2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
        }
        else
        {
            if(Grid[boxI][boxJ+2].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI][boxJ-2].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI-2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
            if(Grid[boxI+2][boxJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
            {
                filledCount++;
            }
        }
        if(filledCount>=2)
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public boolean isSafeUnsafe(int barI,int barJ,GridElement[][] Grid)
    {
        if(GRID_TYPE.isBarHorizontal(Grid[barI][barJ].gridBackEnd))
        {
            if(GRID_TYPE.isBarBottomHorizontal(Grid[barI][barJ].gridBackEnd) || GRID_TYPE.isBarTopHorizontal(Grid[barI][barJ].gridBackEnd))
            {
                return false;
            }
            else
            {
                if(Grid[barI-1][barJ].gridBackEnd==GRID_TYPE.BOX_SAFE || Grid[barI+1][barJ].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
        }
        else {
            if (GRID_TYPE.isBarLeftVertical(Grid[barI][barJ].gridBackEnd) || GRID_TYPE.isBarRightVertical(Grid[barI][barJ].gridBackEnd))
            {
                return false;
            }
            else
            {
                if(Grid[barI][barJ-1].gridBackEnd==GRID_TYPE.BOX_SAFE || Grid[barI][barJ+1].gridBackEnd==GRID_TYPE.BOX_SAFE)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
        }
    }

    {/*if (GridElement.ripeIOne != -1 && GridElement.ripeJOne != -1)
            {
                lastI = GridElement.ripeIOne;
                lastJ = GridElement.ripeJOne;

                GridElement.ripeIOne = -1;
                GridElement.ripeJOne = -1;

                //Log.d("Ripe I", "("+lastI+","+lastJ+")");
                aiFillBoxGeneral(lastI, lastJ);
            }
            else if (GridElement.ripeITwo != -1 && GridElement.ripeJTwo != -1)
            {
                lastI = GridElement.ripeITwo;
                lastJ = GridElement.ripeJTwo;

                GridElement.ripeITwo = -1;
                GridElement.ripeJTwo = -1;

                //Log.d("Ripe II", "("+lastI+","+lastJ+")");
                aiFillBoxGeneral(lastI, lastJ);
            }
            else if(!GridElement.extraRipeI.isEmpty() && !GridElement.extraRipeJ.isEmpty())
            {
                GridElement.ripeIOne=GridElement.extraRipeI.get(0);
                GridElement.ripeJOne=GridElement.extraRipeJ.get(0);
                GridElement.extraRipeI.remove(0);
                GridElement.extraRipeJ.remove(0);
            }
            else
            {
                break;
            }*/}

    public void printLog(String Tag,GridElement[][] Grid)
    {

        if(true)
            return;

        Log.d("Log space Count", GridElement.safeCount + "");



        String space="            ";
        String space1="    ";
        Log.d
                ("-------------" + Tag + "-----------",

                        "x\nx\n" +
                                gridBackEnd(Grid[0][0].gridBackEnd) +
                                gridBackEnd(Grid[0][1].gridBackEnd) +
                                gridBackEnd(Grid[0][1].gridBackEnd) +
                                gridBackEnd(Grid[0][1].gridBackEnd) +
                                gridBackEnd(Grid[0][2].gridBackEnd) +
                                gridBackEnd(Grid[0][3].gridBackEnd) +
                                gridBackEnd(Grid[0][3].gridBackEnd) +
                                gridBackEnd(Grid[0][3].gridBackEnd) +
                                gridBackEnd(Grid[0][4].gridBackEnd) +
                                gridBackEnd(Grid[0][5].gridBackEnd) +
                                gridBackEnd(Grid[0][5].gridBackEnd) +
                                gridBackEnd(Grid[0][5].gridBackEnd) +
                                gridBackEnd(Grid[0][6].gridBackEnd) +
                                gridBackEnd(Grid[0][7].gridBackEnd) +
                                gridBackEnd(Grid[0][7].gridBackEnd) +
                                gridBackEnd(Grid[0][7].gridBackEnd) +
                                gridBackEnd(Grid[0][8].gridBackEnd) +
                                gridBackEnd(Grid[0][9].gridBackEnd) +
                                gridBackEnd(Grid[0][9].gridBackEnd) +
                                gridBackEnd(Grid[0][9].gridBackEnd) +
                                gridBackEnd(Grid[0][10].gridBackEnd) +

                                space + "x\n" + gridBackEnd(Grid[1][0].gridBackEnd) + space + gridBackEnd(Grid[1][2].gridBackEnd) + space + gridBackEnd(Grid[1][4].gridBackEnd) + space + gridBackEnd(Grid[1][6].gridBackEnd) + space + gridBackEnd(Grid[1][8].gridBackEnd) + space + gridBackEnd(Grid[1][10].gridBackEnd) + space +
                                "x\n" + gridBackEnd(Grid[1][0].gridBackEnd) + space1 + gridBackEnd(Grid[1][1].gridBackEnd) + space1 + gridBackEnd(Grid[1][2].gridBackEnd) + space1 + gridBackEnd(Grid[1][3].gridBackEnd) + space1 + gridBackEnd(Grid[1][4].gridBackEnd) + space1 + gridBackEnd(Grid[1][5].gridBackEnd) + space1 + gridBackEnd(Grid[1][6].gridBackEnd) + space1 + gridBackEnd(Grid[1][7].gridBackEnd) + space1 + gridBackEnd(Grid[1][8].gridBackEnd) + space1 + gridBackEnd(Grid[1][9].gridBackEnd) + space1 + gridBackEnd(Grid[1][10].gridBackEnd) +
                                space + "x\n" + gridBackEnd(Grid[1][0].gridBackEnd) + space + gridBackEnd(Grid[1][2].gridBackEnd) + space + gridBackEnd(Grid[1][4].gridBackEnd) + space + gridBackEnd(Grid[1][6].gridBackEnd) + space + gridBackEnd(Grid[1][8].gridBackEnd) + space + gridBackEnd(Grid[1][10].gridBackEnd) + space +

                                "x\n" +
                                gridBackEnd(Grid[2][0].gridBackEnd) +
                                gridBackEnd(Grid[2][1].gridBackEnd) +
                                gridBackEnd(Grid[2][1].gridBackEnd) +
                                gridBackEnd(Grid[2][1].gridBackEnd) +
                                gridBackEnd(Grid[2][2].gridBackEnd) +
                                gridBackEnd(Grid[2][3].gridBackEnd) +
                                gridBackEnd(Grid[2][3].gridBackEnd) +
                                gridBackEnd(Grid[2][3].gridBackEnd) +
                                gridBackEnd(Grid[2][4].gridBackEnd) +
                                gridBackEnd(Grid[2][5].gridBackEnd) +
                                gridBackEnd(Grid[2][5].gridBackEnd) +
                                gridBackEnd(Grid[2][5].gridBackEnd) +
                                gridBackEnd(Grid[2][6].gridBackEnd) +
                                gridBackEnd(Grid[2][7].gridBackEnd) +
                                gridBackEnd(Grid[2][7].gridBackEnd) +
                                gridBackEnd(Grid[2][7].gridBackEnd) +
                                gridBackEnd(Grid[2][8].gridBackEnd) +
                                gridBackEnd(Grid[2][9].gridBackEnd) +
                                gridBackEnd(Grid[2][9].gridBackEnd) +
                                gridBackEnd(Grid[2][9].gridBackEnd) +
                                gridBackEnd(Grid[2][10].gridBackEnd) +

                                space + "x\n" + gridBackEnd(Grid[3][0].gridBackEnd) + space + gridBackEnd(Grid[3][2].gridBackEnd) + space + gridBackEnd(Grid[3][4].gridBackEnd) + space + gridBackEnd(Grid[3][6].gridBackEnd) + space + gridBackEnd(Grid[3][8].gridBackEnd) + space + gridBackEnd(Grid[3][10].gridBackEnd) + space +
                                "x\n" + gridBackEnd(Grid[3][0].gridBackEnd) + space1 + gridBackEnd(Grid[3][1].gridBackEnd) + space1 + gridBackEnd(Grid[3][2].gridBackEnd) + space1 + gridBackEnd(Grid[3][3].gridBackEnd) + space1 + gridBackEnd(Grid[3][4].gridBackEnd) + space1 + gridBackEnd(Grid[3][5].gridBackEnd) + space1 + gridBackEnd(Grid[3][6].gridBackEnd) + space1 + gridBackEnd(Grid[3][7].gridBackEnd) + space1 + gridBackEnd(Grid[3][8].gridBackEnd) + space1 + gridBackEnd(Grid[3][9].gridBackEnd) + space1 + gridBackEnd(Grid[3][10].gridBackEnd) +
                                space + "x\n" + gridBackEnd(Grid[3][0].gridBackEnd) + space + gridBackEnd(Grid[3][2].gridBackEnd) + space + gridBackEnd(Grid[3][4].gridBackEnd) + space + gridBackEnd(Grid[3][6].gridBackEnd) + space + gridBackEnd(Grid[3][8].gridBackEnd) + space + gridBackEnd(Grid[3][10].gridBackEnd) + space +

                                "x\n" +
                                gridBackEnd(Grid[4][0].gridBackEnd) +
                                gridBackEnd(Grid[4][1].gridBackEnd) +
                                gridBackEnd(Grid[4][1].gridBackEnd) +
                                gridBackEnd(Grid[4][1].gridBackEnd) +
                                gridBackEnd(Grid[4][2].gridBackEnd) +
                                gridBackEnd(Grid[4][3].gridBackEnd) +
                                gridBackEnd(Grid[4][3].gridBackEnd) +
                                gridBackEnd(Grid[4][3].gridBackEnd) +
                                gridBackEnd(Grid[4][4].gridBackEnd) +
                                gridBackEnd(Grid[4][5].gridBackEnd) +
                                gridBackEnd(Grid[4][5].gridBackEnd) +
                                gridBackEnd(Grid[4][5].gridBackEnd) +
                                gridBackEnd(Grid[4][6].gridBackEnd) +
                                gridBackEnd(Grid[4][7].gridBackEnd) +
                                gridBackEnd(Grid[4][7].gridBackEnd) +
                                gridBackEnd(Grid[4][7].gridBackEnd) +
                                gridBackEnd(Grid[4][8].gridBackEnd) +
                                gridBackEnd(Grid[4][9].gridBackEnd) +
                                gridBackEnd(Grid[4][9].gridBackEnd) +
                                gridBackEnd(Grid[4][9].gridBackEnd) +
                                gridBackEnd(Grid[4][10].gridBackEnd) +

                                space + "x\n" + gridBackEnd(Grid[5][0].gridBackEnd) + space + gridBackEnd(Grid[5][2].gridBackEnd) + space + gridBackEnd(Grid[5][4].gridBackEnd) + space + gridBackEnd(Grid[5][6].gridBackEnd) + space + gridBackEnd(Grid[5][8].gridBackEnd) + space + gridBackEnd(Grid[5][10].gridBackEnd) + space +
                                "x\n" + gridBackEnd(Grid[5][0].gridBackEnd) + space1 + gridBackEnd(Grid[5][1].gridBackEnd) + space1 + gridBackEnd(Grid[5][2].gridBackEnd) + space1 + gridBackEnd(Grid[5][3].gridBackEnd) + space1 + gridBackEnd(Grid[5][4].gridBackEnd) + space1 + gridBackEnd(Grid[5][5].gridBackEnd) + space1 + gridBackEnd(Grid[5][6].gridBackEnd) + space1 + gridBackEnd(Grid[5][7].gridBackEnd) + space1 + gridBackEnd(Grid[5][8].gridBackEnd) + space1 + gridBackEnd(Grid[5][9].gridBackEnd) + space1 + gridBackEnd(Grid[5][10].gridBackEnd) +
                                space + "x\n" + gridBackEnd(Grid[5][0].gridBackEnd) + space + gridBackEnd(Grid[5][2].gridBackEnd) + space + gridBackEnd(Grid[5][4].gridBackEnd) + space + gridBackEnd(Grid[5][6].gridBackEnd) + space + gridBackEnd(Grid[5][8].gridBackEnd) + space + gridBackEnd(Grid[5][10].gridBackEnd) + space +

                                "x\n" +
                                gridBackEnd(Grid[6][0].gridBackEnd) +
                                gridBackEnd(Grid[6][1].gridBackEnd) +
                                gridBackEnd(Grid[6][1].gridBackEnd) +
                                gridBackEnd(Grid[6][1].gridBackEnd) +
                                gridBackEnd(Grid[6][2].gridBackEnd) +
                                gridBackEnd(Grid[6][3].gridBackEnd) +
                                gridBackEnd(Grid[6][3].gridBackEnd) +
                                gridBackEnd(Grid[6][3].gridBackEnd) +
                                gridBackEnd(Grid[6][4].gridBackEnd) +
                                gridBackEnd(Grid[6][5].gridBackEnd) +
                                gridBackEnd(Grid[6][5].gridBackEnd) +
                                gridBackEnd(Grid[6][5].gridBackEnd) +
                                gridBackEnd(Grid[6][6].gridBackEnd) +
                                gridBackEnd(Grid[6][7].gridBackEnd) +
                                gridBackEnd(Grid[6][7].gridBackEnd) +
                                gridBackEnd(Grid[6][7].gridBackEnd) +
                                gridBackEnd(Grid[6][8].gridBackEnd) +
                                gridBackEnd(Grid[6][9].gridBackEnd) +
                                gridBackEnd(Grid[6][9].gridBackEnd) +
                                gridBackEnd(Grid[6][9].gridBackEnd) +
                                gridBackEnd(Grid[6][10].gridBackEnd) +

                                space + "x\n" + gridBackEnd(Grid[7][0].gridBackEnd) + space + gridBackEnd(Grid[7][2].gridBackEnd) + space + gridBackEnd(Grid[7][4].gridBackEnd) + space + gridBackEnd(Grid[7][6].gridBackEnd) + space + gridBackEnd(Grid[7][8].gridBackEnd) + space + gridBackEnd(Grid[7][10].gridBackEnd) + space +
                                "x\n" + gridBackEnd(Grid[7][0].gridBackEnd) + space1 + gridBackEnd(Grid[7][1].gridBackEnd) + space1 + gridBackEnd(Grid[7][2].gridBackEnd) + space1 + gridBackEnd(Grid[7][3].gridBackEnd) + space1 + gridBackEnd(Grid[7][4].gridBackEnd) + space1 + gridBackEnd(Grid[7][5].gridBackEnd) + space1 + gridBackEnd(Grid[7][6].gridBackEnd) + space1 + gridBackEnd(Grid[7][7].gridBackEnd) + space1 + gridBackEnd(Grid[7][8].gridBackEnd) + space1 + gridBackEnd(Grid[7][9].gridBackEnd) + space1 + gridBackEnd(Grid[7][10].gridBackEnd) +
                                space + "x\n" + gridBackEnd(Grid[7][0].gridBackEnd) + space + gridBackEnd(Grid[7][2].gridBackEnd) + space + gridBackEnd(Grid[7][4].gridBackEnd) + space + gridBackEnd(Grid[7][6].gridBackEnd) + space + gridBackEnd(Grid[7][8].gridBackEnd) + space + gridBackEnd(Grid[7][10].gridBackEnd) + space +

                                "x\n" +
                                gridBackEnd(Grid[8][0].gridBackEnd) +
                                gridBackEnd(Grid[8][1].gridBackEnd) +
                                gridBackEnd(Grid[8][1].gridBackEnd) +
                                gridBackEnd(Grid[8][1].gridBackEnd) +
                                gridBackEnd(Grid[8][2].gridBackEnd) +
                                gridBackEnd(Grid[8][3].gridBackEnd) +
                                gridBackEnd(Grid[8][3].gridBackEnd) +
                                gridBackEnd(Grid[8][3].gridBackEnd) +
                                gridBackEnd(Grid[8][4].gridBackEnd) +
                                gridBackEnd(Grid[8][5].gridBackEnd) +
                                gridBackEnd(Grid[8][5].gridBackEnd) +
                                gridBackEnd(Grid[8][5].gridBackEnd) +
                                gridBackEnd(Grid[8][6].gridBackEnd) +
                                gridBackEnd(Grid[8][7].gridBackEnd) +
                                gridBackEnd(Grid[8][7].gridBackEnd) +
                                gridBackEnd(Grid[8][7].gridBackEnd) +
                                gridBackEnd(Grid[8][8].gridBackEnd) +
                                gridBackEnd(Grid[8][9].gridBackEnd) +
                                gridBackEnd(Grid[8][9].gridBackEnd) +
                                gridBackEnd(Grid[8][9].gridBackEnd) +
                                gridBackEnd(Grid[8][10].gridBackEnd) +

                                space + "x\n" + gridBackEnd(Grid[9][0].gridBackEnd) + space + gridBackEnd(Grid[9][2].gridBackEnd) + space + gridBackEnd(Grid[9][4].gridBackEnd) + space + gridBackEnd(Grid[9][6].gridBackEnd) + space + gridBackEnd(Grid[9][8].gridBackEnd) + space + gridBackEnd(Grid[9][10].gridBackEnd) + space +
                                "x\n" + gridBackEnd(Grid[9][0].gridBackEnd) + space1 + gridBackEnd(Grid[9][1].gridBackEnd) + space1 + gridBackEnd(Grid[9][2].gridBackEnd) + space1 + gridBackEnd(Grid[9][3].gridBackEnd) + space1 + gridBackEnd(Grid[9][4].gridBackEnd) + space1 + gridBackEnd(Grid[9][5].gridBackEnd) + space1 + gridBackEnd(Grid[9][6].gridBackEnd) + space1 + gridBackEnd(Grid[9][7].gridBackEnd) + space1 + gridBackEnd(Grid[9][8].gridBackEnd) + space1 + gridBackEnd(Grid[9][9].gridBackEnd) + space1 + gridBackEnd(Grid[9][10].gridBackEnd) +
                                space + "x\n" + gridBackEnd(Grid[9][0].gridBackEnd) + space + gridBackEnd(Grid[9][2].gridBackEnd) + space + gridBackEnd(Grid[9][4].gridBackEnd) + space + gridBackEnd(Grid[9][6].gridBackEnd) + space + gridBackEnd(Grid[9][8].gridBackEnd) + space + gridBackEnd(Grid[9][10].gridBackEnd) + space +

                                "x\n" +
                                gridBackEnd(Grid[10][0].gridBackEnd) +
                                gridBackEnd(Grid[10][1].gridBackEnd) +
                                gridBackEnd(Grid[10][1].gridBackEnd) +
                                gridBackEnd(Grid[10][1].gridBackEnd) +
                                gridBackEnd(Grid[10][2].gridBackEnd) +
                                gridBackEnd(Grid[10][3].gridBackEnd) +
                                gridBackEnd(Grid[10][3].gridBackEnd) +
                                gridBackEnd(Grid[10][3].gridBackEnd) +
                                gridBackEnd(Grid[10][4].gridBackEnd) +
                                gridBackEnd(Grid[10][5].gridBackEnd) +
                                gridBackEnd(Grid[10][5].gridBackEnd) +
                                gridBackEnd(Grid[10][5].gridBackEnd) +
                                gridBackEnd(Grid[10][6].gridBackEnd) +
                                gridBackEnd(Grid[10][7].gridBackEnd) +
                                gridBackEnd(Grid[10][7].gridBackEnd) +
                                gridBackEnd(Grid[10][7].gridBackEnd) +
                                gridBackEnd(Grid[10][8].gridBackEnd) +
                                gridBackEnd(Grid[10][9].gridBackEnd) +
                                gridBackEnd(Grid[10][9].gridBackEnd) +
                                gridBackEnd(Grid[10][9].gridBackEnd) +
                                gridBackEnd(Grid[10][10].gridBackEnd)

                );


    }

    public void aiFillBar(int i, int j, GridElement[][] Grid,final int type)
    {
        if(!((DotsUI)getActivity()).exited)
        {
            if(!GRID_TYPE.isBarFilled(Grid[i][j].gridBackEnd))
            {
                if(Grid[0][0].gridFrontEnd!=null)
                {
                    final ImageView img = Grid[i][j].gridFrontEnd;
                    final Object obj=new Object();
                    synchronized (obj)
                    {
                        try
                        {
                            obj.wait(500);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    //(col & 0xfefefe) >> 1

                    final ColorDrawable playerBarDrawable=new ColorDrawable(gameDetails.getPlayerBarColor(cP.get()));
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(type==0)
                            {
                                Log.d(" AI fill Bar CP ", cP.get() + "");
                                updateCurrentPlayer();
                                mCallback.updateScoreBarSmallFragment(cP);
                                Players.playShortSound(Players.PLAYER_TYPE.playerBarFill);
                            }
                            ImageViewAnimatedChange(getActivity(), img, playerBarDrawable);
                        }
                    });
                }


                //Grid[i][j].gridFrontEnd.setImageDrawable(DynamicDrawables.playerTwoBarDrawable);
                Grid[i][j].gridBackEnd = GRID_TYPE.toFilled(Grid[i][j].gridBackEnd);
                probe(i, j, Grid);
                if(Grid[0][0].gridFrontEnd!=null)
                    pause(lock);
            }
        }

        //Log.d("Touchdown","TouchDown");




    }
    public void pause(Object lockedObject)
    {
        try
        {
            synchronized (lockedObject)
            {
                lockedObject.wait();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    public void resume(Object lockedObject)
    {
        synchronized (lockedObject)
        {
            lockedObject.notify();
        }
    }

    public synchronized void ImageViewAnimatedChange(Context c, final ImageView v, Drawable new_image)
    {

        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);

        //new_image.setBounds(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        //v.setImageDrawable(null);

        v.setImageDrawable(new_image);


        //int width=v.getRight() - v.getLeft();
        //int height=v.getBottom() - v.getTop();

            //Log.d("B4", "W : " + width + " H : " + height);
        //if(((width!=30) && (width!=60)) || ((height!=30) && (height!=60)))
          //  Log.d("AF", "W : " + width + " H : " + height);
        //v.setBackgroundColor(((ColorDrawable)new_image).getColor());
        //v.invalidate();

        anim_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                resume(lock);
            }
        });
        //anim_in.setDuration(2000);
        v.startAnimation(anim_in);
    }

    public void aiFillBoxGeneral(int barI, int barJ,GridElement[][] Grid)
    {
        if(!((DotsUI)getActivity()).exited)
        {
            if(Grid[0][0].gridBackEnd==GRID_TYPE.TEMP_2)
            {
                Log.d(" EVAL B4 to leave","("+penultimoSafeI+","+penultimoSafeJ+")");
                Log.d(" EVAL B4 to fill","("+safeI+","+safeJ+")");

                if(isSafeUnsafe(barI,barJ,Grid) || !GRID_TYPE.isBarNormal(Grid[barI][barJ].gridBackEnd))
                {
                    if((safeI==-1 && safeJ==-1) && (penultimoSafeI!=-1 && penultimoSafeJ!=-1))
                    {
                        safeI=barI;
                        safeJ=barJ;
                    }
                }
                else
                {
                    if(safeI==-1 && safeJ==-1)
                    {
                        penultimoSafeI = barI;
                        penultimoSafeJ = barJ;
                    }
                }

                Log.d(" EVAL AF to leave","("+penultimoSafeI+","+penultimoSafeJ+")");
                Log.d(" EVAL AF to fill","("+safeI+","+safeJ+")");

            }
            //
            //int playerTwoScoreCopy=gameDetails.playerTwoScore++;

            aiFillBar(barI, barJ,Grid,1);

            if (GRID_TYPE.isBarHorizontal(Grid[barI][barJ].gridBackEnd))
            {
                if (GRID_TYPE.isBarTopHorizontal(Grid[barI][barJ].gridBackEnd))
                {
                    if(isBoxFilled(barI+1,barJ,Grid))
                    {
                        aiFillBox(barI+1,barJ,Grid);
                    }
                }
                else if (GRID_TYPE.isBarBottomHorizontal(Grid[barI][barJ].gridBackEnd))
                {
                    if(isBoxFilled(barI-1,barJ,Grid))
                    {
                        aiFillBox(barI-1,barJ,Grid);
                    }
                }
                else
                {
                    if(isBoxFilled(barI-1,barJ,Grid))
                    {
                        aiFillBox(barI-1, barJ, Grid);
                    }
                    if(isBoxFilled(barI+1,barJ,Grid))
                    {
                        aiFillBox(barI+1,barJ,Grid);
                    }
                }
            }
            else
            {
                if (GRID_TYPE.isBarLeftVertical(Grid[barI][barJ].gridBackEnd))
                {
                    if(isBoxFilled(barI,barJ+1,Grid))
                    {
                        aiFillBox(barI,barJ+1,Grid);
                    }
                }
                else if (GRID_TYPE.isBarRightVertical(Grid[barI][barJ].gridBackEnd))
                {
                    if(isBoxFilled(barI,barJ-1,Grid))
                    {
                        aiFillBox(barI,barJ-1,Grid);
                    }
                }
                else
                {
                    if(isBoxFilled(barI,barJ-1,Grid))
                    {
                        aiFillBox(barI,barJ-1,Grid);
                    }
                    if(isBoxFilled(barI,barJ+1,Grid))
                    {
                        aiFillBox(barI,barJ+1,Grid);
                    }
                }
            }
        }


    }

    public boolean isBoxFilled(int boxI,int boxJ,GridElement[][] Grid)
    {
        //Log.d("BOXED","("+boxI+","+boxJ+")");
        return GRID_TYPE.isBarFilled(Grid[boxI - 1][boxJ].gridBackEnd) &&
                GRID_TYPE.isBarFilled(Grid[boxI][boxJ + 1].gridBackEnd) &&
                GRID_TYPE.isBarFilled(Grid[boxI][boxJ - 1].gridBackEnd) &&
                GRID_TYPE.isBarFilled(Grid[boxI + 1][boxJ].gridBackEnd) &&
                Grid[boxI][boxJ].gridBackEnd != GRID_TYPE.BOX_FILLED;
    }
    public void aiFillBox(int boxI, int boxJ,GridElement[][] Grid)
    {
        final ImageView img = Grid[boxI][boxJ].gridFrontEnd;
        {
            gameDetails.updatePlayerScore(cP.get());
            if(Grid[0][0].gridFrontEnd!=null)
            {
                final ColorDrawable playerBoxDrawable=new ColorDrawable(gameDetails.getPlayerBoxColor(cP.get()));
                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mCallback.updateScoreBarSmallFragment(cP);
                        ImageViewAnimatedChange(getActivity(), img, playerBoxDrawable);
                        Players.playShortSound(Players.PLAYER_TYPE.playerBoxFill);
                    }
                });
            }
        }
        Grid[boxI][boxJ].gridBackEnd = GRID_TYPE.BOX_FILLED;
        updateRange(boxI, boxJ);
        if(Grid[0][0].gridFrontEnd!=null)
            pause(lock);
    }

    public void probe(int barI, int barJ,GridElement[][] Grid)
    {
        if (GRID_TYPE.isBarHorizontal(Grid[barI][barJ].gridBackEnd))
        {
            if (GRID_TYPE.isBarTopHorizontal(Grid[barI][barJ].gridBackEnd))
            {
                probeBox(barI + 1, barJ,Grid);
            }
            else if (GRID_TYPE.isBarBottomHorizontal(Grid[barI][barJ].gridBackEnd))
            {
                probeBox(barI - 1, barJ,Grid);
            }
            else
            {
                probeBox(barI - 1, barJ,Grid);
                probeBox(barI + 1, barJ,Grid);
            }
        }
        else
        {
            if (GRID_TYPE.isBarLeftVertical(Grid[barI][barJ].gridBackEnd))
            {
                probeBox(barI, barJ + 1,Grid);
            }
            else if (GRID_TYPE.isBarRightVertical(Grid[barI][barJ].gridBackEnd))
            {
                probeBox(barI, barJ - 1,Grid);
            }
            else
            {
                probeBox(barI, barJ - 1,Grid);
                probeBox(barI, barJ + 1,Grid);
            }
        }

    }
    public void probeBox(int boxI, int boxJ,GridElement[][] Grid)
    {
        if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_FILLED)
        {
            return;
        }

        if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_RIPE)
        {
            return;
        }

        int filledCount = 0;

        if (GRID_TYPE.isBarFilled(Grid[boxI][boxJ - 1].gridBackEnd))
        {
            filledCount++;
        }
        if (GRID_TYPE.isBarFilled(Grid[boxI][boxJ + 1].gridBackEnd))
        {
            filledCount++;
        }
        if (GRID_TYPE.isBarFilled(Grid[boxI - 1][boxJ].gridBackEnd))
        {
            filledCount++;
        }
        if (GRID_TYPE.isBarFilled(Grid[boxI + 1][boxJ].gridBackEnd))
        {
            filledCount++;
        }

        if (filledCount == 2)
        {
            if(Grid[0][0].gridBackEnd ==GRID_TYPE.TEMP && Grid[boxI][boxJ].gridBackEnd ==GRID_TYPE.BOX_SAFE)
            {
                GridElement.safeBoxI.add(0,boxI);
                GridElement.safeBoxJ.add(0,boxJ);
            }
            else
            {
                Grid[boxI][boxJ].gridBackEnd = GRID_TYPE.BOX_UNSAFE;
            }
            fixBoxSurrounding(boxI, boxJ,Grid);
        }
        else if (filledCount == 3)
        {
            if(Grid[0][0].gridBackEnd ==GRID_TYPE.TEMP && Grid[boxI][boxJ].gridBackEnd ==GRID_TYPE.BOX_SAFE)
            {
                Grid[boxI][boxJ].gridBackEnd = GRID_TYPE.BOX_RIPE;
            }
            else
            {
                Grid[boxI][boxJ].gridBackEnd = GRID_TYPE.BOX_RIPE;
                fixBoxSurrounding(boxI, boxJ,Grid);
            }
        }
    }

    public void fixBoxSurrounding(int boxI, int boxJ,GridElement[][] Grid)
    {

        //Toast.makeText(this,"LEFT   : "+Grid[boxI][boxJ - 1].gridBackEnd,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"RIGHT  : "+Grid[boxI][boxJ + 1].gridBackEnd,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"TOP    : "+Grid[boxI - 1][boxJ].gridBackEnd,Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"BOTTOM : "+Grid[boxI + 1][boxJ].gridBackEnd,Toast.LENGTH_LONG).show();

        //Log.d("FIXED","("+boxI+","+boxJ+")");

        if (!GRID_TYPE.isBarFilled(Grid[boxI][boxJ - 1].gridBackEnd))
        {
            //Toast.makeText(this,"("+boxI+","+(boxJ-1)+"): "+Grid[boxI][boxJ].gridBackEnd,Toast.LENGTH_LONG).show();
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_UNSAFE)
            {
                //Toast.makeText(this,"("+boxI+","+(boxJ-1)+") III : "+Grid[boxI][boxJ].gridBackEnd,Toast.LENGTH_LONG).show();
                Grid[boxI][boxJ - 1].gridBackEnd = GRID_TYPE.toUnsafe(Grid[boxI][boxJ - 1].gridBackEnd);
            }
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_RIPE)
            {
                Grid[boxI][boxJ - 1].gridBackEnd = GRID_TYPE.toRipe(Grid[boxI][boxJ - 1].gridBackEnd);
                setRipeBar(boxI, boxJ - 1);
            }
        }
        if (!GRID_TYPE.isBarFilled(Grid[boxI][boxJ + 1].gridBackEnd))
        {
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_UNSAFE)
            {
                Grid[boxI][boxJ + 1].gridBackEnd = GRID_TYPE.toUnsafe(Grid[boxI][boxJ + 1].gridBackEnd);
            }
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_RIPE)
            {
                Grid[boxI][boxJ + 1].gridBackEnd = GRID_TYPE.toRipe(Grid[boxI][boxJ + 1].gridBackEnd);
                setRipeBar(boxI, boxJ + 1);
            }
        }
        if (!GRID_TYPE.isBarFilled(Grid[boxI - 1][boxJ].gridBackEnd))
        {
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_UNSAFE)
            {
                Grid[boxI - 1][boxJ].gridBackEnd = GRID_TYPE.toUnsafe(Grid[boxI - 1][boxJ].gridBackEnd);
            }
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_RIPE)
            {
                Grid[boxI - 1][boxJ].gridBackEnd = GRID_TYPE.toRipe(Grid[boxI - 1][boxJ].gridBackEnd);
                setRipeBar(boxI - 1, boxJ);

            }
        }
        if (!GRID_TYPE.isBarFilled(Grid[boxI + 1][boxJ].gridBackEnd))
        {
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_UNSAFE)
            {
                Grid[boxI + 1][boxJ].gridBackEnd = GRID_TYPE.toUnsafe(Grid[boxI + 1][boxJ].gridBackEnd);
            }
            if (Grid[boxI][boxJ].gridBackEnd == GRID_TYPE.BOX_RIPE)
            {
                Grid[boxI + 1][boxJ].gridBackEnd = GRID_TYPE.toRipe(Grid[boxI + 1][boxJ].gridBackEnd);
                setRipeBar(boxI + 1, boxJ);
            }
        }
    }

    public void setRipeBar(int i, int j)
    {
        GridElement.ripeI.add(0,i);
        GridElement.ripeJ.add(0,j);
        //Log.d("new ripe","("+i+","+j+")");
    }

    {
    /*public void onUnsafe(int i,int j)
    {
        do
        {
            randomI = rand.nextInt(gridBackEnd.length-1);
            if(randomI==i+1 || randomI==i-1)
            {
                do
                {
                    randomJ = rand.nextInt(gridBackEnd[0].length-1);

                }while(randomJ==j);

            }
            else if(randomI==i)
            {
                do
                {
                    randomJ = rand.nextInt(gridBackEnd[0].length-1);

                }while(randomJ==j+1 || randomJ==j-1);
            }
            else
                randomJ = rand.nextInt(gridBackEnd[0].length-1);

        }while(!(randomI % 2 == 0 && randomJ % 2 != 0)&&!(randomI % 2 != 0 && randomJ % 2 == 0));


    }
    public void fasterUnsafe(int i,int j)
    {
        onSafe();
        if((randomI==i+1 || randomI==i-1) && randomJ==j)
        {
            if(randomJ+2==gridBackEnd[0].length)
            {
                randomJ-=2;
            }
            else
            {
                randomJ+=2;
            }
        }
        else if((randomJ==j+1 || randomI==j-1) && randomI==i)
        {
            if(randomI+2==gridBackEnd.length)
            {
                randomI-=2;
            }
            else
            {
                randomI+=2;
            }
        }

    }
    public void onSafe()
    {
        randomI = rand.nextInt(gridBackEnd.length-1);
        randomJ = rand.nextInt(gridBackEnd[0].length-1);
        if((randomI%2==0 && randomJ%2==0)||(randomI%2!=0 && randomJ%2!=0))
        {
            if(randomJ==gridBackEnd[0].length-1)
            {
                randomJ--;
            }
            else
            {
                randomJ++;
            }
        }


    }
    public int getBarType(int i,int j)
    {
        if(i%2==0)
        {
            if(i==0)
            {
                return HORIZONTAL_BAR_UPPER_EDGE;
            }
            if(i==gridBackEnd.length-1)
            {
                return HORIZONTAL_BAR_LOWER_EDGE;
            }
            return HORIZONTAL_BAR_NORMAL;
        }
        if(j==0)
        {
            return VERTICAL_BAR_LEFT_EDGE;
        }
        if(j==gridBackEnd[0].length-1)
        {
            return VERTICAL_BAR_RIGHT_EDGE;
        }
        return VERTICAL_BAR_NORMAL;

    }
    public GRID_TYPE getBoxType(int i,int j)
    {


        int filledCount=0;
        int ripeUnfilledBar=-1;


        try
        {
            if(gridBackEnd[i][j]!=GRID_TYPE.BOX_SAFE)
            {
                safeBoxes++;
            }
        }
        catch (Exception e)
        {
            ////Log.d("ERROR ","("+i+","+j+")");
            //e.printStackTrace();
        }

        if(gridBackEnd[i][j]==GRID_TYPE.BOX_FILLED)
        {
            safeBoxes--;
            //gridBackEnd[i][j]=BOX_FILLED;

            ////Log.d(" Filled box ","("+i+","+j+")");
            return GRID_TYPE.BOX_FILLED;
        }

        if(gridBackEnd[i-1][j]==GRID_TYPE.BAR_INVALID)
        {
            filledCount++;
        }
        else
        {
            ripeUnfilledBar=TOP_BAR;
        }

        if(gridBackEnd[i+1][j]==GRID_TYPE.BAR_INVALID)
        {
            filledCount++;
        }
        else
        {
            ripeUnfilledBar=BOTTOM_BAR;
        }

        if(gridBackEnd[i][j-1]==GRID_TYPE.BAR_INVALID)
        {
            filledCount++;
        }
        else
        {
            ripeUnfilledBar=LEFT_BAR;
        }

        if(gridBackEnd[i][j+1]==GRID_TYPE.BAR_INVALID)
        {
            filledCount++;
        }
        else
        {
            ripeUnfilledBar=RIGHT_BAR;
        }

        if(filledCount==3)
        {
            ////Log.d(" RIPE box ","("+i+","+j+")");
            switch (ripeUnfilledBar)
            {
                case TOP_BAR:
                {
                    safeBoxes--;
                    gridBackEnd[i][j]=GRID_TYPE.BOX_RIPE_TOP;
                    return GRID_TYPE.BOX_RIPE_TOP;
                }
                case BOTTOM_BAR:
                {
                    safeBoxes--;
                    gridBackEnd[i][j]=GRID_TYPE.BOX_RIPE_BOTTOM;
                    return GRID_TYPE.BOX_RIPE_BOTTOM;
                }
                case LEFT_BAR:
                {
                    safeBoxes--;
                    gridBackEnd[i][j]=GRID_TYPE.BOX_RIPE_LEFT;
                    return GRID_TYPE.BOX_RIPE_LEFT;
                }
                case RIGHT_BAR:
                {
                    safeBoxes--;
                    gridBackEnd[i][j]=GRID_TYPE.BOX_RIPE_RIGHT;
                    return GRID_TYPE.BOX_RIPE_RIGHT;
                }
            }
        }
        if(filledCount==2)
        {
            ////Log.d(" UNSAFE box ","("+i+","+j+")");
            safeBoxes--;
            gridBackEnd[i][j]=GRID_TYPE.BOX_UNSAFE;
            return GRID_TYPE.BOX_UNSAFE;
        }
        if(filledCount==4 && gridBackEnd[i][j]!=GRID_TYPE.BOX_FILLED)
        {
           safeBoxes--;
           fillBox(i,j,0);
           return GRID_TYPE.BOX_FILLED;
        }
        else
        {

            ////Log.d(" SAFE box ","("+i+","+j+")");
            gridBackEnd[i][j]=GRID_TYPE.BOX_SAFE;
            return GRID_TYPE.BOX_SAFE;
        }
    }
    public void fillBar(final int i,final int j,int playerID)
    {
        gridFrontEnd[i][j].setImageDrawable(DynamicDrawables.playerTwoBarDrawable);
        gridBackEnd[i][j]=GRID_TYPE.BAR_INVALID;
        validBarCnt--;
    }
    public void fillBox(final int i,final int j,int playerID)
    {
        gridFrontEnd[i][j].setImageDrawable(DynamicDrawables.playerTwoBoxDrawable);
        gridBackEnd[i][j]=GRID_TYPE.BOX_FILLED;
        playerTwoScore.setText(++Player2Score +"");
        boxI=-1;
        boxJ=-1;
        updateRange(i,j);
        //r.run();
    }*/
    }

    public void getBar(GridElement[][] Grid)
    {
        if(!gameDetails.isGameOver(-1))
        {
            barI = rand.nextInt((upperI == Grid.length - 1 ? upperI + 1 : upperI) - (lowerI == 1 ? 0 : lowerI)) + (lowerI == 1 ? 0 : lowerI);
            barJ = rand.nextInt((upperJ == Grid[0].length - 1 ? upperJ + 1 : upperJ) - (lowerJ == 1 ? 0 : lowerJ)) + (lowerJ == 1 ? 0 : lowerJ);
        }
        else
        {
            gameOver = true;
            Log.d("TRUCE TRUCE TRUCE", "Truce has been declared");
            return;
        }



        if ((barI % 2 == 0 && barJ % 2 == 0) || (barI % 2 != 0 && barJ % 2 != 0))
        {
            if (barJ != (upperJ == Grid[0].length - 2 ? upperJ + 1 : upperJ))
            {
                barJ++;
            }
            else
            {
                if (upperJ != lowerJ)
                {
                    barJ--;
                }
                else
                {
                    if (barI != (upperI == Grid.length - 2 ? upperI + 1 : upperI))
                    {
                        barI++;
                    }
                    else
                    {
                        barI--;
                    }
                }
            }
        }
        probe(barI, barJ, Grid);
    }
    public String gridBackEnd(GRID_TYPE grid_type)
    {
        String gridType=grid_type.name();
        String gridBack="";

        if (gridType.contains("DOT"))
        {
            return "DOT ";
        }
        else if(gridType.contains("BOX"))
        {
            if(gridType.contains("UNSAFE"))
            {
                gridBack+="UNS";
            }
            else if(gridType.contains("SAFE"))
            {
                gridBack+="SAF";
            }
            else if(gridType.contains("RIPE"))
            {
                gridBack+="RIP";
            }
            else if(gridType.contains("FILLED"))
            {
                gridBack+="FIL";
            }
        }
        else
        {
            if(gridType.contains("HORIZONTAL"))
            {
                gridBack+="H";
            }
            else if(gridType.contains("VERTICAL"))
            {
                gridBack+="V";
            }

            if(gridBack.contains("H"))
            {
                if(gridType.contains("TOP"))
                {
                    gridBack+="T";
                }
                else if(gridType.contains("BOTTOM"))
                {
                    gridBack+="B";
                }
                else if(gridType.contains("NORMAL"))
                {
                    gridBack+="N";
                }
            }
            else if(gridBack.contains("V"))
            {
                if(gridType.contains("LEFT"))
                {
                    gridBack+="L";
                }
                else if(gridType.contains("RIGHT"))
                {
                    gridBack+="R";
                }
                else if(gridType.contains("NORMAL"))
                {
                    gridBack+="N";
                }
            }


            if(gridType.contains("UNSAFE"))
            {
                gridBack+="U";
            }
            else if(gridType.contains("SAFE"))
            {
                gridBack+="S";
            }
            else if(gridType.contains("RIPE"))
            {
                gridBack+="R";
            }
            else if(gridType.contains("FILLED"))
            {
                gridBack+="F";
            }
        }

        return gridBack+" ";
    }
    public void updateRange(int i,int j)
    {
        int type=1;
        if(type==0)
        {
            if(filledCountCol[j]== gameDetails.getDotRowCount() -1)
            {
                if(j==lowerJ)
                    lowerJ+=2;
                if(j==upperJ)
                    upperJ-=2;
            }

            if(filledCountRow[i]== gameDetails.getDotColCount() -1)
            {
                if(i==lowerI)
                    lowerI+=2;
                if(i==upperI)
                    upperI-=2;
            }

        }
        //if(filledCountI[i]!=dotRowCount-1,)
        filledCountRow[i]++;
        filledCountCol[j]++;

        ////Log.d("ROW : ", filledCountI[0]+" "+filledCountI[1]+" "+filledCountI[2]+" "+filledCountI[3]+" "+filledCountI[4]+" "+filledCountI[5]+" "+filledCountI[6]+" "+filledCountI[7]+" "+filledCountI[8]);
        ////Log.d("COL : ", filledCountJ[0]+" "+filledCountJ[1]+" "+filledCountJ[2]+" "+filledCountJ[3]+" "+filledCountJ[4]+" "+filledCountJ[5]+" "+filledCountJ[6]+" "+filledCountJ[7]+" "+filledCountJ[8]);

        ////Log.d("i : ", i+"");
        ////Log.d("j : ", j+"");

        ////Log.d("dot row cnt : ", dotRowCount-1+"");
        // //Log.d("dot col cnt : ", dotColCount-1+"");
        //Toast.makeText(this,"filled Cnt of "+j+" is : "+filledCountJ[j],Toast.LENGTH_LONG).show();

        ////Log.d("LOWER B4","("+lowerI+","+lowerJ+")");
        ////Log.d("HIGHR B4","("+upperI+","+upperJ+")");

        if(filledCountCol[j]== gameDetails.getDotRowCount() -1)
        {
            if(j==lowerJ)
                lowerJ+=2;
            if(j==upperJ)
                upperJ-=2;
        }

        ////Log.d("LOWER AJ","("+lowerI+","+lowerJ+")");
        // //Log.d("HIGHR AJ","("+upperI+","+upperJ+")");

        if(filledCountRow[i]== gameDetails.getDotColCount() -1)
        {
            if(i==lowerI)
                lowerI+=2;
            if(i==upperI)
                upperI-=2;
        }


        ////Log.d("LOWER AI","("+lowerI+","+lowerJ+")");
        ////Log.d("HIGHR AI","("+upperI+","+upperJ+")");

    }
}
