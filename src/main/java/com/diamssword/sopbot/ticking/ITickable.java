package com.diamssword.sopbot.ticking;

import java.util.TimerTask;
import java.util.function.Consumer;

import com.diamssword.sopbot.SOP;

import net.dv8tion.jda.api.entities.Guild;

/**
 * Add this to implements a timer event to your class, will be fired every 50 ms
 * Be sure to register your classes in the {@link }!
 * @author Diamssword
 */
public class ITickable extends TimerTask{
	
	private final Consumer<Guild> guildFn;
	private final Runnable globalFn; 
	public ITickable(Consumer<Guild> guildFn,Runnable globalFn)
	{
		this.guildFn=guildFn;
		this.globalFn=globalFn;
	}
	public ITickable(Consumer<Guild> guildFn)
	{
		this.guildFn=guildFn;
		this.globalFn=null;
	}
	public ITickable(Runnable globalFn)
	{
		this.guildFn=null;
		this.globalFn=globalFn;
	}
	 public void run() {
		 if(this.guildFn != null)
		 {
			for(Guild g : SOP.bot.getGuilds())
			{
				guildFn.accept(g);
				
			}
		 }
		 if(this.globalFn != null)
			 globalFn.run();
	 }
}
