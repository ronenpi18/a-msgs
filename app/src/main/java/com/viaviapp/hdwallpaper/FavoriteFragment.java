package com.viaviapp.hdwallpaper;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.util.Constant;

public class FavoriteFragment extends Fragment {

	private TabLayout tabLayout;
	public static ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
		setHasOptionsMenu(true);

		viewPager = (ViewPager)rootView.findViewById(R.id.viewpager_fav);

		tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
		tabLayout.setBackgroundColor(Constant.color);
		tabLayout.setupWithViewPager(viewPager);

		ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(adapter);

		return rootView;
	}

	class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			if(position == 0) {
				return new FragmentFavImages().newInstance();
			} else {
				return new FragmentFavGIFs().newInstance();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(position == 0) {
				return getResources().getString(R.string.wallpapers);
			} else {
				return getResources().getString(R.string.gifs);
			}
		}
	}
}
