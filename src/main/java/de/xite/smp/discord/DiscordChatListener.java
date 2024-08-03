package de.xite.smp.discord;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.xite.smp.main.Main;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

public class DiscordChatListener extends ListenerAdapter implements Listener{
	
	// Minecraft
	@EventHandler
	public void onMCChat(AsyncChatEvent e) {
		String msg = PlainTextComponentSerializer.plainText().serialize(e.message());
		String player = e.getPlayer().getName();
		SMPcord.sendChatMessage("**[MC] "+player+"** ```" + msg + "```");
	}
	
	// Discord
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if(!e.getAuthor().isBot()) {
			if (e.getChannel().getName().equals(Main.getPluginConfig().getDiscordChatChannel())) {
				Message msg = e.getMessage();
				final TextComponent textComponent = Component.text("[Discord] ").color(TextColor.fromCSSHexString("#5865f2"))
						.append(Component.text("<" + e.getAuthor().getName() + "> ").color(TextColor.fromCSSHexString("#009c9b")))
						.append(Component.text(msg.getContentDisplay()).color(TextColor.fromCSSHexString("#c8c4c4")));
				Main.pl.getLogger().info("Received text message: " + msg.getContentDisplay());
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
