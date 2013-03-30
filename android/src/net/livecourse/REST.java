package net.livecourse;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class REST extends AsyncTask <Void, Void, String> 
{
	/**
	 * Activity is pased through so that REST can make changes to the UI and such
	 */
	private SherlockFragmentActivity mActivity;
	
	public REST(SherlockFragmentActivity a)
	{
		super();
		mActivity = a;
	}
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException 
	{
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) 
		{
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) 
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	@Override
	protected String doInBackground(Void... params) 
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/auth").buildUpon()
		    .appendQueryParameter("email", "chengd@purdue.edu")
		    .appendQueryParameter("device", "1")
		    .build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println(httpGet.getURI().toString());
		String text = null;
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
		} 
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		return text;
	}
	
	protected void onPostExecute(String results) 
	{
		Toast.makeText(mActivity, results, Toast.LENGTH_LONG).show();
		System.out.println(results);

	}
}
