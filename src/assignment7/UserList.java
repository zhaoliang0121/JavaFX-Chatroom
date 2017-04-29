package assignment7;

import java.util.ArrayList;
import java.util.Arrays;

public class UserList {

	public static ArrayList<String> list = new ArrayList<String>(Arrays.asList("hi","bye"));
	
	public UserList(){
		list = new ArrayList<String>();
	}
	
	public void add(String name){
		list.add(name);
	}
	
	public void remove(String name){
		if(list.contains(name)){
			list.remove(name);
		}
	}
	
	public ArrayList<String> getList(){
		return list;
	}
	
	public boolean hasName(String name){
		return list.contains(name);
	}
}
