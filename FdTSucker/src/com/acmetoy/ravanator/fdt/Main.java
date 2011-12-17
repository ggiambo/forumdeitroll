package com.acmetoy.ravanator.fdt;

import java.util.Date;
import java.util.Timer;

public class Main {

	public static final long ONE_MINUTE = 60 * 1000;

	public static void main(String[] args) {
		new Timer().schedule(new Sucker(), new Date(), ONE_MINUTE); // every minute
		new Timer().schedule(new SuckBack(), new Date()); // start it only once
	}

}
