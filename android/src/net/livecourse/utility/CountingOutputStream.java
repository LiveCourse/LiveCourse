package net.livecourse.utility;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class extends FilterOutputStream and is used in this application
 * to count the bytes being written to the output to see the progress done
 * when uploading files.
 */
public class CountingOutputStream extends FilterOutputStream
{
	@SuppressWarnings("unused")
	private final String TAG = " == Counting Output Stream == ";
	private final int MAX_SIZE = 100;
	private long fileSize;
	private long sizeWritten;
	private String title;
	private int lastPercent;
	
	public CountingOutputStream(final OutputStream out, String title, long fileSize) 
	{
        super(out);
        this.fileSize = (int) fileSize;
        this.sizeWritten = 0;
        this.lastPercent = 0;
        this.title = title;
        
        Utility.startUploadProgressNotification(Globals.mainActivity, this.title, "0%", this.MAX_SIZE);
	}
	
	@Override
    public void write(int b) throws IOException 
    {
        out.write(b);
        //Log.d(this.TAG, "Written 1 byte");
        this.sizeWritten ++;
    }

    @Override
    public void write(byte[] b) throws IOException 
    {
        out.write(b);
        //Log.d(this.TAG, "Written " + b.length + " bytes");
        this.sizeWritten += b.length;
        
        int percentage = (int)(sizeWritten * 100.0 / fileSize + 0.5);
        if(percentage - this.lastPercent > 1)
        {
        	Utility.updateUploadProgressNotification(Globals.mainActivity, this.title, percentage + "%", percentage, this.MAX_SIZE);
        	this.lastPercent = percentage;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException 
    {
        out.write(b, off, len);
        //Log.d(this.TAG, "Written " + len + " bytes");
        this.sizeWritten += len;
        
        int percentage = (int)(sizeWritten * 100.0 / fileSize + 0.5);
        if(percentage - this.lastPercent > 1)
        {
        	Utility.updateUploadProgressNotification(Globals.mainActivity, this.title, percentage + "%", percentage, this.MAX_SIZE);
        	this.lastPercent = percentage;
        }
    }
}
