package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.plaf.FileChooserUI;

public class Model {
	
	private boolean changed;
	private int curIndex;
	private List<String> curPlaylist;
	private List<String> songList;
	private String playListName;
	private Map<String, String> songMap;
	
	public Model(){
		curIndex = 0;
		curPlaylist = new ArrayList<String>();
		songList = null;
		songMap = new HashMap<String, String>();
		playListName = "";
		changed = false;
	}
	
	public void resetToStart(){
		curIndex = 0;
	}
	
	public String getNextSong(){
		curIndex++;
		if(curPlaylist != null && curIndex < curPlaylist.size()){
			return songMap.get(curPlaylist.get(curIndex));
		}
		curIndex = 0;
		return null;
	}
	
	public String peekNextSong(){
		if(curPlaylist != null && (curIndex + 1) < curPlaylist.size()){
			return songMap.get(curPlaylist.get(curIndex + 1));
		}
		return null;
	}
	
	public List getAvailableSongs(){
		if(songList == null)
			return loadAvailableSongs();
		return songList;
	}
	
	public List loadAvailableSongs(){
		// check if songs.lsl exists
		// if not there, call getSongsFromFile
		//if(! (new File("songs\\songs.lsl").exists())){
		songList = new SortedList<String>();
		getSongs();
		getSongsFromFile();
		//}
		// else, load the file into songList and songMap
		// return songList
		return songList;
	}
	
	private void getSongs(){
		try {
			Scanner scan = new Scanner(new File("songs\\songs.lsl"));
			while(scan.hasNext()){
				String line = scan.nextLine();
				String key = line.substring(0, line.indexOf("=|=|="));
				String value = line.substring(line.indexOf("=|=|=")+1);
				songMap.put(key, value);
				songList.add(key);
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			getSongsFromFile();
			getSongs();
		}
	}
	
	private void getSongsFromFile(){
		// check for all mp3, wav, and m4a files (in current directory)
		// put them all in songList and songMap and songs.lsl
		// return songList
		List<String> playlists = new ArrayList<String>();
		File[] files = new File("songs\\").listFiles();
		for(File file: files){
			String ext = "";
			if(file.getName().endsWith(".mp3"))
				ext = "mp3";
			else
				continue;
			String song = file.getName();
			song = song.substring(0, song.indexOf("." + ext));
			songMap.put(song, file.getAbsolutePath());
			//songList.add(song);
		}
		makeSongFile();
		//return songList;
	}
	
	private void makeSongFile(){
		try {
			PrintWriter fw = new PrintWriter(new File("songs\\songs.lsl"));
			for(String song: songMap.keySet()){
				fw.write(song + "=|=|=" + songMap.get(song) + "\r\n");
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlaylist(String playlist){
		playListName = playlist;
		Configuration.set("playlist", playListName);
		if(playlist.equals(""))
			return;
		// load songs into curPlaylist from playlist.lpl
		try {
			Scanner scan = new Scanner(new File("playlists\\" + playlist + ".lpl"));
			while(scan.hasNext()){
				curPlaylist.add(scan.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void savePlaylist(String fileName){
		// save all songs from curPlaylist to playlist.lpl
		if(changed){
			saveToFile(fileName);
			playListName = fileName;
			Configuration.set("playlist", playListName);
			changed = false;
		}
	}
	
	public void savePlaylist(){
		savePlaylist(playListName);
	}
	
	private void saveToFile(String fileName){
		File file = new File("playlists\\" + fileName + ".lpl");
		if(file.exists())
			file.delete();
		try {
			PrintWriter pw = new PrintWriter(file);
			for(String song: curPlaylist){
				pw.write(song + "\r\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getCurrentSong(){
		return songMap.get(curPlaylist.get(curIndex));
	}
	
	public String getCurrentSongName(){
		return curPlaylist.get(curIndex);
	}
	
	public String getSongPath(String song){
		return songMap.get(song);
	}
	
	public void setCurrentIndex(int index){
		curIndex = index;
	}
	
	public List getPlaylist(){
		return curPlaylist;
	}
	
	public String getPlaylistName(){
		return playListName;
	}
	
	public Object[] getPlaylists(){
		List<String> playlists = new ArrayList<String>();
		File[] files = new File("playlists\\").listFiles();
		for(File file: files){
			if(file.getName().endsWith(".lpl")){
				String playlist = file.getName();
				playlist = playlist.substring(0, playlist.indexOf(".lpl"));
				playlists.add(playlist);
			}
		}
		return playlists.toArray();
	}
	
	public void clearPlaylist(){
		curPlaylist = new ArrayList<String>();
		playListName = "";
		changed = true;
	}
	
	public void addNewSong(String song){
		String songName = new File(song).getName();
		songName = songName.substring(0, songName.length() - 4);
		songMap.put(songName, song);
		try{
			FileWriter fw = new FileWriter(new File("songs\\songs.lsl"), true);
			fw.write(songName + "=|=|=" + song + "\r\n");
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		songList.add(songName);
	}
	
	public void addSongToPlaylist(String song){
		curPlaylist.add(song);
		changed = true;
	}
	
	public void addSongToPlaylist(String song, int index){
		curPlaylist.add(index, song);
		changed = true;
	}
	
	public void removeSongFromPlaylist(String song){
		curPlaylist.remove(song);
		changed = true;
	}
	
	public void removeSongFromPlaylist(int index){
		curPlaylist.remove(index);
		changed = true;
	}
	
	public String getSongAt(int index){
		return curPlaylist.get(index);
	}
}
