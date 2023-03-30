package com.diamssword.sopbot;
import java.awt.Color;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.diamssword.sopbot.commands.MyCommands;
import com.diamssword.sopbot.events.AlternateEventsHandler;
import com.diamssword.sopbot.events.BotEventsHandler;
import com.diamssword.sopbot.events.InteractionsEventHandler;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.music.GPlayerManager;
import com.diamssword.sopbot.storage.Storage;
import com.diamssword.sopbot.ticking.ITickable;
import com.diamssword.sopbot.ticking.Tickable;
import com.diamssword.sopbot.utils.HttpClient;
import com.diamssword.sopbot.utils.LoadUtils;
import com.diamssword.sopbot.utils.LogHandler;
import com.diamssword.sopbot.utils.Spotify;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
//https://discordapp.com/oauth2/authorize?client_id=519224623523692577&scope=bot&permissions=2080631922
public class SOP {
	public static Logger LOG;
	public static JDA bot;
	public static Storage Storage;
	public static LogHandler logHandler= new LogHandler();
	public static GPlayerManager playerManager = new GPlayerManager();
	public static void main(String[] args) {
		LOG = getLogger("SoP-Main");
		LOG.log(Level.INFO, "--Loading Languages--");
		Localise.load();
		LOG.log(Level.INFO, "...DONE!!");
		LOG.log(Level.INFO, "--Loading Storage--");
		Storage = new Storage();
		LOG.log(Level.INFO, "...DONE!!");
		LOG.log(Level.INFO, "--Building and loading SoP Bot--");
		LoadUtils.askToken("discord", new Function<String,Boolean>(){

			@Override
			public Boolean apply(String t) {
				bot = JDABuilder.create(t, GatewayIntent.getIntents(GatewayIntent.DEFAULT)).enableIntents(GatewayIntent.GUILD_PRESENCES,GatewayIntent.GUILD_MEMBERS,GatewayIntent.MESSAGE_CONTENT).enableCache(CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES,CacheFlag.ACTIVITY,CacheFlag.VOICE_STATE).build();
				afterBotInit();
				return true;
			}});

	}
	private static void afterBotInit()
	{
		bot.setRequiredScopes("applications.commands");
		bot.addEventListener(new BotEventsHandler());
		bot.addEventListener(new InteractionsEventHandler());
		bot.addEventListener(new AlternateEventsHandler());
		Storage.saveGlobal();
		try {
			bot.awaitReady();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		LOG.log(Level.INFO, "--Loading MyCommands--");
		try {
			MyCommands.init();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		LOG.log(Level.INFO, "...Done!");
		GuildsManager.init();
		LOG.log(Level.INFO, "--Loading Stored Datas for each guilds--");
		for(Guild g : bot.getGuilds())
			com.diamssword.sopbot.storage.Storage.init(g);
		com.diamssword.sopbot.storage.Storage.init("GLOBAL");
		LOG.log(Level.INFO, "...Done!");
		LOG.log(Level.INFO, "--Loading Guilds Manager--");
		GuildsManager.load();
		LOG.log(Level.INFO, "..Done");

		LOG.log(Level.INFO, "--Loading Other APIs--");
		Spotify.init();
		HttpClient.init();
		LOG.log(Level.INFO, "..Done");
		Statut();
	}


	public static void kill()
	{

		try{
			bot.shutdown();
			for(Guild g : bot.getGuilds())
			{
				if(!g.getVoiceChannels().isEmpty())
					g.getAudioManager().closeAudioConnection();

			}
		}catch(Exception e)
		{
			System.exit(0);
		}
		System.exit(0);
	}

	public static Logger getLogger(String name, ELoggerControl... controls)
	{

		Logger res = Logger.getLogger(name);
		res.setUseParentHandlers(false);
		if(controls == null || controls.length==0)
			res.addHandler((Handler) logHandler);
		else
			res.addHandler((Handler) logHandler.instanciate(null,controls));
		return res;
	}
	public static Logger getLogger(String name,Color color, ELoggerControl... controls)
	{

		Logger res = Logger.getLogger(name);
		res.setUseParentHandlers(false);
		res.addHandler((Handler) logHandler.instanciate(color,controls));
		return res;
	}
	public static void Statut()
	{
		bot.getPresence().setActivity(Activity.playing("Disponibles sur "+bot.getGuilds().size()+" serveurs! | /announcement pour les dernières news" ));
		Tickable.registerTickable(new ITickable(new Runnable(){

			@Override
			public void run() {
				bot.getPresence().setActivity(Activity.playing("Disponibles sur "+bot.getGuilds().size()+" serveurs! | /announcement pour les dernières news" ));

			}}), 2*60*1000);



	}

}