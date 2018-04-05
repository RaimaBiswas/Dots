package com.dots.games.pratick.dots.Lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.Dimensions;

import java.util.Collections;
import java.util.List;

/**
 * Created by Pratick on 29-05-2015.
 */
public class MinimizedListAdapter extends RecyclerView.Adapter<MinimizedListAdapter.MinimizedListViewHolder>
{
    private Context context;
    private LayoutInflater inflater;
    private List<MinimizedListItem> data= Collections.emptyList();
    private ViewGroup.LayoutParams ItemLayoutParams;

    public MinimizedListAdapter(Context context, List<MinimizedListItem> data,ViewGroup.LayoutParams ItemLayoutParams)
    {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.data=data;
        this.ItemLayoutParams=ItemLayoutParams;
    }

    @Override
    public MinimizedListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View MinimizedListItemLayout = inflater.inflate(R.layout.minimized_list_item, viewGroup, false);
        if(ItemLayoutParams!=null)
        {
            MinimizedListItemLayout.setLayoutParams(ItemLayoutParams);
        }
        MinimizedListViewHolder holder=new MinimizedListViewHolder(MinimizedListItemLayout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MinimizedListViewHolder minimizedListViewHolder, int i)
    {
        MinimizedListItem currentItem=data.get(i);
        minimizedListViewHolder.minimizedListItemIcon.setImageResource(currentItem.imageIconResourceID);
        minimizedListViewHolder.itemView.setTag(new Integer(i));

        Dimensions minimizedListItemIconPadding =new Dimensions();
        minimizedListItemIconPadding.leftPadding=(int)(ItemLayoutParams.height*(1d/6d));
        minimizedListItemIconPadding.rightPadding=(int)(ItemLayoutParams.height*(1d/6d));
        minimizedListItemIconPadding.topPadding=(int)(ItemLayoutParams.height*(1d/4d));
        minimizedListItemIconPadding.bottomPadding=(int)(ItemLayoutParams.height*(1d/4d));

        minimizedListViewHolder.minimizedListItemIcon.setPadding
        (
                minimizedListItemIconPadding.leftPadding,
                minimizedListItemIconPadding.topPadding,
                minimizedListItemIconPadding.rightPadding,
                minimizedListItemIconPadding.bottomPadding
        );
    }

    public void addAt(MinimizedListItem minimizedListItem,int position)
    {
        data.add(position,minimizedListItem);
        notifyItemInserted(position);
    }

    public void add(MinimizedListItem minimizedListItem)
    {
        addAt(minimizedListItem, data.size());
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



    public void clicked(RecyclerView.ViewHolder viewHolder,int position)
    {
        ImageView image=((MinimizedListViewHolder)viewHolder).minimizedListItemIcon;
        image.setBackgroundResource(R.drawable.minimized_list_item_background_clicked);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        image.startAnimation(anim_in);
    }
    public void unclicked(RecyclerView.ViewHolder viewHolder,int position)
    {
        ImageView image=((MinimizedListViewHolder)viewHolder).minimizedListItemIcon;
        image.setBackgroundResource(R.drawable.minimized_list_item_background);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        image.startAnimation(anim_in);
    }


    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class MinimizedListViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView minimizedListItemIcon;
        public MinimizedListViewHolder(View itemView)
        {
            super(itemView);
            minimizedListItemIcon=(ImageView)itemView.findViewById(R.id.minimized_list_item_icon);
        }
    }


}
