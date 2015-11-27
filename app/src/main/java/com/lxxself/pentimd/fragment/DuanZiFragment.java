package com.lxxself.pentimd.fragment;


import android.os.Bundle;

/**
 * Created by lxxself on 2015/11/18.
 */
public class DuanZiFragment extends BaseFragment {
    private static DuanZiFragment instance;
    private static final String title = "段子";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = "duanzi";

    }    public static DuanZiFragment getInstance() {
        if (instance == null) {
            instance = new DuanZiFragment();
        }
        return instance;
    }
}
