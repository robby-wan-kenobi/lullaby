package utilities;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import maryb.player.*;

// Known files that work - mp3, wav
// Known files that don't work - m4a, wma
public class Sound extends Observable{

	private Player player;
	
	public Sound(){
		player = null;
		initPlayer();
	}
	
	public void initPlayer(){
		player = new Player();
		player.setListener(new PlayerEventListener() {
			@Override
			public void stateChanged() {
				if(player.getState() == PlayerState.STOPPED){
					setChanged();
					notifyObservers("STOPPED");
				}
			}
			@Override
			public void endOfMedia() {
			}
			@Override
			public void buffer() {
			}
		});
	}
	
	public boolean isPlaying(){
		return player.getState() == PlayerState.PLAYING;
	}
	
	public boolean isPaused(){
		return player.getState() == PlayerState.PAUSED;
	}
	
	public void play(String song){
		if(player == null)
			initPlayer();
		player.setSourceLocation(song);
		// Wait till the player is ready to actually start playing.
		while(player.getState() != PlayerState.STOPPED);
		player.play();
	}
	
	public void pause(){
		if(isPlaying())
			player.pause();
	}
	
	public void unPause(){
		if(isPaused())
			player.play();
	}
	
	public void stop(){
		player.stop();
	}
}
