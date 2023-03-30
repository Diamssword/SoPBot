package com.diamssword.sopbot.music;

import java.util.List;
import java.util.function.BiConsumer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public interface IChannelTrack<A extends IChannelTrack<A>> {

	/**
	 * 
	 * @return the track to play
	 */
	public AudioTrack getTrack();
	
	/**
	 * 
	 * @return the channel where the track have to be played
	 */
	public VoiceChannel getChannel();
	
	
	public List<BiConsumer<A, String>> getEvents();
	@SuppressWarnings("unchecked")
	default void ontrack(String state)
	{
		for(BiConsumer<A, String> con: this.getEvents())
		{
			con.accept((A) this,state);
		}
	}
}
