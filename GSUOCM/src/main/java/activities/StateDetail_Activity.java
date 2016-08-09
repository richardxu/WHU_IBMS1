package activities;

import com.suntrans.whu.R;

import Adapter.TouchListener;
import convert.Converts;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class StateDetail_Activity extends AppCompatActivity {
	
	private TextView content;
	private String Content;
	private LinearLayout back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.statedetail);
		Intent intent =getIntent();
		Content=intent.getStringExtra("Content");
		

		content=(TextView)findViewById(R.id.content);
		back=(LinearLayout)findViewById(R.id.layout_back);
		content.setText(Content);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			}});
		//设置通知栏半透明
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Converts.setTranslucentStatus(StateDetail_Activity.this,true);
		}
	}
		
}
