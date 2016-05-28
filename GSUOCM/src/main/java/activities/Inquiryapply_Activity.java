package activities;

import java.util.ArrayList;
import java.util.Map;

import views.AppManager;

import com.suntrans.whu.R;

import Adapter.TouchListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Inquiryapply_Activity extends Activity {
	private ImageView back;   //返回键
	private TextView id,time,apply;  //学号，时间，申请内容
	private ArrayList<Map<String,String>> data=new ArrayList<Map<String,String>>();  //用户申请内容
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.apply_inquiry);     //设置布局文件
		Intent intent=getIntent();
		data=(ArrayList<Map<String, String>>) intent.getSerializableExtra("data");		
		AppManager.getAppManager().addActivity(this);
		id=(TextView)findViewById(R.id.id);
		time=(TextView)findViewById(R.id.time);
		apply=(TextView)findViewById(R.id.apply);			
		back=(ImageView)findViewById(R.id.back);		
		back.setOnClickListener(new OnClickListener(){	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}			
		});
		Map<String,String> map=data.get(0);
		String gettime=map.get("GetTime");
		id.setText(map.get("UserID"));
		time.setText(gettime.substring(0,4)+"年"+gettime.substring(5, 7)+"月"+gettime.substring(8, 10)+"日  "+gettime.substring(11,19));
		apply.setText(map.get("Application")==null?"null":map.get("Application"));
	}
}
