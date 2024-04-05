package de.xite.smp.commands;

import de.xite.smp.entities.SMPPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements CommandExecutor{
	
	private static String pr = ChatColor.GRAY+"["+ChatColor.RED+"Hilfe"+ChatColor.GRAY+"] ";
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		s.sendMessage(pr+"Nützliche Befehle:");

		List<Component> helpTexts = new ArrayList<>();
		helpTexts.add(generateHelpMessage("hilfe", "Öffnet dieses Info-Menü", null, null));
		helpTexts.add(generateHelpMessage(
				"trustlevel",
				"Zeigt dir dein TrustLevel an.",
				"Es gibt die TrustLevel 1-"+ SMPPlayer.maxTrustLevel+". Um so höher das TrustLevel, desto mehr Rechte bekommst du. Für mehr Infos, schaue bitte in unseren Discord.",
				"/tl"
		));
		helpTexts.add(generateHelpMessage(
				"chunkinfo",
				"Zeigt dir Infos den aktuellen Chunk an.",
				"Wenn neue Chunks erstellt werder oder bestehende bearbeitet werden (Block platziert/abgebaut), wird das gespeichert. So kann man sehen, wann und in welcher MC Version der Chunk erstellt und zuletzt bearbeitet wurde.",
				"/ci"
		));
		helpTexts.add(generateHelpMessage(
				"blockinfo",
				"Zeigt dir Infos über einen Block an.",
				"Mit dem Plugin CoreProtect wird alles geloggt. So kann man sehen, wann zu welcher Zeit ein Spieler einen bestimmten Block platziert/abgebaut oder verändert hat.",
				"/bi"
		));
		helpTexts.add(generateHelpMessage(
				"spielzeit [Spieler]",
				"Die Zeit, die du hier verbracht hast.",
				"Immer, wenn du online bist, wird die Zeit mitgezählt. So kannst du sehen, wie lange du oder andere insgesamt hier gespielt haben.",
				"/playtime, /sz, /pt"
		));

		for(Component text : helpTexts)
			s.sendMessage(text);

		return true;
	}

	private static Component generateHelpMessage(String command, String info, String moreInfo, String additionalCommands) {
		Component c = Component.text(pr+ChatColor.GRAY+"- "+ChatColor.AQUA+"/"+command+ChatColor.GRAY+" | "+info);

		if(moreInfo != null || additionalCommands != null) {
			String hoverText = "";
			if(moreInfo != null)
				hoverText = moreInfo;

			if(moreInfo != null && additionalCommands != null)
				hoverText += "\n\n";

			if(additionalCommands != null)
				hoverText += "Kurzform: "+additionalCommands;

			c = c.hoverEvent(HoverEvent.showText(Component.text(hoverText)));
		}

		return c;
	}
}
