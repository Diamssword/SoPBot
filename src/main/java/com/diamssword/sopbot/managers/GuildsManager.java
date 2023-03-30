package com.diamssword.sopbot.managers;

import java.util.HashMap;
import java.util.Map;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.storage.JsonGuildStorage;
import com.diamssword.sopbot.storage.JsonSavable;

import net.dv8tion.jda.api.entities.Guild;

public class GuildsManager {

	private static Map<String,GuildParameters> guilds= new HashMap<String,GuildParameters>();
	public static GuildParameters DEFAULT;
	private static JsonGuildStorage<GuildParameters> storage = new JsonGuildStorage<GuildParameters>("parameters.json", GuildParameters.class);
	public static void init()
	{
		SOP.Storage.registerStorage(storage);
	}
	public static void load()
	{
		DEFAULT= new GuildParameters().setGuildId("0");
		for(Guild g : SOP.bot.getGuilds())
		{
			JsonSavable<GuildParameters> sav = storage.get(g.getId());
			if(sav != null )
			{
				GuildParameters gp= sav.get();
				if(gp != null)
				{
					gp.setGuildId(g.getId());
					guilds.put(g.getId(), gp );
				}
			}
		}
	}

	public static void save(Guild g)
	{
		JsonSavable<GuildParameters> sav = storage.get(g.getId());
		sav.set(get(g.getId()));
		storage.save(g);
	}
	public static void save(String g)
	{
		JsonSavable<GuildParameters> sav = storage.get(g);
		sav.set(get(g));
		storage.save(g);
	}
	public static GuildParameters get(String guildId)
	{
		GuildParameters res=guilds.get(guildId);
		if(res == null)
		{
			res= new GuildParameters().setGuildId(guildId);
			guilds.put(guildId, res);
		}
		return res;
	}
	public static GuildParameters get(Guild g)
	{
		if(g == null)
			return DEFAULT;
		return get(g.getId());
	}
}
