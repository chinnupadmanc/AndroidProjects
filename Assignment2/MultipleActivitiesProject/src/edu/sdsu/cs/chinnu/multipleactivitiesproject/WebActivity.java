package edu.sdsu.cs.chinnu.multipleactivitiesproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class WebActivity extends Activity {

	private EditText editText1;
	private WebView webView;

	//Method setDisplayHomeAsUpEnabled() is from API level 11.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//If the current activity does not have parent activity defined, no need to display the left pointing caret.
			if (NavUtils.getParentActivityName(this) != null) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	public void goToSite(View button)
	{
		editText1 = (EditText) findViewById(R.id.editText1);
		String  webSite = editText1.getText().toString();
		
		//Adding http:// if the user has not entered it in the URL.
		if (!(webSite.substring(0, 4).equals("http"))) {
			webSite = "http://" + webSite;	 
		}
		
		webView = (WebView) findViewById(R.id.webView1);
		
		webView.setWebViewClient(new WebViewClient()       
	    {
	         @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) 
	        {
	            return false;
	        }
	    });
				
		webView.loadUrl(webSite);
		
		//Hiding the soft keyboard once the web page is opened.
		InputMethodManager manager;
		manager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	//Handle going back to the main activity upon clicking on application icon in the action bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			//If the current activity does not have parent activity defined, no need to navigate up.
			if (NavUtils.getParentActivityName(this) != null) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;

		default:
			return super.onOptionsItemSelected(item); 	   
		}
	}
}
