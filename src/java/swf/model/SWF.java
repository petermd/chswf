package swf.model;

import java.util.HashMap;
import java.util.Map;

/** SWF */
public class SWF
{
	/** Tag Type */
	public enum Tag
	{
		End(0),
		ShowFrame(1),
		DefineShape(2),
		PlaceObject(4),
		RemoveObject(5),
		DefineBits(6),
		DefineButton(7),
		JPEGTables(8),
		SetBackgroundColor(9),
		DefineFont(10),
		DefineText(11),
		DoAction(12),
		DefineFontInfo(13),
		DefineSound(14),
		StartSound(15),
		DefineButtonSound(17),
		SoundStreamHead(18),
		SoundStreamBlock(19),
		DefineBitsLossless(20),
		DefineBitsJPEG2(21),
		DefineShape2(22),
		DefineButtonCxform(23),
		Protect(24),
		PlaceObject2(26),
		RemoveObject2(28),
		DefineShape3(32),
		DefineText2(33),
		DefineButton2(34),
		DefineBitsJPEG3(35),
		DefineBitsLossless2(36),
		DefineEditText(37),
		DefineSprite(39),
		FrameLabel(43),
		SoundStreamHead2(45),
		DefineMorphShape(46),
		DefineFont2(48),
		ExportAssets(56),
		ImportAssets(57),
		EnableDebugger(58),
		DoInitAction(59),
		DefineVideoStream(60),
		VideoFrame(61),
		DefineFontInfo2(62),
		EnableDebugger2(64),
		ScriptLimits(65),
		SetTabIndex(66),
		FileAttributes(69),
		PlaceObject3(70),
		ImportAssets2(71),
		DefineFontAlignZones(73),
		CSMTextSettings(74),
		DefineFont3(75),
		SymbolClass(76),
		Metadata(77),
		DefineScalingGrid(78),
		DoABC(82),
		DefineShape4(83),
		DefineMorphShape2(84),
		DefineSceneAndFrameLabelData(86),
		DefineBinaryData(87),
		DefineFontName(88),
		StartSound2(89),
		DefineBitsJPEG4(90),
		DefineFont4(91),
		Signed(92),
		Telemetry(93),
		Unknown(-1);
		
		// Index
		private static Map<Integer,Tag> codeIndex;
		
		static
		{
			codeIndex=new HashMap<Integer,Tag>();
			for (Tag t : values())
				codeIndex.put(t.code,t);
		}
		
		// Instance variables
		
		/** Code */
		protected int code;
		
		/** Create new Tag */
		private Tag(int code)
		{
			this.code=code;
		}
		
		/** Return code */
		public int getCode()
		{
			return this.code;
		}
		
		/** Tag */
		public static Tag fromCode(int value)
		{
			Tag t=codeIndex.get(value);
			return (t!=null)?t:Unknown;
		}
	}
	
	/** Header */
	public static class Header
	{
		/** Sig */
		public byte[] sig;
		
		/** Version */
		public int version;

		/** File size */
		private long fileSize;

		/** Frame */
		public RECT frameSize;
		public int frameRate,frameCount;

		/** Create new Header */
		public Header(byte[] sig, int version, int fileSize, RECT frameSize, int frameRate, int frameCount)
		{
			this.sig=sig;
			this.version=version;
			this.fileSize=fileSize;
			this.frameSize=frameSize;
			this.frameRate=frameRate;
			this.frameCount=frameCount;
		}
		
		/** Return string representation */
		public String toString()
		{
			return "Header(sig="+new String(sig)+",compressed="+isCompressed()+",version="+version+",fileSize="+fileSize+",frameSize="+frameSize+",frameRate="+getFPS()+",frameCount="+frameCount+")";
		}

		/** Return true if compressed */
		public boolean isCompressed()
		{
			return this.sig[0]=='C';
		}

		/** Return Frames Per Second */
		public float getFPS()
		{
			return (float)(frameRate/256)+(float)(frameRate%256)/100;
		}
	}
	
	/** Block */
	public static class Block
	{
		/** Tag */
		public Tag tag;
		
		/** Code */
		public int code;
		
		/** Data */
		public byte[] data;
		
		/** Create new Block */
		public Block(int code, byte[] data)
		{
			this.tag=Tag.fromCode(code);
			this.code=code;
			this.data=data;
		}
		
		/** Return string representation */
		public String toString()
		{
			return this.tag+"["+code+"]="+data.length;
		}
	}
	
	// Data Types
	
	/** RECT */
	public static class RECT
	{
		/** Limits */
		public int xMin,xMax,yMin,yMax;
	
		/** RECT */
		public RECT(int xMin,int xMax,int yMin,int yMax)
		{
			this.xMin=xMin;
			this.xMax=xMax;
			this.yMin=yMin;
			this.yMax=yMax;
		}
		
		/** Return string representation */
		public String toString()
		{
			return "RECT[("+xMin+","+yMin+")->("+xMax+","+yMax+")]";
		}
	}
}
