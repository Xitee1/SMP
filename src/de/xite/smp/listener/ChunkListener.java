package de.xite.smp.listener;

import de.xite.smp.main.Main;
import de.xite.smp.utils.MySQL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
	@EventHandler
	public void onChunkCreate(ChunkLoadEvent e) {
		if (e.isNewChunk()) {
			Chunk chunk = e.getChunk();
			int x = chunk.getX();
			int z = chunk.getZ();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			MySQL.update("INSERT INTO `" + MySQL.prefix + "chunks` (`id`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified`) VALUES" + 
						"(NULL, '" + x + "', '" + z + "', '" + Main.MCVersion + "', '" + sdf.format(new Date()) + "', 'none', 'none')");
		} 
	}
}
