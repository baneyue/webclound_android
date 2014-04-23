package com.funlib.http.upload;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

/**
 * 可以计算上传进度的MultiPartEntity。
 * 上传过程中调用进度条回调方法。
 * 
 * @author zoubangyue
 */
public class CountMultipartEntity extends MultipartEntity
{
 
	private final ProgressListener listener;
 
	public CountMultipartEntity(final ProgressListener listener)
	{
		super();
		this.listener = listener;
	}
 
	public CountMultipartEntity(final HttpMultipartMode mode, final ProgressListener listener)
	{
		super(mode);
		this.listener = listener;
	}
 
	public CountMultipartEntity(final HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener)
	{
		super(mode, boundary, charset);
		this.listener = listener;
	}
 
	@Override
	public void writeTo(final OutputStream outstream) throws IOException
	{
		super.writeTo(new CountingOutputStream(outstream, CountMultipartEntity.this.getContentLength(), this.listener));
	}
 
	public static interface ProgressListener
	{
		void transferred(long transferred,long totalSize);
	}
 
	public static class CountingOutputStream extends FilterOutputStream
	{
 
		private final ProgressListener listener;
		private long transferred;
		private long totalSize;
 
		public CountingOutputStream(final OutputStream out,final long totalSize, final ProgressListener listener)
		{
			super(out);
			this.listener = listener;
			this.transferred = 0;
			this.totalSize = totalSize;
		}
 
		public void write(byte[] b, int off, int len) throws IOException
		{
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred,this.totalSize);
		}
 
		public void write(int b) throws IOException
		{
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred,this.totalSize);
		}
	}
}