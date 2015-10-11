package edu.sdsu.cs.chinnu.myproject;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

public class MusicController extends MediaController {

	public MusicController(Context c){
		super(c);
	}

	// By default the MediaController hides itself after 3 seconds of time. 
	// In order to make it visible throughout the audio playback, override the hide() of MediaController. 
	public void hide(){}
	
	
	// If media controller is visible when music is playing, 
	// need to handle the KEYCODE_BACK to finish the Activity
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_BACK){
        	Context c = getContext();
            ((Activity) c).finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
	
}
