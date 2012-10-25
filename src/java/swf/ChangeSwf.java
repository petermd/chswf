package swf;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import swf.io.FileUtils;
import swf.io.SWFReader;
import swf.io.SWFWriter;
import swf.model.SWF;
import swf.util.Log;

/** Main */
public class ChangeSwf
{
	//
	// Instance variables
	//
	
	/* File */
	private File swf;
	
	//
	// Public methods
	//
	
	/** Create new ChangeSwf */
	public ChangeSwf(File swf)
	{
		this.swf=swf;
	}
	
	/** Modify */
	public void modify(Map<String,Object> params) throws IOException
	{
		// Create tmp file in the output directory
		File tmpOut=new File(this.swf.getParentFile(),"out.swf");
		
		SWFReader in=null;
		SWFWriter out=null;
		try
		{
			in=new SWFReader(this.swf);
			out=new SWFWriter(tmpOut);
			
			SWF.Header header=in.readHeader();
			
			Log.debug("Read '%s' header",this.swf);
			
			out.writeHeader(header);
			
			SWF.Block b;
			while ((b=in.readBlock())!=null)
			{
				Log.debug(" -%s",b);
				
				switch(b.tag)
				{
					case Signed:
						Log.warn("SWF is signed! Modifications will not work!");
						break;
					case Metadata:
						// If telemetry=true then do not write
						if (getBoolean(params,"telemetry",false))
						{
							Log.info("Adding telemetry");
							// Write existing MetaData
							out.writeBlock(b);
							// Create Telemetry block
							b=createTelemetry();
							break;
						}
						break;
					case Telemetry:
						// If telemetry=false then we are just removing
						if (!getBoolean(params,"telemetry",true))
						{
							Log.info("Removing telemetry");
						}
						// Always remove old tag
						continue;
					case Unknown:
						// Unknown tag..
						break;
				}
				
				out.writeBlock(b);
			}
		}
		catch (Exception e)
		{
			Log.error("Unable to modify (e=%s)",e);
			e.printStackTrace(System.err);
			
			FileUtils.closeQuietly(out);
			FileUtils.tryDelete(tmpOut);
		}
		finally
		{
			FileUtils.closeQuietly(in);
			FileUtils.closeQuietly(out);
		}
		
		// Replace the source file
		FileUtils.move(tmpOut,this.swf);
	}
	
	//
	// Implementation
	//
	
	/** Create telemetry */
	protected SWF.Block createTelemetry()
	{
		// Reserved
		byte[] data=new byte[2];
		
		return new SWF.Block(SWF.Tag.Telemetry.getCode(),data);
	}
	
	//
	// Application
	//
	
	/** Entry point */
	public static void main(String[] args)
	{
		Map params=new HashMap<String,Object>();
		args=extractOptions(params,args);
		
		Log.filter=getBoolean(params,"verbose",false)?Log.Level.DEBUG:Log.Level.INFO;
		
		verify(args.length==1,"Usage: chswf [-telemetry=true|false] <file>");

		File fin=new File(args[0]);
		
		verify(fin.isFile() && fin.canRead(), MessageFormat.format("{0} is not readable",fin.getAbsolutePath()));
		
		ChangeSwf me=new ChangeSwf(fin);
		try
		{
			me.modify(params);
		}
		catch (IOException e)
		{
			Log.error("Unable to modify (e=%s)",e);
			e.printStackTrace(System.err);
		}
	}
	
	/** Return boolean param */
	private static boolean getBoolean(Map params, String name, Boolean defaultValue)
	{
		if (!params.containsKey(name))
			return defaultValue;
		
		return Boolean.valueOf((String)params.get(name));
	}
	
	/** Extract options */
	private static String[] extractOptions(Map<String,Object> opt,String[] args)
	{
		int j=0;
		for (int i=0;i<args.length;i++)
		{
			String s=args[i];
			char c=(s.length()>0)?s.charAt(0):'?';
			if (c!='-')
			{
				// Copy argument and keep moving
				args[j++]=args[i];
				continue;
			}
			
			int sep=s.indexOf('=',1);
			// -NAME=VALUE
			if (sep>=0)
			{
				opt.put(s.substring(1,sep),s.substring(sep+1));
			}
			// Boolean -NAME -> true
			else
			{
				opt.put(s.substring(1),"true");
			}
		}
		
		// Truncate args
		return Arrays.copyOf(args,j);
	}
	
	/** Verify args */
	private static void verify(boolean expr,String reason)
	{
		if (expr)
			return;
		
		System.out.println("[ERROR] "+reason);
		System.exit(1);
	}
}
