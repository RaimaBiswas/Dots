package com.dots.games.pratick.dots.Lists;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.dots.games.pratick.dots.Designers.Players;
import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.R;
import com.dots.games.pratick.dots.Structures.Dimensions;
import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.util.Collections;
import java.util.List;
import java.util.Random;



import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by Pratick on 29-05-2015.
 */
public class GridStyleListAdapter extends RecyclerView.Adapter<GridStyleListAdapter.GridStyleListViewHolder>
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<GridStyleListItem> data= Collections.emptyList();
    private int minimumItemHeight;
    private int maximumItemHeight;
    private int selectedPlayer;

    public GridStyleListAdapter(Activity activity, List<GridStyleListItem> data,int minimumItemHeight,int maximumItemHeight,int selectedPlayer)
    {
        this.activity=activity;
        inflater=LayoutInflater.from(activity);
        this.data=data;
        this.minimumItemHeight=minimumItemHeight;
        this.maximumItemHeight=maximumItemHeight;
        this.selectedPlayer=selectedPlayer;
    }

    @Override
    public GridStyleListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View GridStyleListItemLayout = inflater.inflate(R.layout.grid_style_ui, viewGroup, false);
        GridStyleListViewHolder holder=new GridStyleListViewHolder(GridStyleListItemLayout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GridStyleListViewHolder gridStyleListViewHolder, int i)
    {
        final StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) gridStyleListViewHolder.itemView.getLayoutParams();
        layoutParams.setMargins(1, 1, 1, 1);
        layoutParams.height=(new Random()).nextInt(maximumItemHeight - minimumItemHeight) + minimumItemHeight;

        GridStyleListItem currentItem=data.get(i);

        gridStyleListViewHolder.itemView.setBackgroundColor(currentItem.backgroundDrawableResourceID);
        gridStyleListViewHolder.itemView.setPadding(10, 10, 10, 10);

        gridStyleListViewHolder.gridStyleListHeaderText.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
        gridStyleListViewHolder.gridStyleListHeaderText.setText(currentItem.headerTextResourceID);
        gridStyleListViewHolder.gridStyleListHeaderText.getLayoutParams().height=layoutParams.height;

        if(getBrightness(currentItem.backgroundDrawableResourceID)<=129)
        {
            gridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFFFFFFFF);
        }
        else
        {
            gridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFF000000);
        }

        gridStyleListViewHolder.itemView.setTag(new Integer(i));

        if((int)(gridStyleListViewHolder.itemView.getTag())==7)
        {
            layoutParams.setFullSpan(true);
        }
        setDialog(gridStyleListViewHolder);

    }

    public void getColorDialog(final GridStyleListViewHolder selectedGridStyleListViewHolder,int initialColor)
    {

    }

    String editNameText="";
    public void setDialog(final GridStyleListViewHolder selectedGridStyleListViewHolder)
    {
        selectedGridStyleListViewHolder.gridStyleListHeaderText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                if(selectedPlayer!=gameDetails.getNumberOfPlayers())
                {
                    switch ((int)selectedGridStyleListViewHolder.itemView.getTag())
                    {
                        case 0:
                        {
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, gameDetails.getPlayerBarColor(selectedPlayer), new AmbilWarnaDialog.OnAmbilWarnaListener()
                            {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                                    selectedGridStyleListViewHolder.itemView.setBackgroundColor(color);
                                    gameDetails.updatePlayerBarColor(color,selectedPlayer);


                                    if(getBrightness(color)<=129)
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFFFFFFFF);
                                    }
                                    else
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFF000000);
                                    }

                                    // color is the color selected by the user.
                                }

                                @Override
                                public void onCancel(AmbilWarnaDialog dialog)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    // cancel was selected by the user
                                }
                            });
                            dialog.getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            dialog.show();
                            //DotsUI.exitDetails.overrideBackButton=false;
                            //DotsUI.exitDetails.setStyleItemMenu(dialog.getDialog());
                            break;
                        }
                        case 1:
                        {
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, gameDetails.getPlayerBarColor(selectedPlayer), new AmbilWarnaDialog.OnAmbilWarnaListener()
                            {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                                    selectedGridStyleListViewHolder.itemView.setBackgroundColor(color);
                                    gameDetails.updatePlayerBarColor(color,selectedPlayer);

                                    if(getBrightness(color)<=129)
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFFFFFFFF);
                                    }
                                    else
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFF000000);
                                    }

                                    // color is the color selected by the user.
                                }

                                @Override
                                public void onCancel(AmbilWarnaDialog dialog)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    // cancel was selected by the user
                                }
                            });
                            dialog.getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            dialog.show();
                            //DotsUI.exitDetails.overrideBackButton=false;
                            //DotsUI.exitDetails.setStyleItemMenu(dialog.getDialog());
                            break;
                        }
                        case 2:
                        {
                            final Dialog textDialog=new Dialog(activity);
                            textDialog.setContentView(R.layout.edit_name_dialog_ui);

                            TextView title =(TextView)textDialog.findViewById(R.id.title);

                            title.setText("Set Name : ");
                            title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

                            TextView content =(TextView)textDialog.findViewById(R.id.content);
                            content.setText("Current Name : " + gameDetails.getPlayerName(selectedPlayer));
                            content.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

                            EditText input =(EditText)textDialog.findViewById(R.id.input);
                            input.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
                            input.addTextChangedListener(new TextWatcher()
                            {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                                @Override
                                public void afterTextChanged(Editable s)
                                {
                                    editNameText=s.toString();
                                }
                            });

                            MDButton negative=(MDButton)textDialog.findViewById(R.id.buttonDefaultNegative);
                            MDButton positive=(MDButton)textDialog.findViewById(R.id.buttonDefaultPositive);

                            positive.setText("OK");
                            positive.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));
                            negative.setText("CANCEL");
                            negative.setTypeface(Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf"));

                            positive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (editNameText.equals("")) {
                                        Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                        SuperActivityToast.create(activity, "Not Happening Bro !!", 100, Style.getStyle(Style.PURPLE, SuperToast.Animations.FLYIN)).show();
                                    } else {
                                        Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                                        gameDetails.updatePlayerName(editNameText, selectedPlayer);
                                        editNameText = "";
                                        textDialog.dismiss();
                                    }
                                }
                            });

                            negative.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    textDialog.dismiss();
                                }
                            });


                            textDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            textDialog.show();
                            //DotsUI.exitDetails.overrideBackButton=false;
                            //DotsUI.exitDetails.setStyleItemMenu(textDialog);
                            break;
                        }
                    }
                }
                else
                {
                    switch ((int)selectedGridStyleListViewHolder.itemView.getTag())
                    {
                        case 0:
                        {
                            AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, gameDetails.getPreviewBarColor(), new AmbilWarnaDialog.OnAmbilWarnaListener()
                            {
                                @Override
                                public void onOk(AmbilWarnaDialog dialog, int color)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameSuccessfulTouch);
                                    selectedGridStyleListViewHolder.itemView.setBackgroundColor(color);
                                    gameDetails.setPreviewBarColor(color);

                                    if(getBrightness(color)<=129)
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFFFFFFFF);
                                    }
                                    else
                                    {
                                        selectedGridStyleListViewHolder.gridStyleListHeaderText.setTextColor(0xFF000000);
                                    }

                                    // color is the color selected by the user.
                                }

                                @Override
                                public void onCancel(AmbilWarnaDialog dialog)
                                {
                                    Players.playShortSound(Players.PLAYER_TYPE.nonGameUnsuccessfulTouch);
                                    // cancel was selected by the user
                                }
                            });
                            dialog.getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            dialog.show();
                            //DotsUI.exitDetails.overrideBackButton=false;
                            //DotsUI.exitDetails.setStyleItemMenu(dialog.getDialog());
                            break;
                        }
                    }

                }


            }
        });
    }

    public double getBrightness(int color)
    {
        return Math.sqrt
        (
            0.241*Math.pow(Color.red(color),2)  +
            0.69*Math.pow(Color.green(color),2) +
            0.068*Math.pow(Color.blue(color),2)
        );
    }

    public void addAt(GridStyleListItem gridStyleListItem,int position)
    {
        data.add(position,gridStyleListItem);
        notifyItemInserted(position);
    }

    public void add(GridStyleListItem gridStyleListItem)
    {
        addAt(gridStyleListItem, data.size());
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

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    class GridStyleListViewHolder extends RecyclerView.ViewHolder
    {
        public TextView gridStyleListHeaderText;

        public GridStyleListViewHolder(View itemView)
        {
            super(itemView);
            gridStyleListHeaderText       =(TextView)itemView.findViewById(R.id.grid_style_item_header);
        }
    }


}
