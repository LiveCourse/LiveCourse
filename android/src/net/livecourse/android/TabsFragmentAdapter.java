package net.livecourse.android;

import com.viewpagerindicator.PageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * This adapter handles the swipey tabs, allowing swiping of the tabbed content and switching between the different swiped fragments.
 * This is also the class that handles the main logic of the UI
 * @author Darren
 *
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter{
	
	/**
	 * This is the list of the different tabs, starts with only class list and expands later
	 */
    public String[] CONTENT = new String[] { "Class List"};

    /**
     * This is the length of the list of the different tabs
     */
    private int mCount = CONTENT.length;
    
    
    /**
     * This are the objects that needs to be called by the fragments in order to modify the UI
     * It will get passed here so the fragments can call and change them
     */
    private ViewPager mPager;
    private PageIndicator mIndicator;
    private MainActivity mActivity;

    public TabsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

	/**
	 * The following code switches the fragment the user is on based on the tab selection
	 */
    @Override
    public Fragment getItem(int position) {

    	if(position == 0)
    	{
    		return ClassListFragment.newInstance(CONTENT[position % CONTENT.length], this);
    	}
    	if(position == 1)
    	{
    		return ChatFragment.newInstance(CONTENT[position % CONTENT.length], this);
    	}
    	if(position == 2)
    	{
    		return ParticipantsFragment.newInstance(CONTENT[position % CONTENT.length], this);
    	}
    	return null;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return this.CONTENT[position % CONTENT.length];
    }
    
    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

	public ViewPager getPager() {
		return mPager;
	}

	public void setPager(ViewPager mPager) {
		this.mPager = mPager;
	}

	public PageIndicator getIndicator() 
	{
		return mIndicator;
	}

	public void setIndicator(PageIndicator mIndicator) 
	{
		this.mIndicator = mIndicator;
	}

	public MainActivity getActivity() {
		return mActivity;
	}

	public void setActivity(MainActivity mActivity) {
		this.mActivity = mActivity;
	}
}