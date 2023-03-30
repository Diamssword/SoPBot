package com.diamssword.sopbot.music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.diamssword.sopbot.SOP;
import com.diamssword.sopbot.interfaces.ButtonsManager;
import com.diamssword.sopbot.interfaces.ButtonsManager.ButtonWarper;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.ticking.ITickable;
import com.diamssword.sopbot.ticking.Tickable;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;

public class GuildPlayer extends AudioEventAdapter implements IGuildPlayer {

	private AudioPlayer player;
	private AudioChannel lastChannel;
	private GuildMessageChannel lastTextChannel;
	private Message lastControlMsg;
	private int timeSinceLastSong;
	private boolean autopaused=false;
	private boolean loop=false;
	private Controls lastControls;
	private GuildParameters params;private ITickable updateTickable = new ITickable(new Runnable() {

		@Override
		public void run() {
			if(GuildPlayer.this.shouldRefreshDisplay)
			{
				GuildPlayer.this.updateMessage(getCurrentTrack());
				GuildPlayer.this.shouldRefreshDisplay=false;
			}
			
		}
	});
	private ITickable leaveTickable = new ITickable(new Runnable() {

		@Override
		public void run() {

			if(GuildPlayer.this.getGuild().getAudioManager().isConnected())
			{
				if(GuildPlayer.this.getGuild().getAudioManager().getConnectedChannel().getMembers().size()<=1)
				{
					GuildPlayer.this.getPlayer().setPaused(true);
					GuildPlayer.this.autopaused=true;
				}
				if(GuildPlayer.this.autopaused && GuildPlayer.this.getGuild().getAudioManager().getConnectedChannel().getMembers().size()>1)
				{

					GuildPlayer.this.getPlayer().setPaused(false);
					GuildPlayer.this.autopaused=false;
				}
				boolean paused = GuildPlayer.this.getPlayer().isPaused() ||GuildPlayer.this.getPlayer().getPlayingTrack() ==null;
				if(paused)
				{
					GuildPlayer.this.timeSinceLastSong++;
				}
				else
				{
					GuildPlayer.this.timeSinceLastSong=0;
				}
				if(GuildPlayer.this.timeSinceLastSong>=30)
				{
					GuildPlayer.this.getGuild().getAudioManager().closeAudioConnection();
					GuildPlayer.this.getPlayer().destroy();
					GuildPlayer.this.timeSinceLastSong=0;
				}
			}
		}});
	List<AudioTrack> tracks = new ArrayList<AudioTrack>();
	public final String guildID;
	private Guild guild;
	int pos =0;
	private boolean shouldRefreshDisplay;
	public GuildPlayer(String guildID)
	{
		this.guildID = guildID;
		getPlayer();
		
		this.params=GuildsManager.get(getGuild());
		Tickable.registerTickable(this.leaveTickable, 10000);
		Tickable.registerTickable(this.updateTickable, 1000);

	}
	public AudioTrack getCurrentTrack()
	{
		if(this.tracks.size()<=pos)
			return null;
		return this.tracks.get(pos);
	}
	public Guild getGuild()
	{
		if(guild == null)
			return guild = SOP.bot.getGuildById(guildID);
		return guild;	
	}
	private AudioTrack getTrack(int pos)
	{
		AudioTrack t=tracks.get(pos);
		if(t != null)
			t=t.makeClone();
		return t;
	}
	public AudioPlayer getPlayer()
	{
		if(player == null)
		{
			player = GPlayerManager.playerManager.createPlayer();
			getGuild().getAudioManager().setSendingHandler(new AudioPlayHandler(player));
			player.removeListener(this);
			player.addListener(this);
			return	player;
		}
		return player;
	}
	@Override
	public void onPlayerPause(AudioPlayer player) {
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		this.autopaused=false;
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		this.joinChannel();
		updateMessage(track);

	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		new File(track.getInfo().uri).delete();
		if (endReason.mayStartNext) {
			pos+=1;
			if(pos >= tracks.size())
			{				
				pos=0;
				if(!loop)
				{
					tracks.clear();
					this.getGuild().getAudioManager().closeAudioConnection();
					return;
				}	
			}

			player.playTrack(getTrack(pos));
		}
		if(endReason== AudioTrackEndReason.CLEANUP)
		{
			guild = null;
			tracks.clear();
		}
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		onTrackEnd(player,track, AudioTrackEndReason.LOAD_FAILED);
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		this.skip();
	}

	public void queue(AudioTrack track) {
		this.shouldRefreshDisplay=true;
		tracks.add(track);
		if(this.joinChannel())
		if(this.getPlayer().getPlayingTrack() == null)
		{	
				this.getPlayer().playTrack(getTrack(pos));
		}


	}
	public boolean joinChannel()
	{
		if(this.lastChannel != null)
		{
			if(!getGuild().getAudioManager().isConnected() ||this.getGuild().getAudioManager().getConnectedChannel().getMembers().size()<=1)
			{
				return forceJoin(this.lastChannel);
			}
			else
				return true;
		}
		else if(this.lastTextChannel !=null)
		{
			this.lastTextChannel.sendMessage("You are not in a voice channel!").queue();
		}
		return false;
	}
	public boolean forceJoin(AudioChannel ch)
	{
		try {
			if(this.autopaused)
			{
				this.autopaused=false;
				this.getPlayer().setPaused(false);
			}
			getGuild().getAudioManager().openAudioConnection(ch);
			getGuild().getAudioManager().setSendingHandler(new AudioPlayHandler(player));
			this.lastChannel=ch;
			return true;
		}catch(InsufficientPermissionException pex)
		{
			if(this.lastTextChannel != null)
			{
				this.lastTextChannel.sendMessage("I am not allowed to join "+ch.getAsMention()+" !").queue();	
			}
		}
		return false;

	}

	public void skip()
	{
		pos+=1;
		if(pos >= tracks.size())
		{
			pos=0;
			if(!loop || tracks.isEmpty())
			{
				tracks.clear();
				player.stopTrack();
				return;
			}
		}
		player.playTrack(getTrack(pos));
	}
	public void prev()
	{
		pos-=1;
		if(pos <0)
		{
			pos=0;
			if(player.getPlayingTrack()!=null)
				player.getPlayingTrack().setPosition(0);
			return;
		}
		player.playTrack(getTrack(pos));
	}
	public void clear()
	{
		this.tracks.clear();
		this.getPlayer().stopTrack();
	}
	@Override
	public void remove(AudioTrack track) {


		int ind=this.tracks.indexOf(track);
		if(ind>=0)
		{
			this.tracks.remove(ind);
		}

	}
	@Override
	public void skipTo(int index) {
		if(index<0)
			return;
		if(index <this.tracks.size())
		{
			this.pos=index;
			if(pos >= tracks.size())
			{
				pos=0;
				if(!loop || tracks.isEmpty())
				{
					tracks.clear();
					player.stopTrack();
					return;
				}
			}
			player.playTrack(getTrack(pos));

		}

	}
	@Override
	public void setPreferedVoiceChannel(AudioChannel channel) {
		this.lastChannel=channel;
	}
	@Override
	public void setPreferedTextChannel(GuildMessageChannel channel) {
		this.lastTextChannel=channel;

	}
	public void updateMessage(@Nullable AudioTrack track)
	{

		MessageEmbed emb =null;
		if(track == null)
		{
			EmbedBuilder b=new EmbedBuilder();
			
			b.setTitle(params.translate("player.no_content"));
			emb = 	b.build();
		}
		else
		{
			emb=this.createEmbed(track);
		}
		MessageCreateAction act = null;
		MessageEditAction act1 = null;
		boolean flag=false;
		boolean flag1=false;
		if(this.lastControlMsg !=null && this.lastTextChannel !=null)
		{
			flag1= this.lastControlMsg.getChannel().getId().equals(this.lastTextChannel.getId());
			if(this.lastTextChannel.getLatestMessageId().equals(this.lastControlMsg.getId()))
			{
				flag=true;
				act1=this.lastControlMsg.editMessageEmbeds(emb);
			}
			else
			{
				act =this.lastTextChannel.sendMessageEmbeds(emb);
			}
		}
		else if(this.lastTextChannel != null)
		{
			flag1=true;
			act =this.lastTextChannel.sendMessageEmbeds(emb);
		}
		if(act != null || act1 !=null )
		{
			if(lastControls !=null && !flag &&!flag1)
				lastControls.invalidates();
			if(this.lastControlMsg !=null && !flag && !flag1)
				this.lastControlMsg.delete().queue();
			if(act!=null)
			{
			lastControls =new Controls(act,this);
			lastControls.creatAction();
			act.queue(new Consumer<Message>() {

				@Override
				public void accept(Message t) {


					GuildPlayer.this.lastControlMsg=t;					
				}});
			}
			else if(act1!=null)
			{
			lastControls =new Controls(act1,this);
			lastControls.creatAction();
			act1.queue(new Consumer<Message>() {

				@Override
				public void accept(Message t) {


					GuildPlayer.this.lastControlMsg=t;					
				}});
			}
		}
	}
	private AudioTrack getNext(int pos)
	{
		if(pos>=tracks.size())
		{

			if(loop)
			{
				int p =pos-tracks.size();
				if(p>=tracks.size())
					return null;
				return tracks.get(p);
			}
			return null;
		}
		return tracks.get(pos);
	}
	private static final int listsize=3;
	private MessageEmbed createEmbed(AudioTrack track)
	{

		EmbedBuilder eb=getEmbedDisplay(track).getEmbed(track);
		int index =this.tracks.indexOf(track);
		if((index>-1 && index+1 <this.tracks.size())||loop)
		{
			eb.addField("Next ("+(this.tracks.size()-pos-1)+"):", "", false);
			for(int i=1;i<listsize+1;i++)
			{
				AudioTrack t1 =getNext(index+i);
				if(t1!=null)
				{
					String[] s=this.getEmbedDisplay(t1).smallDisplay(t1);
					eb.addField(s[0],s[1], false);
				}
			}
		}
		return eb.build();

	}
	private EmbedTrackDisplay getEmbedDisplay(AudioTrack track)
	{
		EmbedTrackDisplay etd; 
		if(track.getUserData() !=null && track.getUserData() instanceof EmbedTrackDisplay )
		{
			etd = (EmbedTrackDisplay) track.getUserData();
		}
		else
		{
			etd=defaultEmbedDisplay;
		}
		return etd;
	}
	private EmbedTrackDisplay defaultEmbedDisplay= new EmbedTrackDisplay() {

		@Override
		public EmbedBuilder getEmbed(AudioTrack track) {
			
			EmbedBuilder bl=new EmbedBuilder();
			bl.setTitle(params.translate("player.playing",track.getInfo().title));
			bl.setDescription("*"+track.getInfo().author+"*");
			return bl;
		}

		@Override
		public String[] smallDisplay(AudioTrack track) {
			return new String[] {"**"+track.getInfo().title+"**","by"+track.getInfo().author};
		}}; 

		public static final Emoji PLAY=Emoji.fromUnicode("U+25B6");
		public static final Emoji PAUSE=Emoji.fromUnicode("U+23F8");
		public static final Emoji PREV=Emoji.fromUnicode("U+23EE");
		public static final Emoji NEXT=Emoji.fromUnicode("U+23ED");
		public static final Emoji REPEAT=Emoji.fromUnicode("U+1F501");
		public static final Emoji ARROW_RIGHT=Emoji.fromUnicode("U+27A1");
		private static class Controls
		{
			MessageCreateAction action;
			MessageEditAction action1;
			GuildPlayer player;
			Button[] bts;
			public Controls(MessageCreateAction action,GuildPlayer player)
			{
				this.action=action;
				this.player=player;
			}
			public Controls(MessageEditAction action,GuildPlayer player)
			{
				this.action1=action;
				this.player=player;
			}
			private boolean canSkip(Member clicked,IGuildPlayer player)
			{
				if(!player.getGuild().getAudioManager().isConnected())
					return true;
				if(player.getGuild().getAudioManager().getConnectedChannel().getMembers().size()<=1)
					return true;
				return player.getGuild().getAudioManager().getConnectedChannel() == clicked.getVoiceState().getChannel();

			}
			public void invalidates()
			{
				ButtonsManager.remove(bts);
			}
			public void creatAction()
			{
				String notInChannel =player.params.translate("player.notinchannel");
				
				 ButtonWarper[] ls=new ButtonWarper[] {
						new ButtonWarper(Button.primary("previous",PREV ),new  Consumer<ButtonInteractionEvent>(){
							@Override
							public void accept(ButtonInteractionEvent t) {
								if(canSkip(t.getMember(),player))
								{
									player.getPlayer().setPaused(false);
									player.prev();	
									t.editButton(t.getButton()).queue();
									player.updateMessage(player.getCurrentTrack());
								}
								else
									t.reply(notInChannel).setEphemeral(true).queue();
							}}),
						new ButtonWarper(Button.primary("pause",player.getPlayer().isPaused()?PLAY:PAUSE ),new  Consumer<ButtonInteractionEvent>(){

							@Override
							public void accept(ButtonInteractionEvent t) {
								if(canSkip(t.getMember(),player))
								{
									if(t.getButton().getEmoji().equals(PAUSE))
									{

										t.editButton(t.getButton().withEmoji(PLAY)).queue();
										player.getPlayer().setPaused(true);
									}
									else
									{
										t.editButton(t.getButton().withEmoji(PAUSE)).queue();
										player.getPlayer().setPaused(false);
									}
									player.updateMessage(player.getCurrentTrack());
								}
								else
									t.reply(notInChannel).setEphemeral(true).queue();
							}}),
						new ButtonWarper(Button.primary("next",NEXT ),new  Consumer<ButtonInteractionEvent>(){
							@Override
							public void accept(ButtonInteractionEvent t) {
								if(canSkip(t.getMember(),player))
								{
									player.getPlayer().setPaused(false);
									player.skip();	
									t.editButton(t.getButton()).queue();
									player.updateMessage(player.getCurrentTrack());
								}
								else
									t.reply(notInChannel).setEphemeral(true).queue();
							}}),
						new ButtonWarper(Button.primary("repeat",player.loop?ARROW_RIGHT:REPEAT ),new  Consumer<ButtonInteractionEvent>(){
							@Override
							public void accept(ButtonInteractionEvent t) {
								if(canSkip(t.getMember(),player))
								{
									if(t.getButton().getEmoji().equals(REPEAT))
									{

										t.editButton(t.getButton().withEmoji(ARROW_RIGHT)).queue();
										player.loop=true;
									}
									else
									{
										t.editButton(t.getButton().withEmoji(REPEAT)).queue();
										player.loop=false;
									}
									player.updateMessage(player.getCurrentTrack());
								}
								else
									t.reply(notInChannel).setEphemeral(true).queue();
							}}),

						new ButtonWarper(Button.primary("clear",player.params.translate("player.clear")),new  Consumer<ButtonInteractionEvent>(){
							@Override
							public void accept(ButtonInteractionEvent t) {
								if(canSkip(t.getMember(),player))
								{
									
										t.editButton(t.getButton().asDisabled()).queue();
										player.clear();
									
									player.updateMessage(null);
								}
								else
									t.reply(notInChannel).setEphemeral(true).queue();
							}})};
				 if(this.action !=null)
				 	bts=ButtonsManager.add(this.action,ls);
				 else
					 bts=ButtonsManager.add(this.action1,ls);
			}

		}
}