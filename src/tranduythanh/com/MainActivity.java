package tranduythanh.com;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	ProgressBar bar;
	//khai báo handler class để xử lý đa tiến trình
	Handler handler;
	//dùng AtomicBoolean để thay thế cho boolean
	AtomicBoolean isrunning=new AtomicBoolean(false);
	//boolean
	Button btnstart;
	TextView lblmsg;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bar=(ProgressBar) findViewById(R.id.progressBar1);
		btnstart=(Button) findViewById(R.id.btnstart);
		btnstart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				doStart();
			}
		});
		//viết lệnh cho handler class để nhận thông điệp
		//gửi về từ tiến trình con
		//mọi thông điệp sẽ được xử lý trong handleMessage
		//từ tiến trình con ta gửi Message về cho main thread
		handler=new Handler(){
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//msg.arg1 là giá trị được trả về trong message
				//của tiến trình con
				bar.setProgress(msg.arg1);
				lblmsg.setText(msg.arg1+"%");
			}
		};
		lblmsg=(TextView) findViewById(R.id.textView1);
	}
	public void doStart()
	{
		bar.setProgress(0);
		isrunning.set(false);
		//tạo 1 tiến trình CON
		Thread th=new Thread(new Runnable() {
			@Override
			public void run() {
				//vòng lặp chạy 100 lần
				for(int i=1;i<=100 && isrunning.get();i++)
				{
					//cho tiến trình tạm ngừng 100 mili second
					SystemClock.sleep(100);
					//lấy message từ Main thread
					Message msg=handler.obtainMessage();
					//gán giá trị vào cho arg1 để gửi về Main thread
					msg.arg1=i;
					//gửi lại Message này về cho Main Thread
					handler.sendMessage(msg);
				}
			}
		});
		isrunning.set(true);
		//kích hoạt tiến trình
		th.start();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
