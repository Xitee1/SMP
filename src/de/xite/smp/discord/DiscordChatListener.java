package de.xite.smp.discord;

import net.dv8tion.jda.api.JDA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.xite.smp.main.Main;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class DiscordChatListener extends ListenerAdapter implements Listener{
	String textChannel = "chat";
	
	// Minecraft
	@EventHandler
	public void onMCChat(AsyncChatEvent e) {
		JDA jda = SMPcord.getJDA();
		if(jda != null) {
			for(TextChannel tc : jda.getTextChannels()) {
				if(tc.getName().contains(textChannel)) {
					if(tc.canTalk()) {
						String msg = PlainTextComponentSerializer.plainText().serialize(e.message());
						String player = e.getPlayer().getName();

						tc.sendMessage("** **\n"
										+ "[MC] "+player+"\n"
										+ "```"+msg+"```")
								.queue();
					}
				}
			}
		}
	}
	
	// Discord
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        
        if(!e.getAuthor().isBot()) {
            if(e.getChannel().getName().contains(textChannel)) {
            	final TextComponent textComponent = Component.text("[Discord] ").color(TextColor.fromCSSHexString("#5865f2"))
            											.append(Component.text("<"+e.getAuthor().getName()+"> ").color(TextColor.fromCSSHexString("#009c9b")))
            											.append(Component.text(msg.getContentRaw()).color(TextColor.fromCSSHexString("#c8c4c4")));
            	Main.pl.getServer().broadcast(textComponent);
            }	
        }
        
        /*
        if(msg.getContentRaw().equals("/ban")) {
	        MessageChannel channel = e.getChannel();
	        channel.sendMessage("Pong! e").queue();
        }
        */
    }
    
}
