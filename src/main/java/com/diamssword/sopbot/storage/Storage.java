package com.diamssword.sopbot.storage;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.diamssword.sopbot.ELoggerControl;
import com.diamssword.sopbot.SOP;
import com.google.gson.Gson;

import net.dv8tion.jda.api.entities.Guild;

public class Storage {
	public static Logger LOG = SOP.getLogger("DiamsBot-Storage", Color.YELLOW, ELoggerControl.NO_CLASS_NAME);
	private GlobalOptions globaloptions;
	@SuppressWarnings("rawtypes")
	public static List<AGuildStorage> list = new ArrayList<AGuildStorage>();
	public Storage()
	{
		new File("botdata").mkdirs();
	}

	static boolean first= true;
	public static void init(Guild g)
	{
		init(g.getId());
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void init(String gid)
	{
		File f =new File("botdata/"+gid);
		f.mkdirs();
		for(AGuildStorage gs : list)
		{
			gs.init(gid);
			ISavable sav = gs.load(gid);
			if(sav != null)	
				gs.set(gid,sav);
			if(first)
				LOG.log(Level.INFO,"Loaded GuildStorage:"+gs.getSavableClass().getSimpleName()+"!");
		}
		first = false;
	}

	@SuppressWarnings("rawtypes")

	public void registerStorage(AGuildStorage storage) {
		list.add(storage);

	}


	@SuppressWarnings("rawtypes")
	public void deleteStorage(AGuildStorage storage) {
		list.remove(storage);

	}

	public GlobalOptions getGlobal()
	{
		if(globaloptions == null)
		{
			File f =new File("botdata/global.json");
			try {
				Scanner r = new Scanner(f);
				String s = "";
				while(r.hasNext())
				{
					s = s+r.next();
				}
				r.close();
				globaloptions=	new Gson().fromJson(s, GlobalOptions.class);
				if(globaloptions == null)
					globaloptions = new GlobalOptions();	
				globaloptions.update();
			} catch (FileNotFoundException e) {
				globaloptions = new GlobalOptions();
				e.printStackTrace();
			}
		}
		return globaloptions;
	}

	public void saveGlobal()
	{
		if(globaloptions != null)
		{
			File f =new File("botdata/global.json");
			try {
				FileWriter w = new FileWriter(f);
				String s =	new Gson().toJson(globaloptions);
				w.write(s);
				w.close();
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
	}


}
