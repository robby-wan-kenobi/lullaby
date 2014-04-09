package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Configuration {
	private static Map<String, String> config = null;
	
	private static void initConfig(){
		config = new HashMap<String, String>();
		loadConfig();
	}
	
	private static void loadConfig(){
		try{
			Scanner scan = new Scanner(new File("defaults.conf"));
			while(scan.hasNext()){
				String line = scan.nextLine();
				if(line.trim().equals(""))
					continue;
				int equalsIndex = line.indexOf('=');
				String key = line.substring(0, equalsIndex);
				String value = line.substring(equalsIndex + 1);
				config.put(key, value);
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	private static void addToFile(String key, String value){
		try {
			FileWriter fw = new FileWriter(new File("defaults.conf"));
			fw.write(key + "=" + value.trim() + "\r\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void replace(String key, String value){
		try {
			Scanner scan = new Scanner(new File("defaults.conf"));
			List<String> lines = new ArrayList<String>();
			while(scan.hasNext()){
				String line = scan.nextLine();
				if(line.startsWith(key))
					line = key + "=" + value.trim();
				lines.add(line);
			}
			new File("defaults.conf").delete();
			FileWriter fw = new FileWriter(new File("defaults.conf"));
			for(String newLine: lines){
				fw.write(newLine + "\r\n");
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Returns null if not there
	public static String get(String key){
		if(config == null)
			initConfig();
		if(!config.containsKey(key))
			loadConfig();
		if(config.containsKey(key) && !config.get(key).equals(""))
			return config.get(key);
		else
			return null;
	}
	
	// Can pass optional value to set the value if it doesn't exist yet
	public static String get(String key, String value){
		String storedValue = get(key);
		if(storedValue == null){
			set(key, value);
			return value;
		}
		else{
			return storedValue;
		}
	}
	
	public static void set(String key, String value){
		String added = config.put(key, value);
		if(added == null)
			addToFile(key, value);
		else
			replace(key, value);
	}
}
