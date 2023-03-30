package com.diamssword.sopbot.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import com.diamssword.sopbot.managers.CommandPerm;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface ISlashCommand {

	public void onCommand(SlashCommandEvent event);
	public SlashCommandData command(Guild g, GuildParameters p);


	default CommandPerm perm(User target,MessageChannelUnion chan)
	{
		if(chan.getType() == ChannelType.PRIVATE)
			return new CommandPerm();
		GuildParameters params=GuildsManager.get(chan.asGuildMessageChannel().getGuild());
		CommandPerm perm=params.CommandsPerms.get(this.command(chan.asGuildMessageChannel().getGuild(), params).getName());
		if(perm != null)
			return perm;
		return this.SdefaultPerm();
	}
	default CommandPerm SdefaultPerm()
	{
		return new CommandPerm();
	}
	public String name();
	default void init()
	{

	}
	@SuppressWarnings("deprecation")
	default ISlashCommand Sinstantiate() throws InstantiationException, IllegalAccessException,IllegalArgumentException,InvocationTargetException
	{
		@SuppressWarnings("unchecked")
		Constructor<ISlashCommand>[] ctors = (Constructor<ISlashCommand>[]) this.getClass().getDeclaredConstructors();
		for (int i = 0; i < ctors.length; i++) {
			if (ctors[i].getGenericParameterTypes().length == 0)
			{
				return ctors[i].newInstance();
			}
		}
		return this.getClass().newInstance();
	}
	public static class TranslateInstance
	{
		private GuildParameters guild;
		private final String name;
		public TranslateInstance(GuildParameters guild, String commandeName)
		{
			this.name = commandeName;
			this.guild=guild;

		}
		public String strictTranslate(@Nullable String name, @Nullable Object...objects )
		{
			return this.translate(name, objects).replaceAll("\\.", "_");
		}
		public String translate(@Nullable String name, @Nullable Object...objects )
		{
			String res="";
			if(name == null)
			{
				res=guild.translate("cmd."+this.name, objects);
			}
			else
			res=guild.translate("cmd."+this.name+"."+name, objects);
			
			if(res.length()>100)
				res = res.substring(0,97)+"...";
			return res;
				
		}
	}
}
