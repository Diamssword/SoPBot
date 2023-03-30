package com.diamssword.sopbot.commands;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.music.GPlayerManager;
import com.diamssword.sopbot.music.GuildPlayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComeCmd implements ISlashCommand{

	@Override
	public void onCommand(SlashCommandEvent event) {
		VoiceChannel ch=GPlayerManager.getUserChannel(event.getMember());
		if(ch != null)
		{
			GuildPlayer pl=SOP.playerManager.getGuildPlayer(ch.getGuild());
			pl.forceJoin(ch);
			pl.setPreferedTextChannel(event.getAsTextChannel());
			pl.updateMessage(pl.getCurrentTrack());
			pl.getPlayer().setPaused(false);
			event.reply(event.getGParam().translate("cmd.use.come.joining", ch.getAsMention()));
			
		}
		else
		{
			event.prepareReply(event.getGParam().translate("cmd.use.come.error")).setEphemeral(true).queue();;
		}
	}

	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr=new TranslateInstance(p,name()); 
		return  Commands.slash(name(),tr.translate("desc"));
	}

	@Override
	public String name() {
		return "come";
	}

}
