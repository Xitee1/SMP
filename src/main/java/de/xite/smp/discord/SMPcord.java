package de.xite.smp.discord;

import javax.security.auth.login.LoginException;

import de.xite.smp.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class SMPcord {
	static Main pl = Main.pl;
	
	String roleID_trustLevel_1 = "xxx";
	String roleID_trustLevel_2 = "xxx";
	String roleID_trustLevel_3 = "xxx";
	String roleID_trustLevel_4 = "xxx";
	String roleID_trustLevel_5 = "xxx";
	String roleID_trustLevel_6 = "xxx";
	
	private static final String token = pl.getConfig().getString("discord.token");
	public static final String textChannel = Main.pl.getConfig().getString("discord.chatChannel");
	
	private static JDA jda;
	
	public static void connectDiscord() {
		pl.getLogger().info("Starting Discord bot..");
		JDABuilder builder = JDABuilder.createDefault(token);
		
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.NONE);
		builder.setAutoReconnect(true);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT); // Allow bot to read text messages
		
		builder.setActivity(Activity.watching("dem Chat zu"));
		
		builder.addEventListeners(new DiscordChatListener());

		jda = builder.build();
		try {
			jda.awaitReady();
		} catch (InterruptedException e) {
			pl.getLogger().info("Discord could not connect:");
			e.printStackTrace();
			return;
		}
		pl.getLogger().info("Discord bot is online!");

	}

	public static void sendChatMessage(String message) {
		JDA jda = SMPcord.getJDA();
		if(jda != null && textChannel != null) {
			for(TextChannel tc : jda.getTextChannelsByName(textChannel, false))
				if(tc.canTalk())
					tc.sendMessage(message).queue();
		}
	}
	
	public static JDA getJDA() {
		return jda;
	}
}
