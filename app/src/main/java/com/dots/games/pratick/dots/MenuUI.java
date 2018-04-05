package com.dots.games.pratick.dots;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androidsx.rateme.RateMeDialog;
import com.androidsx.rateme.RateMeDialogTimer;
import com.dots.games.pratick.dots.Enums.MAXIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.Enums.MINIMIZED_LIST_VIEW_ITEM;
import com.dots.games.pratick.dots.Lists.MaximizedListFragment;
import com.dots.games.pratick.dots.Lists.MinimizedListFragment;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.INTELLIGENT_UI_CONSTANTS;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

/**
 * Created by Pratick on 30-05-2015.
 * Makes the Menu
 */
public class MenuUI
{
    Dimensions minimizedMenuListDimensions = new Dimensions();
    Dimensions maximizedMenuListDimensions = new Dimensions();

    MinimizedListFragment minimizedListFragment;
    MaximizedListFragment maximizedListFragment;

    DotsUI activity;
    public MenuUI(DotsUI activity,Dimensions activityDimensions)
    {
        getDimensions(activityDimensions);
        this.activity=activity;
    }

    public void createMenu()
    {
        createMinimizedList();
        createMaximizedList();
    }

    public void getDimensions(Dimensions activityDimensions)
    {
        minimizedMenuListDimensions.width = (int) (activityDimensions.width * INTELLIGENT_UI_CONSTANTS.MINIMIZED_MENU_TO_ACTIVITY_RATIO);
        maximizedMenuListDimensions.width = (int) (activityDimensions.width * INTELLIGENT_UI_CONSTANTS.MAXIMIZED_MENU_TO_ACTIVITY_RATIO) + 10;
    }

    public void createMinimizedList()
    {
        minimizedListFragment=new MinimizedListFragment();
        minimizedListFragment.setArguments(activity.getIntent().getExtras());
        minimizedListFragment.rootLayoutParams = new FrameLayout.LayoutParams(minimizedMenuListDimensions.width, ViewGroup.LayoutParams.WRAP_CONTENT);
        activity.getFragmentManager().beginTransaction().add(R.id.minimized_list_container,minimizedListFragment).addToBackStack(null).commit();
    }
    public void createMaximizedList()
    {
        maximizedListFragment=new MaximizedListFragment();
        maximizedListFragment.setArguments(activity.getIntent().getExtras());
        maximizedListFragment.rootLayoutParams = new FrameLayout.LayoutParams(maximizedMenuListDimensions.width, ViewGroup.LayoutParams.WRAP_CONTENT);
        activity.getFragmentManager().beginTransaction().add(R.id.maximized_list_container, maximizedListFragment).addToBackStack(null).commit();
    }

    public void changeData(MINIMIZED_LIST_VIEW_ITEM activeMinimizedListItem)
    {
        maximizedListFragment.changeData(activeMinimizedListItem);
    }


    public MAXIMIZED_LIST_VIEW_ITEM getSelectedMaximizedItem(int getMaximizedItemPosition)
    {
        return MAXIMIZED_LIST_VIEW_ITEM.values()[MINIMIZED_LIST_VIEW_ITEM.getMaximizedListOffset(minimizedListFragment.getActiveMinimizedItem()) + getMaximizedItemPosition];
    }

}
