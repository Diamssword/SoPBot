package com.diamssword.sopbot.events;

import java.lang.reflect.InvocationTargetException;

import com.diamssword.sopbot.commands.ISlashCommand;
import com.diamssword.sopbot.commands.MyCommands;
import com.diamssword.sopbot.interfaces.ButtonsManager;
import com.diamssword.sopbot.interfaces.ButtonsManager.ButtonTimedWarper;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionsEventHandler  extends ListenerAdapter{
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{

		ISlashCommand cmd=MyCommands.find(event.getName());
			if(cmd != null)
			{
				
				if(cmd.perm(event.getUser(), event.getChannel()).isAllowed(event.getUser(), event.getChannel()))
				{
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {

									cmd.Sinstantiate().onCommand(new com.diamssword.sopbot.commands.SlashCommandEvent(event));

								} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
										| InvocationTargetException e) {
									System.err.println("Instantiation failed for "+cmd);
									cmd.onCommand(new com.diamssword.sopbot.commands.SlashCommandEvent(event));
								}
							}}).start();
				}
				else
				{
					event.reply("Vous n'avez pas la permission d'utiliser cette commande").setEphemeral(true).queue();
				}

			}
			else
			{
				event.reply("Cette commande n'existe plus!").setEphemeral(true).queue();
			}
		
	}
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		ButtonTimedWarper cons=ButtonsManager.buttons.get(event.getButton().getId());
		if(cons != null)
		{
			cons.consumer.accept(event);

			cons.updatedAt = System.currentTimeMillis();



		}
		else
		{
			event.editButton(event.getButton().asDisabled()).queue();
		}
	}
}
