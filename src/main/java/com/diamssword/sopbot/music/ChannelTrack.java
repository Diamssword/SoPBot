package com.diamssword.sopbot.music;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ChannelTrack implements IChannelTrack<ChannelTrack>{
	private final VoiceChannel member;
	private AudioTrack track;
	private List<BiConsumer<ChannelTrack,String>> events = new ArrayList<BiConsumer<ChannelTrack,String>>(); 
	public ChannelTrack(VoiceChannel chan, AudioTrack track)
	{
		this.member = chan;
		this.track = track;
	}
	@SafeVarargs
	public ChannelTrack(VoiceChannel chan, AudioTrack track,BiConsumer<ChannelTrack,String>... events)
	{
		this.member = chan;
		this.track = track;
		for(BiConsumer<ChannelTrack,String> ev : events)
		{
			this.addEvents(ev);
		}
	}
	@Override
	public AudioTrack getTrack() {
		return track;
	}

	@Override
	public VoiceChannel getChannel() {
		return member;
	}
	@Override
	public List<BiConsumer<ChannelTrack,String>> getEvents() {
		return events;
	}
	public void addEvents(BiConsumer<ChannelTrack,String> ev) {
		events.add(ev);
	}

}
