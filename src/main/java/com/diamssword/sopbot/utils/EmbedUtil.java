package com.diamssword.sopbot.utils;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.diamssword.sopbot.Localise;
import com.diamssword.sopbot.commands.CommandEvent;
import com.diamssword.sopbot.ticking.ITickable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class EmbedUtil{
	private static Map<Long, Message> toDelete = new HashMap<Long,Message>();
	public static MessageEmbed denied(String lang, String title, String desc,@Nullable LocalDateTime clearTime,Object... params)
	{
		return basic(lang,"embed.error","https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/twitter/248/prohibited_1f6ab.png",title,desc,"https://media1.tenor.com/images/17a17ae6b93faf667b39af6d8fe34d68/tenor.gif?itemid=14359850g",new Color(9902336),clearTime,null,params);
	}
	public static MessageEmbed basic(String lang, @Nullable String author,@Nullable String authorUrl,@Nullable String title,@Nullable String desc,@Nullable String thumbnailURL,Color color,@Nullable LocalDateTime clearTime,@Nullable Object[] paramsTitle,Object... paramsDesc)
	{
		EmbedBuilder bl=new EmbedBuilder();
		if(author != null)
			bl.setAuthor(Localise.translate(lang, author), null, authorUrl);
		if(title != null)
			bl.setTitle("*"+Localise.translate(lang, title,paramsTitle)+"*");
		if(desc != null)
			bl.setDescription(Localise.translate(lang, desc,paramsDesc));
		bl.setColor(color);
		if(thumbnailURL!=null)
			bl.setThumbnail(thumbnailURL);
		if(clearTime != null)
		{
			bl.setTimestamp(clearTime)
			.setFooter(Localise.translate(lang, "embed.selfDestruct"));
		}
		return bl.build();
	}
	public static MessageEmbed list(String lang, String title, String desc,String[] titles, String[] texts,@Nullable LocalDateTime clearTime, @Nullable boolean bold)
	{
		EmbedBuilder bl =new EmbedBuilder();
		if(desc != null)
			bl.setTitle(Localise.translate(lang, desc));
		bl.setColor(new Color(16098851));
		if(title != null)
			bl.setAuthor(Localise.translate(lang, title), null, null);
		for(int i=0;i<titles.length;i++)
		{
			String t  =titles[i];
			String t1 = "";
			if(i<texts.length)
				t1=texts[i];
			if(bold)
				bl.addField("**"+Localise.translate(lang, t)+"**",Localise.translate(lang, t1) , false);
			else
				bl.addField(Localise.translate(lang, t),Localise.translate(lang, t1) , false);
		}
		if(clearTime != null)
		{
			bl.setTimestamp(clearTime)
			.setFooter(Localise.translate(lang, "embed.selfDestruct"));
		}
		return bl.build();
	}
	public static MessageEmbed confirmed(String lang, @Nullable String title,String text,@Nullable LocalDateTime clearTime, Object...params)
	{
		return basic(lang,"embed.success","https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/microsoft/209/heavy-check-mark_2714.png",title,text,"https://media.tenor.com/images/f480c27af7ffbb58672d53b1eaabcaf8/tenor.gif",new Color(60726),clearTime,null,params);
	}
	public static MessageEmbed warning(String lang, @Nullable String title,String text,@Nullable LocalDateTime clearTime, Object...params)
	{
		return basic(lang,"embed.warning","https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/twitter/248/warning_26a0.png",title,text,"https://thumbs.gfycat.com/ScornfulPointlessIndiancow-max-1mb.gif",new Color(11564288),clearTime,null,params);
	}
	public static void timedEmbeded(MessageChannel ch,MessageEmbed msg,long destruction)
	{
		ch.sendMessageEmbeds(msg).queue(new Consumer<Message>(){

			@Override
			public void accept(Message t) {

				toDelete.put(destruction, t);
			}});
	}
	public static Message timedEmbededInstant(CommandEvent ev,MessageEmbed msg,long destruction)
	{
		Message m=ev.replyDirect(msg);
		toDelete.put(destruction, m);
		return m;
	}
	public static Message timedEmbededInstant(TextChannel ch,MessageEmbed msg,long destruction)
	{
		Message m=ch.sendMessageEmbeds(msg).complete();
		toDelete.put(destruction, m);
		return m;
	}
	public static ITickable createTickable()
	{
		return new ITickable(new Runnable() {

			@Override
			public void run() {
				Map<Long, Message> mp = new HashMap<Long, Message>();
				mp.putAll(toDelete);
				Long now =OffsetDateTime.now().toEpochSecond();
				for(Long t : mp.keySet())
				{
					if(now>=t)
					{
						mp.get(t).delete().queue();
						toDelete.remove(t);
					}

				}
			}});
	}
}
