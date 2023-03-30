package com.diamssword.sopbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.entities.AbstractMessage;

public class SlashCmdWarper extends CommandEvent{
private SlashCommandInteractionEvent event;
	public SlashCmdWarper(SlashCommandInteractionEvent cmd) {
		super(cmd.getMember().getUser(), cmd.getChannel(), new AbstractMessage(rebuildMessage(cmd), "", false) {

			@Override
			public MessageActivity getActivity() {
				return null;
			}

			@Override
			public long getIdLong() {
				return cmd.getCommandIdLong();
			}

			@Override
			protected void unsupported() {
				
				
			}

			@Override
			public long getApplicationIdLong() {
				return cmd.getCommandIdLong(); //TODO check si ça pose pas de souci
			}});
		this.event=cmd;

	}
	private static String rebuildMessage(SlashCommandInteractionEvent cmd)
	{
		String res= "SoP "+cmd.getName();
		for(OptionMapping k : cmd.getOptions())
		{
			res= res+" "+k.getAsString();
		}
		return res;
	}
	@Override 
	public void reply(MessageEmbed m)
	{
		event.replyEmbeds(m).queue();
	}
	@Override 
	public void reply(String m)
	{
		event.reply(m).queue();;
	}
	@Override 
	public Message replyDirect(String m)
	{
		event.reply(((char)3000)+"").complete();
		return event.getChannel().sendMessage(m).complete();
	}
	@Override 
	public Message replyDirect(MessageEmbed m)
	{
		
	event.reply(((char)Integer.parseInt("200B",16))+"").complete();
		return event.getChannel().sendMessageEmbeds(m).complete();
	}

}
