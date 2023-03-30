package com.diamssword.sopbot.storage;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonSavable <T>implements ISavable<T> {

	private String path;
	private T object;
	Type type;
	private Class<T> clazz;
	@SuppressWarnings({ "deprecation"})
	public JsonSavable(String path,Class<T> clazz)
	{
		type = new TypeToken<T>(){}.getType();
		this.clazz = clazz;
		this.path = path;
		try {
			object = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String path() {
		return path;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T fromText(String json, Gson gson) {
		object = gson.fromJson(json, clazz);
		return (T) this;

	}

	@Override
	public String toText(Gson json) {
		return json.toJson(object,clazz);
	}
	@Override
	public Class<?> savedClass() {
		return clazz;
	}
	@SuppressWarnings("deprecation")
	public T get()
	{
		if(object == null)
		{
			try {
				object= clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return object;
	}
	public void set(T data)
	{
		object = data;
	}

}
