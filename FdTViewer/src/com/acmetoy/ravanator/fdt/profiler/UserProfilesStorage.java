package com.acmetoy.ravanator.fdt.profiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.log4j.Logger;

public class UserProfilesStorage {
	private static Logger logger = Logger.getLogger(UserProfilesStorage.class);
	private static String fs_path = "/tmp/fdt_user_profiles";
	
	public static void store(ArrayList<UserProfile> profiles) {
		//logger.info("UserProfilesStorage::store");
		try {
			FileOutputStream fos = new FileOutputStream(fs_path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(profiles);
			oos.close();
			fos.close();
		} catch (IOException e) {
			logger.error("IOException: "+e.getMessage(), e);
		}
	}
	
	public static ArrayList<UserProfile> load() {
		ArrayList<UserProfile> profiles = null;
		if (new File(fs_path).exists()) {
			try {
				FileInputStream fis = new FileInputStream(fs_path);
				ObjectInputStream ois = new ObjectInputStream(fis);
				profiles = (ArrayList<UserProfile>) ois.readObject();
				ois.close();
				fis.close();
			} catch (IOException e) {
				logger.error("IOException: "+e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException: "+e.getMessage(), e);
			}
			
		}
		if (profiles == null) {
			profiles = new ArrayList<UserProfile>();
		}
		new Thread(new PeriodicStore(profiles)).start();
		return profiles;
	}
	
	private static long FS_Store_timeout = 60 * 1000;
	private static long Cleanup_timeout = 60 * 60 * 1000;
	private static long UserProfile_expires = 2 * 24 * 60 * 60 * 1000;
	
	public static void cleanup(ArrayList<UserProfile> profiles) {
		for (ListIterator<UserProfile> profilesIter = profiles.listIterator(); profilesIter.hasNext();) {
			UserProfile profile = profilesIter.next();
			if (!profile.isBannato()) {
				if (profile.getUltimoRiconoscimentoUtente() + UserProfile_expires < System.currentTimeMillis()) {
					logger.info("cancellazione del seguente profilo dallo storage persistente:");
					logger.info(profile.getUuid());
					logger.info(profile.getNicknames());
					logger.info(profile.getMsgIds());
					profilesIter.remove();
				}
			}
		}
	}
	
	private static class PeriodicStore implements Runnable {
		private ArrayList<UserProfile> profiles = null; // reference
		public PeriodicStore(ArrayList<UserProfile> profiles) {
			this.profiles = profiles;
		}
		@Override
		public void run() {
			long lastCleanup = System.currentTimeMillis() - Cleanup_timeout; // esegue all'avvio
			while (true) {
				try {
					if (lastCleanup + Cleanup_timeout < System.currentTimeMillis()) {
						cleanup(profiles);
						lastCleanup = System.currentTimeMillis();
					}
					Thread.sleep(FS_Store_timeout); // 1 minuto
				} catch (InterruptedException e) {
					store(profiles);
					return;
				}
				store(profiles);
			}
		}
	}
}
