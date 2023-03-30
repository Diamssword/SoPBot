package com.diamssword.sopbot.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.apache.hc.core5.http.ParseException;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.jsons.YTVideoJson;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.music.EmbedTrackDisplay;
import com.diamssword.sopbot.ticking.ITickable;
import com.diamssword.sopbot.ticking.Tickable;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class Spotify {
	public static SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken("BQCo_qerJxewk3rnQv94KkDr0wf9gha3km7yGrUZPYumuZfEGeh01MBBEpA19KiLs_cf49zMTVqSmiSABn8").build();

	// Create a request object with the optional parameter "market"


	public static boolean isSpotifyLink(String str)
	{
		return str.contains("open.spotify.com");

	}
	private static List<Runnable> toLoad=Collections.synchronizedList(new ArrayList<Runnable>());
	private static ITickable slowLoader=new ITickable(new Runnable() {

		@Override
		public void run() {
			if(!toLoad.isEmpty())
				toLoad.remove(0).run();

		}});
	public static void init()
	{
		Tickable.registerTickable(slowLoader, 1000);

		SpotifyApi.Builder bl=new SpotifyApi.Builder();

		LoadUtils.askToken("spotifyClient", new Function<String,Boolean>(){

			@Override
			public Boolean apply(String t) {
				bl.setClientId(t);
				return true;
			}});
		LoadUtils.askToken("spotifySecret", new Function<String,Boolean>(){

			@Override
			public Boolean apply(String t) {
				spotifyApi=bl.setClientSecret(t).build();
				refreshToken();
				return true;
			}});
	}

	public static void handleLink(String url,Member origin,TextChannel ch)
	{
		int pos=url.indexOf("/track/"); 
		if(pos>-1)
		{
			System.out.println(extractID(url,"track"));
		}
		pos=url.indexOf("/playlist/"); 
		if(pos>-1)
		{
			String d=extractID(url,"playlist");
			try {
				Playlist p=spotifyApi.getPlaylist(d).build().execute();
				for(PlaylistTrack tr:p.getTracks().getItems())
				{
					findOnYT(tr.getTrack(), origin, ch);
					//System.out.println(tr.getTrack().getClass());
				}
			} catch (ParseException | SpotifyWebApiException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		pos=url.indexOf("/album/"); 
		if(pos>-1)
		{
			System.out.println(extractID(url,"album"));
		}
		pos=url.indexOf("/show/"); 
		if(pos>-1)
		{
			System.out.println(extractID(url,"show"));
		}
		pos=url.indexOf("/episode/"); 
		if(pos>-1)
		{
			System.out.println(extractID(url,"episode"));
		}
	}
	private static void findOnYT(IPlaylistItem item,Member origin,TextChannel ch)
	{
		if(item instanceof Track)
		{
			toLoad.add(new Runnable() {

				@Override
				public void run() {
					Track tr=(Track) item;
					try {
						YTVideoJson[] vids=HttpClient.searchvid(1, tr.getName()+" "+tr.getArtists()[0].getName(),GuildsManager.get(ch.getGuild()));
						if(vids.length>0)
						{
							System.out.println(vids[0].id);
							SOP.playerManager.loadTrack("https://www.youtube.com/watch?v="+vids[0].id, origin, null, new SpotifyDisplay(tr, origin), ch);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}});



		}


	}
	private static String extractID(String url,String type)
	{
		int t=url.indexOf(type);
		t+=type.length()+1;
		if(t+22<=url.length())
		{
			return url.substring(t,t+22);
		}
		return null;
	}
	public static String refreshToken()
	{
		try {
			String token=spotifyApi.clientCredentials().grant_type("client_credentials").build().execute().getAccessToken();
			spotifyApi.setAccessToken(token);
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static class SpotifyDisplay implements EmbedTrackDisplay
	{
		Member m;
		Track tr;
		public SpotifyDisplay(Track track,Member m)
		{
			this.m=m;
			this.tr=track;
		}
		public String authors()
		{
			String s1="";
			for(ArtistSimplified s : tr.getArtists())
			{
				s1+=s.getName()+",";	
			}
			s1= s1.substring(0,s1.length()-1);
			if(s1.length()>25)
				s1=s1.substring(0,25)+"...";
			return s1;
		}
		@Override
		public EmbedBuilder getEmbed(AudioTrack track) {
			GuildParameters g =GuildsManager.get(m.getGuild());
			EmbedBuilder bl=new EmbedBuilder();
			bl.setColor(0x1ed760);
			bl.setAuthor(m.getEffectiveName(),null,m.getUser().getEffectiveAvatarUrl());
			Image[] imgs=tr.getAlbum().getImages();
			if(imgs.length>0)
				bl.setThumbnail(imgs[0].getUrl());
			bl.setTitle(g.translate("cmd.use.play.playing",tr.getName()),tr.getHref());
			bl.setDescription(tr.getAlbum().getName()+" by "+this.authors());
			return bl;
		}
		@Override
		public String[] smallDisplay(AudioTrack track) {
			return new String[]{"**"+track.getInfo().title+"**","by "+track.getInfo().author};
		}



	}
}
