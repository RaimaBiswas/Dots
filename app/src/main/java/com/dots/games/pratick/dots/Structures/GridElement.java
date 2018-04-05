package com.dots.games.pratick.dots.Structures;


import android.widget.ImageView;
import android.widget.ImageView;

import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.GRID_TYPE;

import java.util.ArrayList;

/**
 * Created by Pratick on 05-06-2015.
 */
public class GridElement
{
    public GRID_TYPE gridBackEnd;
    public ImageView gridFrontEnd;

    public static int ripeIOne=-1;
    public static int ripeJOne=-1;

    public static int ripeITwo=-1;
    public static int ripeJTwo=-1;

    public static ArrayList<Integer> ripeI=new ArrayList();
    public static ArrayList<Integer> ripeJ=new ArrayList();

    public static ArrayList<Integer> safeBoxI=new ArrayList();
    public static ArrayList<Integer> safeBoxJ=new ArrayList();

    public static int safeCount=0;

    public static void reset()
    {
        ripeIOne=-1;
        ripeJOne=-1;
        ripeITwo=-1;
        ripeJTwo=-1;

        ripeI=new ArrayList();
        ripeJ=new ArrayList();
        safeBoxI=new ArrayList();
        safeBoxJ=new ArrayList();
    }
    public GridElement()
    {
        gridBackEnd=null;
        gridFrontEnd=null;
    }
    public GridElement(DotsUI context)
    {
        gridBackEnd=null;
        gridFrontEnd=new ImageView(context);
    }
}
