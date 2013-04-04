package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;

import net.livecourse.android.R;
import net.livecourse.database.Chatroom;
import net.livecourse.rest.REST;
import net.livecourse.utility.ChatroomViewHolder;

public class QueryActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener, OnItemClickListener
{
	private String query;
	private ListView queryListView;
	private ClassQueryArrayAdapter adapter;
	private Chatroom[] emptyList = {};
	private ArrayList<Chatroom> emptyAList;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);
                
        queryListView = (ListView) this.findViewById(R.id.query_list_view);
        
        emptyAList = new ArrayList<Chatroom>();
        emptyAList.addAll(Arrays.asList(emptyList));
        adapter = new ClassQueryArrayAdapter(this,R.layout.classlist_item_layout, emptyAList);
        
        queryListView.setAdapter(adapter);
        queryListView.setOnItemClickListener(this);
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
        {
          query = intent.getStringExtra(SearchManager.QUERY);
          this.processQuery(query);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.query_activity_menu,menu);
		
		/**
		 * Inits the search view
		 */
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.query_search_class_menu_item).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setQueryHint("Search For a Class");
        searchView.setQuery(this.query, false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(this);
		
		return true;
	}
	
	private void processQuery(String query)
	{
        new REST(this,null,null,null,null,query,null,null,REST.CLASS_QUERY).execute();
	}
	
	public ClassQueryArrayAdapter getAdapter() 
	{
		return adapter;
	}

	public void setAdapter(ClassQueryArrayAdapter adapter) 
	{
		this.adapter = adapter;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		this.finish();
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		ChatroomViewHolder v = (ChatroomViewHolder) view.getTag();
		
		new REST(this, null, null, null, null, null, null, v.idString, REST.JOIN_CHAT).execute();
	}
}
