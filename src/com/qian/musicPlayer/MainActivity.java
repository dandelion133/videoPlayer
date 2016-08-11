package com.qian.musicPlayer;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.qian.utils.FormatTime;

public class MainActivity extends Activity {

	private EditText et_path;
	private MediaPlayer mediaPlayer;
	private Button bt_pause,bt_start,bt_stop;
	private SeekBar seekBar1;
	private Timer timer;
	private TimerTask task;
	
	private TextView tv,tv_total;
	
	private SurfaceView sv;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			long miliseconds =  (Long) msg.obj;
			String time = FormatTime.formatTime(miliseconds);

			tv.setText(time + "");//需要放在主线程
		};
	};
	private SurfaceHolder holder;
	private String path;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		et_path = (EditText) findViewById(R.id.et);
		bt_pause = (Button) findViewById(R.id.bt_pause);
		bt_start = (Button) findViewById(R.id.bt_start);
		bt_stop = (Button) findViewById(R.id.bt_stop);
		
		
		
		bt_stop.setEnabled(false);
		bt_pause.setEnabled(false);
		
		tv = (TextView) findViewById(R.id.tv);
		tv_total = (TextView) findViewById(R.id.tv_total);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int postion = seekBar.getProgress();
				System.out.println(postion);						
				mediaPlayer.seekTo(postion);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}
		});		
		
		
		sv = (SurfaceView) findViewById(R.id.sv);
		holder = sv.getHolder();
		//在低版本模拟器上运行记得加上下面的参数。不自己维护双缓冲区，而是等待多媒体播放框架主动的推送数据。
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		

		//后台运行需要的逻辑
		holder.addCallback(new Callback(){

			private int position;

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if(position > 0 ) {
					try {
						mediaPlayer = new MediaPlayer();//创建一个音乐播放器
						System.out.println(path);
						mediaPlayer.setDataSource(path);//设置播放源文件
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置数据流类型流
						mediaPlayer.setDisplay(holder);
						mediaPlayer.prepare();//准备播放    播放的逻辑是c代码在新的线程里面执行。
						
						mediaPlayer.start();//开始播放
						
						seekBar1.setProgress((position-13000));
						Message msg = Message.obtain();//比new Message（） 节省内存
						msg.obj = (long)(position-13000);
						handler.sendMessage(msg);
						
						mediaPlayer.seekTo(seekBar1.getProgress());
						System.out.println("发现位置："+position);
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								mediaPlayer.seekTo(0);
								mediaPlayer.start();//开始播放
							}
						});
						
						int max = mediaPlayer.getDuration();
						seekBar1.setMax(max);
						tv_total.setText(FormatTime.formatTime(max));
						timer = new Timer();
						task = new TimerTask() {
							@Override
							public void run() {
								
								if(mediaPlayer != null) {
									long currentPosition = mediaPlayer.getCurrentPosition();
									seekBar1.setProgress((int) currentPosition);
									Message msg = Message.obtain();//比new Message（） 节省内存
									msg.obj = currentPosition;
									handler.sendMessage(msg);
								}
								
								
							}
						};
						timer.schedule(task, 0, 500);
										
						bt_stop.setEnabled(true);
						bt_pause.setEnabled(true);
					} catch (Exception e) {
						
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "播放失败", 0).show();
					} 
				}
				
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
				if(mediaPlayer != null ) {
					position = seekBar1.getProgress();
					//position = mediaPlayer.getCurrentPosition();
					System.out.println("保存位置："+position);
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
					timer.cancel();
					task.cancel();
					timer = null;
					task = null;
				}
				
				
			}
			
		});
		
		
	}
	
	public void start(View v) {
		
		if(mediaPlayer != null && mediaPlayer.isPlaying()) {
			
		}else{

			path = et_path.getText().toString().trim();
			File file = new File(path);
			if(file.exists()) {
				try {
					mediaPlayer = new MediaPlayer();//创建一个音乐播放器
					System.out.println(path);
					mediaPlayer.setDataSource(path);//设置播放源文件
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置数据流类型流
					mediaPlayer.setDisplay(holder);
					mediaPlayer.prepare();//准备播放    播放的逻辑是c代码在新的线程里面执行。
					mediaPlayer.start();//开始播放
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							mediaPlayer.seekTo(0);
							mediaPlayer.start();//开始播放
						}
					});
					
					int max = mediaPlayer.getDuration();
					seekBar1.setMax(max);
					tv_total.setText(FormatTime.formatTime(max));
					timer = new Timer();
					task = new TimerTask() {
						@Override
						public void run() {
							
							if(mediaPlayer != null) {
								long currentPosition = mediaPlayer.getCurrentPosition();
								seekBar1.setProgress((int) currentPosition);
								Message msg = Message.obtain();//比new Message（） 节省内存
								msg.obj = currentPosition;
								handler.sendMessage(msg);
							}
							
							
						}
					};
					timer.schedule(task, 0, 500);
					bt_stop.setEnabled(true);
					bt_pause.setEnabled(true);
				} catch (Exception e) {
					
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "播放失败", 0).show();
				} 
				
			} else {
				Toast.makeText(getApplicationContext(), "文件不存在，请检查文件", 0).show();
			}
		
		}
		
		
	}
	
	   
	public void pause(View v) {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			bt_pause.setText("继续");
			bt_start.setEnabled(false);
		}else if("继续".equals( bt_pause.getText().toString().trim())) {
			mediaPlayer.start();
			bt_pause.setText("暂停");
		}
	}
	
	
	public void stop(View v) {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			bt_start.setEnabled(true);
			bt_pause.setEnabled(false);
			bt_stop.setEnabled(false);
			
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
		
		
	}
	
	
	
	

}
