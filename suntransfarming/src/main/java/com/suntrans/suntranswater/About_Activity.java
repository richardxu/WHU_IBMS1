package com.suntrans.suntranswater;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class About_Activity extends Activity {

	private LinearLayout layout_back;     //返回键

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.about);      //设置activity布局文件
		layout_back=(LinearLayout)findViewById(R.id.layout_back);
		//返回键监听
		layout_back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);   //设置activity切换动画
			}});				
		
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK)   //如果按下的是返回键
		{
			finish();
			overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);   //设置activity切换动画
			return true;
		}
		else
			return super.onKeyDown(keyCode, event);
	}

}
