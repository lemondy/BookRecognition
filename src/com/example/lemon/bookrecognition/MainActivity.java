package com.example.lemon.bookrecognition;

import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.bookrecognition.R;
import com.example.bookrecognition.RecognizePicActivity;


public class MainActivity extends ActionBarActivity {


	private SurfaceView picSV;
	@SuppressWarnings("deprecation")
	private Camera camera;

	private ImageButton btn;

	private Image pic = null;
	private RelativeLayout layout;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picSV = (SurfaceView)findViewById(R.id.picSV);
        btn = (ImageButton)findViewById(R.id.takeBtn);
        layout = (RelativeLayout) findViewById(R.id.layout);
        picSV.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        picSV.getHolder().addCallback(new MyCallback());
    }
    
   

    private class MyCallback implements Callback{

		@SuppressWarnings("deprecation")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			//SurfaceView创建的时候打开摄像头，设置预览景所在的SurfaceView、设置拍照的参数、开启预览取景等操作
			try{
				camera = Camera.open();  //默认打开后置摄像头
				camera.setPreviewDisplay(picSV.getHolder());
				//设置相机中照片显示的方向
				MainActivity.setCameraDisplayOrientation(MainActivity.this,0,camera);
				Parameters params = camera.getParameters();
				params.setPictureSize(800, 480);
				params.setPreviewSize(800, 480);
				params.setFlashMode("on");  //开启闪光灯
				params.setJpegQuality(50); //设置图片质量为50
				
				camera.setParameters(params);
				camera.startPreview();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			camera.stopPreview();
			camera.release();
			camera = null;
		}
    	
    }


    public static void setCameraDisplayOrientation(Activity activity,int cameraId,
			android.hardware.Camera camera){
    	android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    	android.hardware.Camera.getCameraInfo(cameraId,info);
    	int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    	int degrees = 0;
    	switch(rotation){
    	case Surface.ROTATION_0:
    		degrees = 0;
    		break;
    	case Surface.ROTATION_90:
    		degrees = 90;
    		break;
    	case Surface.ROTATION_180:
    		degrees = 180;
    		break;
    	case Surface.ROTATION_270:
    		degrees = 270;
    		break;
    	}
    	int result;
    	if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
    		result = (info.orientation + degrees) % 360;
    		result = (360 -result ) % 360;
    	}else{
    		result = (info.orientation - degrees + 360) % 360;
    	}
    	camera.setDisplayOrientation(result);
    }
    //响应拍照按钮
    public void takepic(View v){
    	camera.autoFocus(new MyAutoFocusCallback());
    }
    
    private class MyAutoFocusCallback implements AutoFocusCallback{

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			camera.takePicture(null, null, null,new MyPictureCallback());
		}
    	
    }
    
    @SuppressLint("SdCardPath") 
    private class MyPictureCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try{
				String path = "/mnt/sdcard/DCIM/Camera/"+System.currentTimeMillis()+".jpeg";
				//保存图片到sd卡
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(data);
				fos.close();
				//读取图片显示
				/*Bitmap bitmap = BitmapFactory.decodeFile(path);
				ImageView img = new ImageView(MainActivity.this);
				img.setId(1);
				img.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
				img.setScaleType(ImageView.ScaleType.CENTER_CROP);
				img.setPadding(2, 0, 0, 5);
				img.setImageBitmap(bitmap);
				
				layout.addView(img);
				
				progressBar.setVisibility(View.VISIBLE);
		    	btn.setVisibility(View.INVISIBLE);
		    	hints.setVisibility(View.VISIBLE);
		    	*/
				
				  Intent intent = new Intent();
				  intent.putExtra("path", path);
			      intent.setClass(MainActivity.this,RecognizePicActivity.class);
			      startActivity(intent);
				
			}catch(Exception e){
				
			}
		}
    	
    }
    
   
}
