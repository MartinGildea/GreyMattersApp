package com.GreyMatters.GreyMattersApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private EventList mEventList = new EventList();
    private ArrayList<String> mImageAddresses = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    private ArrayList<String> mSummaries = new ArrayList<>();
    private ArrayList<DocumentSnapshot> mDocuments = new ArrayList<>();
    private Context mContext;
    private String mParentType;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageAddresses, ArrayList<String> mTitles, ArrayList<String> mDates, ArrayList<String> mSummaries, ArrayList<DocumentSnapshot> mDocuments, String mParentType) {
        this.mImageAddresses = mImageAddresses;
        this.mTitles = mTitles;
        this.mDates = mDates;
        this.mSummaries = mSummaries;
        this.mDocuments = mDocuments;
        this.mContext = mContext;
        this.mParentType = mParentType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_entry, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        Log.d(TAG, "onBindViewHolder: called. Address name: " + mImageAddresses.get(position));
        Glide.with(mContext)
                .load(mImageAddresses.get(position))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.image);
        holder.title.setText(mTitles.get(position));
        holder.date.setText(mDates.get(position));
        holder.summary.setText(mSummaries.get(position));

        if(mParentType.equals("Event")){
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked on: " + mTitles.get(position));
                    Intent intent = new Intent(mContext, EventOverview.class);
                    intent.putExtra("wide_image_address", mDocuments.get(position).getString("WideImageAddress"));
                    intent.putExtra("title", mTitles.get(position));
                    intent.putExtra("date", mDates.get(position));
                    intent.putExtra("location", mDocuments.get(position).getString("Location"));
                    intent.putExtra("text", mDocuments.get(position).getString("Text"));
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked on: " + mTitles.get(position));
                    Toast.makeText(mContext, mDocuments.get(position).getString("Title"), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        TextView date;
        TextView summary;
        ConstraintLayout parentLayout;
        ConstraintLayout textLayout;

        public ViewHolder(@NonNull View itemView) {
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
