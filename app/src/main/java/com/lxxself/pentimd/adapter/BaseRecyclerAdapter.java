package com.lxxself.pentimd.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lxxself.pentimd.model.ItemObj;
import com.lxxself.pentimd.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lxxself on 2015/11/19.
 */
public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.VersionViewHolder> {
    private List<ItemObj> models;
    private Context mContext;
    private onMyItemClickListener mOnItemClickListener;
    private onMyItemLongClickListener mOnItemLongClickListener;

    public BaseRecyclerAdapter(Context context, List<ItemObj> list) {
        mContext = context;
        models = list;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlist_item, parent, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    public interface onMyItemClickListener {
        public void onItemClick(View view,int position);
    }
    public interface onMyItemLongClickListener {
        public void onItemLongClick(View view,int position);
    }

    //暴露给外部用
    public void setOnItemClickListener(onMyItemClickListener listener){
        this.mOnItemClickListener= listener;
    }

    public void setOnItemLongClickListener(onMyItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(VersionViewHolder holder, int position) {
        holder.title.setText(models.get(position).getTitle());
        holder.imageView.setVisibility(View.GONE);
        holder.description.setVisibility(View.GONE);
        if (models.get(position).getImgUrl() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(models.get(position).getImgUrl())
                    .fit()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(holder.imageView);
        }
        if (models.get(position).getDescription() != null) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(Html.fromHtml(models.get(position).getDescription()));
            //设置后超链接可点击
            holder.description.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }



    @Override
    public int getItemCount() {
        return models.size();
    }

    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView title;
        ImageView imageView;
        TextView description;

        public VersionViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tugua_title);
            imageView = (ImageView) itemView.findViewById(R.id.tugua_img);
            description = (TextView) itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v,getPosition());

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, getPosition());
            }
            return true;
        }
    }

}
