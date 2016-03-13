package com.threebitter.beacons.specialcontentsample.layout;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threebitter.beacons.specialcontentsample.MainActivity;
import com.threebitter.beacons.specialcontentsample.OnCheckBeaconDataListener;
import com.threebitter.beacons.specialcontentsample.R;
import com.threebitter.beacons.specialcontentsample.layout.dungeon.DungeonOverview;
import com.threebitter.sdk.BeaconData;

import java.util.List;

/**
 * Created by ueda on 2016/02/05.
 */
public class DungeonFragment extends Fragment implements OnCheckBeaconDataListener {

    private static final String PREF_FILE_KEY = "com.threebitter.beacons.specialcontentsample";

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private MyDungeonRecyclerViewAdapter mRecyclerAdapter;

    public DungeonFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DungeonFragment newInstance(int columnCount) {
        Log.e("temp", "DungeonFragment newInstance !!!!!");
        DungeonFragment fragment = new DungeonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        // TODO: check beacon option and region status
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dungeon_list, container, false);
        Log.e("temp", "onCreate View!!!!!!!");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.e("temp", "RecyclerView");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            for (int i = 0; i < DungeonOverview.dungeons.size(); i++) {
                DungeonOverview.Dungeon aDungeon = DungeonOverview.dungeons.get(i);
                Log.e("temp", aDungeon.dungeonName);
            }
            mRecyclerAdapter = new MyDungeonRecyclerViewAdapter(DungeonOverview.dungeons, mListener);
            recyclerView.setAdapter(mRecyclerAdapter);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                int dungeonsNum = DungeonOverview.dungeons.size();
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = 60 * dungeonsNum;
                recyclerView.setLayoutParams(params);
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        /*    int defaultDungeonsNum = DungeonOverview.dungeons.size();
            boolean useBeacon =  getActivity().getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE).getBoolean("useBeacon", false);
            if (useBeacon) {
                // TODO:
                List<BeaconData> foundBeaconDataList = null;
                if (getActivity() instanceof MainActivity) {
                    foundBeaconDataList = ((MainActivity) getActivity()).getCumulativeBeaconList();
                    if (foundBeaconDataList != null && !foundBeaconDataList.isEmpty()) {
                        Log.e("temp", "found !!!!!!!");
                        for (BeaconData foundBeacon : foundBeaconDataList) {
                            DungeonOverview.SpecialDungeon specialDungeon = DungeonOverview.getSpecialDungeonByBeacon(foundBeacon.getKeycode());
                            DungeonOverview.addDungeon(specialDungeon);
                        }
                    }
                }
            } */
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("temp", "onDestroyView !!!!!!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        Log.e("temp", "onAttach !!!!!!");
    }

    @Override
    public void onDetach() {
        Log.e("temp", "onDetach !!!!!!");
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("temp", "onPause !!!!!!");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("temp", "onResume !!!!!!");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("temp", "onStop !!!!!!");
    }

    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            Log.e("temp", "onHiddenChanged !!!!!!");
        }
    }

    public void onFinishBeaconRegionCheck(List<BeaconData> beaconDataList) {
        // TODO:
        if (beaconDataList != null && !beaconDataList.isEmpty()) {
            Log.e("temp", "found !!!!!!!");
            for (BeaconData foundBeacon : beaconDataList) {
                DungeonOverview.SpecialDungeon specialDungeon = DungeonOverview.getSpecialDungeonByBeacon(foundBeacon.getKeycode());
                DungeonOverview.addDungeon(specialDungeon);
            }
        }
        RecyclerView recyclerView = (RecyclerView)getView();
        Log.e("temp", Integer.toString(DungeonOverview.dungeons.size()));
        MyDungeonRecyclerViewAdapter recyclerViewAdapter = (MyDungeonRecyclerViewAdapter)recyclerView.getAdapter();
         recyclerViewAdapter.setItems(DungeonOverview.dungeons);
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DungeonOverview.Dungeon dungeon);
    }
}
