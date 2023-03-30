package com.diamssword.sopbot.interfaces;
import com.diamssword.sopbot.managers.CommandPerm;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IAction {
	
	/**
	 * @return case-insensitive, space sensitive, phrases to trigger action
	 */
	public String[] getTriggers(Message m);
	
	public String getID();
	default String help()
	{
	return "actions.use."+this.getID();	
	}
	
	/**
	 * Executed when the bot detect on of the triggers on a textchannel
	 * @param event the event containings infos on message, guild and textchannel wich issued the Action
	 * @param trigger the string wich triggered the Action
	 * @param before the part of the message before the triggering string
	 * @param after the part of the message after the triggering string
	 * for '>command args1 args2 args3arg4' will return in args the list: {args1,args2,args3arg4} 
	 */
	public void execute(MessageReceivedEvent event,String trigger, String before,String after);
	
	/**
	 * Is called on time at the bot launch on the {@code 'MyCommands.initActions()'} 
	 */
	public void init();
	/**
	 * 
	 * @return true if the trigger should be spaced from any other word
	 */
	default boolean shouldBeSpaced()
	{
		return true;
		
	}
	default CommandPerm perm(User target,MessageChannel chan)
	{
		if(chan.getType() == ChannelType.PRIVATE)
			return new CommandPerm();
		GuildParameters params=GuildsManager.get(((TextChannel) chan).getGuild());
		CommandPerm perm=params.ActionsPerms.get(this.getID());
		if(perm != null)
			return perm;
		return this.defaultPerm();
	}
	default CommandPerm defaultPerm()
	{
		return new CommandPerm();
	}
}
