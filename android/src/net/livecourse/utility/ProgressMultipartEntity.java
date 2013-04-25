package net.livecourse.utility;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;


public class ProgressMultipartEntity extends MultipartEntity
{
	private long fileSize;
	private String title;
	
	public ProgressMultipartEntity(HttpMultipartMode browserCompatible, String title, long fileSize) 
	{
		super(browserCompatible);
		this.fileSize = fileSize;
		this.title = title;
	}

	@Override
    public void writeTo(final OutputStream outstream) throws IOException 
    {
		super.writeTo(new CountingOutputStream(outstream, this.title, this.fileSize));
    }
}
