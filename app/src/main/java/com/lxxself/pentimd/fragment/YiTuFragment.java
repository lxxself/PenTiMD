package com.lxxself.pentimd.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxxself.pentimd.util.RestClient;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.lxxself.pentimd.util.JSONUtil.getList;

/**
 * Created by lxxself on 2015/11/18.
 */
public class YiTuFragment extends BaseFragment {
    private static final String title = "意图";
    private static YiTuFragment instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = "yitu";
    }
    public static YiTuFragment getInstance() {
        if (instance == null) {
            instance = new YiTuFragment();
        }
        return instance;
    }

//    @Override
//    protected void requestNetMoreList(int p, final int limit) {
//        RequestParams params = new RequestParams();
//        params.put("p", p);
//        params.put("limit", limit);
//        RestClient.postJson(url + type, params, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        JSONObject jsonObject = response;
//                        try {
//                            getList(getContext(), response, type);
//                            if (list.size() != 0) {
//                                latestId = list.get(0).getId();
//                            }
//                            KLog.d("MoreList", response.toString());
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } finally {
//                            refreshLayout.setRefreshing(false);
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable
//                            throwable, JSONObject errorResponse) {
//                        refreshLayout.setRefreshing(false);
//                        Toast.makeText(getContext(), "没有获取到内容，请稍后再试", Toast.LENGTH_SHORT).show();
//                        KLog.e("没有获取到信息");
//                    }
//                }
//
//        );
//    }

}
