package com.acmetoy.ravanator.fdt.test;

import junit.framework.Assert;

import org.junit.Test;

import com.acmetoy.ravanator.fdt.fetcher.MessageFetcherMetadata;

public class ThreadTest {

	@Test public void testCapoThread() {
		// capothread
		MessageFetcherMetadata m = new MessageFetcherMetadata(2636950);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636950);
	}
	
	@Test public void testTreeRoot() {
		// radice di un albero
		MessageFetcherMetadata m = new MessageFetcherMetadata(2636972);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636950);
		
		m = new MessageFetcherMetadata(2636957);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636950);
	}
	
	@Test public void testChildRoot() {
		// direttamente sotto root
		MessageFetcherMetadata m = new MessageFetcherMetadata(2636953);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636950);
	}
	
	@Test public void testTree1() {
		// figli dell'albero
		MessageFetcherMetadata m = new MessageFetcherMetadata(2637034);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636972);
		m = new MessageFetcherMetadata(2637037);
		m.run();
		Assert.assertEquals(m.getParentId(), 2637034);
		
		m = new MessageFetcherMetadata(2636993);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636972);
		m = new MessageFetcherMetadata(2637035);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636993);
		m = new MessageFetcherMetadata(2637162);
		m.run();
		Assert.assertEquals(m.getParentId(), 2637035);
		
	}
	
	@Test public void testTree2() {
		// 2636957 -> 2636950
		MessageFetcherMetadata m = new MessageFetcherMetadata(2636957);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636950);

		// 2636957 -> 2636979
		m = new MessageFetcherMetadata(2636979);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636957);
		
		// 2636979 -> 2636987
		// 2636979 -> 2636995
		m = new MessageFetcherMetadata(2636987);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636979);
		m = new MessageFetcherMetadata(2636995);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636979);
		
		// 2636987 -> 2636990
		// 2636987 -> 2636991
		m = new MessageFetcherMetadata(2636991);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636987);
		m = new MessageFetcherMetadata(2636990);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636987);
		
		// 2636995 -> 2637018
		m = new MessageFetcherMetadata(2637018);
		m.run();
		Assert.assertEquals(m.getParentId(), 2636995);
		
		
	}
	
	
}
