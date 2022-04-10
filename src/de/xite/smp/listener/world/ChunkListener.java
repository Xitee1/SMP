package de.xite.smp.listener.world;

import de.xite.smp.main.Main;
import de.xite.smp.sql.MySQL;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
	static Main pl = Main.pl;
	
	@EventHandler
	public void onChunkCreate(ChunkLoadEvent e) {
		if(e.isNewChunk()) {
			Chunk chunk = e.getChunk();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			MySQL.waitingUpdates.add("INSERT INTO `" + MySQL.prefix + "chunks` (`id`, `world`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified`) VALUES" + 
									"(NULL, '"+chunk.getWorld().getName()+"', '"+chunk.getX()+"', '"+chunk.getZ()+"', '"+Main.MCVersion+"', '" + sdf.format(new Date()) + "', 'none', 'none')");
		} 
	}
	
	/*
	public static void loadAllChunks(World world) {
	    final Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");

	    File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
	    File regionDir = new File(worldDir, "region");

	    File[] regionFiles = regionDir.listFiles(new FilenameFilter() {
	        @Override
	        public boolean accept(File dir, String name) {
	            return regionPattern.matcher(name).matches();
	        }
	    });

	    pl.getLogger().info("Found " + (regionFiles.length * 1024) + " chunk candidates in " + regionFiles.length + " files to check for loading. Beginning to load in 5 seconds..");
	    
	    try {
			Thread.sleep(1000*5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    for (File f : regionFiles) {
	        // extract coordinates from filename
	        Matcher matcher = regionPattern.matcher(f.getName());
	        if (!matcher.matches()) {
	            pl.getLogger().warning("FilenameFilter accepted unmatched filename: " + f.getName());
	            continue;
	        }

	        int mcaX = Integer.parseInt(matcher.group(1));
	        int mcaZ = Integer.parseInt(matcher.group(2));

	        int loadedCount = 0;

	        for (int cx = 0; cx < 32; cx++) {
	            for (int cz = 0; cz < 32; cz++) {
	            	int x = (mcaX << 5) + cx;
	            	int z = (mcaZ << 5) + cz;
	            	
					if(checkChunk(world, x, z))
						loadedCount += 1;
	            }
	        }
	        pl.getLogger().info("Actually loaded "+loadedCount+" from " + f.getName() + ".");
	    }
	}
	public static Boolean checkChunk(World world, int x, int z) {
		
		
    	boolean didLoad = world.loadChunk(x, z, false);
        if(didLoad) {

        	Chunk chunk = world.getChunkAt(x, z);
        	
        	pl.getLogger().info("Confirmed X: "+chunk.getX()+"; Z: "+chunk.getZ());
			//MySQL.update("INSERT INTO `" + MySQL.prefix + "chunks` (`id`, `loc_x`, `loc_z`, `version_created`, `date_created`, `version_modified`, `date_modified`) VALUES" + 
					//"(NULL, '" + chunk.getX() + "', '" + chunk.getZ() + "', 'Unbekannt (<1.17)', 'Ende 2020 - Ende 2021', 'none', 'none')");
			chunk.unload();
			return true;
        }else {
        	//pl.getLogger().info("Chunk does not exists");
        }
        return false;
	}
	
	
	
	
	
    public static void listRegions(World world) {
    	File worldDir = new File(world.getWorldFolder()+"/region");
    	if(world.getName().equals("world_nether")) {
    		worldDir = new File(world.getWorldFolder()+"/DIM-1/region");
    	}
    	if(world.getName().equals("world_the_end")) {
    		worldDir = new File(world.getWorldFolder()+"/DIM1/region");
    	}
    	if(!worldDir.exists()) {
    		pl.getLogger().info("Folder "+worldDir.getAbsolutePath()+" does not exists!");
    		return;
    	}
    	pl.getLogger().info("Folder: "+worldDir.getAbsolutePath());
    		
        File[] regionFiles = worldDir.listFiles();
        
        int totalChunks = 0;
        
        for (File file : regionFiles) {
        	totalChunks += listChunks(world, file, new File(world.getName()+".csv"));
        }
        pl.getLogger().info("Total Chunks discovered: "+totalChunks);
    }
    public static Integer listChunks(World world, File regionFile, File csvFile) {
    	int i = 0;
        if(!regionFile.getName().endsWith(".mca")) return i;
        if (regionFile.length() <= 0) return i;
        
        try {
            String[] filenameParts = regionFile.getName().split("\\.");
            int rX = Integer.parseInt(filenameParts[1]);
            int rZ = Integer.parseInt(filenameParts[2]);
            
            pl.getLogger().info("Found file "+rX+"; "+rZ);
            
            if(!csvFile.exists())
            	csvFile.createNewFile();
    		FileWriter fw = new FileWriter(csvFile, true);

            try (RandomAccessFile raf = new RandomAccessFile(regionFile, "r")) {
                for (int x = 0; x < 32; x++) {
                    for (int z = 0; z < 32; z++) {
                        int xzChunk = z * 32 + x;

                        raf.seek(xzChunk * 4 + 3);
                        int size = raf.readByte() * 4096;

                        if (size == 0) continue;

                        raf.seek(xzChunk * 4 + 4096);
                        
                        pl.getLogger().info("X: "+(rX * 32 + x)+"; Z: "+(rZ * 32 + z));
            			
                		fw.append((rX * 32 + x)+"");
                		fw.append(';');
                		fw.append((rZ * 32 + z)+"");
                		fw.append('\n');
                        i++;
                    }
                }
            } catch (RuntimeException | IOException ex) {
                pl.getLogger().severe("Failed!");
            }
            fw.flush();
            fw.close();
        } catch (NumberFormatException | IOException ignore) {}
    	
    	return i;
    }
    */
}