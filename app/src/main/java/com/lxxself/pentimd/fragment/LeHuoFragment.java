package com.lxxself.pentimd.fragment;

import android.os.Bundle;

/**
 * Created by lxxself on 2015/11/18.
 */
public class LeHuoFragment extends BaseFragment{
    private static final String title = "乐活";
    private static LeHuoFragment instance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = "lehuo";

    }
    public static LeHuoFragment getInstance() {
        if (instance == null) {
            instance = new LeHuoFragment();
        }
        return instance;
    }
}
