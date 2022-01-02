package de.xite.smp.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Chunk;

import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;

public class ChunkManager {
	private static HashMap<Chunk, ChunkManager> chunks = new HashMap<>();
	
	Chunk chunk;
	String dateModified = null;
	String versionModified = null;
	
	private ChunkManager(Chunk chunk) {
		this.chunk = chunk;
		this.versionModified = Main.MCVersion;
	}
	public static ChunkManager getChunk(Chunk chunk, boolean insertToCache) {
		if(insertToCache) {
			if(!chunks.containsKey(chunk))
				chunks.put(chunk, new ChunkManager(chunk));
		}else if(!chunks.containsKey(chunk)) {
			return null;
		}
		return chunks.get(chunk);
	}
	public static ArrayList<ChunkManager> getAllChunks() {
		ArrayList<ChunkManager> list = new ArrayList<>();
		for(Entry<Chunk, ChunkManager> e : chunks.entrySet())
			list.add(e.getValue());
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
