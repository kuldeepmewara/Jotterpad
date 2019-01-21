package com.example.texteditorapp;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

class ContentFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<ContentFragment> fragmentList;
    private FragmentManager manager;

    ContentFragmentPagerAdapter(FragmentManager manager, ArrayList<ContentFragment> fragmentList) {
        super(manager);
        this.manager = manager;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    void addTab() {
        fragmentList.add(new ContentFragment());
        notifyDataSetChanged();
    }

    void addTab(Uri fileUri) {
        fragmentList.add(ContentFragment.newInstance(fileUri));
        notifyDataSetChanged();
    }

    void saveCurrentFile(int position) {
        fragmentList.get(position).saveFile();
    }

    void saveCurrentFileAs(int position, Uri saveFileUri) {
        fragmentList.get(position).saveFileAs(saveFileUri);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).getContentFileName(position);
    }

    void removeFragment(int position) {
        ContentFragment removedFragment = fragmentList.remove(position);
        manager.beginTransaction()
                .remove(removedFragment)
                .commit();
    }
}
