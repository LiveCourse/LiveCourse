package net.livecourse.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;

import net.livecourse.R;
import net.livecourse.database.Chatroom;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.ChatroomViewHolder;
import net.livecourse.utility.Globals;

public class QueryActivity extends SherlockFragmentActivity implements SearchView.OnQueryTextListener, OnItemClickListener, OnRestCalled
{
	private final String TAG = " == QueryActivity == ";
	
	private String query;
	private ListView queryListView;
	private ClassQueryArrayAdapter adapter;
	private ArrayList<Chatroom> emptyAList;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		Log.d(this.TAG, "Running");
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);
                
        queryListView = (ListView) this.findViewById(R.id.query_list_view);
        
        emptyAList = new ArrayList<Chatroom>(10);
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
		new Restful(Restful.SEARCH_FOR_CHAT_PATH,Restful.GET, new String[]{"query"}, new String[]{this.query}, 1, this);
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
	public boolean onQueryTextSubmit(String query) 
	{
		this.finish();
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) 
	{
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		ChatroomViewHolder v = (ChatroomViewHolder) view.getTag();	
		new Restful(Restful.JOIN_CHAT_PATH, Restful.POST, new String[]{"id"}, new String[]{v.idString}, 1, this);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		JSONArray parse;
		JSONObject ob;
		
		if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			try 
			{
				ob = new JSONObject(response);
				Globals.chatId = ob.getString("id_string");
				Globals.chatName = ob.getString("name");				
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		else if(restCall.equals(Restful.SEARCH_FOR_CHAT_PATH))
		{
			
			try 
			{
				parse = new JSONArray(response);
				this.emptyAList = new ArrayList<Chatroom>(parse.length());
				
				for(int x = 0; x < parse.length(); x++)
				{
					Chatroom room = new Chatroom();
					ob = parse.getJSONObject(x);
					
		        	room.setIdString(ob.getString(		"id_string"));
	            	room.setSubjectId(ob.getString(		"subject_id"));
	            	room.setCourseNumber(ob.getString(	"course_number"));
	            	room.setName(ob.getString(			"name"));
	            	room.setStartTime(ob.getString(		"start_time"));	            	
		        	room.setInstitutionId(ob.getString(	"institution_id"));
		        	room.setRoomId(ob.getString(		"room_id"));
		        	room.setStartTime(ob.getString(		"start_time"));
		        	room.setEndTime(ob.getString(		"end_time"));
		        	room.setStartDate(ob.getString(		"start_date"));
		        	room.setEndDate(ob.getString(		"end_date"));
		        	room.setDowMonday(ob.getString(		"dow_monday"));
		        	room.setDowTuesday(ob.getString(	"dow_tuesday"));
		        	room.setDowWednesday(ob.getString(	"dow_wednesday"));
		        	room.setDowThursday(ob.getString(	"dow_thursday"));
		        	room.setDowFriday(ob.getString(		"dow_friday"));
		        	room.setDowSaturday(ob.getString(	"dow_saturday"));
		        	room.setDowSunday(ob.getString(		"dow_sunday"));
		        	
		        	this.emptyAList.add(room);
					Log.d(this.TAG, "Added Chatroom " + room.getName() + " to query array list");
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.SEARCH_FOR_CHAT_PATH))
		{
			this.adapter.clear();
        	this.adapter.addAll(this.emptyAList);
        	this.adapter.notifyDataSetChanged();
        	
        	Log.d(this.TAG, "Updating Query List View");
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			Globals.appDb.recreateClassEnroll();
			Globals.classListFragment.updateList();
			this.finish();
		}

	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
	}

	@Override
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}
}
