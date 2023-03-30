package com.diamssword.sopbot.storage;

import com.google.gson.Gson;

public interface ISavable  <T>{

	
	public String path();
	public T fromText(String json,Gson gson);
	public String toText(Gson json);
	public Class<?> savedClass();
}
