package net.livecourse;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TestFragmentAdapter extends FragmentPagerAdapter{
    protected static final String[] CONTENT = new String[] { "Class List", "Chat", "Participants"};


    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        
    	if(position == 0)
    		return ClassListFragment.newInstance(CONTENT[position % CONTENT.length]);
    	if(position == 1)
    		return ChatFragment.newInstance(CONTENT[position % CONTENT.length]);
    	if(position == 2)
    		return ParticipantsFragment.newInstance(CONTENT[position % CONTENT.length]);
    	return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }
    
    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}