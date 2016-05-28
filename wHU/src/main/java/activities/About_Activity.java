package activities;

import views.AppManager;

import com.suntrans.whu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class About_Activity extends Activity {

	private ImageView back;     //返回键	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.about);      //设置activity布局文件
		back=(ImageView)findViewById(R.id.back);		
		//返回键监听
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});				
		
	}


}
