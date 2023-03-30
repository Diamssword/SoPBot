package com.diamssword.sopbot.jsons;

public class YTVideoJson {
	public ID id;
   public Snippet snippet;
    
   public static class Snippet
    {
	   public String title;
	   public String channelTitle;
    }
    public static class ID
    {
	  public String kind;
	  public String videoId;
    }
}
