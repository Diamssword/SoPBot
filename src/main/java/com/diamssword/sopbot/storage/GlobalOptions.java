package com.diamssword.sopbot.storage;

import java.util.HashMap;
import java.util.Map;

public class GlobalOptions {

	@Deprecated
	private String BotToken = null;
	public Map<String,String> tokens = new HashMap<String,String>();
	public void update()
	{
		if(BotToken != null)
			tokens.put("discord", BotToken);
		BotToken=null;	
	}
}
