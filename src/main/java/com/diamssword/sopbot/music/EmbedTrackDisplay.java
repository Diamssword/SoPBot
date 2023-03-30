package com.diamssword.sopbot.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;

public interface EmbedTrackDisplay {

	public EmbedBuilder getEmbed(AudioTrack track);
	/**
	 * 
	 * @param track
	 * @return a 2 string array (mostly used on embed field so first is title field and the second is desc)
	 */
	public String[] smallDisplay(AudioTrack track);
}
