package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import maryb.player.*;

import utilities.SilentInputStream;
import utilities.Sound;
import view.Blank;
import view.View;
import model.Configuration;
import model.Model;

public class Controller extends Observable implements Observer{
	
	private String timeError;
	
	private boolean ignoreStopEvent;
	
	private Model model;
	private View view;
	private Sound sound;
	private boolean stopRequested;
	private boolean sleepMode;
	private boolean blackoutMode;
	private boolean timerMode;
	private double duration;
	private Timer timer;
	
	private class MP3Converter extends SwingWorker<String, Object>{

		private String song;
		
		public MP3Converter(String song){
			this.song = song;
		}
		
		public String getSong(){
			return song;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			String origSong = song;
			song = new File(origSong).getName();
			boolean m4a = song.contains(".m4a");
			boolean wav = song.contains(".wav");
			if(m4a)
				song = song.substring(0, song.indexOf(".m4a"));
			else if(wav)
				song = song.substring(0, song.indexOf(".wav"));
			String newSong = "songs\\" + song;
			String wavSong = newSong + ".wav";
			if(wav)
				wavSong = origSong;
			Process process;
			SilentInputStream errorStream = null;
			SilentInputStream inputStream = null;
			try {
				String commandBase = new File(".").getCanonicalPath();
				if(m4a){
					process = new ProcessBuilder(commandBase + "\\faad.exe", "-o", newSong+".wav", origSong).start();
					errorStream = new SilentInputStream(process.getErrorStream());
					inputStream = new SilentInputStream(process.getInputStream());
					errorStream.start();
					inputStream.start();
					process.waitFor();
				}
				process = new ProcessBuilder(commandBase + "\\lame.exe", wavSong, newSong+".mp3").start();
				errorStream = new SilentInputStream(process.getErrorStream());
				inputStream = new SilentInputStream(process.getInputStream());
				errorStream.start();
				inputStream.start();
				process.waitFor();
				if(!wav){
					File wavSongFile = new File(newSong+".wav");
					wavSongFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(errorStream.getInput());
			System.out.println(inputStream.getInput());
			song = newSong+".mp3";
			return song;
		}
		
		@Override
		protected void done(){
			try {
				File songFile = new File(get());
				if(songFile.exists())
					addNewSong(songFile.getAbsolutePath());
				else
					errorMessage("Error: File not converted");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class Timer extends SwingWorker<String, Object>{

		private double time;
		
		public Timer(double playTime){
			time = playTime;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			double start = System.currentTimeMillis();
			while((milliToSeconds(System.currentTimeMillis()) - milliToSeconds(start)) < time);
			return null;
		}
		
		private double milliToSeconds(double milli){
			return milli/1000.0;
		}
		
		@Override
		protected void done(){
			if(isCancelled())
				return;
			if(sound.isPlaying()){
				stopPlaylist();
				model.resetToStart();
				if(getBlackoutMode())
					view.unBlackOut();
				setStatus("Playback stopped.");
				if(sleepMode)
					sleep();
			}
		}
	}
	
	public Controller(Model model, View view){
		this.model = model;
		this.view = view;
		sound = null;
		stopRequested = false;
		sleepMode = getSleepMode();
		timerMode = getTimerMode();
		duration = Double.parseDouble(getTimerDuration());
		blackoutMode = getBlackoutMode();
		timeError = "";
		ignoreStopEvent = false;
		timer = null;
	}
	
	public List getAvailableSongs(){
		return model.getAvailableSongs();
	}
	
	public List getPlaylist(String list){
		model.loadPlaylist(list);
		return model.getPlaylist();
	}
	
	public List getPlaylist(){
		return model.getPlaylist();
	}
	
	public String getPlaylistName(){
		return model.getPlaylistName();
	}
	
	public Object[] getPlaylists(){
		return model.getPlaylists();
	}
	
	public void newSong(String song){
		if(song.endsWith(".m4a") || song.endsWith(".wav")){
			setStatus("Converting to MP3");
			new MP3Converter(song).execute();
		}
		else{
			addNewSong(song);
		}
	}
	
	private void addNewSong(String song){
		model.addNewSong(song);
		setChanged();
		notifyObservers("NEW");
		setStatus("Added new song: " + new File(song).getName());
	}
	
	public void addToPlaylist(String song){
		if(song == null){
			setStatus("Please select a song to add.");
		}
		else{
			setStatus("Added " + song + " to playlist.");
			model.addSongToPlaylist(song);
			setChanged();
			notifyObservers("ADD");
		}
	}
	
	public void addToPlaylist(String song, int index){
		model.addSongToPlaylist(song, index);
		setChanged();
		notifyObservers("ADD");
	}
	
	public void removeFromPlaylist(String song){
		if(song == null){
			setStatus("Please select a song to remove.");
		}
		else{
			setStatus("Removed " + song + " from playlist.");
			model.removeSongFromPlaylist(song);
			setChanged();
			notifyObservers("REMOVE");
		}
	}
	
	public void removeFromPlaylist(int index){
		setStatus("Removed " + model.getSongAt(index) + " from playlist.");
		model.removeSongFromPlaylist(index);
		setChanged();
		notifyObservers("REMOVE");
	}
	
	public void loadPlaylist(String playlist){
		if(model.getPlaylistName().equals(playlist))
			return;
		setStatus("Loading playlist " + playlist);
		model.clearPlaylist();
		model.loadPlaylist(playlist);
		setChanged();
		notifyObservers("LOAD");
		stopRequested = true;
		if(sound != null && sound.isPlaying())
			sound.stop();
		model.resetToStart();
		setStatus("Loaded playlist " + playlist);
	}
	
	public void savePlaylist(String playlistName){
		setStatus("Saving playlist.");
		model.savePlaylist(playlistName);
		setChanged();
		notifyObservers("SAVE");
		setStatus("Playlist saved.");
	}
	
	public void savePlaylist(){
		savePlaylist(getPlaylistName());
	}
	
	public void newPlaylist(){
		setChanged();
		notifyObservers("SAVE");
		model.clearPlaylist();
		setChanged();
		notifyObservers("NEW_PLAYLIST");
	}
	
	public void playPushed(){
		if(sound == null)
			initSound();
		if(sound.isPaused()){
			sound.unPause();
			setChanged();
			notifyObservers("PLAY");
		}
		else if(!sound.isPlaying()){
			playPlaylist();
			setChanged();
			notifyObservers("PLAY");
		}
		else{
			sound.pause();
			setChanged();
			notifyObservers("PAUSE");
		}
	}
	
	public void playPlaylist(){
		if(sound != null && sound.isPlaying()){
			setStatus("Already playing.");
			return;
		}
		else if(timerMode && !timeError.equals("")){
			setStatus(timeError);
			return;
		}
		setStatus("Playing playlist");
		String song = model.getCurrentSong();
		if(timerMode){
			timer = new Timer(duration * 60);
			timer.execute();
		}
		play(song);
	}
	
	private void initSound(){
		sound = new Sound();
		sound.addObserver(this);
	}
	
	public void play(String song){
		if(sound == null){
			initSound();
		}
		playSong(song);
	}
	
	public void play(int index){
		model.setCurrentIndex(index);
		if(sound == null){
			initSound();
		}
		if(sound.isPlaying()){
			sound.stop();
		}
		if(timerMode){
			timer = new Timer(duration * 60);
			timer.execute();
		}
		play(model.getCurrentSong());
	}
	
	private void playSong(String song){
		if(sound != null){
			setStatus("Now playing: " + model.getCurrentSongName() + ".");
			sound.play(song);
			view.blackOut();
		}
	}
	
	public void stopPlaylist(){
		if(sound == null || (sound != null && !sound.isPlaying())){
			setStatus("Not currently playing.");
			return;
		}
		if(timer != null){
			timer.cancel(true);
			timer = null;
		}
		stopRequested = true;
		setStatus("Stopping playlist");
		sound.stop();
		sound = null;
		setStatus("");
		model.resetToStart();
		setChanged();
		notifyObservers("STOP");
	}
	
	public void skipSong(){
		if(sound == null || (sound != null && !sound.isPlaying())){
			setStatus("Not currently playing.");
			return;
		}
		if(model.peekNextSong() == null){
			setStatus("At the end of the playlist");
			return;
		}
		setStatus("Skipping song");
		ignoreStopEvent = true;
		sound.stop();
		play(model.getNextSong());
	}
	
	public void clearStatus(){
		setStatus("");
	}

	public void setBlackOutMode(boolean mode){
		updateConfig("blackout", Boolean.toString(mode));
		if(mode)
			updateConfig("blackout", "true");
		else
			updateConfig("blackout", "false");
	}
	
	public void setSleepMode(boolean mode){
		if(mode)
			updateConfig("sleep", "true");
		else
			updateConfig("sleep", "false");
		sleepMode = mode;
	}
	
	public void setTimerInterval(String time){
		updateConfig("timer", "true");
		updateConfig("duration", time);
		timerMode = true;
		try{
			duration = Double.parseDouble(time);
			timeError = "";
		}catch(NumberFormatException e){
			timeError = "Must specify a valid time to play.";
		}
	}
	
	public void setContinualMode(){
		updateConfig("timer", "false");
		timerMode = false;
	}
	
	public void setAddPath(String path){
		Configuration.set("addPath", path);
	}
	
	public void setDefaultPlaylist(String playlist){
		Configuration.set("playlist", playlist);
	}
	
	private void sleep(){
		try {
			String commandBase = new File(".").getCanonicalPath();
			Process process = new ProcessBuilder(commandBase + "\\sleep.exe").start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateConfig(String key, String value){
		Configuration.set(key, value);
	}
	
	public boolean getTimerMode(){
		timerMode = Boolean.parseBoolean(Configuration.get("timer"));
		return timerMode;
	}
	
	public String getTimerDuration(){
		String timeDuration = Configuration.get("duration");
		duration = Double.parseDouble(timeDuration);
		return timeDuration;
	}
	
	public boolean getSleepMode(){
		sleepMode = Boolean.parseBoolean(Configuration.get("sleep"));
		return sleepMode;
	}
	
	public boolean getBlackoutMode(){
		blackoutMode = Boolean.parseBoolean(Configuration.get("blackout"));
		return blackoutMode;
	}
	
	public String getDefaultPlaylist(){
		String defaultPlaylist = Configuration.get("playlist");
		if(defaultPlaylist == null)
			return "";
		return defaultPlaylist;
	}
	
	public String getAddPath(){
		String addPath = Configuration.get("addPath");
		if(addPath == null)
			return ".";
		return addPath;
	}
	
	public void setStatus(String status){
		view.setStatus(status);
	}
	
	public void errorMessage(String message){
		view.errorMessage(message);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1.equals("STOPPED")){
			if(ignoreStopEvent){	// Happens in the event of a skip
				ignoreStopEvent = false;
				return;
			}
			if(!stopRequested && model.peekNextSong() != null){
				play(model.getNextSong());
			}
			else if(!stopRequested && model.peekNextSong() == null){
				setStatus("End of playlist");
				if(timerMode){
					model.resetToStart();
					play(model.getCurrentSong());
				}
				else if(sleepMode)
					sleep();
			}
			stopRequested = false;
		}
	}
}
