package swf.util;

import java.util.Arrays;

/** Simple StdOut Log */
public class Log
{
	public enum Level 
	{ 
		DEBUG(0), 
		INFO(1), 
		WARN(2), 
		ERROR(3);
		
		private int value;
		
		/** Create new Level */
		Level(int value)
		{
			this.value=value;
		}
		    
		/** Return true if the level matches a given filter */
		public boolean match(Level filter)
		{
			return filter.value<=this.value;
		}
	}
	
	public static Level filter=Level.INFO;

	/** Short-cuts */
	
	public static void debug(String format, Object... args) { out(Level.DEBUG,format,args); }
	public static void info(String format, Object... args) { out(Level.INFO,format,args); }
	public static void warn(String format, Object... args) { out(Level.WARN,format,args); }
	public static void error(String format, Object... args) { out(Level.ERROR,format,args); }
	
	/** Output */
	public static void out(Level level, String format, Object... args)
	{
		if (!level.match(filter))
			return;
		
		System.out.println("["+level+"] "+String.format(format,args));
	}
}
