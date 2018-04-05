package com.dots.games.pratick.dots.Enums;

/**
 * Created by Pratick on 05-06-2015.
 *
 */
public enum MAXIMIZED_LIST_VIEW_ITEM
{
    SINGLE_PLAYER__START,
    SINGLE_PLAYER__AI_LEVEL,
    SINGLE_PLAYER__GRID_SIZE,
    SINGLE_PLAYER__STYLE,

    MULTI_PLAYER__START,
    MULTI_PLAYER__NO_OF_PLAYERS,
    MULTI_PLAYER__GRID_SIZE,
    MULTI_PLAYER__STYLE,

    MIXED__START,
    MIXED__OPPONENTS,
    MIXED__GRID_SIZE,
    MIXED__STYLE,

    ACHIEVEMENT__SIGN_IN,
    ACHIEVEMENT__SHOW_TROPHY,
    ACHIEVEMENT__SHOW_LEADER_BOARD,
    ACHIEVEMENT__SHOW_OTHERS1;

    public static int getPosition(MAXIMIZED_LIST_VIEW_ITEM maximized_list_view_item)
    {
        int i=0;
        while (true)
        {
            if(MAXIMIZED_LIST_VIEW_ITEM.values()[i].equals(maximized_list_view_item))
            {
                return i;
            }
            i++;
        }
    }
}