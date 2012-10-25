package swf.io;

import swf.model.SWF;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/** SWFWriter */
public class SWFWriter extends Writer
{
	//
	// Instance variables
	//
	
	/** Nested */
	private FileChannel ch;
	
	/** Compressor */
	private ByteArrayOutputStream compressed;
	
	/** Output */
	private OutputStream out;
	
	/** Buffer */
	private ByteBuffer data;
	
	/** Bits */
	private BitBuffer bits;
	
	/** Size */
	private int size;

	//
	// Public methods
	//

	/** SWFWriter */
	public SWFWriter(File f) throws FileNotFoundException
	{
		this.ch=new FileOutputStream(f).getChannel();
		this.out=Channels.newOutputStream(this.ch);
		this.data=ByteBuffer.allocate(1024*1024);
		this.data.order(ByteOrder.LITTLE_ENDIAN);
		this.bits=new BitBuffer(this.data);
		this.compressed=null;
		this.size=0;
	}

	/** Write header */
	public void writeHeader(SWF.Header h) throws IOException
	{
		this.data.rewind();
		this.data.put(h.sig);
		this.data.put((byte)h.version);
		// File-size - re:written on close
		this.data.putInt(0x0);
		
		flushBuffer();
		
		if (h.isCompressed())
		{
			// Wrap output to compressed buffer
			this.compressed=new ByteArrayOutputStream();
			this.out=new DeflaterOutputStream(this.compressed);
		}
		
		writeRect(h.frameSize);
		this.data.putShort((short)h.frameRate);
		this.data.putShort((short)h.frameCount);

		flushBuffer();
	}
	
	/** Write tag */
	public void writeBlock(SWF.Block bl) throws IOException
	{
		int rh=bl.code<<6;

		if (bl.data.length>=0x3f)
		{
			rh|=0x3f;
			this.data.putShort((short)rh);
			this.data.putInt(bl.data.length);
		}
		else
		{
			rh|=bl.data.length;
			this.data.putShort((short)rh);
		}

		this.data.put(bl.data);

		flushBuffer();
	}
	
	//
	// Writer implementation
	//
	
	/** Not supported */
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		throw new UnsupportedOperationException("direct write() not supported");
	}

	/** Flush */
	public void flush() throws IOException
	{
		flushBuffer();
	}

	/** Close stream */
	public void close() throws IOException
	{
		flushBuffer();
		this.out.flush();
		
		if (this.compressed!=null)
		{
			// Finish compression
			this.out.close();
			// Write the complete buffer to output
			this.ch.write(ByteBuffer.wrap(this.compressed.toByteArray()));
		}

		this.data.clear();
		this.data.putInt(this.size);
		this.data.flip();
		
		this.ch.position(4);
		this.ch.write(this.data);
		this.ch.close();
	}
	
	//
	// Implementation
	//
	
	/** Write data */
	protected void flushBuffer() throws IOException
	{
		this.data.flip();

		// Write the full buffer
		int br=this.data.remaining();
		this.out.write(this.data.array(),this.data.position(),br);
		this.data.position(this.data.position()+br);
		this.size+=br;
		
		this.data.compact();
	}
	
	/** Write RECT */
	protected void writeRect(SWF.RECT rect)
	{
		int max=Math.max(Math.max(rect.xMin,rect.xMax),Math.max(rect.yMin,rect.yMax));
		
		// Calculate number of bits needed to represent max
		int bits=0;
		for (;max>0;bits++)
			max/=2;
		
		// Add sign-bit
		bits+=1;
		
		this.bits.writeChunk(bits,5);
		this.bits.writeChunk(rect.xMin,bits);
		this.bits.writeChunk(rect.xMax,bits);
		this.bits.writeChunk(rect.yMin,bits);
		this.bits.writeChunk(rect.yMax,bits);
		this.bits.flush();
	}
}
