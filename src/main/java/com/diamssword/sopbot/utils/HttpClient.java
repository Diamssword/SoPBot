package com.diamssword.sopbot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.diamssword.sopbot.jsons.YTRequestJson;
import com.diamssword.sopbot.jsons.YTVideoJson;
import com.diamssword.sopbot.managers.GuildParameters;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class HttpClient {

	public static String KEY = "";
	private final static String USER_AGENT = "Mozilla/5.0";

	public static void init()
	{
		LoadUtils.askToken("google", new Function<String,Boolean>(){

			@Override
			public Boolean apply(String t) {
				KEY=t;
				return true;
			}

		});
	}
	public static YTVideoJson[] searchvid(int maxRes,String research,@Nullable GuildParameters param) throws IOException
	{
		String key=KEY;

		if(param != null)
		{
			key=param.tokens.get("google");
			if(key == null)
				key=KEY;
		}
		String stringUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults="+maxRes+"&order=relevance&q="+research.replace(" ", "%20")+"&type=video&key="+key;
		URL url = new URL(stringUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);


		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		try {
			YTRequestJson req = new GsonBuilder().setPrettyPrinting().create().fromJson(response.toString(), YTRequestJson.class);
			return req.getasVideo();
		}catch(JsonSyntaxException e)
		{e.printStackTrace();

		}
		return new YTVideoJson[0];
		//	System.out.println(response.toString());


	}
}