package swf.io;

import junit.framework.TestCase;

import java.nio.ByteBuffer;

/** TestBitBuffer */
public class TestBitBuffer extends TestCase
{
	/** Debug buffer */
	public void debug(String label, ByteBuffer b)
	{
		ByteBuffer c=b.slice();

		String res="";
		while(c.remaining()>0)
		{
			res+=toPaddedString(c.get()&0xff,2,8)+" ";
		}
		
		c.rewind();
		
		while(c.remaining()>0)
		{
			res+=toPaddedString(c.get()&0xff,16,2)+" ";
		}

		System.out.println(label+" "+res);
	}
	
	//
	// Tests
	//
	
	/** Test basic */
	public void testBasic()
	{
		ByteBuffer b=ByteBuffer.allocate(64);
		BitBuffer bb=new BitBuffer(b);
		
		bb.writeChunk(1,4);
		bb.writeChunk(2,4);
		bb.writeChunk(3,4);
		bb.flush();
		
		b.flip();
		
		debug("BASIC",b);
		
		assertEquals(1,bb.readChunk(4));
		assertEquals(2,bb.readChunk(4));
		assertEquals(3,bb.readChunk(4));
		bb.skip();
	}
	
	/** Test Limits */
	public void testLimits()
	{
		ByteBuffer b=ByteBuffer.allocate(64);
		BitBuffer bb=new BitBuffer(b);

		bb.writeChunk(0,3); 
		bb.writeChunk(1,3);
		bb.writeChunk(-1,3);
		bb.writeChunk(7,3); // Bad encoding (-1)
		bb.flush();
		
		b.flip();
		
		debug("0,1,7 Limits",b);

		assertEquals(0,bb.readChunk(3));
		assertEquals(1,bb.readChunk(3));
		assertEquals(-1,bb.readChunk(3));
		assertEquals(-1,bb.readChunk(3));
		bb.skip();
	}

	/** Test encode/decode */
	public void testRECT11()
	{
		ByteBuffer b=ByteBuffer.allocate(64);
		
		BitBuffer bb=new BitBuffer(b);
		
		bb.writeChunk(11,5);
		bb.writeChunk(127,11);
		bb.writeChunk(260,11);
		bb.writeChunk(15,11);
		bb.writeChunk(514,11);
		bb.flush();
		
		b.flip();
		
		debug("Rect11bit",b);

		assertEquals(11,bb.readChunk(5));
		assertEquals(127,bb.readChunk(11));
		assertEquals(260,bb.readChunk(11));
		assertEquals(15,bb.readChunk(11));
		assertEquals(514,bb.readChunk(11));
		bb.skip();
	}

	/** Test encode/decode */
	public void testRECT15()
	{
		ByteBuffer b=ByteBuffer.allocate(64);
		
		BitBuffer bb=new BitBuffer(b);
		
		bb.writeChunk(15,5);
		bb.writeChunk(0,15);
		bb.writeChunk(0,15);
		bb.writeChunk(600,15);
		bb.writeChunk(400,15);
		bb.flush();
		
		b.flip();
		
		debug("Rect15bit",b);

		assertEquals(15,bb.readChunk(5));
		assertEquals(0,bb.readChunk(15));
		assertEquals(0,bb.readChunk(15));
		assertEquals(600,bb.readChunk(15));
		assertEquals(400,bb.readChunk(15));
		bb.skip();
	}
	
	//
	// Implementation
	//
	
	/** Return number padded */
	private String toPaddedString(int value, int radix, int chars)
	{
		String res=Integer.toString(value,radix);
		
		return (res.length()<chars)?("0000000000000000".substring(0,chars-res.length())+res):res;
	}
}
