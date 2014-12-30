package com.example.bookrecognition;


import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qq.wx.img.imgsearcher.ImgListener;
import com.qq.wx.img.imgsearcher.ImgResult;
import com.qq.wx.img.imgsearcher.ImgSearcher;
import com.qq.wx.img.imgsearcher.ImgSearcherState;

public class RecognizePicActivity extends ActionBarActivity implements ImgListener{

	private ImageView img;
	
	private String mResUrl;
	private String mResMD5;
	private String mResPicDesc;
	private Bitmap bm;
	private TextView hints;
	private Button cancel;
	//微信应用授权码
	private static final String screKey = "ceb253c4e98eccf48a417b3dfb9c9bd6";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognize_pic);
		hints = (TextView) findViewById(R.id.start_searching);
		cancel = (Button)findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("cancel");
				ImgSearcher.shareInstance().cancel();
				RecognizePicActivity.this.finish();
				
			}
		});
		
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String path = bundle.getString("path");
		img = (ImageView)findViewById(R.id.img);
		bm = BitmapFactory.decodeFile(path);
		img.setImageBitmap(bm);
		int ret = searchImage(getJpg(bm));
		if (ret != 0)
			this.finish();
	}
	
	public byte[] getJpg(Bitmap bitmap) {
		ByteArrayOutputStream outs = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outs);
		
		return outs.toByteArray();
	}
	
	
	@Override
	public void onGetError(int arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "ErrorCode=" + arg0, Toast.LENGTH_LONG).show();
		this.finish();
	}


	@Override
	public void onGetResult(ImgResult result) {
		System.out.println("ongetResult");
		// TODO Auto-generated method stub
		boolean ret = true;
		if (result != null) {
			if (1 == result.ret && result.res != null) {
				int resSize = result.res.size();
				for (int i = 0; i < resSize; ++i) {
					ImgResult.Result res = (ImgResult.Result) result.res.get(i);
					if (res != null) {
						mResMD5 = res.md5;
						mResUrl = res.url;
						mResPicDesc = res.picDesc;
					}
				}
				ret = true;
			}
			else {
				ret = false;
			}
		}
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
		vibrator.vibrate(200);
		turnToResultActivity(ret);
	}


	public void turnToResultActivity(boolean isFound){
		Intent intent = new Intent(this,ResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("ret",isFound);
		bundle.putString("url", mResUrl);
		bundle.putString("md5", mResMD5);
		bundle.putString("picDesc",mResPicDesc);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onGetState(ImgSearcherState state) {
		// TODO Auto-generated method stub
		if (ImgSearcherState.Canceling == state){
			hints.setText("正在取消...");
		}else if(ImgSearcherState.Canceled == state){
			this.finish();
		}
	}


	public int searchImage(byte[] img){
		ImgSearcher.shareInstance().setListener(this);
		int init = ImgSearcher.shareInstance().init(this, screKey);
		if (0 != init){
			Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
			return -1;
		}
		System.out.println("img:"+img);
		int ret = ImgSearcher.shareInstance().start(img);
		
		if (0 == ret) {
			return 0;
		}
		else {
			Toast.makeText(this, "ErrorCode = " + ret, Toast.LENGTH_LONG).show();;
			return -1;
		}
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (null != bm && !bm.isRecycled()) {
               bm.recycle();
		}
	   	ImgSearcher.shareInstance().destroy();
	}

}
