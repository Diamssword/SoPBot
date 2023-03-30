package com.diamssword.sopbot.commands;

import java.awt.Color;

import javax.annotation.Nullable;

import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.utils.EmbedUtil;
import com.diamssword.sopbot.utils.StringUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class CommandEvent {

	private final String[] arguments;
	private final User author;
	private final MessageChannel channel;
	private final Message message;
	public CommandEvent(User author, MessageChannel channel,Message message,String... arguments ) {
		this.arguments = arguments;
		this.author = author;
		this.channel = channel;
		this.message=message;
	}
	public void reply(MessageEmbed m)
	{
		this.getChannel().sendMessageEmbeds(m).queue();
	}
	public void reply(String m)
	{
		this.getChannel().sendMessage(m).queue();
	}
	public Message replyDirect(String m)
	{
		return this.getChannel().sendMessage(m).complete();
	}
	public Message replyDirect(MessageEmbed m)
	{
		return this.getChannel().sendMessageEmbeds(m).complete();
	}
	public Message getMessage()
	{
		return this.message;
	}
	public String[] getArguments() {
		return arguments;
	}
	public String getUserEffectiveName()
	{
		if(this.isPrivate())
			return this.author.getName();
		return this.getAsMember().getEffectiveName();
	}
	public GuildParameters getGParam() {
		GuildParameters man=GuildsManager.get(this.getGuild());
		if(man == null)
		{
			man= new GuildParameters();

		}
		return man;
	}
	public User getAuthor() {
		return author;
	}
	public MessageChannel getChannel() {
		return channel;
	}
	public boolean isPrivate()
	{
		return this.channel.getType() == ChannelType.PRIVATE;
	}
	public TextChannel getAsTextChannel()
	{
		return (TextChannel) this.channel;
	}
	public PrivateChannel getAsPrivateChannel()
	{
		return (PrivateChannel) this.channel;
	}
	public Member getAsMember()
	{
		Member m= null;
		m=this.getGuild().getMember(author);
		if(m == null)
			m=this.getGuild().retrieveMember(author).complete();
		return m;
	}
	public Guild getGuild()
	{
		if(this.isPrivate())
			return null;
		return  this.getAsTextChannel().getGuild();
	}
	public String getArgsAsString()
	{
		String res= "";
		for(String s : this.arguments)
		{
			res=res+s+" ";
		}
		return res.trim();
	}
	public String weldArgs(int start,int end)
	{
		return StringUtils.weldArgs(this.arguments, start, end);
	}
	public void sendResponse(Type type,String response,int minutes,@Nullable Object... params)
	{
		MessageEmbed msg= null;
		switch(type)
		{
		case success:
			msg=EmbedUtil.confirmed(this.getGParam().defaultLang(), null,response, minutes >0?this.getMessage().getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		case warning:
			msg=EmbedUtil.warning(this.getGParam().defaultLang(),  null,response, minutes >0?this.getMessage().getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		case error:
			msg=EmbedUtil.denied(this.getGParam().defaultLang(),  null,response, minutes >0?this.getMessage().getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
			break;
		default :
			EmbedUtil.basic(this.getGParam().defaultLang(), null, null,response,null, null, new Color((int)Math.random()*255,(int)Math.random()*255,(int)Math.random()*255),  minutes >0?this.getMessage().getTimeCreated().plusMinutes(minutes).toLocalDateTime():null, params);
		}
		if(minutes >0)
			EmbedUtil.timedEmbeded(this.getAsTextChannel(), msg, this.getMessage().getTimeCreated().plusMinutes(minutes).toEpochSecond());
		else
			this.channel.sendMessageEmbeds(msg).queue();
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
}
