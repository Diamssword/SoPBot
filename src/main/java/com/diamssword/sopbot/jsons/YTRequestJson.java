package com.diamssword.sopbot.jsons;

public class YTRequestJson {
	 public String kind;
	 public String regionCode;
	 public PageInfo pageInfo;
	public YTVideoJson[] items;
	
	public YTVideoJson[] getasVideo()
	{
		return (YTVideoJson[]) items;
	}
public static class PageInfo
{
	public int totalResults;
	public int resultsPerPage;
}
}
