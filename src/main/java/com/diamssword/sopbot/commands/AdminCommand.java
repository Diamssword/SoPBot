package com.diamssword.sopbot.commands;

import com.diamssword.sopbot.Localise;
import com.diamssword.sopbot.commands.SlashCommandEvent.Type;
import com.diamssword.sopbot.managers.CommandPerm;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.managers.GuildsManager;
import com.diamssword.sopbot.utils.EmbedUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class AdminCommand implements ISlashCommand{
	@Override
	public void init() {

	}

	public CommandPerm defaultPerm()
	{
		return new CommandPerm().setAdminOnly();
	}

	@Override
	public void onCommand(SlashCommandEvent event) {
		GuildParameters g =event.getGParam();
		if(event.isSubCommand("language"))
		{
			g.setDefaultLang(event.getArgument("value"));
			event.sendResponse(Type.success, "cmd.use.admin.language.set", 5);
			GuildsManager.save(event.getGuild());
			
		}
		else if(event.isSubCommand("perms"))
		{
			String cmdS =event.getArgument("command");
		
				ISlashCommand cm=MyCommands.find(cmdS);
				
				if(cm.perm(event.getAuthor(), event.getChannel()).isAllowed(event.getAuthor(), event.getChannel()))
				{
					String type=event.getArgument("perm");
					if(type.equals("admin"))
					{
						CommandPerm p =new CommandPerm().setAdminOnly();
						if(p.isAllowed(event.getAuthor(), event.getChannel()))
						{
							g.CommandsPerms.put(type,p );
							event.sendResponse(Type.success, "cmd.use.admin.perms.set", -2,cmdS, g.translate("generic.perms.admin"));
						}
						else
							event.sendResponse(Type.error, "cmd.use.admin.perms.denied", -2,cmdS);
					}
					else if(type.equals("owner"))
					{
						CommandPerm p =new CommandPerm().setOwnerOnly();

						if(p.isAllowed(event.getAuthor(), event.getChannel()))
						{

							g.CommandsPerms.put(type,p );
							event.sendResponse(Type.success, "cmd.use.admin.perms.set", -2,cmdS, g.translate("generic.perms.owner"));					
						}
						else
							event.sendResponse(Type.error, "cmd.use.admin.perms.denied", -2,cmdS);
					}
					else if(type.equals("all"))
					{
						CommandPerm p =new CommandPerm();
						if(p.isAllowed(event.getAuthor(), event.getChannel()))
						{
							g.CommandsPerms.put(type,p );
							event.sendResponse(Type.success, "cmd.use.admin.perms.set", -2,cmdS, g.translate("generic.perms.all"));
						}
						else
							event.sendResponse(Type.error, "cmd.use.admin.perms.denied", -2,cmdS);
					}
					else if(type.equals("reset"))
					{
						g.CommandsPerms.remove(cmdS);
						event.sendResponse(Type.success, "cmd.use.admin.perms.reset", -2,cmdS, g.translate("generic.perms.reset"));

					}
					g.save();
				}
				else
					event.sendResponse(Type.error, "cmd.use.admin.perms.denied", -2,cmdS);
			
		}
		else if(event.isSubCommand("tokens"))
		{
			if(event.getArgument("type").equals("info"))
			{
				String[] title=new String[] {
					"cmd.admin.token.type.google"
				};
				String[] descs=new String[] {
						"cmd.use.admin.type.google.desc"
					};
				event.prepareReply(EmbedUtil.list(event.getGParam().defaultLang(),null, "cmd.use.admin.tokens.info.title", title, descs, null, false)).setEphemeral(true).queue();;
			}
			else
			{
				String type=event.getArgument("type");
				String tk =event.getArgument("token");
				System.out.println(tk);
				if(tk != null && tk.length()>1)
				{
					event.getGParam().tokens.put(type, tk);
					event.getGParam().save();
					event.prepareReply(event.getGParam().translate("cmd.use.admin.tokens.set", type,tk)).setEphemeral(true).queue();;
				}
				else
				{

					event.getGParam().tokens.remove(type);
					event.getGParam().save();
					event.prepareReply(event.getGParam().translate("cmd.use.admin.tokens.del", type)).setEphemeral(true).queue();;
				}
					
			}
		}
	}

	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr = new TranslateInstance(p,name());

		OptionData d =new OptionData(OptionType.STRING, "value", tr.translate("language.value"), true);
		OptionData d1 =new OptionData(OptionType.STRING, "command", tr.translate("perms.command"), true);
		OptionData d2 =new OptionData(OptionType.STRING, "perm", tr.translate("perms.perm"), true);
		OptionData d3 =new OptionData(OptionType.STRING, "type", tr.translate("tokens.type"), true);
		OptionData d4 =new OptionData(OptionType.STRING, "token", tr.translate("tokens.token"), false);
		d3.addChoice(tr.translate("tokens.type.info"),"info");
		d3.addChoice(tr.translate("tokens.type.google"),"google");
		
		d2.addChoice(tr.translate("generic.perms.reset"), "reset");
		d2.addChoice(tr.translate("generic.perms.admin"), "admin");
		d2.addChoice(tr.translate("generic.perms.owner"), "owner");
		d2.addChoice(tr.translate("generic.perms.all"), "all");
		for(String[] l :Localise.getAllLangues())
		{
			d.addChoice(l[1], l[0]);
		}
			for(ISlashCommand k : MyCommands.slashCommands)
			{
				d1.addChoice(k.name(), k.name());
			}
		return Commands.slash(name(),tr.translate("desc"))

				.addSubcommands(new SubcommandData("language", tr.translate("language.desc")).addOptions(d),
						new SubcommandData("perms", tr.translate("perms.desc")).addOptions(d1,d2),
						new SubcommandData("tokens", tr.translate("tokens.desc")).addOptions(d3,d4));
	}

	@Override
	public String name() {
		return "admin";
	}
}
