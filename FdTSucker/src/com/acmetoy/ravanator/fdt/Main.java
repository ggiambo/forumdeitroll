package com.acmetoy.ravanator.fdt;

import java.util.Date;
import java.util.Timer;

public class Main {
	
	public static void main(String[] args) {
		new Timer().schedule(new Sucker(), new Date(), 1 * 60 * 1000); // every minute
		new Timer().schedule(new Repair(), new Date(), 1 * 60 * 1000 * 60); // every hour
	}

}
