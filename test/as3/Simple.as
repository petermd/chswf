package 
{

import flash.display.Sprite;
import flash.text.TextField;

[SWF(width="600", height="400", frameRate="60", backgroundColor="#cccc00")]
public class Simple extends Sprite 
{
	public function Simple() 
	{
		var tf:TextField=new TextField();
		tf.text="Hello World!";
		addChild(tf);
	}
}

}