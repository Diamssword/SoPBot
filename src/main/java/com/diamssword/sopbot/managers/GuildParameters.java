package com.diamssword.sopbot.managers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.diamssword.sopbot.Localise;
import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.commands.MyCommands;
import com.diamssword.sopbot.commands.ISlashCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class GuildParameters {

	public static Logger log =SOP.getLogger("SlashCMD", Color.magenta);
	public String[] commandPrefix = new String[0];
	public Map<String,String> tokens= new HashMap<String,String>();
	public String defaultLang="us";
	public Map<String,CommandPerm> CommandsPerms = new HashMap<String,CommandPerm>();
	public Map<String,CommandPerm> ActionsPerms = new HashMap<String,CommandPerm>();
	public String guildId="0";
	public GuildParameters()
	{
		try {
			this.reloadSlashCommmands();

		}catch(ErrorResponseException e)
		{
			log.log(Level.WARNING,"Not allowed to add commands on "+this.guildId);
		}

	}
	public void save()
	{
		if(this.guildId!=null)
			GuildsManager.save(this.guildId);
	}
	public GuildParameters setGuildId(String guild)
	{
		this.guildId=guild;
		try {
			this.reloadSlashCommmands();

		}catch(ErrorResponseException e)
		{
			log.log(Level.WARNING,"Not allowed to add commands on "+this.guildId);
		}
		return this;
	}
	private void reloadSlashCommmands() throws ErrorResponseException
	{

		Guild g =SOP.bot.getGuildById(this.guildId);

		if(g!=null)
		{
			log.info("Updating command list for "+g.getName());			

			CommandListUpdateAction cmList=g.updateCommands();
			for(ISlashCommand com: MyCommands.slashCommands)
			{
				cmList.addCommands(com.command(g, this));
			}
			cmList.queue(null,new Consumer<Throwable>() {

				@Override
				public void accept(Throwable t) {
					log.log(Level.WARNING, "Error loading commands for "+g.getName()+"("+g.getId()+")");
					//AnouncementUtil.getBestChannel(g).sendMessageEmbeds(AnouncementUtil.getAnounceMSG()).queue();
				}});



		}
	}

	public void setDefaultLang(String lang)
	{
		this.defaultLang = lang;
		try {
			this.reloadSlashCommmands();

		}catch(ErrorResponseException e)
		{
			log.log(Level.WARNING,"Not allowed to add commands on "+this.guildId);
		}
	}
	public String[] getAliasFor(String name)
	{
		List<String> res= new ArrayList<String>();

		String key = Localise.translate(this.defaultLang, name);
		String[] keys=key.split(",");
		for(String key1 : keys)
		{
			res.add(key1);
		}
		return res.toArray(new String[0]);
	}
	public String defaultLang()
	{
		return this.defaultLang;
	}
	public void addPrefix(String prefix)
	{
		for(String s : this.commandPrefix)
		{
			if(s.equals(prefix))
			{
				return;
			}
		}
		String[] str = new String[this.commandPrefix.length+1];
		for(int i=0;i <this.commandPrefix.length;i++)
		{
			str[i]= this.commandPrefix[i];
		}
		str[str.length-1] = prefix;
		this.commandPrefix = str;
	}
	public void removePrefix(String prefix)
	{
		if(this.commandPrefix.length==1)
			return;
		boolean flag =false;
		for(String s : this.commandPrefix)
		{
			if(s.equals(prefix))
			{
				flag=true;
				break;
			}
		}
		if(flag)
		{
			String[] str = new String[this.commandPrefix.length-1];
			for(int i=0;i <str.length;i++)
			{
				if(!this.commandPrefix[i].equals(prefix))
				{
					str[i]= this.commandPrefix[i];
				}
			}
			this.commandPrefix = str;
		}

	}

	public String translate(String key,Object... params)
	{
		return Localise.translate(this.defaultLang(), key, params);
	}
}
