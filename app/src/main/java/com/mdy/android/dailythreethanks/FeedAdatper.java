package com.mdy.android.dailythreethanks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdy.android.dailythreethanks.domain.Memo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MDY on 2017-07-06.
 */

public class FeedAdatper extends RecyclerView.Adapter<FeedAdatper.Holder>{

    List<Memo> data = new ArrayList<>();
    LayoutInflater inflater;

    public FeedAdatper(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFeedData(List<Memo> data){
        this.data = data;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Memo memo = data.get(position);
        holder.txtTitle1.setText(memo.title1);
        holder.txtTitle2.setText(memo.title2);
        holder.txtTitle3.setText(memo.title3);
        holder.txtContent1.setText(memo.content1);
        holder.txtContent1.setText(memo.content2);
        holder.txtContent1.setText(memo.content3);
        holder.txtDate.setText(memo.date);

        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class Holder extends RecyclerView.ViewHolder{
        private int position;
        TextView txtTitle1, txtTitle2, txtTitle3;
        TextView txtContent1, txtContent2, txtContent3;
        TextView txtDate;

        public Holder(View itemView) {
            super(itemView);
            txtTitle1 = (TextView) itemView.findViewById(R.id.txtTitle1);
            txtTitle2 = (TextView) itemView.findViewById(R.id.txtTitle2);
            txtTitle3 = (TextView) itemView.findViewById(R.id.txtTitle3);
            txtContent1 = (TextView) itemView.findViewById(R.id.txtContent1);
            txtContent2 = (TextView) itemView.findViewById(R.id.txtContent2);
            txtContent3 = (TextView) itemView.findViewById(R.id.txtContent3);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReadActivity.class);
                    intent.putExtra("LIST_POSITION", position);
                    v.getContext().startActivity(intent);
                }
            });
        }
        public void setPosition(int position){
            this.position = position;
        }
    }
}
