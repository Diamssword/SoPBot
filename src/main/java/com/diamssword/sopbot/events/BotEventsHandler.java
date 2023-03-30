package com.diamssword.sopbot.events;

import javax.annotation.Nonnull;

import com.diamssword.sopbot.commands.MyCommands;
import com.diamssword.sopbot.interfaces.IAction;
import com.diamssword.sopbot.managers.GuildsManager;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotEventsHandler extends ListenerAdapter{

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event) {
		GuildsManager.get(event.getGuild());
	}
	@Override
	public void onMessageReceived( MessageReceivedEvent event)
	{
		if(event.getChannelType()!=ChannelType.PRIVATE)
		{
			String msg = event.getMessage().getContentRaw();
			for(IAction action :MyCommands.actions)
			{
				for(String s : action.getTriggers(event.getMessage()))
				{
					if(msg.toLowerCase().contains(s.toLowerCase()))
					{

						int index = msg.toLowerCase().indexOf(s.toLowerCase());
						String first = msg.substring(0, index);
						String last = msg.substring(index+s.length());
						if(!action.shouldBeSpaced() || ((first.length() < 2 ||first.endsWith(" ")) && (last.length() <2 ||last.startsWith(" "))))
						{
							if(action.perm(event.getAuthor(), event.getChannel()).isAllowed(event.getAuthor(), event.getChannel()))
							{
								new Thread(new Runnable() {


									@SuppressWarnings("deprecation")
									@Override
									public void run() {
										try {
											action.getClass().newInstance().execute(event ,s, first,last);
										} catch (InstantiationException | IllegalAccessException e) {
											action.execute(event,s, first,last);
										}

									}}).start();
								break;
							}
						}
					}

				}
			}
		}
	}

}