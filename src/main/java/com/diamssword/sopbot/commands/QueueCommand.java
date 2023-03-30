package com.diamssword.sopbot.commands;

import com.diamssword.sopbot.managers.GuildParameters;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class QueueCommand implements ISlashCommand{
	@Override
	public void onCommand(SlashCommandEvent event) {
/*
		OptionMapping mp=event.getOption("goto");
		if(mp != null)
		{
			int pos=(int) mp.getAsDouble()-1;
			QueuedMusic q =MusicCommand.queues.get(event.getGuild().getId());
			if(q != null)
			{
				q.goTo(pos);
				event.prepareReply(event.getGParam().translate("cmd.use.queue.goto")).setEphemeral(true).queue();
			}
		}
		else
			generateResponse(event.prepareReply("** **"),event.getGuild());


	}
	public void generateResponse(ReplyAction act,Guild g)
	{
		QueuedMusic q =MusicCommand.queues.get(g.getId());
		if(q != null)
		{
			EmbedList ls =new EmbedList(new EmbedBuilder().setTitle(GuildsManager.get(g).translate("cmd.use.queue.title")),q.getTracksFormated());
			act.addEmbeds(ls.go(q.position()));
			ls.getButtons(act);
			act.queue();
		}
		else
		{
			act.addEmbeds(new EmbedBuilder().setTitle(GuildsManager.get(g).translate("cmd.use.queue.title")).setDescription("...").build()).queue();
		}*/
	}
	@Override
	public void init() {

	}
	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr = new TranslateInstance(p,name());
		return Commands.slash(name(),tr.translate("desc")).addOption(OptionType.NUMBER, "goto", tr.translate("goto"));
	}
	@Override
	public String name() {
		return "queue";
	}


}
