package com.diamssword.sopbot.music;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.diamssword.sopbot.SOP;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class AudioLoader implements AudioLoadResultHandler{
	String guildId;
	Object data;
	VoiceChannel preferedVoice;
	GuildMessageChannel preferedText;
	BiConsumer<AudioPlaylist,Boolean> consumer;
	/**
	 * 
	 * @param guild
	 * @param consumer Consumer[Audio,isSuccessful]
	 * @param additionalData 
	 */
	public AudioLoader(String guild, @Nullable BiConsumer<AudioPlaylist,Boolean> consumer, Object additionalData,VoiceChannel preferedVoice,GuildMessageChannel preferedText)
	{
		this.data=additionalData;
		this.consumer = consumer;
		this.guildId=guild;
		this.preferedText=preferedText;
		this.preferedVoice=preferedVoice;
	}
	@Override
	public void trackLoaded(AudioTrack track) {
		if(this.consumer != null)
			consumer.accept(new AudioPlaylist() {

				@Override
				public String getName() {
					return track.getInfo().title;
				}

				@Override
				public List<AudioTrack> getTracks() {

					ArrayList<AudioTrack> ar=new ArrayList<AudioTrack>();
					ar.add(track);
					return ar;
				}

				@Override
				public AudioTrack getSelectedTrack() {
					return track;
				}

				@Override
				public boolean isSearchResult() {
					return false;
				}}, true);

		if(data != null)
			track.setUserData(data);
			SOP.playerManager.queueTrack(this.guildId,track, preferedVoice, preferedText);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		if(this.consumer != null)
			consumer.accept(playlist, true);
			for(AudioTrack tr : playlist.getTracks())
			{
				if(data != null)
					tr.setUserData(data);
				SOP.playerManager.queueTrack(this.guildId,tr, preferedVoice, preferedText);
			}
		}

	@Override
	public void noMatches() {
		if(this.consumer != null)
			consumer.accept(null, false);

	}

	@Override
	public void loadFailed(FriendlyException exception) {
		if(this.consumer != null)
			consumer.accept(null, false);

	}

}
