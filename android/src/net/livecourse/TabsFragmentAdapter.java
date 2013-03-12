package net.livecourse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This adapter handles the swipey tabs, allowing swiping of the tabbed content and switching between the different swiped fragments.
 * @author Darren
 *
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter{
	
	/**
	 * This is the list of the different tabs
	 */
    protected static final String[] CONTENT = new String[] { "Class List", "Chat", "Participants"};

    /**
     * This is the length of the list of the different tabs
     */
    private int mCount = CONTENT.length;

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
    		return ClassListFragment.newInstance(CONTENT[position % CONTENT.length]);
    	}
    	if(position == 1)
    	{
    		return ChatFragment.newInstance(CONTENT[position % CONTENT.length]);
    	}
    	if(position == 2)
    	{
    		return ParticipantsFragment.newInstance(CONTENT[position % CONTENT.length]);
    	}
    	return null;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TabsFragmentAdapter.CONTENT[position % CONTENT.length];
    }
    
    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}