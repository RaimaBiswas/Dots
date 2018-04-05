package com.dots.games.pratick.dots.Enums;


import com.dots.games.pratick.dots.Structures.GridElement;

/**
 * Created by Pratick on 05-06-2015.
 *
 */
public enum GRID_TYPE
{
    BOX_SAFE                   , BOX_UNSAFE                   , BOX_RIPE                   , BOX_FILLED                   ,
    BAR_HORIZONTAL_NORMAL_SAFE , BAR_HORIZONTAL_NORMAL_UNSAFE , BAR_HORIZONTAL_NORMAL_RIPE , BAR_HORIZONTAL_NORMAL_FILLED ,
    BAR_HORIZONTAL_TOP_SAFE    , BAR_HORIZONTAL_TOP_UNSAFE    , BAR_HORIZONTAL_TOP_RIPE    , BAR_HORIZONTAL_TOP_FILLED    ,
    BAR_HORIZONTAL_BOTTOM_SAFE , BAR_HORIZONTAL_BOTTOM_UNSAFE , BAR_HORIZONTAL_BOTTOM_RIPE , BAR_HORIZONTAL_BOTTOM_FILLED ,
    BAR_VERTICAL_NORMAL_SAFE   , BAR_VERTICAL_NORMAL_UNSAFE   , BAR_VERTICAL_NORMAL_RIPE   , BAR_VERTICAL_NORMAL_FILLED   ,
    BAR_VERTICAL_LEFT_SAFE     , BAR_VERTICAL_LEFT_UNSAFE     , BAR_VERTICAL_LEFT_RIPE     , BAR_VERTICAL_LEFT_FILLED     ,
    BAR_VERTICAL_RIGHT_SAFE    , BAR_VERTICAL_RIGHT_UNSAFE    , BAR_VERTICAL_RIGHT_RIPE    , BAR_VERTICAL_RIGHT_FILLED    ,


    BOX,BAR,DOT,TEMP,TEMP_2;

    public static boolean isBarTopHorizontal(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_TOP_SAFE: case BAR_HORIZONTAL_TOP_FILLED: case BAR_HORIZONTAL_TOP_UNSAFE: case BAR_HORIZONTAL_TOP_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarNormalHorizontal(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_NORMAL_SAFE: case BAR_HORIZONTAL_NORMAL_FILLED: case BAR_HORIZONTAL_NORMAL_UNSAFE: case BAR_HORIZONTAL_NORMAL_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarBottomHorizontal(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_BOTTOM_SAFE : case BAR_HORIZONTAL_BOTTOM_FILLED: case BAR_HORIZONTAL_BOTTOM_UNSAFE: case BAR_HORIZONTAL_BOTTOM_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarHorizontal(GRID_TYPE bar)
    {
        return isBarBottomHorizontal(bar) || isBarTopHorizontal(bar) || isBarNormalHorizontal(bar);
    }

    public static boolean isBarLeftVertical(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_VERTICAL_LEFT_SAFE: case BAR_VERTICAL_LEFT_FILLED: case BAR_VERTICAL_LEFT_UNSAFE: case BAR_VERTICAL_LEFT_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarNormalVertical(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_VERTICAL_NORMAL_SAFE: case BAR_VERTICAL_NORMAL_FILLED: case BAR_VERTICAL_NORMAL_UNSAFE: case BAR_VERTICAL_NORMAL_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarRightVertical(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_VERTICAL_RIGHT_SAFE : case BAR_VERTICAL_RIGHT_FILLED: case BAR_VERTICAL_RIGHT_UNSAFE: case BAR_VERTICAL_RIGHT_RIPE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarVertical(GRID_TYPE bar)
    {
        return isBarLeftVertical(bar) || isBarRightVertical(bar) || isBarNormalVertical(bar);
    }

    public static boolean isBarNormal(GRID_TYPE bar)
    {
        return isBarNormalVertical(bar) || isBarNormalHorizontal(bar);
    }
    public static boolean isBarSafe(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_NORMAL_SAFE : case BAR_HORIZONTAL_TOP_SAFE : case BAR_HORIZONTAL_BOTTOM_SAFE:
            case BAR_VERTICAL_NORMAL_SAFE   : case BAR_VERTICAL_LEFT_SAFE  : case BAR_VERTICAL_RIGHT_SAFE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarUnsafe(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_NORMAL_UNSAFE : case BAR_HORIZONTAL_TOP_UNSAFE : case BAR_HORIZONTAL_BOTTOM_UNSAFE:
            case BAR_VERTICAL_NORMAL_UNSAFE   : case BAR_VERTICAL_LEFT_UNSAFE  : case BAR_VERTICAL_RIGHT_UNSAFE:
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isBarRipe(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_NORMAL_RIPE : case BAR_HORIZONTAL_TOP_RIPE : case BAR_HORIZONTAL_BOTTOM_RIPE:
            case BAR_VERTICAL_NORMAL_RIPE   : case BAR_VERTICAL_LEFT_RIPE  : case BAR_VERTICAL_RIGHT_RIPE:
        {
            return true;
        }
        }
        return false;
    }
    public static boolean isBarFilled(GRID_TYPE bar)
    {
        switch (bar)
        {
            case BAR_HORIZONTAL_NORMAL_FILLED : case BAR_HORIZONTAL_TOP_FILLED : case BAR_HORIZONTAL_BOTTOM_FILLED:
            case BAR_VERTICAL_NORMAL_FILLED   : case BAR_VERTICAL_LEFT_FILLED  : case BAR_VERTICAL_RIGHT_FILLED:
            {
                return true;
            }
        }
        return false;
    }

    public static GRID_TYPE toUnsafe(GRID_TYPE bar)
    {
        if(isBarUnsafe(bar))
        {
            return bar;
        }
        if(isBarSafe(bar))
        {
            GridElement.safeCount--;
        }

        if(isBarHorizontal(bar))
        {
            if (isBarTopHorizontal(bar))
            {
                return BAR_HORIZONTAL_TOP_UNSAFE;
            }
            else if (isBarBottomHorizontal(bar))
            {
                return BAR_HORIZONTAL_BOTTOM_UNSAFE;
            }
            else
            {
                return BAR_HORIZONTAL_NORMAL_UNSAFE;
            }
        }
        else
        {
            if(isBarLeftVertical(bar))
            {
                return BAR_VERTICAL_LEFT_UNSAFE;
            }
            else if(isBarRightVertical(bar))
            {
                return BAR_VERTICAL_RIGHT_UNSAFE;
            }
            else
            {
                return BAR_VERTICAL_NORMAL_UNSAFE;
            }
        }

    }
    public static GRID_TYPE toRipe(GRID_TYPE bar)
    {
        if(isBarRipe(bar))
        {
            return bar;
        }
        if(isBarSafe(bar))
        {
            GridElement.safeCount--;
        }
        if(isBarHorizontal(bar))
        {
            if (isBarTopHorizontal(bar))
            {
                return BAR_HORIZONTAL_TOP_RIPE;
            }
            else if (isBarBottomHorizontal(bar))
            {
                return BAR_HORIZONTAL_BOTTOM_RIPE;
            }
            else
            {
                return BAR_HORIZONTAL_NORMAL_RIPE;
            }
        }
        else
        {
            if(isBarLeftVertical(bar))
            {
                return BAR_VERTICAL_LEFT_RIPE;
            }
            else if(isBarRightVertical(bar))
            {
                return BAR_VERTICAL_RIGHT_RIPE;
            }
            else
            {
                return BAR_VERTICAL_NORMAL_RIPE;
            }
        }

    }
    public static GRID_TYPE toFilled(GRID_TYPE bar)
    {
        if(isBarFilled(bar))
        {
            return bar;
        }
        if(isBarSafe(bar))
        {
            GridElement.safeCount--;
        }
        if(isBarHorizontal(bar))
        {
            if (isBarTopHorizontal(bar))
            {
                return BAR_HORIZONTAL_TOP_FILLED;
            }
            else if (isBarBottomHorizontal(bar))
            {
                return BAR_HORIZONTAL_BOTTOM_FILLED;
            }
            else
            {
                return BAR_HORIZONTAL_NORMAL_FILLED;
            }
        }
        else
        {
            if(isBarLeftVertical(bar))
            {
                return BAR_VERTICAL_LEFT_FILLED;
            }
            else if(isBarRightVertical(bar))
            {
                return BAR_VERTICAL_RIGHT_FILLED;
            }
            else
            {
                return BAR_VERTICAL_NORMAL_FILLED;
            }
        }

    }


}
