package com.diamssword.sopbot.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.dv8tion.jda.api.entities.Guild;


@SuppressWarnings("rawtypes")
public abstract class AGuildStorage <T extends ISavable> {

	public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	/**
	 * All stored files are under "botdata"
	 */
	public static File root =  new File("botdata");
	private Map<String,T> instances = new HashMap<String,T>();
	public Class<?> getSavableClass()
	{
		for(String key :instances.keySet())
		{
			return instances.get(key).savedClass();
		}
		return this.getClass();
	}
	/**
	 * Set a instance of T for the given guild (replace any existing one) 
	 * @param guild
	 * @param object
	 */
	public void set(Guild guild,T object)
	{
		instances.put(guild.getId(), object);
	}
	/**
	 * Set a instance of T for the given guild id (replace any existing one) 
	 * @param serverId
	 * @param object
	 */
	public void set(String serverId,T object)
	{
		instances.put(serverId, object);
	}

	/**
	 * Get the stored data for the specified guild
	 * @param serverId the guild ID
	 * @return T 
	 */
	public T get(String serverId)
	{ 
		return instances.get(serverId);
	}
	public T get(Guild server)
	{ 
		return this.get(server.getId());
	}
	public abstract void init(String serverID);

	/**
	 *Load a ISavable from the disk (is called automatically on start)
	 * @param ServerID
	 * @return the ISavable instance of the required object
	 */
	@SuppressWarnings("unchecked")
	public T load(String ServerID)
	{
		File f = new File(root,ServerID+"/"+instances.get(ServerID).path());
		T obj = instances.get(ServerID);
		if(f.exists() && obj != null)
		{
			try {
				Scanner s = new Scanner(f,"UTF-8");
				String txt="";
				while(s.hasNextLine())
				{
					txt = txt+s.nextLine();
				}
				s.close();
				return (T) obj.fromText(txt, gson);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;

	}
	/**
	 * save a ISavable to the disk
	 * (You might want to do it each time you change a data)
	 * @param serverID 
	 */
	public void save(String serverID)
	{
		T save = instances.get(serverID);
		if(save!=null)
		{
			File f = new File(root,serverID+"/"+save.path());
			f.getParentFile().mkdirs();
			try {
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(f), "UTF-8"));
				try {
					out.write(save.toText(gson));
				} finally {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void save(Guild server)
	{
		save(server.getId());	
	}
}
