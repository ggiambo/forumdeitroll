package com.acmetoy.ravanator.fdt;

import com.acmetoy.ravanator.fdt.datafetcher.PageFetcher;

public class FdTMain {

	public static void main(String[] args) {
		for (int i = 1; i < 6; i++) {
			new PageFetcher(i).run();
		}
	}
}