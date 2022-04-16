package de.xite.smp.discord;

import javax.security.auth.login.LoginException;

import de.xite.smp.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
	
	private static String token = "OTY0NjQ5NzI4ODk1NzQ2MTk4.YlnuEQ.gWQhANrtWWD41Cxn4IlXG8-MbXM";
	
	private static JDA jda;
	
	public static void connectDiscord() {
		pl.getLogger().info("Starte den Discord Bot..");
		JDABuilder builder = JDABuilder.createDefault(token);
		
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setCompression(Compression.NONE);
		builder.setAutoReconnect(true);
		
		builder.setActivity(Activity.watching("dem Chat zu"));
		
		builder.addEventListeners(new DiscordChatListener());
		
		try {
			jda = builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
		pl.getLogger().info("Discord Bot ist online!");
	}
	
	public static JDA getJDA() {
		return jda;
	}
}
