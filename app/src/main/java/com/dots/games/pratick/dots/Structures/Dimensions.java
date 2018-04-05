package com.dots.games.pratick.dots.Structures;

import android.util.DisplayMetrics;

/**
 * Created by Pratick on 05-06-2015.
 */
public class Dimensions extends DisplayMetrics
{
    public int width;
    public int height;
    public int leftPadding;
    public int rightPadding;
    public int topPadding;
    public int bottomPadding;
    public Dimensions()
    {
        width=  0;
        height=  0;
        leftPadding=  0;
        rightPadding=  0;
        topPadding=  0;
        bottomPadding=  0;
    }
    public Dimensions(int width,int height,int leftPadding,int rightPadding,int topPadding,int bottomPadding)
    {
      this.width=  width;
      this.height=  height;
      this.leftPadding=  leftPadding;
      this.rightPadding=  rightPadding;
      this.topPadding=  topPadding;
      this.bottomPadding=  bottomPadding;
    }

}
