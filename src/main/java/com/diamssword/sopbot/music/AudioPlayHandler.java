package com.diamssword.sopbot.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayHandler implements AudioSendHandler {

	 private final AudioPlayer audioPlayer;
	  private AudioFrame lastFrame;

	  public AudioPlayHandler(AudioPlayer audioPlayer) {
	    this.audioPlayer = audioPlayer;
	  }

	  @Override
	  public boolean canProvide() {
	    lastFrame = audioPlayer.provide();
	    return lastFrame != null;
	  }

	  @Override
	  public ByteBuffer provide20MsAudio() {
	    return ByteBuffer.wrap(lastFrame.getData());
	  }

	  @Override
	  public boolean isOpus() {
	    return true;
	  }

}
