package net.livecourse.android.chat;

import java.util.ArrayList;

import net.livecourse.R;
import net.livecourse.android.classlist.Chatroom;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class DocumentsActivity extends SherlockFragmentActivity implements OnRestCalled
{
	private final String 	TAG	= " == Documents Activity == ";
	
	private ListView 				documentsListView;
	//private ArrayList<Document> 	emptyAList;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_layout);
        
        this.documentsListView = (ListView) this.findViewById(R.id.documents_list_view);
        
        Log.d(this.TAG, "Chat ID: " + Globals.chatId + " Section ID: " + Globals.sectionId);
        new Restful(Restful.GET_ALL_FILES_PATH, Restful.GET, new String[]{"chat_id"}, new String[]{Globals.chatId}, 1, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public void preRestExecute(String restCall) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		Log.d(this.TAG, response);
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}

	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}
}
