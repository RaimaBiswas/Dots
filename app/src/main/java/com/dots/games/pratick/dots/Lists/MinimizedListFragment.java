package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dots.games.pratick.dots.Designers.DividerItemDecoration;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.Designers.Toasts;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.MINIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Created by Pratick on 29-05-2015.
 */
public class MinimizedListFragment extends Fragment
{
    public FrameLayout.LayoutParams rootLayoutParams;
    private View rootLayout;
    private RecyclerView minimizedList;
    private MinimizedListAdapter minimizedListAdapter;
    private ViewGroup.LayoutParams ItemLayoutParams;
    GestureDetector gestureDetector;
    MINIMIZED_LIST_VIEW_ITEM activeMinimizedList=MINIMIZED_LIST_VIEW_ITEM.SINGLE_PLAYER;
    RecyclerView.ViewHolder oldViewHolder;

    MinimizedListCommunicationInterface mCallback;

    public interface MinimizedListCommunicationInterface
    {
        void sendOnMinimizedListItemClick(MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (MinimizedListCommunicationInterface) activity;
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
        minimizedList=(RecyclerView)rootLayout.findViewById(R.id.recycler_view);
        rootLayout.setBackgroundResource(R.drawable.minimized_list_drop_shadow);
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.minimized_list_item_divider));
        minimizedList.addItemDecoration(itemDecoration);
        if(rootLayoutParams!=null)
        {
            setDimensions();
        }

        SlideInDownAnimator animator=new SlideInDownAnimator();
        animator.setRemoveDuration(500);
        animator.setAddDuration(200);
        minimizedList.setItemAnimator(animator);

        minimizedListAdapter=new MinimizedListAdapter(getActivity(),getDummyData(),ItemLayoutParams);
        minimizedList.setLayoutManager(new LinearLayoutManager(getActivity()));
        minimizedList.setAdapter(minimizedListAdapter);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (lock)
                {
                    try
                    {
                        lock.wait(800);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    addOneByOne(minimizedList.getItemAnimator().getAddDuration());
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                    oldViewHolder=minimizedList.findViewHolderForAdapterPosition(0);
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            minimizedListAdapter.clicked(oldViewHolder, 0);
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
        minimizedList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {
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
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        gestureDetector=new GestureDetector(getActivity(),new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                View selectedView=minimizedList.findChildViewUnder(e.getX(), e.getY());
                int selectedViewPosition=getRealIndex(selectedView);

                if(selectedViewPosition>3)
                {
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.waitForUpdate);
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                    return super.onSingleTapUp(e);
                }


                MINIMIZED_LIST_VIEW_ITEM selectedMinimizedList=MINIMIZED_LIST_VIEW_ITEM.values()[selectedViewPosition];

                //SuperActivityToast.create(getActivity(), selectedMinimizedList.name()+"",1000, Style.getStyle(Style.WHITE, SuperToast.Animations.FLYIN)).show();

                if(selectedMinimizedList!=activeMinimizedList)
                {
                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);

                    if(oldViewHolder!=null)
                    {
                        minimizedListAdapter.unclicked(oldViewHolder, getRealIndex(oldViewHolder));
                    }
                    activeMinimizedList = selectedMinimizedList;
                    mCallback.sendOnMinimizedListItemClick(activeMinimizedList);
                    oldViewHolder=minimizedList.getChildViewHolder(selectedView);
                    minimizedListAdapter.clicked(oldViewHolder, selectedViewPosition);

                }
                else
                {
                    Toasts.appPresetToast(getActivity(), Toasts.ToastMessages.alreadySelected);
                }
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
        ItemLayoutParams=new ViewGroup.LayoutParams(rootLayoutParams.width,rootLayoutParams.width);
    }
    public List<MinimizedListItem> getDummyData()
    {
        List<MinimizedListItem> data=new ArrayList<>();

        int[] imageIconResourceIDList = {};

        for(int i=0;i<imageIconResourceIDList.length;i++)
        {
            MinimizedListItem currentItem = new MinimizedListItem();
            currentItem.imageIconResourceID=imageIconResourceIDList[i];
            data.add(currentItem);
        }
        return data;
    }
    public List<MinimizedListItem> getData()
    {
        List<MinimizedListItem> data=new ArrayList<>();

        int[] imageIconResourceIDList =
        {
            R.drawable.minimized__single_player_icon,
            R.drawable.minimized__multi_player_icon,
            R.drawable.minimized__mixed_icon,
            R.drawable.minimized__achivements_icon,
            R.drawable.minimized__tournament_icon,
            R.drawable.minimized__settings_icon
        };

        for(int i=0;i<imageIconResourceIDList.length;i++)
        {
            MinimizedListItem currentItem = new MinimizedListItem();
            currentItem.imageIconResourceID=imageIconResourceIDList[i];
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
        final List<MinimizedListItem> newList=getData();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < newList.size(); i++)
                {
                    minimizedListAdapter.add(newList.get(i));
                }
            }
        });
    }
    public void addOneByOne(long waitTime)
    {
        final List<MinimizedListItem> newList=getData();
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
                        minimizedListAdapter.add(newList.get(index));
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

    public int getRealIndex(View selectedView)
    {
        return (int)selectedView.getTag();
    }
    public int getRealIndex(RecyclerView.ViewHolder selectedViewHolder)
    {
        return (int)selectedViewHolder.itemView.getTag();
    }

    public int getActiveMinimizedItemPosition()
    {
        return MINIMIZED_LIST_VIEW_ITEM.getPosition(activeMinimizedList);
    }
    public MINIMIZED_LIST_VIEW_ITEM getActiveMinimizedItem()
    {
        return activeMinimizedList;
    }

}
