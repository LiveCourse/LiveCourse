package net.livecourse.android;

import net.livecourse.android.chat.ChatFragment;
import net.livecourse.android.classlist.ClassListFragment;
import net.livecourse.android.notes.NotesFragment;
import net.livecourse.android.participants.ParticipantsFragment;
import net.livecourse.utility.Globals;

import com.viewpagerindicator.PageIndicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * This adapter handles the swipey tabs, allowing swiping of the tabbed content
 * and switching between the different swiped fragments. This is also the class
 * that handles the main logic of the UI
 * 
 * @author Darren
 * 
 */
public class TabsFragmentAdapter extends FragmentStatePagerAdapter
{

	/**
	 * This is the list of the different tabs, starts with only class list and
	 * expands later
	 */
	public String[]			CONTENT	= new String[] { "Class List" };

	/**
	 * This is the length of the list of the different tabs
	 */
	private int				mCount	= CONTENT.length;

	/**
	 * This are the objects that needs to be called by the fragments in order to
	 * modify the UI It will get passed here so the fragments can call and
	 * change them
	 */
	private ViewPager		mPager;
	private PageIndicator	mIndicator;
	private MainActivity	mActivity;

	public TabsFragmentAdapter(FragmentManager fm)
	{
		super(fm);
	}

	/**
	 * Calls fragment to be pre-rendered
	 * 
	 * DO NOT CALL THIS METHOD
	 */
	@Override
	public Fragment getItem(int position)
	{
		if (position == 0)
		{
			return ClassListFragment.newInstance(CONTENT[position % CONTENT.length], this);
		}
		if (position == 1)
		{
			return ChatFragment.newInstance(CONTENT[position % CONTENT.length], this);
		}
		if (position == 2)
		{
			return NotesFragment.newInstance(CONTENT[position % CONTENT.length], this);
		}
		if (position == 3)
		{
			return ParticipantsFragment.newInstance(CONTENT[position % CONTENT.length], this);
		}
		return null;
	}

	@Override
	public int getItemPosition(Object object)
	{
		if (object == Globals.classListFragment)
			return PagerAdapter.POSITION_UNCHANGED;
		return PagerAdapter.POSITION_NONE;
	}

	@Override
	public int getCount()
	{
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return this.CONTENT[position % CONTENT.length];
	}

	public void setCount(int count)
	{
		if (count > 0 && count <= 10)
		{
			mCount = count;
			notifyDataSetChanged();
		}
	}

	public void expand()
	{
		if (!this.CONTENT.equals(new String[] { "Class List", "Chat", "Notes", "Participants" }))
		{
			this.CONTENT = new String[] { "Class List", "Chat", "Notes", "Participants" };
			this.setCount(4);
			this.notifyDataSetChanged();
		}
	}

	public void collapse()
	{
		if (!this.CONTENT.equals(new String[] { "Class List" }))
		{
			this.CONTENT = new String[] { "Class List" };
			this.setCount(1);
			this.notifyDataSetChanged();
			this.mIndicator.notifyDataSetChanged();
		}
	}

	public ViewPager getPager()
	{
		return mPager;
	}

	public void setPager(ViewPager mPager)
	{
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

	public MainActivity getActivity()
	{
		return mActivity;
	}

	public void setActivity(MainActivity mActivity)
	{
		this.mActivity = mActivity;
	}
}