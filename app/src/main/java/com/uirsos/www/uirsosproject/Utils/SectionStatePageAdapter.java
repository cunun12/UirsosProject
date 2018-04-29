package com.uirsos.www.uirsosproject.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cunun12
 */

public class SectionStatePageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragment = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumber = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();


    public SectionStatePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentNames){
        mFragmentList.add(fragment);
        mFragment.put(fragment, mFragmentList.size()-1);
        mFragmentNumber.put(fragmentNames, mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1, fragmentNames);
    }

    /**
     * return the fragment with the name @param
     * @param fragmentNames
     * @return
     */
    public Integer getFragmentNumber(String fragmentNames){
        if (mFragmentNumber.containsKey(fragmentNames)){
            return mFragmentNumber.get(fragmentNames);
        }else{
            return null;
        }
    }

    /**
     * return the fragment with the name @param
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment){
        if (mFragmentNumber.containsKey(fragment)){
            return mFragmentNumber.get(fragment);
        }else{
            return null;
        }
    }

    /**
     * return the fragment with the name @param
     * @param fragmentNumber
     * @return
     */
    public String getFragmentNames(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }else{
            return null;
        }
    }
}
