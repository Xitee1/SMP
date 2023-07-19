package de.xite.smp.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Chunk;

import de.xite.smp.main.Main;
import de.xite.smp.database.Database;

public class ChunkManager {
	Chunk chunk;
	String dateModified;
	String versionModified;
	
	private ChunkManager(Chunk chunk) {
		this.chunk = chunk;
	}

	public static ChunkManager getChunk(Chunk chunk) {
		return new ChunkManager(chunk);
	}
	
	public String getDateModified() {
		return dateModified;
	}

	public String getVersionModified() {
		return versionModified;
	}


}
