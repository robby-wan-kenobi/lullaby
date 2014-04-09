package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SilentInputStream implements Runnable{

	private Thread thread;
	private InputStream is;
	private String input;
	
	public SilentInputStream(InputStream is){
		input = "";
		thread = null;
		this.is = is;
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	public String getInput(){
		return input;
	}
	
	@Override
	public void run() {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		try {
			while(br.ready()){
				input += br.readLine() + "\n";
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
