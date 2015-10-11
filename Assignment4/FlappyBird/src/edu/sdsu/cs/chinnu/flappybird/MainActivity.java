package edu.sdsu.cs.chinnu.flappybird;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {

	private AnimationDrawable scenceAnimation;
	boolean isInMotion = false;
	boolean pressed = false;
	int screenWidth;
	int screenHeight;
	int layoutHeight;
	RelativeLayout layoutMain;
	ImageView bird;
	Button startBtn;
	Button restartBtn;
	Drawing rectShape1;
	Drawing rectShape2;
	TextView tapToStart;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        startBtn = (Button) findViewById(R.id.start);
        startBtn.setVisibility(View.VISIBLE);
        
        restartBtn = (Button) findViewById(R.id.restart);
        tapToStart = (TextView) findViewById(R.id.tapToStart);
        bird = (ImageView) findViewById(R.id.flappyBird1);
        
        rectShape1 = (Drawing) findViewById(R.id.drawing1);
        rectShape2 = (Drawing) findViewById(R.id.drawing2);
            
        this.scenceAnimation = (AnimationDrawable) bird.getBackground();
        
        layoutMain = (RelativeLayout) findViewById(R.id.main_layout);
        layoutMain.setOnTouchListener(this);         
        
        //Getting screen height and width
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        screenHeight = screenSize.y;
        screenWidth = screenSize.x;
             
        //Placing rectangles outside the screen. Once the game begins, it will moves left and appear on screen.
        rectShape1.setX(screenWidth + 10);
        rectShape2.setX(screenWidth + 10);
                
    }

	//Method for Start button click
	public void start(View source) {
		this.scenceAnimation.start();
		isInMotion = true;
		startBtn.setVisibility(View.INVISIBLE);
		tapToStart.setVisibility(View.VISIBLE);
	}

	//Method for Play Again button click
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void restart(View source) {
		//Recreate the activity
		super.recreate();
		
		this.scenceAnimation.start();
		isInMotion = true;
		startBtn.setVisibility(View.INVISIBLE);
		tapToStart.setVisibility(View.VISIBLE);
	}

	//Method for tapping on the screen
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		tapToStart.setVisibility(View.INVISIBLE);

		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		switch (actionCode) {  
			//A pressed gesture has started.
			case MotionEvent.ACTION_DOWN:
			{
				pressed = true;
				Log.i("onTouch", "Pressed: "+pressed);
				move();
				break;
			}
			//A pressed gesture has finished.
			case MotionEvent.ACTION_UP:
			{
				pressed = false;
				Log.i("onTouch", "Pressed: "+pressed);
				move();
				break;
			}
		}

		return pressed;
	}
   
	//Move rectangles towards left on the x-axis and bird up and down on the y-axis.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void move() {    	
    	if (isInMotion)
    	{
	    	//Move rectangle from right to left.
	    	rectShape1.setX(rectShape1.getX() - 5);
	    	rectShape2.setX(rectShape2.getX() - 5);
	    	
	    	//Move bird up and down.
	    	if(pressed)
	    	{
	    		bird.setY(bird.getY() - 5);
	    	}
	    	else
	    	{
	    		bird.setY(bird.getY() + 5);
	    	}
	    		
    		checkCollison();
    	}
    	
    	if (isInMotion)
    	{
    		checkEndOfGame();
    	}
    	
    	if (isInMotion)
    	{
    		bird.postDelayed(new Mover(), 50);
    	}
    		
    }
    
    public class Mover implements Runnable {
    	@Override
    	public void run() {
    		move();
    	}
    }

    //Check for collision of bird on upper and lower edges of the screen and with rectangles.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void checkCollison() {
    	//Check collision on upper and lower edges of the screen.
    	if (yIsOutOfBounds(bird)) 
    	{
    		isInMotion = false;
    		this.scenceAnimation.stop();
    		
    		//Show dialog to show 'Game Over!'
    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Game Over!");
    		
    		//Play Again
    		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Play Again", new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	MainActivity.this.recreate();
    		    }
    		});
    		
    		//Exit Game
    		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	MainActivity.this.finish();
    		    }
    		});
    		alertDialog.setCancelable(false);
    		alertDialog.show();
    	}
    	
    	//Check collision of bird and rectangles.
    	if(checkWidgetCollision()) {
    		isInMotion = false;
    		this.scenceAnimation.stop();
    		
    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Game Over!");
    		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Play Again", new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	MainActivity.this.recreate();
    		    }
    		});
    		
    		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog, int which) {
    		    	MainActivity.this.finish();
    		    }
    		});
    		
    		alertDialog.setCancelable(false);
    		alertDialog.show();	
    	}
    }
    
    //Check if rectangles reached left side.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void checkEndOfGame() {
    	float x = rectShape1.getX();
    	Log.i("checkEndOfGame", "rectShape1.getX(): "+x);
    	Log.i("checkEndOfGame", "bird.getX(): "+ bird.getX());
    	Log.i("checkEndOfGame", "rectShape1.getWidth(): "+ rectShape1.getWidth());
    	if (x + rectShape1.getWidth() < 0) 
    	{
    		Toast.makeText(this, "Congrats! Won the game!", Toast.LENGTH_SHORT).show();
    		isInMotion = false;
    		this.scenceAnimation.stop();
    		restartBtn.setVisibility(View.VISIBLE);
    	}
    }

    //Check collision on upper and lower edges of the screen.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean yIsOutOfBounds(View widget) {
    	
    	layoutHeight = layoutMain.getHeight();
    	
    	float y = widget.getY();
    	
    	//Upper edge collision condition
    	if (y < 0) 
    	{
    		return true;
    	}
    	
    	//Lower edge collision condition
    	if (y + widget.getHeight() > layoutHeight)
    	{
    		return true;
    	}
    	
    	return false;
    }

    //Check collision of bird and rectangles.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean checkWidgetCollision() {
    	float x1 = bird.getX();
    	float x2 = rectShape1.getX();
    	
    	float y1 = bird.getY();
    	float yRect1 = rectShape1.getY();
    	float yRect2 = rectShape2.getY();
    	
    	Log.i("checkWidgetCollision", "bird.getX(): " + x1);
    	Log.i("checkWidgetCollision", "bird.getWidth(): " + bird.getWidth());
    	Log.i("checkWidgetCollision", "rectShape1.getX(): " + x2);
    	
    	//Bird crosses the rectangles
    	if ((x1 + bird.getWidth()) >= x2) 
    		{
    		Log.i("checkWidgetCollision", "bird.getY(): " + y1);
    		Log.i("checkWidgetCollision", "yRect1: " + yRect1);
    		Log.i("checkWidgetCollision", "yRect2: " + yRect2);
    		Log.i("checkWidgetCollision", "rectShape1.getHeight(): " + rectShape1.getHeight());
    		
    		//Bird enters the gap. It does not hit the rectangle.
    		if ( (y1 < (yRect1 + rectShape1.getHeight())) || ((y1 + bird.getHeight()) > yRect2) )
    				return true;   	
    		}
    	
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
