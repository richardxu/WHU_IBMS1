package activities;

import com.suntrans.whu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import convert.Converts;

public class About_Activity extends AppCompatActivity {

	private LinearLayout back;     //返回键

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	setContentView(R.layout.about);      //设置activity布局文件
		back=(LinearLayout)findViewById(R.id.layout_back);

		//返回键监听
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(About_Activity.this,true);
		}
	}


}
