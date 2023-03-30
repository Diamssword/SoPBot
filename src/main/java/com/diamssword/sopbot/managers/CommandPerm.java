package com.diamssword.sopbot.managers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class CommandPerm {

	private String roleID;
	public static String ADMIN = "ADMIN";
	public static String OWNER = "OWNER";
	public static String MP_ONLY = "MPONLY";
	public static String NONE= "NONE";
	/**
	 * set the permission level to a role: you need this role or above to use the command
	 */
	public CommandPerm(String roleID)
	{
		this.roleID = roleID;
	}

	/**
	 * set perm to none : you need no permissions to use this command (except permission to write in channel of course)
	 */
	public CommandPerm()
	{
		this.roleID = NONE;
	}
	/**
	 * set the permission level to a role: you need this role or above to use the command
	 * @return the same object for convenience
	 */
	public CommandPerm setRole(String roleID)
	{
		this.roleID = roleID;
		return this;
	}
	/**
	 * set the permission level to admin only : you need the administrator permission to use the command
	 * @return the same object for convenience
	 */
	public CommandPerm setAdminOnly()
	{
		this.roleID = ADMIN;
		return this;
	}
	/**
	 * set the permission level to owner only : you need to be the owner of the guild to use the command
	 * @return the same object for convenience
	 */
	public CommandPerm setOwnerOnly()
	{
		this.roleID = OWNER;
		return this;
	}
	/**
	 * set the permission level to MP only : the command can be used only in private message channel
	 * @return the same object for convenience
	 */
	public CommandPerm setMPOnly()
	{
		this.roleID = MP_ONLY;
		return this;
	}
	/**
	 * set the permission level to none : you need no permissions to use this command (except permission to write in channel of course)
	 * @return the same object for convenience
	 */
	public CommandPerm setNone()
	{
		this.roleID = NONE;
		return this;
	}

	public boolean isAllowed(User user, MessageChannel channel)
	{
		if(this.roleID.equals(NONE))
			return true;
		if(channel.getType() == ChannelType.PRIVATE )
		{
			return true;
		}
		else if(channel.getType() == ChannelType.TEXT )
		{
			
			Member memb = ((TextChannel)channel).getGuild().retrieveMember(user).complete();
			if(memb.isOwner() && !this.roleID.equals(MP_ONLY))
				return true;
			if(this.roleID.equals(ADMIN))
				return memb.hasPermission(Permission.ADMINISTRATOR);
			if(this.roleID.equals(OWNER))
				return memb.isOwner();
			if(this.roleID.equals(MP_ONLY))
				return memb.isOwner();
			try {
				Role r = memb.getGuild().getRoleById(this.roleID);
				if(r != null)
				{
					int i =memb.getGuild().getRoles().indexOf(r);
					for(int i1=0;i1<=i;i1++)
					{
						Role r1 = memb.getGuild().getRoles().get(i1);
						if(memb.getRoles().contains(r1))
						{
							return true;
						}
					}

				}
			}catch(NumberFormatException e)
			{
				return false;
			}
		}
		return false;
	}

	public String getReadableName(Guild g)
	{
		if(this.roleID.equals(NONE))
			return "NONE";
		if(this.roleID.equals(ADMIN))
			return "ADMIN";
		if(this.roleID.equals(MP_ONLY))
			return "PRIVATE MESSAGE ONLY";
		if(this.roleID.equals(OWNER))
			return "SERVER OWNER";
		Role r = g.getRoleById(this.roleID);
		if(r != null)
		{
			return "@"+r.getName();
		}
		return "NONE";
	}
}
