package com.diamssword.sopbot.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AlternateEventsHandler  extends ListenerAdapter{
	public static Map<String,JSEventExecuter> JSEventOnMessageFrom = new HashMap<String,JSEventExecuter>();
	public static Map<String,JSEventExecuter> JSEventOnUserStatut = new HashMap<String,JSEventExecuter>();
	@Override
	public void onMessageReceived( MessageReceivedEvent event)
	{
		List<String> toRem = new ArrayList<String>();
		for(String user : JSEventOnMessageFrom.keySet())
		{
			if(user.equals(event.getAuthor().getId()))
			{
				JSEventExecuter exec=JSEventOnMessageFrom.get(user);
				if(exec!=null)
				{
					try {
						exec.engine.invokeFunction(exec.function,event.getAuthor().getId(),event.getMessage().getContentRaw());
						exec.usesLeft-=1;
						if(exec.usesLeft<=0)
							toRem.add(user);
					} catch (NoSuchMethodException | ScriptException e) {
						e.printStackTrace();
					}
				}
			}
		}
		for(String rem : toRem)
		{
			JSEventOnMessageFrom.remove(rem);
		}
	}
	@Override
	 public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
		List<String> toRem = new ArrayList<String>();
		for(String user : JSEventOnUserStatut.keySet())
		{
			if(user.equals(event.getMember().getId()))
			{
				JSEventExecuter exec=JSEventOnUserStatut.get(user);
				if(exec!=null)
				{
					try {
						exec.engine.invokeFunction(exec.function,event.getMember().getId(),event.getNewOnlineStatus().name(),event.getOldOnlineStatus().name());
						exec.usesLeft-=1;
						if(exec.usesLeft<=0)
							toRem.add(user);
					} catch (NoSuchMethodException | ScriptException e) {
						e.printStackTrace();
					}
				}
			}
		}
		for(String rem : toRem)
		{
			JSEventOnUserStatut.remove(rem);
		}
	 }

	public static class JSEventExecuter{
		String function;
		Invocable engine;
		int usesLeft;
		public JSEventExecuter(String function,int count,ScriptEngine engine)
		{
			this.function=function;
			this.engine=(Invocable) engine;
			this.usesLeft=count;
			if(this.usesLeft>20)
				this.usesLeft=20;
		}
	}
}
