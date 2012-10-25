package swf.io;

import java.nio.ByteBuffer;

/** Bit Buffer */
public class BitBuffer
{
	// Instance variables

	/** Current */
	private int cur;
	
	/** Remaining bits */
	private int remaining;
	
	/** Source */
	private ByteBuffer src;

	// Public methods
	
	/** Create new BitBuffer */
	public BitBuffer(ByteBuffer src)
	{
		this.src=src;
		this.cur=0;
		this.remaining=0;
	}

	/** Write N bits */
	public void writeChunk(int value, int len)
	{
		// Shift left and add the new data
		this.cur=(this.cur<<len)|(value&((1<<len)-1));
		this.remaining+=len;
		
		// Output bytes
		while(this.remaining>=8)
		{
			this.remaining-=8;
			int b=((this.cur>>this.remaining)&0xff);
			this.src.put((byte)b);
		}
	}
	
	/** Flush */
	public void flush()
	{
		if (this.remaining==0)
			return;
		
		// Pad out with 0
		this.cur<<=(8-this.remaining);

		this.src.put((byte)(this.cur&0xff));

		this.cur=0;
		this.remaining=0;
	}
	
	/** Return N bits */
	public int readChunk(int len)
	{
		while (len>this.remaining)
		{
			int b=this.src.get()&0xff;
			this.cur=(this.cur<<8)+b;
			this.remaining+=8;
		}
		
		this.remaining-=len;
		
		int signBit=1<<(len-1);
		int mask=signBit-1;
		
		int res=(this.cur>>>this.remaining);
		
		if ((res&signBit)==signBit)
		{
			res=(~res)&mask;
			
			return -1-res;
		}
		else
		{
			return res&mask;
		}
	}
	
	/** Skip remainder */
	public void skip()
	{
		this.remaining=0;
	}
}
