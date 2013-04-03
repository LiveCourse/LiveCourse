package net.livecourse.android;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import net.livecourse.android.R;
import net.livecourse.rest.REST;

public class QueryActivity extends SherlockFragmentActivity
{
	private String query;
	private ListView queryListView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);
        
        queryListView = (ListView) this.findViewById(R.id.query_list_view);
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
        {
          query = intent.getStringExtra(SearchManager.QUERY);
          this.processQuery(query);
        }
	}
	
	private void processQuery(String query)
	{
        new REST(this,null,null,null,query,REST.token,REST.CLASS_QUERY).execute();
	}
}
