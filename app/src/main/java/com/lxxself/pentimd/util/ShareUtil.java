package com.lxxself.pentimd.util;

import android.content.Context;
import android.content.Intent;

import com.lxxself.pentimd.fragment.BaseFragment;

/**
 * Created by lxxself on 2015/11/22.
 */
public class ShareUtil {
    public static void shareTO(Context context,String title,String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.putExtra(Intent.EXTRA_TEXT, title+" —— "+content);
        context.startActivity(Intent.createChooser(intent, "分享到"));
    }
}
