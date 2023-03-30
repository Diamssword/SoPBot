package com.diamssword.sopbot.ticking;

import java.util.Timer;
/**
 *  Add the ITickable interface to your class to add a timer event fired every 50 ms for each guilds
 * Be sure to register your classes in the add(ITickable tickable)!
 * @author Diamssword
 *
 */
public class Tickable {

	public static void registerTickable(ITickable tickable,int time)
	{
		 Timer t = new Timer();
		 t.scheduleAtFixedRate(tickable, time,time);
	}
}
