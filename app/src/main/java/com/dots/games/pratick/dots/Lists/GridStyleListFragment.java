package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dots.games.pratick.dots.Designers.DividerItemDecoration;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.MINIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Created by Pratick on 29-05-2015.
 */
public class GridStyleListFragment extends Fragment
{
    public FrameLayout.LayoutParams rootLayoutParams;
    public int selectedPlayer;

    private View rootLayout;
    private RecyclerView gridStyleList;
    private GridStyleListAdapter gridStyleListAdapter;
    GestureDetector gestureDetector;

    GridStyleListCommunicationInterface mCallback;

    public interface GridStyleListCommunicationInterface
    {
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (GridStyleListCommunicationInterface) activity;
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
        gridStyleList=(RecyclerView)rootLayout.findViewById(R.id.recycler_view);

        setDimensions();
        int minimumItemHeight=rootLayoutParams.height/4;
        int maximumItemHeight=rootLayoutParams.height/2;

        gridStyleListAdapter=new GridStyleListAdapter(getActivity(),getDummyData(),minimumItemHeight,maximumItemHeight,selectedPlayer);

        OvershootInLeftAnimator animator=new OvershootInLeftAnimator();
        animator.setRemoveDuration(500);
        animator.setAddDuration(200);
        gridStyleList.setItemAnimator(animator);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (lock)
                {
                    addOneByOne(gridStyleList.getItemAnimator().getAddDuration());
                }
            }
        }).start();

        gridStyleList.setAdapter(gridStyleListAdapter);
        gridStyleList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        //(rootLayout.findViewById(R.id.recycler_view_shadow)).setPadding(20, 20, 20, 20);
        (rootLayout.findViewById(R.id.recycler_view_shadow)).setBackgroundResource(R.drawable.grid_style_drop_shadow);


        return rootLayout;
    }

    public void setDimensions()
    {
        rootLayout.setLayoutParams(rootLayoutParams);

    }
    public List<GridStyleListItem> getDummyData()
    {
        return new ArrayList<>();
    }
    public List<GridStyleListItem> getPlayerData()
    {
        List<GridStyleListItem> data=new ArrayList<>();

        int[] backgroundDrawableResourceIDList =
        {
            gameDetails.getPlayerBarColor(selectedPlayer),
            gameDetails.getPlayerBoxColor(selectedPlayer),
            getActivity().getResources().getColor(R.color.Amber500)

        };
        int[] headerTextResourceIDList         =
        {
            R.string.change_bar_color,
            R.string.change_box_color,
            R.string.change_name
        };

        for(int i=0;i<backgroundDrawableResourceIDList.length;i++)
        {
            GridStyleListItem currentItem = new GridStyleListItem();

            currentItem.backgroundDrawableResourceID = backgroundDrawableResourceIDList[i];
            currentItem.headerTextResourceID         = headerTextResourceIDList[i];
            data.add(currentItem);
        }
        return data;
    }
    public List<GridStyleListItem> getSettingsData()
    {
        List<GridStyleListItem> data=new ArrayList<>();

        int[] backgroundDrawableResourceIDList =
        {
                gameDetails.getPreviewBarColor()
        };
        int[] headerTextResourceIDList         =
        {
                R.string.change_preview_bar_color,
        };

        for(int i=0;i<backgroundDrawableResourceIDList.length;i++)
        {
            GridStyleListItem currentItem = new GridStyleListItem();

            currentItem.backgroundDrawableResourceID = backgroundDrawableResourceIDList[i];
            currentItem.headerTextResourceID         = headerTextResourceIDList[i];
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
        final List<GridStyleListItem> newList=getPlayerData();
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < newList.size(); i++)
                {
                    gridStyleListAdapter.add(newList.get(i));
                }
            }
        });
    }
    public void addOneByOne(long waitTime)
    {
        final List<GridStyleListItem> newList;

        if(selectedPlayer!=gameDetails.getNumberOfPlayers())
        {
            newList=getPlayerData();
        }
        else
        {
            newList=getSettingsData();
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
                        gridStyleListAdapter.add(newList.get(index));
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

}
