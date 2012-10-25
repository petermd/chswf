package swf.io;

import java.io.*;

import junit.framework.TestCase;

import swf.model.SWF;

/** TestWriter */
public class TestWriter extends TestCase
{
	/** Data */
	protected File testData,testOutput;
	
	/** Setup */
	public void setUp()
	{
		this.testData=new File(System.getProperty("test.data"));
		this.testOutput=new File(System.getProperty("test.output"));
	}
	
	/** Read a SWF from Input */
	protected SWF.Header checkRead(File src) throws IOException
	{
		SWFReader reader=new SWFReader(new FileInputStream(src));
		
		SWF.Header header=reader.readHeader();
		
		reader.close();
		
		return header;
	}

	/** Copy a SWF from Input to Output and check binary equivalence */
	protected SWF.Header checkCopy(File src) throws IOException
	{
		byte[] raw=fetch(src);

		File outFile=File.createTempFile("copy-",".swf",this.testOutput);
		
		SWFReader reader=new SWFReader(new ByteArrayInputStream(raw));
		SWFWriter writer=new SWFWriter(outFile);
		
		SWF.Header header=reader.readHeader();
		
		writer.writeHeader(header);
		
		SWF.Block b;
		while ((b=reader.readBlock())!=null)
		{
			writer.writeBlock(b);
		}
		writer.close();
		
		byte[] res=fetch(outFile);
		
		assertEquals("Byte length",raw.length,res.length);
		for (int i=0;i<res.length;i++)
		{
			assertEquals("byte["+i+"]",raw[i],res[i]);
		}
		
		return header;
	}

	//
	// Tests
	//
	
	/** Test simple */
	public void testSimple() throws IOException
	{
		SWF.Header h=checkCopy(new File(this.testData,"Simple.swf"));
		
		assertEquals(14,h.version);
		assertEquals(0,h.frameSize.xMin);
		assertEquals(0,h.frameSize.yMin);
		assertEquals(600*20,h.frameSize.xMax);
		assertEquals(400*20,h.frameSize.yMax);
		assertTrue(60.0f-h.getFPS()<0.001);
		assertEquals(1,h.frameCount);
	}

	/** Test compressed */
	public void testCompressed() throws IOException
	{
		SWF.Header h=checkRead(new File(this.testData,"Compressed.swf"));
		
		assertEquals(14,h.version);
		assertEquals(0,h.frameSize.xMin);
		assertEquals(0,h.frameSize.yMin);
		assertEquals(400*20,h.frameSize.xMax);
		assertEquals(400*20,h.frameSize.yMax);
		assertTrue(60.0f-h.getFPS()<0.001);
		assertEquals(1,h.frameCount);
	}

	//
	// Implementation
	//
	
	/** Read a file into a buffer */
	protected byte[] fetch(File src) throws IOException
	{
		byte[] data=new byte[(int)src.length()];
		InputStream in=null;
		try
		{
			in=new FileInputStream(src);
			
			int br,tr=0;
			while((br=in.read(data,tr,data.length-tr))>0)
			{
				tr+=br;
			}
		}
		finally
		{
			FileUtils.closeQuietly(in);
		}
		
		return data;
	}
}
