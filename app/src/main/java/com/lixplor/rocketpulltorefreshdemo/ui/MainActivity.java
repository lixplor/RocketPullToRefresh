/*
 *  Copyright 2016 Lixplor
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lixplor.rocketpulltorefreshdemo.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lixplor.rocketpulltorefreshdemo.R;
import com.lixplor.rocketpulltorefreshdemo.adapter.ViewPagerAdapter;
import com.lixplor.rocketpulltorefreshdemo.fragment.GridViewFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.HoriRecyclerViewFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.ListViewFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.ButtonFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.VertiRecyclerViewFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.ScrollViewFragment;
import com.lixplor.rocketpulltorefreshdemo.fragment.StaggerRecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created :  2016-10-30
 * Author  :  Lixplor
 * Web     :  http://blog.lixplor.com
 * Email   :  me@lixplor.com
 */
public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mTabLayout = (TabLayout) findViewById(R.id.tl_tab);
        mViewPager = (ViewPager) findViewById(R.id.vp);

        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        ButtonFragment buttonFragment = new ButtonFragment();
        ScrollViewFragment scrollViewFragment = new ScrollViewFragment();
        ListViewFragment listViewFragment = new ListViewFragment();
        GridViewFragment gridViewFragment = new GridViewFragment();
        VertiRecyclerViewFragment vertiRecyclerViewFragment = new VertiRecyclerViewFragment();
        StaggerRecyclerViewFragment staggerRecyclerViewFragment = new StaggerRecyclerViewFragment();
//        HoriRecyclerViewFragment horiRecyclerViewFragment = new HoriRecyclerViewFragment();
        fragments.add(gridViewFragment);
        fragments.add(listViewFragment);
        fragments.add(vertiRecyclerViewFragment);
        fragments.add(staggerRecyclerViewFragment);
//        fragments.add(horiRecyclerViewFragment);
        fragments.add(scrollViewFragment);
        fragments.add(buttonFragment);
        titles.add("GridView");
        titles.add("ListView");
        titles.add("RecyclerView");
        titles.add("StaggerRecyclerView");
//        titles.add("HoriRecyclerView");
        titles.add("ScrollView");
        titles.add("Button");

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
