package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.GAME_TYPE;
import com.dots.games.pratick.dots.Enums.MINIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.gameDetails;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

/**
 * Created by Pratick on 30-05-2015.
 */
public class MaximizedListFragment extends Fragment
{
    public FrameLayout.LayoutParams rootLayoutParams;
    private View rootLayout;
    private RecyclerView maximizedList;
    private MaximizedListAdapter maximizedListAdapter;
    private ViewGroup.LayoutParams ItemLayoutParams;
    private Typeface ItemTextTypeface;
    GestureDetector gestureDetector;

    MaximizedListCommunicationInterface mCallback;

    public interface MaximizedListCommunicationInterface
    {
        void sendOnMaximizedListItemClick(int activeMaximizedListItemPosition);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (MaximizedListCommunicationInterface) activity;
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
        maximizedList=(RecyclerView)rootLayout.findViewById(R.id.recycler_view);
        ItemTextTypeface =Typeface.createFromAsset(getActivity().getAssets(), "font/RobotoCondensed-BoldItalic.ttf");
        if(rootLayoutParams!=null)
        {
            setDimensions();
        }


        FlipInTopXAnimator animator=new FlipInTopXAnimator();
        animator.setRemoveDuration(200);
        animator.setAddDuration(200);
        maximizedList.setItemAnimator(animator);

        maximizedListAdapter=new MaximizedListAdapter((DotsUI)getActivity(),getData(MINIMIZED_LIST_VIEW_ITEM.DUMMY),ItemLayoutParams,ItemTextTypeface);
        maximizedList.setAdapter(maximizedListAdapter);
        maximizedList.setLayoutManager(new LinearLayoutManager(getActivity()));

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (lock)
                {
                    try
                    {
                        lock.wait(2000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {

                            (getActivity().findViewById(R.id.screen_guard)).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.waitForSometime);
                                }
                            });
                        }
                    });

                    addOneByOne(MINIMIZED_LIST_VIEW_ITEM.SINGLE_PLAYER, maximizedList.getItemAnimator().getAddDuration());

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            (getActivity().findViewById(R.id.screen_guard)).setClickable(false);
                        }
                    });
                }
            }
        }).start();

        setTouchListener();
        return rootLayout;
    }

    public void setTouchListener()
    {
        maximizedList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent)
            {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent)
            {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b)
            {

            }
        });

        gestureDetector=new GestureDetector(getActivity(),new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                View selectedView=maximizedList.findChildViewUnder(e.getX(), e.getY());
                int[] location =new int[2];
                selectedView.getLocationInWindow(location);
                int selectedViewPosition=getRealIndex(selectedView);
                Log.d("Log", location[0] + "," + location[1]);
                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                mCallback.sendOnMaximizedListItemClick(selectedViewPosition);
                return super.onSingleTapUp(e);
            }
            @Override
            public void onLongPress(MotionEvent e)
            {
                ((DotsUI)getActivity()).openTutorial();
                super.onLongPress(e);
            }

        });
    }

    public void setDimensions()
    {
        rootLayout.setLayoutParams(rootLayoutParams);
        ItemLayoutParams=new ViewGroup.LayoutParams(rootLayoutParams.width,(int)(rootLayoutParams.width*(60d/100d)));
    }
    public List<MaximizedListItem> getData(MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem)
    {
        List<MaximizedListItem> data=new ArrayList<>();
        int[] backgroundDrawableResourceIDList;
        int[] imageIconResourceIDList;
        int[] textResourceIDList;


        switch (activeMinimizedListItem)
        {
            default:
            case SINGLE_PLAYER:
            {
                gameDetails.setGameType(GAME_TYPE.SINGLE_PLAYER);

                backgroundDrawableResourceIDList = new int[]
                {
                        R.color.Orange500,
                        R.color.Amber500,
                        R.color.Green500,
                        R.color.Blue500,
                };
                imageIconResourceIDList = new int[]
                {
                        R.drawable.maximized__start_icon,
                        R.drawable.maximized__single_player__ai_icon,
                        R.drawable.maximized_grid_size_icon,
                        R.drawable.maximized_style_icon
                };
                textResourceIDList=new int[]
                {
                        R.string.single_player_start_taunt,
                        R.string.single_player_ai_level_taunt,
                        R.string.single_player_grid_size_taunt,
                        R.string.single_player_color_taunt
                };
                break;
            }
            case MULTI_PLAYER:
            {
                gameDetails.setGameType(GAME_TYPE.MULTI_PLAYER);
                backgroundDrawableResourceIDList = new int[]
                {
                        R.color.DeepOrange500,
                        R.color.Amber500,
                        R.color.LightBlue500,
                        R.color.Teal500,
                };
                imageIconResourceIDList = new int[]
                {
                        R.drawable.maximized__start_icon,
                        R.drawable.maximized__multi_player__number_of_players_icon,
                        R.drawable.maximized_grid_size_icon,
                        R.drawable.maximized_style_icon
                };
                textResourceIDList=new int[]
                {
                        R.string.multi_player_start_taunt,
                        R.string.multi_player_number_of_players_level_taunt,
                        R.string.multi_player_grid_size_taunt,
                        R.string.multi_player_color_taunt
                };
                break;
            }
            case MIXED:
            {
                gameDetails.setGameType(GAME_TYPE.MIXED);
                backgroundDrawableResourceIDList = new int[]
                {
                        R.color.Amber500,
                        R.color.Lime500,
                        R.color.LightBlue500,
                        R.color.DeepOrange500,
                };
                imageIconResourceIDList = new int[]
                        {
                                R.drawable.maximized__start_icon,
                                R.drawable.mixed__opponents_icon,
                                R.drawable.maximized_grid_size_icon,
                                R.drawable.maximized_style_icon
                        };
                textResourceIDList=new int[]
                        {
                                R.string.mixed_start_taunt,
                                R.string.mixed_players_taunt,
                                R.string.mixed_grid_size_taunt,
                                R.string.mixed_color_taunt
                        };
                break;
            }
            case ACHIEVEMENT:
            {
                gameDetails.setGameType(GAME_TYPE.MIXED);
                backgroundDrawableResourceIDList = new int[]
                {
                        R.color.LightBlue500,
                        R.color.Lime500,
                        R.color.Purple500,
                        R.color.Orange500,
                };
                imageIconResourceIDList = new int[]
                {
                        R.drawable.google_play_sign_in,
                        R.drawable.google_play_achievements,
                        R.drawable.google_play_leaderboard,
                        R.drawable.google_play_save_game
                };
                textResourceIDList=new int[]
                {
                        R.string.achievement_sign_in_taunt,
                        R.string.achievement_show_achievement_taunt,
                        R.string.achievement_show_leaderboard_taunt,
                        R.string.achievement_show_leaderboard_taunt
                };
                if(((DotsUI)getActivity()).getGoogleApiClient().isConnected())
                {
                    textResourceIDList[0]=R.string.achievement_sign_out_taunt;
                }
                break;
            }
            case DUMMY:
            {
                backgroundDrawableResourceIDList = new int[]
                {

                };
                imageIconResourceIDList = new int[]
                {

                };
                textResourceIDList=new int[]
                {

                };
                break;
            }
        }
        for(int i=0;i<imageIconResourceIDList.length && i<backgroundDrawableResourceIDList.length && i<textResourceIDList.length;i++)
        {
            MaximizedListItem currentItem = new MaximizedListItem();
            currentItem.backgroundDrawableResourceID=backgroundDrawableResourceIDList[i];
            currentItem.imageIconResourceID=imageIconResourceIDList[i];
            currentItem.textResourceID=textResourceIDList[i];
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

    public void changeData(final MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (lock)
                {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //(getActivity().findViewById(R.id.screen_guard)).setBackgroundResource(R.color.BlackAlpha50);
                            (getActivity().findViewById(R.id.screen_guard)).setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.waitForSometime);
                                }
                            });
                        }
                    });


                    clearOneByOne(maximizedList.getItemAnimator().getRemoveDuration());
                    addOneByOne(activeMinimizedListItem, maximizedList.getItemAnimator().getAddDuration());



                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            (getActivity().findViewById(R.id.screen_guard)).setClickable(false);
                            //(getActivity().findViewById(R.id.screen_guard)).setBackgroundResource(R.color.Transparent);
                        }
                    });





                    //clearAllAtOnce();
                    //addAllAtOnce(activeMinimizedListItem);
                }
            }
        }).start();
    }
    public void addAllAtOnce(final MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem)
    {
        final List<MaximizedListItem> newList=getData(activeMinimizedListItem);
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for(int i=0;i<newList.size();i++)
                {
                    maximizedListAdapter.add(newList.get(i));
                }
            }
        });
    }
    public void addOneByOne(final MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem,long waitTime)
    {

        final List<MaximizedListItem> newList;

        if(activeMinimizedListItem!=null)
        {
            newList=getData(activeMinimizedListItem);
        }
        else
        {
            newList=getData(MINIMIZED_LIST_VIEW_ITEM.ACHIEVEMENT);
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
                        maximizedListAdapter.add(newList.get(index));
                        increment();
                    }
                    catch (Exception e){}

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
    public void clearOneByOne(long waitTime)
    {
        index=maximizedListAdapter.getItemCount()-1;
        while (index>=0)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        maximizedListAdapter.removeAt(getRemovalIndex());
                        decrement();
                    }
                    catch (Exception e){}
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
    public void clearAllAtOnce()
    {
        index=maximizedListAdapter.getItemCount()-1;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        maximizedListAdapter.removeAt(getRemovalIndex());
                        decrement();
                    } catch (Exception e) {
                        break;
                    }

                }
            }
        });
    }
    public int getRealIndex(View selectedView)
    {
        return (int)selectedView.getTag();
    }
    public int getRealIndex(RecyclerView.ViewHolder selectedViewHolder)
    {
        return (int)selectedViewHolder.itemView.getTag();
    }
}
