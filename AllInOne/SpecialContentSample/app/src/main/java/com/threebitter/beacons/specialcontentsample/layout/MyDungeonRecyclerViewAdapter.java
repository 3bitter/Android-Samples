package com.threebitter.beacons.specialcontentsample.layout;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.threebitter.beacons.specialcontentsample.R;
import com.threebitter.beacons.specialcontentsample.layout.DungeonFragment.OnListFragmentInteractionListener;
import com.threebitter.beacons.specialcontentsample.layout.dummy.DummyContent.DummyItem;
import com.threebitter.beacons.specialcontentsample.layout.dungeon.DungeonOverview;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */

public class MyDungeonRecyclerViewAdapter extends RecyclerView.Adapter<MyDungeonRecyclerViewAdapter.ViewHolder> {

    private List<DungeonOverview.Dungeon> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDungeonRecyclerViewAdapter(List<DungeonOverview.Dungeon> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        Log.e("temp", "No. of mValues: " + mValues.size());
    }

    public void setItems(List<DungeonOverview.Dungeon> items) {
        this.mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("temp", "onCreateViewHolder !!!!!!!");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dungeon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.e("temp", "onBindViewHolder !!!!!!!");
        //holder.mItem = mValues.get(position);
        holder.mDungeon = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).dungeonName);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mDungeon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
       // public DummyItem mItem;
        public DungeonOverview.Dungeon mDungeon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
