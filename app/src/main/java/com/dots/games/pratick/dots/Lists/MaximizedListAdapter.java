package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Player;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pratick on 30-05-2015.
 */
public class MaximizedListAdapter extends RecyclerView.Adapter<MaximizedListAdapter.MaximizedListViewHolder>
{
    private LayoutInflater inflater;
    private List<MaximizedListItem> data= Collections.emptyList();
    private ViewGroup.LayoutParams ItemLayoutParams;
    private Typeface ItemTextTypeface;
    private DotsUI activity;

    public MaximizedListAdapter(DotsUI context, List<MaximizedListItem> data,ViewGroup.LayoutParams ItemLayoutParams,Typeface ItemTextTypeface)
    {
        activity=context;
        inflater=LayoutInflater.from(context);
        this.data=data;
        this.ItemLayoutParams=ItemLayoutParams;
        this.ItemTextTypeface=ItemTextTypeface;
    }

    @Override
    public MaximizedListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View MaximizedListItemLayout = inflater.inflate(R.layout.maximized_list_item,viewGroup,false);
        if(ItemLayoutParams!=null)
        {
            MaximizedListItemLayout.setLayoutParams(ItemLayoutParams);
        }

        MaximizedListViewHolder holder=new MaximizedListViewHolder(MaximizedListItemLayout);

        return holder;
    }

    @Override
    public void onBindViewHolder(MaximizedListViewHolder maximizedListViewHolder, int i)
    {
        MaximizedListItem currentItem=data.get(i);
        maximizedListViewHolder.maximizedListItemIcon.setBackgroundResource(currentItem.backgroundDrawableResourceID);

        maximizedListViewHolder.maximizedListItemIcon.setImageResource(currentItem.imageIconResourceID);
        maximizedListViewHolder.maximizedListItemText.setText(currentItem.textResourceID);
        if(i==0)
        {
            activity.signInOutText=maximizedListViewHolder.maximizedListItemText;
        }
        maximizedListViewHolder.itemView.setTag(new Integer(i));

        Dimensions maximizedListItemIconPadding =new Dimensions();
        maximizedListItemIconPadding.leftPadding=(int)(ItemLayoutParams.height*(1d/3d))+10;
        maximizedListItemIconPadding.rightPadding=(int)(ItemLayoutParams.height*(1d/3d));
        maximizedListItemIconPadding.topPadding=(int)(ItemLayoutParams.height*(1d/3d));
        maximizedListItemIconPadding.bottomPadding=(int)(ItemLayoutParams.height*(1d/3d));

        maximizedListViewHolder.maximizedListItemIcon.setPadding
        (
            maximizedListItemIconPadding.leftPadding,
            maximizedListItemIconPadding.topPadding,
            maximizedListItemIconPadding.rightPadding,
            maximizedListItemIconPadding.bottomPadding

        );
        maximizedListViewHolder.maximizedListItemText.setTextSize((float) (maximizedListItemIconPadding.bottomPadding * (12d / 100d)));
        maximizedListViewHolder.maximizedListItemText.setTypeface(ItemTextTypeface);

        Dimensions maximizedListItemTextPadding =new Dimensions();
        maximizedListItemTextPadding.leftPadding=(int)(ItemLayoutParams.height*(1d/40d));
        maximizedListItemTextPadding.rightPadding=(int)(ItemLayoutParams.height*(1d/40d));
        maximizedListItemTextPadding.topPadding=(int)(ItemLayoutParams.height-maximizedListItemIconPadding.bottomPadding+(maximizedListItemIconPadding.bottomPadding)*(1d/2d)-(maximizedListViewHolder.maximizedListItemText.getTextSize()));
        maximizedListItemTextPadding.bottomPadding = 0;

        maximizedListViewHolder.maximizedListItemText.setPadding
        (
            maximizedListItemTextPadding.leftPadding,
            maximizedListItemTextPadding.topPadding,
            maximizedListItemTextPadding.rightPadding,
            maximizedListItemTextPadding.bottomPadding
        );

    }

    public void removeAt(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void removeView(View view)
    {
        removeAt(data.indexOf(view));
    }

    public void addAt(MaximizedListItem maximizedListItem,int position)
    {
        data.add(position,maximizedListItem);
        notifyItemInserted(position);
    }
    public void add(MaximizedListItem maximizedListItem)
    {
        addAt(maximizedListItem,data.size());
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class MaximizedListViewHolder extends RecyclerView.ViewHolder
    {
        ImageView maximizedListItemIcon;
        TextView maximizedListItemText;
        public MaximizedListViewHolder(View itemView)
        {
            super(itemView);
            maximizedListItemIcon=(ImageView)itemView.findViewById(R.id.maximized_list_item_icon);
            maximizedListItemText=(TextView)itemView.findViewById(R.id.maximized_list_item_text);


        }
    }
}
