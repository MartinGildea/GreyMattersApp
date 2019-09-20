package com.GreyMatters.GreyMattersApp;

import android.net.Uri;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Intent;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mImageAddresses;
    private ArrayList<String> mTitles;
    private ArrayList<String> mDates;
    private ArrayList<String> mSummaries;
    private ArrayList<DocumentSnapshot> mDocuments;
    private ArrayList<String> mNewsLinks;
    private Context mContext;
    private String mParentType;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageAddresses, ArrayList<String> mTitles, ArrayList<String> mDates, ArrayList<String> mSummaries, ArrayList<DocumentSnapshot> mDocuments, String mParentType, ArrayList<String> mNewsLinks) {
        this.mImageAddresses = mImageAddresses;
        this.mTitles = mTitles;
        this.mDates = mDates;
        this.mSummaries = mSummaries;
        this.mDocuments = mDocuments;
        this.mContext = mContext;
        this.mParentType = mParentType;
        this.mNewsLinks = mNewsLinks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mParentType.equals("EventCalendar")) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entry_calendar, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entry, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        Glide.with(mContext)
                .load(mImageAddresses.get(position))
                .placeholder(circularProgressDrawable)
                .into(holder.image);
        holder.title.setText(mTitles.get(position));
        holder.date.setText(mDates.get(position));
        holder.summary.setText(mSummaries.get(position));

        if((mParentType.equals("Event")) | (mParentType.equals("EventCalendar"))){
            holder.parentLayout.setOnClickListener(view -> {
                Intent intent = new Intent(mContext, EventOverview.class);
                intent.putExtra("image_address", mDocuments.get(position).getString("ImageAddress"));
                intent.putExtra("wide_image_address", mDocuments.get(position).getString("WideImageAddress"));
                intent.putExtra("title", mTitles.get(position));
                intent.putExtra("date", mDates.get(position));
                intent.putExtra("location", mDocuments.get(position).getString("Location"));
                intent.putExtra("text", mDocuments.get(position).getString("Text"));
                intent.putExtra("id", mDocuments.get(position).getId());
                mContext.startActivity(intent);
            });
        } else if(mParentType.equals("News")) {
            holder.parentLayout.setOnClickListener(view -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNewsLinks.get(position)));
                mContext.startActivity(browserIntent);
            });
        } else {
            holder.parentLayout.setOnClickListener(view -> Toast.makeText(mContext, mDocuments.get(position).getString("Title"), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView date;
        TextView summary;
        ConstraintLayout parentLayout;
        ConstraintLayout textLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.Icon);
            title = itemView.findViewById(R.id.Title);
            date = itemView.findViewById(R.id.Date);
            summary = itemView.findViewById(R.id.Summary);
            parentLayout = itemView.findViewById(R.id.ParentLayout);
            textLayout = itemView.findViewById(R.id.TextLayout);
        }
    }


}
