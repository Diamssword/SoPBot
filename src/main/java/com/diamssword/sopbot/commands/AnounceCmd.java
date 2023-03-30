package com.diamssword.sopbot.commands;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.utils.AnouncementUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AnounceCmd implements ISlashCommand{
	public static MessageEmbed[] msgs;
	@Override
	public void onCommand(SlashCommandEvent event) {
			event.reply(msgs);
	}
	@Override
	public void init()
	{
		
		EmbedBuilder b2=new EmbedBuilder();
		b2.setAuthor(SOP.bot.getSelfUser().getName(),null,SOP.bot.getSelfUser().getEffectiveAvatarUrl());
		b2.setTitle("I'm back baby!");
		b2.setDescription("It's been a lon time of neglect for this bot =c\nBut I have updated all the code! It should work again!");
		EmbedBuilder b1=new EmbedBuilder();
		b1.setAuthor(SOP.bot.getSelfUser().getName(),null,SOP.bot.getSelfUser().getEffectiveAvatarUrl());
		b1.setTitle("Private Tokens!");
		b1.setDescription("You can now add custom api tokens, for now :google wich is used for youtube search, default token (used for all guilds) is limited to 100 search by day (not a lot). You can get ride of this limit with your own token!\nUse /admin tokens to set it"+
		"\nMore infos here: https://developers.google.com/youtube/registering_an_application?hl=fr");
		msgs= new MessageEmbed[] {b2.build(),b1.build(),AnouncementUtil.getAnounceMSG()};
	}
	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr=new TranslateInstance(p,name()); 
		return Commands.slash(name(),tr.translate("desc"));
	}

	@Override
	public String name() {
		return "announcement";
	}
}
