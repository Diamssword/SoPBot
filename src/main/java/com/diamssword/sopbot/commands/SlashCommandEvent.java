package com.diamssword.sopbot.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.utils.EmbedUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class SlashCommandEvent {

	private GuildParameters gparam ;
	private final SlashCommandInteractionEvent event;
	public SlashCommandEvent(SlashCommandInteractionEvent event ) {
		this.event = event;
		gparam=GuildsManager.get(this.getGuild());
		if(gparam == null)
		{
			gparam= new GuildParameters();
		}
	}
	public boolean isSubCommand(String name)
	{
		return this.event.getSubcommandName().equalsIgnoreCase(name);
	}
	public void reply(MessageEmbed m)
	{
		this.event.replyEmbeds(m).queue();
	}
	public void reply(MessageEmbed[] m)
	{
		ArrayList<MessageEmbed> ar=new ArrayList<MessageEmbed>();
		for(MessageEmbed m1 : m)
			ar.add(m1);
		this.event.replyEmbeds(ar).queue();
	}
	public ReplyCallbackAction prepareReply(MessageEmbed m)
	{
		return	this.event.replyEmbeds(m);
	}
	public ReplyCallbackAction prepareReply(String m)
	{
		return	this.event.reply(m);
	}
	public void reply(String m)
	{
		this.event.reply(m).queue();
	}
	public InteractionHook replyDirect(String m)
	{
		return this.event.reply(m).complete();
	}
	public InteractionHook replyDirect(MessageEmbed m)
	{
		return this.event.replyEmbeds(m).complete();
	}
	public String getCommmand()
	{
		return this.event.getCommandString();
	}
	public SlashCommandInteractionEvent getEvent()
	{
		return event;
	}
	public String getArgument(String name) {
		OptionMapping map=event.getOption(name);
		if(map == null)
			return "";
		return map.getAsString();
	}
	public boolean hasOption(String name)
	{
		return event.getOption(name)!=null;
	}
	public OptionMapping getOption(String name)
	{
		return event.getOption(name);
	}
	public GuildParameters getGParam() {

		return gparam;
	}
	public User getAuthor() {
		return this.event.getUser();
	}
	public MessageChannelUnion getChannel() {
		return this.event.getChannel();
	}
	public boolean isPrivate()
	{
		return this.getChannel().getType() == ChannelType.PRIVATE;
	}
	public GuildMessageChannel getAsTextChannel()
	{
		
		return this.event.getChannel().asGuildMessageChannel();
	}
	public PrivateChannel getAsPrivateChannel()
	{
		return this.event.getChannel().asPrivateChannel();
	}
	public Member getMember()
	{
		Member m= null;
		m=this.getGuild().getMember(this.getAuthor());
		if(m == null)
			m=this.getGuild().retrieveMember(this.getAuthor()).complete();
		return m;
	}
	public Guild getGuild()
	{
		if(this.isPrivate())
			return null;
		return  this.getAsTextChannel().getGuild();
	}

	public void sendResponse(Type type,String response,int minutes,@Nullable Object... params)
	{
		MessageEmbed msg= null;
		switch(type)
		{
		case success:
			msg=EmbedUtil.confirmed(this.getGParam().defaultLang(), null,response, minutes >0?this.event.getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		case warning:
			msg=EmbedUtil.warning(this.getGParam().defaultLang(),  null,response, minutes >0?this.event.getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		case error:
			msg=EmbedUtil.denied(this.getGParam().defaultLang(),  null,response, minutes >0?this.event.getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		default :
			EmbedUtil.basic(this.getGParam().defaultLang(), null, null,response,null, null, new Color((int)Math.random()*255,(int)Math.random()*255,(int)Math.random()*255),  minutes >0?this.event.getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
		}
		if(minutes >0)
		{
			this.reply("** **");
			EmbedUtil.timedEmbeded(this.getAsTextChannel(), msg, this.event.getTimeCreated().plusMinutes(minutes).toEpochSecond());
		}
		else
			this.prepareReply(msg).setEphemeral(minutes<=-2).queue();;
	}
	public void sendResponse1(String response,int minutes,@Nullable Object... params)
	{
		sendResponse(null,response,minutes,params);
	}
	public void sendResponse1(String response,@Nullable Object... params)
	{
		sendResponse(null,response,-1,params);
	}
	public void sendResponse2(Type type,String response,@Nullable Object... params)
	{
		sendResponse(type,response,-1,params);
	}
	public static enum Type
	{
		success,
		warning,
		error;
	}
	public OffsetDateTime getTimeCreated() {
		return event.getTimeCreated();
	}
}
