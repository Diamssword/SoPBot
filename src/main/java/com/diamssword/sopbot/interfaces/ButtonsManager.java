package com.diamssword.sopbot.interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import com.diamssword.sopbot.ticking.ITickable;
import com.diamssword.sopbot.ticking.Tickable;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ButtonsManager {
	private static final int MAXLIFESPAN= 60*60*1000;
	static 
	{
		Tickable.registerTickable(new ITickable(new Runnable()   {
			@Override
			public void run() {
				for(Iterator<Map.Entry<String, ButtonTimedWarper>> it = buttons.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, ButtonTimedWarper> entry = it.next();
					if(System.currentTimeMillis()  >entry.getValue().updatedAt +MAXLIFESPAN) {
						it.remove();
					}
				}
			}}), 60000);
	}
	public static Map<String,ButtonTimedWarper> buttons = new HashMap<String,ButtonTimedWarper>();
	public static Button add(ReplyCallbackAction action,Button button,Consumer<ButtonInteractionEvent> consumer)
	{
		String id=button.getId()+System.currentTimeMillis();
		button= button.withId(id);
		buttons.put(id, new ButtonTimedWarper(consumer));
		action.addActionRow(button);
		return button;
	}
	public static Button[] add(ReplyCallbackAction action,ButtonWarper... button)
	{
		Button[] bt=new Button[button.length];
		for(int k=0;k<button.length;k++ )
		{
			String id=button[k].button.getId()+System.currentTimeMillis();
			bt[k] = button[k].button.withId(id);
			buttons.put(id,new ButtonTimedWarper(button[k].consumer));
		}
		action.addActionRow(bt);
		return bt;
	}
	public static Button[] add(MessageCreateAction  action,ButtonWarper... button)
	{
		Button[] bt=new Button[button.length];
		for(int k=0;k<button.length;k++ )
		{
			String id=button[k].button.getId()+System.currentTimeMillis();
			bt[k] = button[k].button.withId(id);
			buttons.put(id,new ButtonTimedWarper(button[k].consumer));
		}
		action.addActionRow(bt);
		return bt;
	}
	public static Button[] add(MessageEditAction action,ButtonWarper... button)
	{
		Button[] bt=new Button[button.length];
		for(int k=0;k<button.length;k++ )
		{
			String id=button[k].button.getId()+System.currentTimeMillis();
			bt[k] = button[k].button.withId(id);
			buttons.put(id,new ButtonTimedWarper(button[k].consumer));
		}
		action.setActionRow(bt);
		return bt;
	}
	public static void remove(String buttonid)
	{
		buttons.remove(buttonid);
	}
	public static void remove(Button button)
	{
		remove(button.getId());
	}
	public static void remove(Button... button)
	{
		for(Button k: button)
		remove(k.getId());
	}
	public static class ButtonWarper
	{
		public Consumer<ButtonInteractionEvent> consumer;
		public Button button;
		public ButtonWarper(Button button,Consumer<ButtonInteractionEvent> consumer) {
			this.consumer = consumer;
			this.button = button;
		}

	}
	public static class ButtonTimedWarper
	{
		public final Consumer<ButtonInteractionEvent> consumer;
		public Long updatedAt;
		public ButtonTimedWarper(Consumer<ButtonInteractionEvent> consumer) {
			this.consumer = consumer;
			this.updatedAt=System.currentTimeMillis();
		}

	}
}
