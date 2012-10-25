package swf.io;

import swf.util.Log;

import java.io.*;

/** FileUtils */
public class FileUtils
{
	/** Close quietly */
	public static void closeQuietly(Reader in)
	{
		try
		{
			if (in!=null)
				in.close();
		}
		catch (Throwable t)
		{
			System.out.println("[WARN] Unable to close input (e="+t+")");
		}
	}

	/** Close quietly */
	public static void closeQuietly(Writer out)
	{
		try
		{
			if (out!=null)
				out.close();
		}
		catch (Throwable t)
		{
			System.out.println("[WARN] Unable to close output (e="+t+")");
		}
	}

	/** Delete a file */
	public static void tryDelete(File file)
	{
		if (!file.delete())
			System.err.println("Unable to delete '"+file+"'");
	}

	/** Close a stream */
	public static void closeQuietly(InputStream in)
	{
		try
		{
			if (in!=null)
				in.close();
		}
		catch (Throwable t)
		{
			System.out.println("[WARN] Unable to close input (e="+t+")");
		}
	}

	/** Move file */
	public static void move(File src, File target)
	{
		// TODO: Rename target to tmp file
		
		if (target.exists())
		{
			if (!target.delete())
			{
				Log.error("Unable to replace '"+target+"'");
			}
		}
		
		src.renameTo(target);
	}
}
