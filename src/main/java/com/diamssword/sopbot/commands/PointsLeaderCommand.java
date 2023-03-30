package com.diamssword.sopbot.commands;

import java.awt.Color;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.diamssword.sopbot.commands.PointsCommand.PointsSavable;
import com.diamssword.sopbot.managers.GuildParameters;
import com.diamssword.sopbot.storage.JsonGuildStorage;
import com.diamssword.sopbot.utils.EmbedUtil;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PointsLeaderCommand implements ISlashCommand{

	public static JsonGuildStorage<PointsSavable> storage =PointsCommand.storage;

	public void appliGrade(CommandEvent e, int points,Member m)
	{
		Map<Integer,String> str=storage.get(e.getGuild()).get().Ranks;
		for(Integer k:str.keySet())
		{
			if(points >=k.intValue())
			{
				Role r =e.getGuild().getRoleById(str.get(k));
				e.getGuild().addRoleToMember(m, r).queue();
			}
		}
	}
	@Override
	public void init() {
	}
 
	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(comparingByValue());

		Map<K, V> result = new LinkedHashMap<>();
		for (Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
	@SuppressWarnings("unchecked")
	public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
		return (Comparator<Map.Entry<K, V>> & Serializable)
				(c1, c2) -> c2.getValue().compareTo(c1.getValue());
	}

	@Override
	public void onCommand(SlashCommandEvent event) {
		
			PointsSavable ob =storage.get(event.getGuild().getId()).get();
			Map<String, Integer> mp1=sortByValue(ob.Users);
			String users = "";
			boolean first=true;
			for(String user: mp1.keySet())
			{
				if(first)
				{
					users=users+"\n:balloon:<@"+user+"> => "+mp1.get(user)+" points:balloon:\n";
					first = false;
				}
				else
					users=users+"\n:small_blue_diamond:<@"+user+"> => "+mp1.get(user)+" points\n";
				if(users.length()>2000)
				{
					users= users.substring(0, 1997)+"...";
					break;
				}
			}
			OffsetDateTime tm =event.getTimeCreated().plusMinutes(10);

			MessageEmbed msg =EmbedUtil.basic(event.getGParam().defaultLang(), null, null, "cmd.leaderboard.title",users, null,new Color(16098851), tm.toLocalDateTime(), null);
			event.reply("** **");
			EmbedUtil.timedEmbeded(event.getAsTextChannel(), msg,tm.toEpochSecond());
		
	}

	@Override
	public SlashCommandData command(Guild g, GuildParameters p) {

		TranslateInstance tr = new TranslateInstance(p,name());
		return Commands.slash(name(), tr.translate("desc"));
	}
	@Override
	public String name() {
		return "leaderboard";
	}
}
