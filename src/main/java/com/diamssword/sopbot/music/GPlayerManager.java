package com.diamssword.sopbot.music;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.diamssword.sopbot.SOP;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class GPlayerManager  {

	public final static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	public static final Logger LOG = SOP.getLogger("DiamsBot-Audio");
	private Map<String,GuildPlayer> players = new HashMap<String,GuildPlayer>();
	public GPlayerManager()
	{
		AudioSourceManagers.registerRemoteSources(playerManager);
		playerManager.setPlayerCleanupThreshold(10000);

	}

	public GuildPlayer getGuildPlayer(String guildID)
	{
		GuildPlayer play= players.get(guildID);
		if(play==null)
		{
			play = new GuildPlayer(guildID);
			players.put(guildID, play);
		}
		return play;
	}

	public GuildPlayer getGuildPlayer(Guild g)
	{
		return this.getGuildPlayer(g.getId());
	}

	public void queueTrack(String GuildID,AudioTrack track,VoiceChannel preferedVoice,GuildMessageChannel preferedText)
	{
		IGuildPlayer pl=this.getGuildPlayer(GuildID);
		if(preferedText != null)
			pl.setPreferedTextChannel(preferedText);
		if(preferedVoice != null)
			pl.setPreferedVoiceChannel(preferedVoice);
		pl.queue(track);
	}
	public void loadTrack(String source,Member m, @Nullable BiConsumer<AudioPlaylist,Boolean> callback,Object additionalData,GuildMessageChannel preferedText)
	{
		playerManager.loadItem(source, new AudioLoader(m.getGuild().getId(),callback,additionalData, getUserChannel(m), preferedText));
	}
	public void loadTrack(String source, String guild,@Nullable BiConsumer<AudioPlaylist,Boolean> callback,Object additionalData,VoiceChannel preferedVoice,TextChannel preferedText)
	{
		playerManager.loadItem(source, new AudioLoader(guild,callback,additionalData, preferedVoice, preferedText));
	}
	
	public static VoiceChannel getUserChannel(Member m)
	{
		return (VoiceChannel) m.getVoiceState().getChannel();
	}
}
