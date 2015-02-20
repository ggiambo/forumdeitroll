<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div class=main>
	<div class=userPanel>
		<div class=userPanelCaption>Regole</div>
		<c:if test="${not empty rule}">
			<form action=UserProfiler method=post>
				<input type=hidden name=action value=saveRule>
				<input type=hidden name=uuid value=${rule.uuid}>
				<input type=text name=label value="${rule.label}">
				<br>
				<textarea name=code style='width:100%; font-family: monospace;' rows=15>${rule.code}</textarea>
				<br>
				<input type=button value=test onclick=testRule()>
				<input type=submit value=salva>
			</form>
		</c:if>
		<a href=UserProfiler?action=newRule>Nuova</a>
		<table style="width: 100%">
			<tbody>
				<c:forEach var="r" items="${rules}">
					<tr>
						<td>
							${r.label}
						</td>
						<td>
							<a href="UserProfiler?action=editRule&amp;uuid=${r.uuid}">
								Modifica
							</a>
						</td>
						<td>
							<a href="UserProfiler?action=deleteRule&amp;uuid=${r.uuid}">
								Elimina
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class=userPanel>
		<div class=userPanelCaption>Log</div>
		<table style="width: 100%">
			<thead>
				<tr>
					<th>tstamp</th>
					<th>label</th>
					<th>reqInfo</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="record" items="${records}">
					<tr>
						<td>
							<jsp:useBean id="dateValue" class="java.util.Date"/>
							<jsp:setProperty name="dateValue" property="time" value="${record.tstamp}"/>
							<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss"/>
						</td>
						<td>
							<a name="${record.label}">
								${record.label}
							</a>
						</td>
						<td>
							<c:set var="reqInfo" value="${record.reqInfo}"/>
							<pre><%=
								new com.google.gson.GsonBuilder()
								.setPrettyPrinting()
								.create()
								.toJson(pageContext.getAttribute("reqInfo"))
							%></pre>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>