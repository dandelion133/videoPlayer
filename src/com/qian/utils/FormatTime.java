package com.qian.utils;

public class FormatTime {
	private static String time;

	public static String formatTime(long miliseconds) {
		
		int seconds = (int) (miliseconds/1000);
		int minute = seconds/60;
		int second = seconds - minute * 60;
		
		
		if(second >= 0 && second < 10 && minute >= 0 && minute < 10) {
			time = "0"+minute + ":0" + second;
		}else if(second >= 0 && second < 10) {
			time = minute + ":0" + second;
		}else if(minute >= 0 && minute < 10) {
			time = "0"+minute + ":" + second;
		}else{
			time = minute + ":" + second;
		}
		
		
		
		return time;
		
	}
}
