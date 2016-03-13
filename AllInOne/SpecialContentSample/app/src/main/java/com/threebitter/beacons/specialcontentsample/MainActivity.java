package com.threebitter.beacons.specialcontentsample;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.threebitter.beacons.specialcontentsample.layout.DungeonFragment;
import com.threebitter.beacons.specialcontentsample.layout.ItemFragment;
//import com.threebitter.beacons.specialcontentsample.layout.MyDungeonRecyclerViewAdapter;
import com.threebitter.beacons.specialcontentsample.layout.dummy.DummyContent;
import com.threebitter.beacons.specialcontentsample.layout.dungeon.DungeonOverview;

import com.threebitter.sdk.Beacon;
import com.threebitter.sdk.BeaconCallback;
import com.threebitter.sdk.BeaconConsumer;
import com.threebitter.sdk.BeaconData;
import com.threebitter.sdk.BeaconManager;
import com.threebitter.sdk.BeaconRangeNotifier;
import com.threebitter.sdk.BeaconRegion;
import com.threebitter.sdk.BeaconServiceHelper;
import com.threebitter.sdk.BeaconViewDispatcher;
import com.threebitter.sdk.IBeaconManager;
import com.threebitter.sdk.service.BeaconService;
import com.threebitter.sdk.ui.OnAgreementDialogListener;
import com.threebitter.sdk.utils.StartUp;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, DungeonFragment.OnListFragmentInteractionListener, BeaconConsumer, OnAgreementDialogListener, BeaconRangeNotifier {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    private static final String PREF_FILE_KEY = "com.threebitter.beacons.specialcontentsample";

    private IBeaconManager mBeaconManager;
    private int rangedCount = 0;

    private final int MAX_RANGE_COUNT = 5;
    private List<BeaconData> mCumulativeBeaconList;

    public List<BeaconData> getCumulativeBeaconList() {
        return mCumulativeBeaconList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // TODO: check beacon option and fetch avaiable reagions
        SharedPreferences pref = getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE);
        boolean useBeacon = pref.getBoolean("useBeacon", false);
        if (useBeacon) {
            StartUp.init(getApplication());
            mBeaconManager = BeaconManager.getInstance(getApplicationContext());
            /* this is optional */
            if (mBeaconManager != null) {
                mBeaconManager.bind(this);
                mBeaconManager.setRangeNotifier(this);
            }
        }
    }

    public void onStop() {
        if (mBeaconManager != null) {
            mBeaconManager.unbind(this);
            mBeaconManager = null;
        }
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(DungeonOverview.Dungeon dungeon) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            Log.e("temp", "onPageScrolled 0 !!!!");
            if (mSectionsPagerAdapter.getItem(0) == null) {
                mSectionsPagerAdapter.instantiateItem(mViewPager, 0);
            }
            if (mBeaconManager != null) {
                mBeaconManager.stopRangingInitialRegions();
            }
        } else if (position == 1) {
            Log.e("temp", "onPageScrolled 1 !!!!");
            if (mSectionsPagerAdapter.getItem(1) == null) {
                mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
            }
           /* FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(mSectionsPagerAdapter.getItem(1));
            Log.e("temp", "removed");
            ft.detach(mSectionsPagerAdapter.getItem(1)); // attach new in didRange or didNotFound
            Log.e("temp", "dettached"); */
           // mSectionsPagerAdapter.destroyItem(mViewPager, 1, mSectionsPagerAdapter.instantiateItem(1));
            Log.e("temp", "reset dungeon fragment");
            //ft.commit();
            if (mBeaconManager != null) {
                // Start ranging and make special dungeon list
                  mBeaconManager.startRangingInitialRegions();
                // TODO: set timeout timer
            }
        } else if (position == 2) {
            Log.e("temp", "onPagePageScrolled 2 !!!!");
            if (mSectionsPagerAdapter.getItem(2) == null) {
                mSectionsPagerAdapter.instantiateItem(mViewPager, 2);
            }
            if (mBeaconManager != null) {
                mBeaconManager.stopRangingInitialRegions();
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            Log.e("temp", "onPageSelected 0 !!!!");
        } else if (position == 1) {
            Log.e("temp", "onPageSelected 1 !!!!");

        } else if (position == 2) {
            Log.e("temp", "onPageSelected 2 !!!!");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class HomeFragment extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;


        public HomeFragment() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        public static HomeFragment newInstance(String param1, String param2) {
            HomeFragment fragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_home, container, false);
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment itemFragment = null;
            switch (position) {
                case 0:
                    itemFragment = HomeFragment.newInstance("sample", "home");
                    break;
                case 1:
                 /*   if (currentDungeonFragment == null) {
                        currentDungeonFragment = DungeonFragment.newInstance(1);
                        Log.e("temp", "getItem(1):new !!!!!");
                    } else {
                        Log.e("temp", "getItem(1):current !!!!!");
                    }
                    itemFragment = currentDungeonFragment; */
                    break;
                case 2:
                    itemFragment = OptionFragment.newInstance("sample", "option");
                    break;
                default:
                    break;
            }
            return itemFragment;
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("temp", "getItem !!!!!!!");
            Fragment itemFragment = null;
            switch (position) {
                case 0:
                    itemFragment = HomeFragment.newInstance("sample", "home");
                    break;
                case 1:
                    itemFragment = DungeonFragment.newInstance(1);
                    break;
                case 2:
                    itemFragment = OptionFragment.newInstance("sample", "option");
                    break;
                default:
                    break;
            }
            return itemFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "Dungeons";
                case 2:
                    return "Options";
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    public static class OptionFragment extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        private OnFragmentInteractionListener mListener;

        public OptionFragment() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OptionFragment.
         */
        // TODO: Rename and change types and number of parameters
        public static OptionFragment newInstance(String param1, String param2) {
            OptionFragment fragment = new OptionFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_option, container, false);
            Switch beaconFuncSwitch = (Switch)rootView.findViewById(R.id.btSwitch);
            if (!BeaconServiceHelper.isSupportBeacon(getActivity())) {
                beaconFuncSwitch.setEnabled(false);
                return rootView;
            }
            beaconFuncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!BeaconServiceHelper.isAgreement3BitterService(getActivity())) {
                            BeaconViewDispatcher.showAgreementDialog(getActivity());
                            return;
                        }
                    } else {
                        BeaconManager.getInstance(getActivity(), false);
                        getActivity().getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", false).apply();
                    }
            }
        });

            TextView statusLabel = (TextView)rootView.findViewById(R.id.btStatusLabel);
            SharedPreferences pref = getActivity().getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE);
            boolean useBeacon = pref.getBoolean("useBeacon", false);
            if (useBeacon) {
                beaconFuncSwitch.setChecked(true);
                if (!BeaconServiceHelper.isBluetoothEnabled()) {
                    statusLabel.setText("※ Bluetoothをオンにしてください");
                } else {
                    statusLabel.setText("機能しています");
                }
            } else {
                beaconFuncSwitch.setChecked(false);
                statusLabel.setText("機能停止しています");

            }
            return rootView;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (BeaconServiceHelper.handlePermissionRequestResult(getActivity(), requestCode, permissions, grantResults)) {
                if (BeaconServiceHelper.allowedPermission(getActivity())) {
                    String message = "規約同意しました";
                    if (!BeaconServiceHelper.isBluetoothEnabled()) {
                        message = message.concat("※ Bluetoothをオンにしてください");
                    }
                    new AlertDialog.Builder(getActivity())
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    TextView statusLabel = (TextView)getActivity().findViewById(R.id.btStatusLabel);
                                    if (!BeaconServiceHelper.isBluetoothEnabled()) {
                                        statusLabel.setText("※ Bluetoothをオンにしてください");
                                    } else {
                                        statusLabel.setText("機能しています");
                                    }
                                }
                            }).create().show();
                } else {
                    BeaconServiceHelper.requestPermissionIfNeeded(getActivity());
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFragmentInteraction(Uri uri);
        }
    }


    public void onDialogSelected(boolean isAllowed) {
        if (isAllowed) {
            // Enable SDK
            StartUp.init(getApplication());
            mBeaconManager = BeaconManager.getInstance(this, isAllowed);
            mBeaconManager.bind(this);
            mBeaconManager.setRangeNotifier(this);
            getSharedPreferences(PREF_FILE_KEY, MODE_PRIVATE).edit().putBoolean("useBeacon", true).apply();

            if (!BeaconServiceHelper.allowedPermission(this)) {
                BeaconServiceHelper.requestPermissionIfNeeded(this);
                return; // Go to onRequestPermissionsResult
            }
            String message = "規約同意しました";
            if (!BeaconServiceHelper.isBluetoothEnabled()) {
                message = message.concat("※ Bluetoothをオンにしてください");
            }
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            TextView statusLabel = (TextView)findViewById(R.id.btStatusLabel);
                            if (!BeaconServiceHelper.isBluetoothEnabled()) {
                                statusLabel.setText("※ Bluetoothをオンにしてください");
                            } else {
                                statusLabel.setText("機能しています");
                            }
                        }
                    }).create().show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("規約に非同意の場合は機能しません")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
            ((Switch)findViewById(R.id.btSwitch)).setChecked(false);
            return;
        }
    }

    public void onBeaconScannerConnect() {
    }

    @Override
    public void didRangeBeacons(@NonNull final List<BeaconData> beaconDataList, @NonNull final BeaconRegion region) {
        Log.e("temp", "didRange!!!!!");
        rangedCount++;
        if (rangedCount <= MAX_RANGE_COUNT) {
            if (mCumulativeBeaconList == null) {
              mCumulativeBeaconList = new ArrayList<BeaconData>();
            }
            if (!beaconDataList.isEmpty()) {
                for (BeaconData beaconData :beaconDataList) {
                    boolean alreadyStored = false;
                    String keycode = beaconData.getKeycode();
                    if (!mCumulativeBeaconList.isEmpty()) {
                        for (BeaconData stored : mCumulativeBeaconList) {
                            String storedKey = stored.getKeycode();
                            if (keycode.equals(stored.getKeycode())) {
                                alreadyStored = true;
                                break;
                            }
                        }
                    }
                    if (!alreadyStored) {
                        mCumulativeBeaconList.add(beaconData);
                    }
                }
            }
        } else {
           // mBeaconCheckListener.onFinishBeaconRegionCheck(mCumulativeBeaconList);
            checkContentForRegions();
            mBeaconManager.stopRangingInitialRegions();
            mCumulativeBeaconList = null;
            rangedCount = 0;
          //  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          //  ft.add(R.id.container, mSectionsPagerAdapter.getItem(1));
          //  ft.commit();
        }
    }

    private void checkContentForRegions() {
        Log.e("temp", "checkContentForRegions");
             int defaultDungeonsNum = DungeonOverview.dungeons.size();
            boolean useBeacon = getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE).getBoolean("useBeacon", false);
            if (useBeacon) {
                // TODO:
                List<BeaconData> foundBeaconDataList = null;
                foundBeaconDataList = mCumulativeBeaconList;
                if (foundBeaconDataList != null && !foundBeaconDataList.isEmpty()) {
                    Log.e("temp", "found !!!!!!!");
                    for (BeaconData foundBeacon : foundBeaconDataList) {
                        DungeonOverview.SpecialDungeon specialDungeon = DungeonOverview.getSpecialDungeonByBeacon(foundBeacon.getKeycode());
                        DungeonOverview.addDungeon(specialDungeon);
                    }
                }
            }
    }
}
