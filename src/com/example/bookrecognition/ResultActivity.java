package com.example.bookrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ResultActivity extends ActionBarActivity {

	private TextView recUrl;
	private TextView recMd5;
	private TextView recPicDesc;
	private boolean isFound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		recUrl = (TextView)findViewById(R.id.url);
		recMd5 = (TextView)findViewById(R.id.md5);
		recPicDesc = (TextView)findViewById(R.id.picDesc);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		isFound = bundle.getBoolean("ret");
		if(isFound){
			recUrl.setText(bundle.getString("url"));
			recMd5.setText(bundle.getString("md5"));
			recPicDesc.setText(bundle.getString("picDesc"));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
