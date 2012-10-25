package swf.io;

import swf.model.SWF;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.InflaterInputStream;

/** SWFReader */
public class SWFReader extends Reader
{
	//
	// Instance variables
	//
	
	/** Input */
	private InputStream in;
	
	/** Buffer */
	private ByteBuffer src; 
	
	/** BitBuffer */
	private BitBuffer bits;
	
	//
	// Public methods
	//

	/* Create new SWFReader for File */
	public SWFReader(File f) throws IOException
	{
		this(new FileInputStream(f));
	}
	
	/** Create new SWFReader */
	public SWFReader(InputStream in) throws IOException
	{
		this.in=in;
		
		// Create empty buffer
		this.src=ByteBuffer.allocate(1024*1024);
		this.src.order(ByteOrder.LITTLE_ENDIAN);
		// Flip to READ mode (empty to trigger read on first prefetch)
		this.src.flip();
		
		// Wrap with bitbuffer 
		this.bits=new BitBuffer(this.src);
	}

	/** Read header and return version number */
	public SWF.Header readHeader() throws IOException
	{
		prefetch(8);
		
		byte[] sig=new byte[3];
		this.src.get(sig,0,3);
		int version=this.src.get();
		
		int fileSize=this.src.getInt();
		
		// If the file is compressed then deflate on the fly
		if ('C'==sig[0])
		{
			this.in=new InflaterInputStream(this.in);
		}
		
		prefetch();
		
		return new SWF.Header(
			sig,
			version,
			fileSize,
			readRect(),
			getUnsignedShort(),
			getUnsignedShort());
	}
	
	/** Read RECT */
	public SWF.RECT readRect() throws IOException
	{
		prefetch(10);
		
		int fieldSize=this.bits.readChunk(5);
		
		SWF.RECT res=new SWF.RECT(
			this.bits.readChunk(fieldSize),	
			this.bits.readChunk(fieldSize),	
			this.bits.readChunk(fieldSize),	
			this.bits.readChunk(fieldSize)	
		);
		
		this.bits.skip();
		
		return res;
	}
	
	/** Read block */
	public SWF.Block readBlock() throws IOException
	{
		prefetch(6);
		
		if (this.src.remaining()==0)
			return null;
		
		int rh=getUnsignedShort();
		
		int tag=(rh>>6);
		int len=(rh&0x3f);
		if (len==0x3f)
		{
			len=this.src.getInt();
		}
		
		byte[] data=new byte[len];

		prefetch(len);
		
		this.src.get(data);
		
		return new SWF.Block(tag,data);
	}

	/** Read buffer */
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		throw new UnsupportedOperationException("read(char[],int,int) not supported");
	}

	/** Close */
	public void close() throws IOException
	{
		if (this.in!=null)
			this.in.close();
	}
	
	//
	// Implementation
	//
	
	/** Read unsigned short */
	protected int getUnsignedShort()
	{
		return this.src.getShort()&0xffff;
	}
	
	/** Prefetch as much data as possible */
	protected void prefetch() throws IOException
	{
		prefetch(this.src.capacity());
	}

	/** Prefetch max bytes */
	protected void prefetch(int max) throws IOException
	{
		if (this.src.remaining()>max)
			return;
		
		this.src.compact();
		
		max=Math.min(max,src.limit()-src.position());

		// Keep reading data until the max target is reached
		int rb;
		while(max>0)
		{
			rb=this.in.read(this.src.array(),this.src.position(),max);
			if (rb<=0)
				break;
			
			this.src.position(this.src.position()+rb);
			max-=rb;
		} 
		
		this.src.flip();
	}		
}
