package com.webcloud.widget;
/**
 * @title SingerNameIndexer.java
 * @package com.ct.lbs.vehicle
 * @projectName AndroidLBS
 * @author Johnny
 * @date 2013-11-8
 * @version V1.0
 */
import com.webcloud.R.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 自定义控件 右侧A-Z拼音标识
 * @author Johnny
 */
public class SingerNameIndexer extends View{

	/**
	 * 显示的内容
	 */
	public static String[] indexs = {"#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};;
	private Paint paint = new Paint();
	private int choosed = -1;
	
	/**
	 * 回调的接口
	 */
	public interface OnSingerNameIndexerClicked
	{
		public void singerNameItemClicked(String s);
	}
	
	OnSingerNameIndexerClicked singerClicker;
	
	public void setSingerClicker(OnSingerNameIndexerClicked singerClicker) {
		this.singerClicker = singerClicker;
	}

	public SingerNameIndexer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SingerNameIndexer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SingerNameIndexer(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int h = getHeight();
		int w = getWidth();
		int singleH = h / indexs.length;
		for(int i = 0; i < indexs.length; i ++)
		{
			paint.setColor(color.zm_color);
			paint.setAntiAlias(true);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(20);
			if(choosed == i)
			{
				paint.setColor(Color.RED);
				paint.setFakeBoldText(true);
			}
			float x = w /2 - paint.measureText(indexs[i])/2;
			float y = singleH * i+20;
			canvas.drawText(indexs[i], x, y, paint);
			paint.reset();
		}
	}
	
	/**
	 * 每当触摸一个字母的时候
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int oldChoosed = choosed;
		int c = (int) (event.getY() / getHeight() * indexs.length);
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if(singerClicker != null && oldChoosed != c)
			{
				if(c >= 0 && c < indexs.length)
				{
					choosed = c;
					singerClicker.singerNameItemClicked(indexs[c]);
					invalidate();
				}
			}
		}
		return true;
	}
}
