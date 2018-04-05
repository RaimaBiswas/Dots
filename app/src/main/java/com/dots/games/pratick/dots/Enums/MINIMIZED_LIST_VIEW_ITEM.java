package com.dots.games.pratick.dots.Enums;

/**
 * Created by Pratick on 05-06-2015.
 *
 */

public enum MINIMIZED_LIST_VIEW_ITEM
{
    SINGLE_PLAYER,MULTI_PLAYER,MIXED,ACHIEVEMENT,TOURNAMENT,SETTINGS,DUMMY;

    public static int getPosition(MINIMIZED_LIST_VIEW_ITEM currentMinimizedItem)
    {
        switch (currentMinimizedItem)
        {
            case SINGLE_PLAYER:
            {
                return 0;
            }
            case MULTI_PLAYER:
            {
                return 1;
            }
            case MIXED:
            {
                return 2;
            }
            case ACHIEVEMENT:
            {
                return 3;
            }
            case TOURNAMENT:
            {
                return 4;
            }
            case SETTINGS:
            {
                return 5;
            }
            default:
            case DUMMY:
            {
                return -1;
            }
        }
    }
    public static int getMaximizedListOffset(MINIMIZED_LIST_VIEW_ITEM currentMinimizedItem)
    {
        switch (currentMinimizedItem)
        {
            case SINGLE_PLAYER:
            {
                return 0;
            }
            case MULTI_PLAYER:
            {
                return 4;
            }
            case MIXED:
            {
                return 8;
            }
            case ACHIEVEMENT:
            {
                return 12;
            }
            case TOURNAMENT:
            {
                return 0;
            }
            case SETTINGS:
            {
                return 0;
            }
            default:
            case DUMMY:
            {
                return -1;
            }
        }
    }
}
