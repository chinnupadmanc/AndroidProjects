package edu.sdsu.cs.chinnu.flappybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Drawing extends View {
	ShapeDrawable rect;
	Paint paint = new Paint();
	
	public Drawing(Context context) {
		super(context);
	}

	public Drawing(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//Each time the view needs to be drawn on the screen its onDraw method is called.
	@Override
	protected void onDraw(Canvas canvas) {		
		paint.setColor(Color.DKGRAY);
		
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		canvas.drawRect(0, 0, width, height, paint);
		
		Log.i("onDraw", "canvas.getWidth(): "+ canvas.getWidth());
		Log.i("onDraw", "canvas.getHeight(): "+ canvas.getHeight());
	}
}