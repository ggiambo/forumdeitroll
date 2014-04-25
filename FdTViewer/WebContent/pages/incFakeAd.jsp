<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<jsp:useBean id="random" class="java.util.Random" scope="application" />

<c:set var="fakeAdIndex" value="${random.nextInt(fn:length(randomAds))}"/>
<c:set var="randomFakeAd" value="${randomAds[fakeAdIndex]}"/>

<div class="fakeAdContainer">
	<h3 class="fakeAdTitle">
		<a href="/" class="fakeAdLink">${randomFakeAd.title}</a>
	</h3>
	<div class="fakeAdVisurl">
		<span class="fdtAd">Ad</span>
		<cite class="fakeAdUrl">${randomFakeAd.visurl}</cite>
	</div>
	<div class="fakeAdContent">${randomFakeAd.content}‎</div>
</div>