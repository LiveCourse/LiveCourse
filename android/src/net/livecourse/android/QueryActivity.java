package net.livecourse.android;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import net.livecourse.android.R;
import net.livecourse.rest.REST;

public class QueryActivity extends SherlockFragmentActivity
{
	private String query;
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
        {
          query = intent.getStringExtra(SearchManager.QUERY);
          this.processQuery(query);
        }
	}
	
	private void processQuery(String query)
	{
        new REST(this,query,REST.password,REST.token).execute();
	}
}
