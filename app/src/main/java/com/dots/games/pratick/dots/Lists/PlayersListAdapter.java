package com.dots.games.pratick.dots.Lists;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dots.games.pratick.dots.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Pratick on 29-05-2015.
 *
 */
public class PlayersListAdapter extends RecyclerView.Adapter<PlayersListAdapter.PlayerListViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<PlayersListItem> data= Collections.emptyList();
    private int itemHeight;

    public PlayersListAdapter(Activity activity, List<PlayersListItem> data, int itemHeight)
    {
        this.activity=activity;
        inflater=LayoutInflater.from(activity);
        this.data=data;
        this.itemHeight=itemHeight;
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    @Override
    public PlayerListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View PlayersListItemLayout = inflater.inflate(R.layout.player_list_item, viewGroup, false);
        return new PlayerListViewHolder(PlayersListItemLayout);
    }

    @Override
    public void onBindViewHolder(final PlayerListViewHolder playerListViewHolder, int i)
    {
        PlayersListItem currentItem=data.get(i);

        int itemIconPadding=((int)(itemHeight*10d/100d));
        int itemMainTextPadding=((int)(itemHeight*20d/100d));

        RelativeLayout.LayoutParams playerIconImageLayoutParams=new RelativeLayout.LayoutParams(itemHeight,itemHeight);
        playerIconImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        RelativeLayout.LayoutParams playerHeaderTextLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerHeaderTextLayoutParams.setMargins(itemIconPadding, itemIconPadding, 0, 0);

        RelativeLayout.LayoutParams playerMainTextLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerMainTextLayoutParams.setMargins(itemMainTextPadding, 0, 0, itemIconPadding);
        playerMainTextLayoutParams.addRule(RelativeLayout.BELOW, R.id.player_item_title);
        playerMainTextLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.player_item_title);

        RelativeLayout.LayoutParams playerDifficultyTextLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playerDifficultyTextLayoutParams.setMargins(0, 0, 0, itemMainTextPadding);
        playerDifficultyTextLayoutParams.addRule(RelativeLayout.BELOW, R.id.player_item_text);
        playerDifficultyTextLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.player_item_text);

        playerListViewHolder.itemView.setBackgroundColor(activity.getResources().getColor(currentItem.backgroundColorResourceID));

        playerListViewHolder.PlayerListIconImage.setImageResource(currentItem.playerItemIconResourceID);
        playerListViewHolder.PlayerListIconImage.setLayoutParams(playerIconImageLayoutParams);
        playerListViewHolder.PlayerListIconImage.setPadding(itemMainTextPadding, itemMainTextPadding, itemMainTextPadding, itemMainTextPadding);

        playerListViewHolder.PlayerListHeaderText.setText(activity.getResources().getString(currentItem.playerItemHeaderItemResourceID));
        playerListViewHolder.PlayerListHeaderText.setLayoutParams(playerHeaderTextLayoutParams);
        playerListViewHolder.PlayerListHeaderText.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

        playerListViewHolder.PlayerListMainText.setText(activity.getResources().getString(currentItem.playerItemTextResourceID));
        playerListViewHolder.PlayerListMainText.setLayoutParams(playerMainTextLayoutParams);
        playerListViewHolder.PlayerListMainText.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

        playerListViewHolder.PlayerListDifficultyText.setText(activity.getResources().getString(currentItem.playerItemDifficultyTextResourceID));
        playerListViewHolder.PlayerListDifficultyText.setLayoutParams(playerDifficultyTextLayoutParams);
    }



    public void addAt(PlayersListItem playersListItem,int position)
    {
        data.add(position,playersListItem);
        notifyItemInserted(position);
    }
    public void add(PlayersListItem playersListItem)
    {
        addAt(playersListItem, data.size());
    }
    public void removeAt(int position)
    {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void removeView(View view)
    {
        data.remove(view);
    }

    public List<PlayersListItem> getList()
    {
        return this.data;
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class PlayerListViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView PlayerListIconImage;
        public TextView  PlayerListHeaderText;
        public TextView  PlayerListMainText;
        public TextView  PlayerListDifficultyText;

        public PlayerListViewHolder(View itemView)
        {
            super(itemView);
            PlayerListIconImage =(ImageView)itemView.findViewById(R.id.player_item_icon);
            PlayerListHeaderText =(TextView)itemView.findViewById(R.id.player_item_title);
            PlayerListMainText =(TextView)itemView.findViewById(R.id.player_item_text);
            PlayerListDifficultyText =(TextView)itemView.findViewById(R.id.player_item_difficulty_level);
        }
    }


}
