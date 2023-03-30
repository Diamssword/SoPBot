package com.diamssword.sopbot.commands;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.diamssword.sopbot.commands.SlashCommandEvent.Type;
import com.diamssword.sopbot.managers.CommandPerm;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.storage.JsonGuildStorage;
import com.diamssword.sopbot.storage.Storage;
import com.diamssword.sopbot.utils.EmbedUtil;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;



public class PointsCommand implements ISlashCommand{
	public static JsonGuildStorage<PointsSavable> storage = new JsonGuildStorage<PointsSavable>("commands/points.json", PointsSavable.class);
	@Override
	public CommandPerm SdefaultPerm()
	{
		return new CommandPerm().setAdminOnly();
	}

	@Override
	public void onCommand(SlashCommandEvent event) {

		if(event.isSubCommand("add"))
		{
			PointsSavable ob =storage.get(event.getGuild().getId()).get();

			Member m=event.getOption("user").getAsMember();
			if(event.hasOption("points"))
			{
				String reason =event.getArgument("reason");
				int res = addScore(ob,m.getId(),(int) event.getOption("points").getAsDouble(), reason,event.getTimeCreated().toLocalDateTime());
				appliGrade(event,res,m);
				storage.save(event.getGuild());
				event.sendResponse(Type.success, "cmd.use.points.set", -1,m.getAsMention(), res);
			}
			else
			{
				 Integer i=ob.Users.get(m.getUser().getId());
				 if(i==null)
					 i=0;
				event.sendResponse(Type.success, "cmd.use.points.get", -1,m.getAsMention(),(int)i);
			}

		}
		else if(event.isSubCommand("role"))
		{

			PointsSavable ob =storage.get(event.getGuild().getId()).get();
			if(event.getArgument("action").equals("get"))
			{
				String res= "";
				for(Integer nb :ob.Ranks.keySet())
				{
					String id =ob.Ranks.get(nb);
					if(id != null)
					{
						Role r =event.getGuild().getRoleById(id);

						res= res+"\n"+nb+" => "+r.getAsMention();
					}
				}
				event.sendResponse(Type.success, res, -2);
				return;
			}
			else if(event.getArgument("action").equals("set"))
			{
				if(event.hasOption("points") ||event.hasOption("role"))
				{

					Role r = event.getOption("role").getAsRole();
					int nb = (int) event.getOption("points").getAsDouble();
					ob.Ranks.put(nb,r.getId());
					storage.save(event.getGuild());
					event.sendResponse(Type.success, "cmd.use.points.setGrade", -2, r.getAsMention(),nb);
				}
				else
					event.sendResponse(Type.error, "cmd.use.points.error.set", -2);
				return;

			}
			else if(event.getArgument("action").equals("remove"))
			{

				if(event.hasOption("points"))
				{
					int nzw=(int) event.getOption("points").getAsDouble();

					ob.Ranks.remove(nzw);
					storage.save(event.getGuild());
					event.sendResponse(Type.success, "cmd.use.points.delGrade", -2,nzw);
					return;
				}else
					event.sendResponse(Type.error, "cmd.use.points.error.remove", -2);
				{

				}

			}	
		}
		else if(event.isSubCommand("history"))
		{
			PointsSavable ob =storage.get(event.getGuild().getId()).get();
				Member m= event.getOption("member").getAsMember();
				Integer score =ob.Users.get(m.getId());
				if(score==null)
					score=0;
				List<String> log =ob.Logs.get(m.getId());
				if(log == null)
					log = new ArrayList<String>();
				String logs= "";
				for(String k :log )
				{
					logs=logs+"\n"+k;
				}
				OffsetDateTime tm =event.getTimeCreated().plusMinutes(10);
				MessageEmbed msg=EmbedUtil.basic(event.getGParam().defaultLang(), null,null, m.getEffectiveName()+" : "+score+" points", logs, null, new Color(16098851), tm.toLocalDateTime(), null);
				event.reply("** **");
				EmbedUtil.timedEmbeded(event.getAsTextChannel(), msg,tm.toEpochSecond());
		}

	}
	public void appliGrade(SlashCommandEvent e, int points,Member m)
	{
		Map<Integer,String> str=storage.get(e.getGuild()).get().Ranks;
		for(Integer k:str.keySet())
		{
			if(points >=k.intValue())
			{
				try {
				Role r =e.getGuild().getRoleById(str.get(k));
				e.getGuild().addRoleToMember(m, r).queue();
				}catch(net.dv8tion.jda.api.exceptions.HierarchyException ex)
				{
					
				}
			}
		}
	}
	@Override
	public void init() {
		Storage.list.add(storage);
	}
	public static class PointsSavable
	{
		Map<String,Integer> Users = new HashMap<String,Integer>();
		Map<String,List<String>> Logs = new HashMap<String,List<String>>();
		Map<Integer,String> Ranks = new HashMap<Integer,String>();
	}
	public static int addScore(PointsSavable storage,String user,int score, @Nullable String reason,@Nullable LocalDateTime time)
	{
		String res = "unknown";
		if(reason != null)
		{

			res = reason;
			if(score >0)
				res = "+"+score+"> "+res;
			else
				res = score+"> "+res;
			if(time != null)
			{
				res=time.getDayOfMonth()+"/"+time.getMonthValue()+"/"+(time.getYear()-2000)+": **"+res+"**";
			}
			List<String> log=storage.Logs.get(user);
			if( log== null)
				storage.Logs.put(user, log = new ArrayList<String>());
			log.add(res);
		}
		Integer nb = storage.Users.get(user);
		if(nb == null)
			nb=0;
		nb = nb+score;
		storage.Users.put(user, nb);
		return nb;
	}



	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {
		TranslateInstance tr = new TranslateInstance(p,name());
		OptionData d =new OptionData(OptionType.STRING, "action", tr.translate("action"), true).addChoice(tr.translate("action.set"), "set").addChoice(tr.translate("action.get"), "get").addChoice(tr.translate("action.remove"), "remove");
		return Commands.slash(name(),tr.translate("desc"))
				
				.addSubcommands(new SubcommandData("role", tr.translate("role.desc")).addOptions(d).addOption(OptionType.ROLE, "role",tr.translate("role.role.desc"),false)
				.addOption(OptionType.INTEGER, "points", tr.translate("role.points.desc")),
				
				new SubcommandData("add", tr.translate("add")).addOption(OptionType.USER, "user", tr.translate("add.user"),true)
				.addOption(OptionType.INTEGER, "points", tr.translate("add.points"),false).addOption(OptionType.STRING, "reason",  tr.translate("add.reason")),
				new SubcommandData("history", tr.translate("history")).addOption(OptionType.USER, "member", tr.translate("history.member"),true)
						);
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "points";
	}


}
