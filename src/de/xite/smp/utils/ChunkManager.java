package de.xite.smp.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Chunk;

import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;

public class ChunkManager {
	private static HashMap<Chunk, ChunkManager> chunks = new HashMap<>();
	
	Chunk chunk;
	String dateModified = "";
	String versionModified = Main.MCVersion;
	Boolean isNew = false;
	
	private ChunkManager(Chunk chunk) {
		this.chunk = chunk;
	}
	public static ChunkManager getChunk(Chunk chunk, boolean cache) {
		if(!cache) {
			if(chunks.containsKey(chunk))
				return chunks.get(chunk);
			return null;
		}
		
		if(!chunks.containsKey(chunk))
			chunks.put(chunk, new ChunkManager(chunk));
		return chunks.get(chunk);
	}
	public static Collection<ChunkManager> getAllChunks() {
		ArrayList<ChunkManager> list = new ArrayList<>();
		list.addAll(chunks.values());
		return list;
	}
	
	public String getDateModified() {
		return dateModified;
	}
	public String getVersionModified() {
		return versionModified;
	}
	public void updateChunk() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.dateModified = sdf.format(new Date());
	}
	public void removeChunkFromCache() {
		chunks.remove(this.chunk);
	}
	
	public String getMySQLUpdateQuery() {
		if(dateModified == null || versionModified == null)
			return "";
		return("UPDATE `"+MySQL.prefix+"chunks` SET `version_modified`='"+versionModified+"', `date_modified`='"+dateModified+"' " + 
				"WHERE `world`='"+chunk.getWorld().getName()+"' AND `loc_x`='"+chunk.getX()+"' AND `loc_z`='"+chunk.getZ()+"';");
	}
}
