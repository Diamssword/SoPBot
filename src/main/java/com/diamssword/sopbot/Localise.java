package com.diamssword.sopbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import com.diamssword.sopbot.utils.LoadUtils;

public class Localise {

	public static String[] langFiles=new String[] {"us","fr"};
	private static Map<String,Map<String,String>> langs = new HashMap<String,Map<String,String>>();
	public static void load()
	{

		for(String k : langFiles)
		{
			
				compute(k);	
		}

	}
	public static List<String[]> getAllLangues()
	{
		List<String[]> res=new ArrayList<String[]>();
		for(String k :langs.keySet())
		{
			String r=translate(k,"language");	
			res.add(new String[] {k,r});
		}
		return res;
	}
	private static void compute(String code)
	{
		langs.put(code,LoadUtils.loadlang("langs/"+code+".lang"));
	}
	public static void clear()
	{
		langs.clear();
	}
	public static String translate(String lang,String key, @Nullable Object... params)
	{
		if(lang== null || key == null)
			return null;
		Map<String,String> lg =langs.get(lang);
		if(lg == null)
		{
			lg =langs.get("us");
		}
		String res = lg.get(key);
		if(res == null)
		{
			lg =langs.get("us");
			if(lg == null)
				return key;
			res = lg.get(key);
			if(res == null) 
				return key;
		}
		res = res.replaceAll("\\\\n",System.getProperty("line.separator"));
		try {
			return String.format(res, params);
		}catch(IllegalFormatException e)
		{
			SOP.getLogger("Translation", Color.orange, ELoggerControl.NO_CLASS_NAME).log(Level.WARNING, "A formating failed for : "+res);
			e.printStackTrace();
			return res;
		}
	}
}
