package net.livecourse;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

public class ClassListFragment extends SherlockListFragment {

	//Code for the class list goes here
	private static final String KEY_CONTENT = "TestFragment:Content";
	
	//Temporary list of classes used, will be changed later
	List<String> classes = Arrays.asList(
	        "CS252",
	        "PSY200",
	        "MA261",
	        "MA265",
	        "CS307",
	        "CS180",
	        "CS240",
	        "CS250",
	        "CS251",
	        "MA161",
	        "MA165",
	        "PSY240"
	        );

    public static ClassListFragment newInstance(String content) {
    	ClassListFragment fragment = new ClassListFragment();
    	/*
        StringBuilder builder = new StringBuilder();
        builder.append("This is the class list fragment");
        fragment.mContent = builder.toString();
		*/
        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	/** Creating an array adapter to store the list of countries **/
        ClassListAdapter<String> adapter = new ClassListAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,classes);
 
        /** Setting the list adapter for the ListFragment */
        setListAdapter(adapter);
 
        return super.onCreateView(inflater, container, savedInstanceState);
    
    	
    	
    	
    	/*TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setText(mContent);
        text.setTextSize(20 * getResources().getDisplayMetrics().density);
        text.setPadding(20, 20, 20, 20);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(text);
        */

        //return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
