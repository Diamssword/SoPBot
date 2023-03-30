package com.diamssword.sopbot.utils;

import java.util.List;

import com.diamssword.sopbot.SOP;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class AnouncementUtil {

	public static TextChannel getBestChannel(Guild g)
	{
		List<TextChannel> cs= g.getTextChannels();
		for(TextChannel c:cs)
		{
			if(c.getName().toLowerCase().contains("bot") || c.getName().toLowerCase().contains("command") )
			{
				if(c.canTalk())
				{
					return c;
				}
			}
			
		}
		return g.getDefaultChannel().asTextChannel();
	}
	public static MessageEmbed getAnounceMSG()
	{
		EmbedBuilder b1=new EmbedBuilder();
		b1.setTitle("Massive Changes");
		b1.setDescription("Hello, I'm changing a lot of things on this bot (mostly an improved music systeme)\r\n"
				+ "A lot of commands have gone away, some will be re-introduced later, some will not.\r\n"
				+ "**If you want to keep using this bot you need to enable the creation of app commands, easiest way is to kick the re-invite the bot with the bottom link.**\r\n"
				+ "\r\n"
				+ "Bonjour, je change beaucoup de choses sur ce bot (principalement une amelioration du système de musique)\r\n"
				+ "Beaucoup de commandes ont disparu, certaines seront réintroduites plus tard, d'autres non.\r\n"
				+ "** Si vous souhaitez continuer à utiliser ce bot, vous devez activer la creation des commandes, le plus simple et de retirer le bot puis de le ré-inviter via ce lien:** \n"+
				SOP.bot.getInviteUrl(Permission.ADMINISTRATOR));
		b1.setFooter("Diamssword", "https://cdn.discordapp.com/avatars/193667428410982401/a_798c952a06a2c1fd0e67ef278041f0e6.gif");
		b1.setColor(0x75000a);
		return b1.build();
	}
}
