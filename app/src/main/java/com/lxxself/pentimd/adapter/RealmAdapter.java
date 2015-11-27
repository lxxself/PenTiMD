package com.lxxself.pentimd.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxxself.pentimd.R;
import com.lxxself.pentimd.model.ItemObj;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by lxxself on 2015/11/20.
 */
public class RealmAdapter extends RealmBaseAdapter<ItemObj> {

    private onMyItemClickListener mOnItemClickListener;
    private onMyItemLongClickListener mOnItemLongClickListener;


    public interface onMyItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface onMyItemLongClickListener {
        public void onItemLongClick(View view, int position);
    }

    //暴露给外部用
    public void setOnItemClickListener(onMyItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(onMyItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }


    private static class ViewHolder {
        TextView title;
        ImageView imageView;
        TextView description;
    }

    public RealmAdapter(Context context, RealmResults<ItemObj> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    public RealmResults<ItemObj> getRealmResults() {
        return realmResults;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recyclerlist_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tugua_title);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.tugua_img);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //不能放到上面缓存里
        viewHolder.title.setText(realmResults.get(position).getTitle());
        viewHolder.imageView.setVisibility(View.GONE);
        viewHolder.description.setVisibility(View.GONE);
        if (realmResults.get(position).getImgUrl() != null) {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context).load(realmResults.get(position).getImgUrl())
                    .fit()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(viewHolder.imageView);
        }
        if (realmResults.get(position).getDescription() != null) {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(Html.fromHtml(realmResults.get(position).getDescription()));
            //设置后超链接可点击
            viewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemLongClickListener.onItemLongClick(v,position);
                return false;
            }
        });
        return convertView;
    }

}
