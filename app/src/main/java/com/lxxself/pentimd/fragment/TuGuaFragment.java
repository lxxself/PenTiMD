package com.lxxself.pentimd.fragment;

import android.os.Bundle;

public class TuGuaFragment extends BaseFragment {
    private static final String title = "图卦";
    private static TuGuaFragment instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        type = "tugua";
    }

    public static TuGuaFragment getInstance() {
        if (instance == null) {
            instance = new TuGuaFragment();
        }
        return instance;
    }
}