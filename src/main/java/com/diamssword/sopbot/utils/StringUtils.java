package com.diamssword.sopbot.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class StringUtils {

	public static String removeSpaces(String s)
	{
		return s.trim().replaceAll(" +", " ");
	}

	public static int getMaxFittingFontSize(Graphics g, Font font, String string, int width, int height){
		int minSize = 0;
		int maxSize = 288;
		int curSize = font.getSize();

		while (maxSize - minSize > 2){
			FontMetrics fm = g.getFontMetrics(new Font(font.getName(), font.getStyle(), curSize));
			int fontWidth = fm.stringWidth(string);
			int fontHeight = fm.getLeading() + fm.getMaxAscent() + fm.getMaxDescent();

			if ((fontWidth > width) || (fontHeight > height)){
				maxSize = curSize;
				curSize = (maxSize + minSize) / 2;
			}
			else{
				minSize = curSize;
				curSize = (minSize + maxSize) / 2;
			}
		}

		return curSize;
	}
	public static String weldArgs(String[] args, int startIndex, int endIndex)
	{
		String res="";
		if(startIndex<0)
			startIndex=0;
		if(endIndex <0)
			endIndex = args.length;
		for(int i = startIndex; i<endIndex && i<args.length;i++)
		{
			res =res +" "+args[i];
		}
		return res.replaceFirst(" ", "");
	}
	public static void SendLongMessage(TextChannel channel,String message)
	{
	if(message.length() <=2000)
		channel.sendMessage(message).queue();
	else
	{
		String[] parts = message.split("\n");
		List<String> ls = new ArrayList<String>();
		String res="";
		for(int i=0;i<parts.length;i++)
		{
		String temp = res+parts[i];
		if(temp.length() <=2000)
			res = res+parts[i];
		else
		{
			ls.add(res);
			res= parts[i];
		}
		
		}
		for(String str : ls)
		{
			channel.sendMessage(str).queue();
		}
		
	}
	}
private static Random rand= new Random();
	public static String getWeirdName()
	{
	int t =rand.nextInt(3);
	String res="";
	for(int i=0;i<=t;i++)
	{
		res = res +" "+weirds[rand.nextInt(weirds.length)];
	}
	return res.replaceFirst(" ", "");
	}
	private static String[] weirds=new String[] {"verrucose",
			"broadcloth",
			"oxytocic",
			"stanchion",
			"ouranomancy",
			"peotomy",
			"pluviograph",
			"philodox",
			"merism",
			"mulse",
			"phytology",
			"furunculoid",
			"tachism",
			"huemul",
			"tetralemma",
			"veitchberry",
			"systematology",
			"caprine",
			"blucher",
			"or",
			"brontology",
			"starbolins",
			"radiometer",
			"aptronym",
			"philogyny",
			"serratic",
			"serpette",
			"tuism",
			"mixotrophic",
			"spuria",
			"gambrel",
			"taw",
			"braird",
			"sophomoric",
			"hamiform",
			"nadir",
			"lautitious",
			"egestion",
			"lampadephore",
			"eutaxy",
			"meristic",
			"nautiliform",
			"aiué"};
}
