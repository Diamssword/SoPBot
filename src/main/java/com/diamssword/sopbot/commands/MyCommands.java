package com.diamssword.sopbot.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.diamssword.sopbot.ELoggerControl;
import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.interfaces.IAction;

public class MyCommands {

	public static List<ISlashCommand> slashCommands = new ArrayList<ISlashCommand>();
	public static List<IAction> actions = new ArrayList<IAction>();
	public static void init()
	{
	//	slashCommands.add(new CadavreCMd());
		slashCommands.add(new PointsCommand());
		slashCommands.add(new PointsLeaderCommand());
		slashCommands.add(new MemeCmd());
		slashCommands.add(new MusicCommand());
		slashCommands.add(new ComeCmd());
		slashCommands.add(new AnounceCmd());
		
		
		
		//doit tj rester en dernière
		slashCommands.add(new AdminCommand());
		
		Logger log = SOP.getLogger("CmdsInit", Color.MAGENTA, ELoggerControl.NO_CLASS_NAME);
		for(ISlashCommand cmd : slashCommands)
		{
			cmd.init();
			log.log(Level.INFO, "Loaded '"+cmd.getClass().getName()+"'");
		
		}
		for(IAction cmd : actions)
			cmd.init();
	}
	public static ISlashCommand find(String name)
	{
		for(ISlashCommand cm: slashCommands)
		{
			if(name.equals(cm.name()))
				return cm;
		}
		return null;
	}
}
