package com.diamssword.sopbot.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.diamssword.sopbot.Localise;
import com.diamssword.sopbot.commands.SlashCommandEvent.Type;
import com.diamssword.sopbot.jsons.RedditJson;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.utils.LoadUtils;
import com.google.gson.Gson;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class MemeCmd implements ISlashCommand{

	private static Random rand = new Random();
	
	@Override
	public void init() {	
	}
	@Override
	public void onCommand(SlashCommandEvent event) {
		
			Runnable t =new Runnable() {

				@Override
				public void run() {

					String sub =event.getArgument("sub");
					
					try {
						String res=LoadUtils.urlToString(new URL("https://meme-api.herokuapp.com/gimme/"+sub));
						RedditJson post = new Gson().fromJson(res, RedditJson.class);
						if(post.title == null || post.url == null)
						{
						
							event.sendResponse(Type.error, "cmd.use.meme.error", -2);
							return;
						}
						EmbedBuilder bl=new EmbedBuilder();
							bl.setAuthor( event.getMember().getEffectiveName(), null, event.getAuthor().getEffectiveAvatarUrl());
							bl.setTitle( post.title);
							bl.setImage( post.url);
							bl.setColor(new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)));
							bl.setFooter(Localise.translate(event.getGParam().defaultLang(), "cmd.use.meme.from", post.subreddit));
							
							event.reply(bl.build());
							
					} catch (IOException e) {
						event.sendResponse(Type.error, "cmd.use.meme.error.1", -2);
						return;
					}
					return;
				}};
				ExecutorService executor = Executors.newSingleThreadExecutor();
				try {
					 executor.submit(t).get(6, TimeUnit.SECONDS);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					event.sendResponse(Type.error, "cmd.use.meme.error", -2);
				}
				executor.shutdown();
		}
	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr = new TranslateInstance(p,name());
		return Commands.slash(name(), tr.translate("desc")).addOption(OptionType.STRING, "sub", p.translate("sub"));
	}
	@Override
	public String name() {
		return "meme";
	}
}
