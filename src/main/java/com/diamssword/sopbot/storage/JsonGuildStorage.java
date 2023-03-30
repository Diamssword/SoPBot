package com.diamssword.sopbot.storage;

/**
 * A complete storage system that save and load an object from a Json file;
 * @author Diamssword
 * @param <T> the Class of the object to serialize as json
 *
 */
public class JsonGuildStorage<T> extends AGuildStorage<JsonSavable<T>>{
	public String path;
	public Class<T> clazz;
	/**
	 * 
	 * @param path the file to save the object at on the disk, the root of this path is botdata/guildID
	 */
	public JsonGuildStorage(String path,Class<T> clazz)
	{
		this.path = path;
		this.clazz = clazz;
	}
	@Override
	public void init(String serverID) {
		this.set(serverID, new JsonSavable<T>(path,clazz));
	}
	

}
