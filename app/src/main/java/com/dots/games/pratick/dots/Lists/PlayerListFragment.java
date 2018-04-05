package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.dots.games.pratick.dots.Designers.Misc;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.Enums.PURPOSE;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;

/**
 * Created by Pratick on 29-05-2015.
 */
public class PlayerListFragment extends Fragment
{
    public FrameLayout.LayoutParams rootLayoutParams;
    public PURPOSE playerListFragmentPurpose;

    private View rootLayout;
    private RecyclerView playerList;
    private PlayersListAdapter playersListAdapter;
    private GestureDetector gestureDetector;


    PlayerListCommunicationInterface mCallback;

    public interface PlayerListCommunicationInterface
    {
        void backToMenu();
        void callStyleFragment(int selectedPlayerPosition);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (PlayerListCommunicationInterface) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnViewScreenSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootLayout=inflater.inflate(R.layout.recycler_view_container,container,false);
        playerList=(RecyclerView)rootLayout.findViewById(R.id.recycler_view);

        rootLayout.setLayoutParams(rootLayoutParams);
        int itemHeight=rootLayoutParams.height/4;

        playersListAdapter=new PlayersListAdapter(getActivity(),getDummyData(),itemHeight);

        FlipInBottomXAnimator animator=new FlipInBottomXAnimator();
        animator.setRemoveDuration(200);
        animator.setAddDuration(200);
        playerList.setItemAnimator(animator);

        Log.d("numbers", gameDetails.getNumberOfPlayers()+"");



        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (lock)
                {
                    addOneByOne(playerList.getItemAnimator().getAddDuration(), -1, null,0);
                }
            }
        }).start();


        playerList.setAdapter(playersListAdapter);
        playerList.setLayoutManager(new LinearLayoutManager(getActivity()));


        (rootLayout.findViewById(R.id.recycler_view_shadow)).setBackgroundResource(R.drawable.grid_style_drop_shadow);



        switch (playerListFragmentPurpose)
        {
            case MIXED_OPPONENTS:
            {
                FloatingActionButton addButton    = (FloatingActionButton)rootLayout.findViewById(R.id.add_player);
                FloatingActionButton removeButton = (FloatingActionButton)rootLayout.findViewById(R.id.remove_player);
                FloatingActionButton doneButton   = (FloatingActionButton)rootLayout.findViewById(R.id.done);

                addButton.setAlpha(1f);
                removeButton.setAlpha(1f);
                doneButton.setAlpha(1f);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (lock) {
                                    addOneByOne(playerList.getItemAnimator().getAddDuration(), -1, PLAYER_TYPE.values()[(new Random()).nextInt(4)],0);
                                }
                            }
                        }).start();

                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        playersListAdapter.removeAt(playersListAdapter.getItemCount() - 1);
                        playerList.smoothScrollToPosition(playersListAdapter.getItemCount());
                    }
                });

                doneButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        gameDetails.updateGameDetails(getPlayerTypes());
                        Toasts.appMonoSpacedToast(getActivity(),Misc.getAllPlayersNameAndType()).show();
                        mCallback.backToMenu();
                    }
                });
                break;
            }
            case MULTI_PLAYER_OPPONENTS:
            {
                FloatingActionButton addButton    = (FloatingActionButton)rootLayout.findViewById(R.id.add_player);
                FloatingActionButton removeButton = (FloatingActionButton)rootLayout.findViewById(R.id.remove_player);
                FloatingActionButton doneButton   = (FloatingActionButton)rootLayout.findViewById(R.id.done);

                addButton.setAlpha(1f);
                removeButton.setAlpha(1f);
                doneButton.setAlpha(1f);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (lock) {
                                    addOneByOne(playerList.getItemAnimator().getAddDuration(), -1, PLAYER_TYPE.HUMAN,0);
                                }
                            }
                        }).start();

                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        playersListAdapter.removeAt(playersListAdapter.getItemCount() - 1);
                        playerList.smoothScrollToPosition(playersListAdapter.getItemCount());
                    }
                });

                doneButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        gameDetails.updateGameDetails(getPlayerTypes());
                        Toasts.appMonoSpacedToast(getActivity(),"Number of Players : " + gameDetails.getNumberOfPlayers()).show();
                        mCallback.backToMenu();
                    }
                });
                break;
            }
        }

        //RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.minimized_list_item_divider));
        //playerList.addItemDecoration(itemDecoration);

        interpretTouchActions();

        return rootLayout;
    }

    public void interpretTouchActions()
    {
        gestureDetector=new GestureDetector(getActivity(),new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(final MotionEvent e)
            {
                switch (playerListFragmentPurpose)
                {
                    case AI_LEVEL_CHOOSER:
                    {
                        View selectedView=playerList.findChildViewUnder(e.getX(), e.getY());
                        int selectedViewPosition=playerList.getChildAdapterPosition(selectedView);

                        Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                        PLAYER_TYPE currentPlayerType=getCurrentPlayerTypePosition(selectedViewPosition);

                        if(gameDetails.getPlayerType(0)!=PLAYER_TYPE.HUMAN)
                        {
                            gameDetails.updatePlayerType(currentPlayerType,0);
                        }
                        else
                        {
                            gameDetails.updatePlayerType(currentPlayerType,1);
                        }

                        selectedView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                        if(gameDetails.getPlayerType(0)!=PLAYER_TYPE.HUMAN)
                        {
                            Toasts.appMonoSpacedToast(getActivity(), PLAYER_TYPE.getPlayerTypeString(gameDetails.getPlayerType(0)) + " AI Selected").show();
                        }
                        else
                        {
                            Toasts.appMonoSpacedToast(getActivity(), PLAYER_TYPE.getPlayerTypeString(gameDetails.getPlayerType(1)) + " AI Selected").show();
                        }

                        mCallback.backToMenu();

                        break;
                    }
                    case MIXED_OPPONENTS:
                    {
                        View selectedView=playerList.findChildViewUnder(e.getX(), e.getY());
                        int selectedViewPosition=playerList.getChildAdapterPosition(selectedView);
                        if(selectedViewPosition!=-1)
                        {
                            PLAYER_TYPE newPlayerType=getNewPlayerTypePosition(selectedViewPosition);
                            playersListAdapter.removeAt(selectedViewPosition);
                            addOneByOne(playerList.getItemAnimator().getAddDuration(), selectedViewPosition, newPlayerType, 1);
                            Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                        }
                        else
                        {
                            Toasts.appPresetToast(getActivity(),Toasts.ToastMessages.invalidMove2);
                            Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                        }


                        break;
                    }
                    case STYLE:
                    {
                        View selectedView=playerList.findChildViewUnder(e.getX(), e.getY());
                        int selectedViewPosition=playerList.getChildAdapterPosition(selectedView);
                        if(selectedViewPosition!=-1)
                        {
                            mCallback.callStyleFragment(selectedViewPosition);
                            Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                        }
                        else
                        {
                            Toasts.appPresetToast(getActivity(),Toasts.ToastMessages.invalidMove2);
                            Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                        }
                        break;
                    }
                }

                return super.onSingleTapUp(e);
            }
            @Override
            public void onLongPress(MotionEvent e)
            {
                super.onLongPress(e);
            }

        });

        playerList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent motionEvent)
            {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

        });
    }



    public List<PlayersListItem> getDummyData()
    {
        return new ArrayList<>();
    }

    public List<PlayersListItem> getInitialData()
    {
        int numberOfPlayers=gameDetails.getNumberOfPlayers();
        if(playerListFragmentPurpose==PURPOSE.STYLE)
        {
            numberOfPlayers++;
        }

        PLAYER_TYPE[] playerTypes=new PLAYER_TYPE[numberOfPlayers];
        int[] backgroundColorResourceIDList=new int[numberOfPlayers];
        int[] playerItemIconResourceIDList=new int[numberOfPlayers];
        int[] playerItemHeaderItemResourceIDList=new int[numberOfPlayers];
        int[] playerItemTextResourceIDList=new int[numberOfPlayers];
        int[] playerItemDifficultyTextResourceIDList=new int[numberOfPlayers];

        for (int i=0;i<gameDetails.getNumberOfPlayers();i++)
        {
            switch (gameDetails.getPlayerType(i))
            {
                case EASY:
                {
                    playerTypes[i]=PLAYER_TYPE.EASY;
                    backgroundColorResourceIDList[i]          = R.color.LightGreenA700;
                    playerItemIconResourceIDList[i]           = R.drawable.easy_ai_icon;
                    playerItemHeaderItemResourceIDList[i]     = R.string.easy_ai_header_text;
                    playerItemTextResourceIDList[i]           = R.string.easy_ai_taunt_text;
                    playerItemDifficultyTextResourceIDList[i] = R.string.easy_ai_difficulty_text;
                    break;
                }
                case MEDIUM:
                {
                    playerTypes[i]=PLAYER_TYPE.MEDIUM;
                    backgroundColorResourceIDList[i]          = R.color.AmberA700;
                    playerItemIconResourceIDList[i]           = R.drawable.medium_ai_icon;
                    playerItemHeaderItemResourceIDList[i]     = R.string.medium_ai_header_text;
                    playerItemTextResourceIDList[i]           = R.string.medium_ai_taunt_text;
                    playerItemDifficultyTextResourceIDList[i] = R.string.medium_ai_difficulty_text;
                    break;
                }
                case HARD:
                {
                    playerTypes[i]=PLAYER_TYPE.HARD;
                    backgroundColorResourceIDList[i]          = R.color.OrangeA700;
                    playerItemIconResourceIDList[i]           = R.drawable.hard_ai_icon;
                    playerItemHeaderItemResourceIDList[i]     = R.string.hard_ai_header_text;
                    playerItemTextResourceIDList[i]           = R.string.hard_ai_taunt_text;
                    playerItemDifficultyTextResourceIDList[i] = R.string.hard_ai_difficulty_text;
                    break;
                }
                case TITAN:
                {
                    playerTypes[i]=PLAYER_TYPE.TITAN;
                    backgroundColorResourceIDList[i]          = R.color.RedA700;
                    playerItemIconResourceIDList[i]           = R.drawable.titan_ai_icon;
                    playerItemHeaderItemResourceIDList[i]     = R.string.titan_ai_header_text;
                    playerItemTextResourceIDList[i]           = R.string.titan_ai_taunt_text;
                    playerItemDifficultyTextResourceIDList[i] = R.string.titan_ai_difficulty_text;
                    break;
                }
                case HUMAN:
                {
                    playerTypes[i]=PLAYER_TYPE.HUMAN;
                    backgroundColorResourceIDList[i]          = R.color.TealA700;
                    playerItemIconResourceIDList[i]           = R.drawable.human_ai_icon;
                    playerItemHeaderItemResourceIDList[i]     = R.string.human_ai_header_text;
                    playerItemTextResourceIDList[i]           = R.string.human_ai_taunt_text;
                    playerItemDifficultyTextResourceIDList[i] = R.string.human_ai_difficulty_text;
                    break;
                }
            }


        }
        if(playerListFragmentPurpose==PURPOSE.STYLE)
        {
            backgroundColorResourceIDList[gameDetails.getNumberOfPlayers()]          = R.color.LimeA700;
            playerItemIconResourceIDList[gameDetails.getNumberOfPlayers()]           = R.drawable.minimized__settings_icon;
            playerItemHeaderItemResourceIDList[gameDetails.getNumberOfPlayers()]     = R.string.general_header_text;
            playerItemTextResourceIDList[gameDetails.getNumberOfPlayers()]           = R.string.general_taunt_text;
            playerItemDifficultyTextResourceIDList[gameDetails.getNumberOfPlayers()] = R.string.general_difficulty_text;
        }
        List<PlayersListItem> data=new ArrayList<>();

        for(int i=0;i<playerItemIconResourceIDList.length;i++)
        {
            PlayersListItem currentItem = new PlayersListItem();

            currentItem.player_type=playerTypes[i];
            currentItem.backgroundColorResourceID          = backgroundColorResourceIDList[i];
            currentItem.playerItemIconResourceID           = playerItemIconResourceIDList[i];
            currentItem.playerItemHeaderItemResourceID     = playerItemHeaderItemResourceIDList[i];
            currentItem.playerItemTextResourceID           = playerItemTextResourceIDList[i];
            currentItem.playerItemDifficultyTextResourceID = playerItemDifficultyTextResourceIDList[i];

            data.add(currentItem);
        }
        return data;
    }

    public List<PlayersListItem> getAILevelChooserData()
    {
        List<PlayersListItem> data=new ArrayList<>();

        PLAYER_TYPE[] playerTypes =
        {
                PLAYER_TYPE.EASY,
                PLAYER_TYPE.MEDIUM,
                PLAYER_TYPE.HARD,
                PLAYER_TYPE.TITAN
        };
        int[] backgroundColorResourceIDList =
        {
                R.color.LightGreenA700,
                R.color.AmberA700,
                R.color.OrangeA700,
                R.color.RedA700
        };
        int[] playerItemIconResourceIDList =
        {
                R.drawable.easy_ai_icon,
                R.drawable.medium_ai_icon,
                R.drawable.hard_ai_icon,
                R.drawable.titan_ai_icon
        };
        int[] playerItemHeaderItemResourceIDList         =
        {
                R.string.easy_ai_header_text,
                R.string.medium_ai_header_text,
                R.string.hard_ai_header_text,
                R.string.titan_ai_header_text
        };
        int[] playerItemTextResourceIDList         =
        {
                R.string.easy_ai_taunt_text,
                R.string.medium_ai_taunt_text,
                R.string.hard_ai_taunt_text,
                R.string.titan_ai_taunt_text
        };
        int[] playerItemDifficultyTextResourceIDList         =
        {
                R.string.easy_ai_difficulty_text,
                R.string.medium_ai_difficulty_text,
                R.string.hard_ai_difficulty_text,
                R.string.titan_ai_difficulty_text
        };

        for(int i=0;i<playerItemIconResourceIDList.length;i++)
        {
            PlayersListItem currentItem = new PlayersListItem();

            currentItem.player_type                        = playerTypes[i];
            currentItem.backgroundColorResourceID          = backgroundColorResourceIDList[i];
            currentItem.playerItemIconResourceID           = playerItemIconResourceIDList[i];
            currentItem.playerItemHeaderItemResourceID     = playerItemHeaderItemResourceIDList[i];
            currentItem.playerItemTextResourceID           = playerItemTextResourceIDList[i];
            currentItem.playerItemDifficultyTextResourceID = playerItemDifficultyTextResourceIDList[i];

            data.add(currentItem);
        }
        return data;
    }

    public List<PlayersListItem> getMixedOpponentsData(PLAYER_TYPE newPlayerType)
    {
        PLAYER_TYPE[] playerTypes=new PLAYER_TYPE[1];
        int[] backgroundColorResourceIDList=new int[1];
        int[] playerItemIconResourceIDList=new int[1];
        int[] playerItemHeaderItemResourceIDList=new int[1];
        int[] playerItemTextResourceIDList=new int[1];
        int[] playerItemDifficultyTextResourceIDList=new int[1];

        switch (newPlayerType)
        {
            case EASY:
            {
                playerTypes[0]=PLAYER_TYPE.EASY;
                backgroundColorResourceIDList[0]          = R.color.LightGreenA700;
                playerItemIconResourceIDList[0]           = R.drawable.easy_ai_icon;
                playerItemHeaderItemResourceIDList[0]     = R.string.easy_ai_header_text;
                playerItemTextResourceIDList[0]           = R.string.easy_ai_taunt_text;
                playerItemDifficultyTextResourceIDList[0] = R.string.easy_ai_difficulty_text;
                break;
            }
            case MEDIUM:
            {
                playerTypes[0]=PLAYER_TYPE.MEDIUM;
                backgroundColorResourceIDList[0]          = R.color.AmberA700;
                playerItemIconResourceIDList[0]           = R.drawable.medium_ai_icon;
                playerItemHeaderItemResourceIDList[0]     = R.string.medium_ai_header_text;
                playerItemTextResourceIDList[0]           = R.string.medium_ai_taunt_text;
                playerItemDifficultyTextResourceIDList[0] = R.string.medium_ai_difficulty_text;
                break;
            }
            case HARD:
            {
                playerTypes[0]=PLAYER_TYPE.HARD;
                backgroundColorResourceIDList[0]          = R.color.OrangeA700;
                playerItemIconResourceIDList[0]           = R.drawable.hard_ai_icon;
                playerItemHeaderItemResourceIDList[0]     = R.string.hard_ai_header_text;
                playerItemTextResourceIDList[0]           = R.string.hard_ai_taunt_text;
                playerItemDifficultyTextResourceIDList[0] = R.string.hard_ai_difficulty_text;
                break;
            }
            case TITAN:
            {
                playerTypes[0]=PLAYER_TYPE.TITAN;
                backgroundColorResourceIDList[0]          = R.color.RedA700;
                playerItemIconResourceIDList[0]           = R.drawable.titan_ai_icon;
                playerItemHeaderItemResourceIDList[0]     = R.string.titan_ai_header_text;
                playerItemTextResourceIDList[0]           = R.string.titan_ai_taunt_text;
                playerItemDifficultyTextResourceIDList[0] = R.string.titan_ai_difficulty_text;
                break;
            }
            case HUMAN:
            {
                playerTypes[0]=PLAYER_TYPE.HUMAN;
                backgroundColorResourceIDList[0]          = R.color.TealA700;
                playerItemIconResourceIDList[0]           = R.drawable.human_ai_icon;
                playerItemHeaderItemResourceIDList[0]     = R.string.human_ai_header_text;
                playerItemTextResourceIDList[0]           = R.string.human_ai_taunt_text;
                playerItemDifficultyTextResourceIDList[0] = R.string.human_ai_difficulty_text;
                break;
            }
        }
        List<PlayersListItem> data=new ArrayList<>();

        for(int i=0;i<playerItemIconResourceIDList.length;i++)
        {
            PlayersListItem currentItem = new PlayersListItem();

            currentItem.player_type=playerTypes[i];
            currentItem.backgroundColorResourceID          = backgroundColorResourceIDList[i];
            currentItem.playerItemIconResourceID           = playerItemIconResourceIDList[i];
            currentItem.playerItemHeaderItemResourceID     = playerItemHeaderItemResourceIDList[i];
            currentItem.playerItemTextResourceID           = playerItemTextResourceIDList[i];
            currentItem.playerItemDifficultyTextResourceID = playerItemDifficultyTextResourceIDList[i];

            data.add(currentItem);
        }
        return data;
    }

    public List<PlayersListItem> getMultiPlayerOpponentsData()
    {
        PLAYER_TYPE[] playerTypes=new PLAYER_TYPE[1];
        int[] backgroundColorResourceIDList=new int[1];
        int[] playerItemIconResourceIDList=new int[1];
        int[] playerItemHeaderItemResourceIDList=new int[1];
        int[] playerItemTextResourceIDList=new int[1];
        int[] playerItemDifficultyTextResourceIDList=new int[1];

        playerTypes[0]=PLAYER_TYPE.HUMAN;
        backgroundColorResourceIDList[0]          = R.color.TealA700;
        playerItemIconResourceIDList[0]           = R.drawable.human_ai_icon;
        playerItemHeaderItemResourceIDList[0]     = R.string.human_ai_header_text;
        playerItemTextResourceIDList[0]           = R.string.human_ai_taunt_text;
        playerItemDifficultyTextResourceIDList[0] = R.string.human_ai_difficulty_text;

        List<PlayersListItem> data=new ArrayList<>();

        for(int i=0;i<playerItemIconResourceIDList.length;i++)
        {
            PlayersListItem currentItem = new PlayersListItem();

            currentItem.player_type=playerTypes[i];
            currentItem.backgroundColorResourceID          = backgroundColorResourceIDList[i];
            currentItem.playerItemIconResourceID           = playerItemIconResourceIDList[i];
            currentItem.playerItemHeaderItemResourceID     = playerItemHeaderItemResourceIDList[i];
            currentItem.playerItemTextResourceID           = playerItemTextResourceIDList[i];
            currentItem.playerItemDifficultyTextResourceID = playerItemDifficultyTextResourceIDList[i];

            data.add(currentItem);
        }
        return data;
    }

    private Object lock =new Object();
    int index;
    public synchronized void decrement()
    {
        index--;
    }
    public synchronized void increment()
    {
        index++;
    }
    public synchronized int getRemovalIndex()
    {
        return index;
    }

    public void addAllAtOnce()
    {
        final List<PlayersListItem> newList;
        switch (playerListFragmentPurpose)
        {
            default:
            case AI_LEVEL_CHOOSER:
            {
                newList = getAILevelChooserData();
                break;
            }
            case MIXED_OPPONENTS:
            {
                newList = getInitialData();
                break;
            }
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < newList.size(); i++)
                {
                    playersListAdapter.add(newList.get(i));
                }
            }
        });
    }

    public void addOneByOne(long waitTime, final int position,final PLAYER_TYPE playerType, final int follow)
    {
        final List<PlayersListItem> newList;

        Log.d("Type",playerListFragmentPurpose.name());
        switch (playerListFragmentPurpose)
        {
            default:
            case AI_LEVEL_CHOOSER:
            {
                newList = getAILevelChooserData();
                break;
            }
            case STYLE:
            {
                newList = getInitialData();
                break;
            }
            case MULTI_PLAYER_OPPONENTS:
            {
                if(playerType==null)
                {
                    newList = getInitialData();
                }
                else
                {
                    newList = getMultiPlayerOpponentsData();
                }
                break;
            }
            case MIXED_OPPONENTS:
            {
                if(playerType==null)
                {
                    newList = getInitialData();
                }
                else
                {
                    newList = getMixedOpponentsData(playerType);
                }

                break;
            }

        }

        index=0;
        while (index!=newList.size())
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if(position==-1)
                        {
                            playersListAdapter.add(newList.get(index));
                        }
                        else
                        {
                            playersListAdapter.addAt(newList.get(index), position);
                        }
                        if(follow==0)
                        {
                            playerList.smoothScrollToPosition(playersListAdapter.getItemCount());
                        }

                        increment();
                    }
                    catch (Exception ignored){}

                }
            });
            try
            {
                lock.wait(waitTime);
            }
            catch(Exception e)
            {
                break;
            }
        }

    }
    public PLAYER_TYPE getNewPlayerTypePosition(int selectedViewPosition)
    {
        return PLAYER_TYPE.getNextPlayerType(playersListAdapter.getList().get(selectedViewPosition).player_type);
    }
    public PLAYER_TYPE getCurrentPlayerTypePosition(int selectedViewPosition)
    {
        return playersListAdapter.getList().get(selectedViewPosition).player_type;
    }

    public PLAYER_TYPE[] getPlayerTypes()
    {
        Log.d("Number",playersListAdapter.getItemCount()+"");
        PLAYER_TYPE[] playerTypes=new PLAYER_TYPE[playersListAdapter.getItemCount()];
        for (int i=0;i<playerTypes.length;i++)
        {
            playerTypes[i]=getCurrentPlayerTypePosition(i);
        }
        return playerTypes;
    }

}
