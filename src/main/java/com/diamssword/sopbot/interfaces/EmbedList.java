package com.diamssword.sopbot.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.diamssword.sopbot.interfaces.ButtonsManager.ButtonWarper;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class EmbedList {
	public static Emoji RIGHT = Emoji.fromUnicode("U+27A1");
	public static Emoji LEFT = Emoji.fromUnicode("U+2B05");
	public final List<TitleDesc> list= new ArrayList<TitleDesc>();
	public int position=0;
	public EmbedBuilder builder;
	public EmbedList(EmbedBuilder embed, List<String> titles, @Nullable List<String> descs)
	{
		this.builder=embed;
		this.addToList(titles, descs);
	}
	public EmbedList(EmbedBuilder embed,List<TitleDesc> l)
	{
		this.builder=embed;
		this.list.addAll(l);
	}
	public void addToList(List<String> titles,List<String> descs)
	{
		for(int i=0;i<titles.size();i++)
		{
			TitleDesc d =new TitleDesc(titles.get(i));
			if(descs != null &&i<descs.size())
				d.setDesc(descs.get(i));
			list.add(d);
		}
		
	}
	public MessageEmbed go(int position)
	{
		if(position>=list.size())
			position = list.size()-25;
		if(position <0)
			position=0;
		builder.clearFields();
		List<TitleDesc> d=list.subList(position, Math.min(list.size(), position+24));
		for(TitleDesc l:d)
		{
			builder.addField(l.getTitle(), l.getDesc(), false);	
		}
		this.position=position;
		
		return builder.build();
		
	}
	public MessageEmbed next()
	{
		return go(this.position+25);
	}
	public MessageEmbed prev()
	{
		return go(this.position-25);
	}
	public EmbedBuilder getEmbed()
	{
		return this.builder;
	}
	private Consumer<ButtonInteractionEvent> btEvent= new  Consumer<ButtonInteractionEvent>() {

		@Override
		public void accept(ButtonInteractionEvent t) {
			if(t.getButton().getEmoji() .equals(LEFT))
			{
				t.editMessageEmbeds(EmbedList.this.prev()).queue();
			}
			else
			{
				t.editMessageEmbeds(EmbedList.this.next()).queue();
			}
		}};
	public Button[] getButtons(ReplyCallbackAction action,ButtonWarper... additionals)
	{
		ButtonWarper[] warp=new ButtonWarper[2+additionals.length];
		warp[0]=new ButtonWarper(Button.primary("left", LEFT),btEvent);
		for(int k=0;k<additionals.length;k++)
		{
			warp[k+1]=additionals[k];
		}
		
		warp[warp.length-1]=new ButtonWarper(Button.primary("right", RIGHT),btEvent);
		
		return ButtonsManager.add(action, warp);
	}
	public static class TitleDesc
	{
		private String title;
		private String desc;
		
		public TitleDesc(String title, String desc)
		{
			this.title=title;
			this.desc=desc;
		}
		public TitleDesc(String title)
		{
			this.title=title;
			this.desc=null;
		}
		public void setDesc(String desc)
		{
			this.desc=desc;
		}
		public void  setTitle(String title)
		{
			this.title=title;
		}
		public String getTitle()
		{
			if(this.title.length()>256)
				return this.title.substring(0,253)+"...";
			return this.title;
		}
		public String getDesc()
		{
			if(this.desc == null)
				return "";
			if(this.desc.length()>1024 )
				return this.desc.substring(0,1021)+"...";
			return desc;
		}
	}
}
