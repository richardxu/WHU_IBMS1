package activities;

import com.suntrans.whu.R;

import Adapter.TouchListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class StateDetail_Activity extends Activity {
	
	private TextView title,content;
	private String Content;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.statedetail);
		Intent intent =getIntent();
		Content=intent.getStringExtra("Content");
		
		title=(TextView)findViewById(R.id.title);
		content=(TextView)findViewById(R.id.content);
		back=(ImageView)findViewById(R.id.back);
		content.setText(Content);
		back.setOnTouchListener(new TouchListener());
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
	}
		
}
