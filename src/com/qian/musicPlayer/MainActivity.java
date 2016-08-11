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

			tv.setText(time + "");//��Ҫ�������߳�
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
		//�ڵͰ汾ģ���������мǵü�������Ĳ��������Լ�ά��˫�����������ǵȴ���ý�岥�ſ���������������ݡ�
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		

		//��̨������Ҫ���߼�
		holder.addCallback(new Callback(){

			private int position;

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if(position > 0 ) {
					try {
						mediaPlayer = new MediaPlayer();//����һ�����ֲ�����
						System.out.println(path);
						mediaPlayer.setDataSource(path);//���ò���Դ�ļ�
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//����������������
						mediaPlayer.setDisplay(holder);
						mediaPlayer.prepare();//׼������    ���ŵ��߼���c�������µ��߳�����ִ�С�
						
						mediaPlayer.start();//��ʼ����
						
						seekBar1.setProgress((position-13000));
						Message msg = Message.obtain();//��new Message���� ��ʡ�ڴ�
						msg.obj = (long)(position-13000);
						handler.sendMessage(msg);
						
						mediaPlayer.seekTo(seekBar1.getProgress());
						System.out.println("����λ�ã�"+position);
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								mediaPlayer.seekTo(0);
								mediaPlayer.start();//��ʼ����
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
									Message msg = Message.obtain();//��new Message���� ��ʡ�ڴ�
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
						Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
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
					System.out.println("����λ�ã�"+position);
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
					mediaPlayer = new MediaPlayer();//����һ�����ֲ�����
					System.out.println(path);
					mediaPlayer.setDataSource(path);//���ò���Դ�ļ�
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//����������������
					mediaPlayer.setDisplay(holder);
					mediaPlayer.prepare();//׼������    ���ŵ��߼���c�������µ��߳�����ִ�С�
					mediaPlayer.start();//��ʼ����
					mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							mediaPlayer.seekTo(0);
							mediaPlayer.start();//��ʼ����
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
								Message msg = Message.obtain();//��new Message���� ��ʡ�ڴ�
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
					Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
				} 
				
			} else {
				Toast.makeText(getApplicationContext(), "�ļ������ڣ������ļ�", 0).show();
			}
		
		}
		
		
	}
	
	   
	public void pause(View v) {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			bt_pause.setText("����");
			bt_start.setEnabled(false);
		}else if("����".equals( bt_pause.getText().toString().trim())) {
			mediaPlayer.start();
			bt_pause.setText("��ͣ");
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
