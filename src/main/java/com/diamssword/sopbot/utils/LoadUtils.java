package com.diamssword.sopbot.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import com.diamssword.sopbot.SOP;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

public class LoadUtils {


	/**
	 * load an image in the ressource folder
	 * @param path relative to the ressources folder
	 * @return
	 */
	public static BufferedImage loadImg(String path)
	{
		InputStream url = SOP.class.getClassLoader().getResourceAsStream(path);
		if(url != null)
		{
			try {
				return ImageIO.read(url);
			} catch (IOException e) {
				e.printStackTrace();
				return new BufferedImage(40, 40, 1);
			}
		}
		return new BufferedImage(40, 40, 1);
	}
	public static String[] loadLines(String path)
	{
		List<String> ls = new ArrayList<String>();

		InputStream url = SOP.class.getClassLoader().getResourceAsStream(path);
		if(url != null)
		{
			Scanner s = new Scanner(url);
			while(s.hasNextLine())
			{
				ls.add(s.nextLine());
			}
			s.close();
		}
		else
		{
			System.err.println("ERROR loadLines()! Can't find "+ path);
		}
		return ls.toArray(new String[0]);
	}
	private static BufferedReader  InputStreamReader = new BufferedReader( new InputStreamReader(System.in));
	public static void askToken(String name,Function<String,Boolean> success)
	{
		String tk=SOP.Storage.getGlobal().tokens.get(name);
		if(tk != null)
		{
			if(success.apply(tk))
				return;
		}
		try {
			System.out.println("Il manque le token pour "+name+"! Entrez le ici:");
			String col = InputStreamReader.readLine();
			if(!success.apply(col))
			{
				System.out.println("Le token entré pour"+name+"N'est pas bon, dernière chance:");
				col = InputStreamReader.readLine();
				if(success.apply(col))
				{
					SOP.Storage.getGlobal().tokens.put(name, col);
				}
			}
			else
			{
				SOP.Storage.getGlobal().tokens.put(name, col);
				
			}
			SOP.Storage.saveGlobal();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/*public static String[] getListOfRessource(String pathToExistingFileInDir)
	{
		URL url = SOP.class.getResource("/"+pathToExistingFileInDir);
		System.out.println(url.getProtocol());
		 String path =url.toExternalForm().replaceFirst(url.getProtocol()+":/", "");
		 
			try {
				System.out.println(new File(url.toURI()));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				File[] f=new File(path).getParentFile().listFiles();

				System.out.println(f);
				String[] res= new String[f.length];
		 for(int i=0;i<f.length;i++)
		 {
			 if(f[i].isFile())
				 res[i]= f[i].getName();
		 }
		 return res;	
	
			
	}*/
	public static Map<String,String> loadlang(String path)
	{
		Map<String,String> ls = new HashMap<String,String>();
		InputStream url = SOP.class.getClassLoader().getResourceAsStream(path);
		if(url != null)
		{
			try {
				InputStreamReader f= new InputStreamReader(url, "UTF-8");
				Scanner s =new Scanner(new BufferedReader(f));
				while(s.hasNextLine())
				{
					String[] sp = s.nextLine().trim().split("="); 
					if(sp.length>1)
					{
						ls.put(sp[0],sp[1]);
					}
				}
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("ERROR loadLang() ! Can't find "+ path);
		}
		return ls;
	}
	/**
	 * shortcut methods to send a file to a text channel
	 * @param channel the channel to send a file
	 * @param data the file itself in raw byte array
	 * @param name the name of the file (for exemple 'image.png' will display it as an image on discord)
	 * @param text an additional text message to send
	 */
	public static void sendFile(MessageChannel channel, byte[] data, String name, @Nullable String text)
	{
		if(text == null)
			text="";
		
		channel.sendMessage(text != null? text:"").setFiles(FileUpload.fromData(data, name)).queue();
	}
	
	/**
	 * Fetch the text content of this URL (mainly used to get api datas (for exemple in json format)
	 * @param url
	 * @return a string form of the returneds datas
	 */
	public static String urlToString(URL url)
	{
		
		String res="";
		try {
			HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
			try {

				openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				openConnection.setRequestMethod("GET");
				openConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
				openConnection.connect();

			} catch (Exception e) {
				System.out.println("Couldn't create a connection to the link, please recheck the link.");
				e.printStackTrace();
			}
			Scanner s = new Scanner(openConnection.getInputStream());
			
			while(s.hasNextLine())
				res = res+s.nextLine();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
		
	}
	
	/**
	 * Download a BufferedImage from an URL 
	 * @param url
	 * @return a bufferedImage or null if an error as occured
	 */
	public static BufferedImage dlImage(URL url)
	{
		try {

			URLConnection openConnection = url.openConnection();
			try {

				openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				openConnection.connect();

			} catch (Exception e) {
				e.printStackTrace();
			}

				return ImageIO.read(new BufferedInputStream(openConnection.getInputStream()));
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
