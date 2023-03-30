package com.diamssword.sopbot.commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.interfaces.ButtonsManager;
import com.diamssword.sopbot.interfaces.ButtonsManager.ButtonWarper;
import com.diamssword.sopbot.jsons.YTVideoJson;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.music.EmbedTrackDisplay;
import com.diamssword.sopbot.utils.EmbedUtil;
import com.diamssword.sopbot.utils.HttpClient;
import com.diamssword.sopbot.utils.Spotify;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class MusicCommand implements ISlashCommand{


	@Override
	public void init() {


	}


	private class ctrlNb implements Consumer<ButtonInteractionEvent>
	{
		private YTVideoJson[] vid;
		private final String member;
		private Button[] buttons =new Button[0];
		public ctrlNb(YTVideoJson[] vid, Member member)
		{
			this.vid=vid;
			this.member=member.getId();
		}
		public void setButtons(Button[] buttons)
		{
			this.buttons= buttons;
		}
		@Override
		public void accept(ButtonInteractionEvent event) {
			if(event.getMember().getId().equals(this.member))
			{
				GuildParameters g =GuildsManager.get(event.getGuild());
				for(Button b: this.buttons)
					ButtonsManager.remove(b);
				event.editComponents().queue();
				int nb=Integer.parseInt(event.getComponent().getLabel())-1;
				SOP.playerManager.loadTrack("https://www.youtube.com/watch?v="+vid[nb].id.videoId, event.getMember(), new BiConsumer<AudioPlaylist,Boolean>() {
					@Override
					public void accept(AudioPlaylist t, Boolean u) {
						event.getHook().editOriginalEmbeds(youtbeloadedEmbed(t, u, g,"https://www.youtube.com/watch?v="+vid[nb].id.videoId).build()).queue();}}, new YoutubeDisplay(event.getMember()), event.getChannel().asGuildMessageChannel());
			}
			else
			{
				event.deferEdit();
			}
		}

	}


	@Override
	public void onCommand(SlashCommandEvent event) {

		try {
			URL url=new URL(event.getArgument("link"));
			if(Spotify.isSpotifyLink(url.toExternalForm()))
			{
				event.prepareReply("Queuing "+url).setEphemeral(true).queue();
			//	Spotify.handleLink(url.toExternalForm(), event.getMember(), event.getAsTextChannel());
				return;
			}
			this.loadSingleUrl(event, url);
			return;
		} catch (MalformedURLException e1) {
		}
		YTVideoJson[] vids;
		try {
			vids = HttpClient.searchvid(4,event.getArgument("link"),event.getGParam());
			List<String> titles = new ArrayList<String>(); 
			List<ButtonWarper> buttons=new ArrayList<ButtonWarper>();
			String[] emotes=new String[] {"one","two","three","four"};
			ctrlNb ctrl=new ctrlNb(vids,event.getMember());
			for(int k=0;k<vids.length || k<4;k++)
			{
				titles.add(":"+emotes[k]+": **"+vids[k].snippet.title+"** by **"+vids[k].snippet.channelTitle+"**");
				buttons.add(new ButtonWarper(Button.primary((k+1)+"", (k+1)+""),ctrl));
			}
			MessageEmbed m=	EmbedUtil.list(event.getGParam().defaultLang(), "cmd.use.play.list.title", null, titles.toArray(new String[0]), new String[0],null,true);
			ReplyCallbackAction act= event.prepareReply(m);
			ctrl.setButtons(ButtonsManager.add(act,buttons.toArray(new ButtonWarper[0])));
			act.setEphemeral(true).queue();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public SlashCommandData command(Guild g,GuildParameters p) {

		TranslateInstance tr=new TranslateInstance(p,name()); 
		return Commands.slash(name(),tr.translate("desc")).addOption(OptionType.STRING,"link",tr.translate("link"),true);
	}

	private void loadSingleUrl(SlashCommandEvent event, URL url)
	{

		GuildParameters g =GuildsManager.get(event.getGuild());
		SOP.playerManager.loadTrack(url.toExternalForm(), event.getMember(), new BiConsumer<AudioPlaylist,Boolean>() {
			@Override
			public void accept(AudioPlaylist t, Boolean u) {
				event.prepareReply(youtbeloadedEmbed(t, u, g,url.toExternalForm()).build()).setEphemeral(true).queue();}},
				new YoutubeDisplay(event.getMember()), event.getAsTextChannel());

	}
	public EmbedBuilder youtbeloadedEmbed(AudioPlaylist t,Boolean u,GuildParameters g,String error)
	{

		EmbedBuilder bl=new EmbedBuilder();
		if(u)
		{
			boolean pl=t.getTracks().size()>1;
			AudioTrack t1=t.getTracks().get(0);
			bl.setThumbnail("https://img.youtube.com/vi/"+t1.getInfo().identifier+"/0.jpg");
			bl.setTitle(g.translate("cmd.use.play.added",(pl?"Playlist ":"")+t1.getInfo().title),"https://www.youtube.com/watch?v="+t1.getInfo().identifier);
			bl.setDescription("*"+t1.getInfo().author+"*");
		}
		else
		{
			bl.setTitle(g.translate("cmd.use.play.failloading",error));
		}
		return bl;
	}
	public static class YoutubeDisplay implements EmbedTrackDisplay
	{
		Member m;
		public YoutubeDisplay(Member m)
		{
			this.m=m;
		}
		@Override
		public EmbedBuilder getEmbed(AudioTrack track) {
			GuildParameters g =GuildsManager.get(m.getGuild());
			EmbedBuilder bl=new EmbedBuilder();
			bl.setColor(0xff0000);
			bl.setAuthor(m.getEffectiveName(),null,m.getUser().getEffectiveAvatarUrl());
			bl.setThumbnail("https://img.youtube.com/vi/"+track.getInfo().identifier+"/0.jpg");
			bl.setTitle(g.translate("cmd.use.play.playing",track.getInfo().title),"https://www.youtube.com/watch?v="+track.getInfo().identifier);
			bl.setDescription("*"+track.getInfo().author+"*");
			return bl;
		}
		@Override
		public String[] smallDisplay(AudioTrack track) {
			return new String[]{"**"+track.getInfo().title+"**","by "+track.getInfo().author};
		}



	}
	@Override
	public String name() {
		return "play";
	}
}
