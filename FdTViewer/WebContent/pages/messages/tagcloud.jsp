<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<div id="main">
	<script src=https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js></script>
	<script src=js/d3.layout.cloud.js></script>
	<div class="userPanelContent">
		<div class="userPanelSection">
			<a href=# onclick="drawWordCloud(window.tagsData); return false;" class=userPanelButton>Wordcloud</a>
			<a href=# onclick="drawList(window.tagsData); return false;" class=userPanelButton>Lista</a>
			<div style="clear:both"></div>
		</div>
		<div class="userPanelSection">
			<div id=relevant-content></div>
			<div style="clear:both"></div>
		</div>
	</div>
	
	<fdt:delayedScript dump="false">
		var entityMap = {
			'&': '&amp;',
			'<': '&lt;',
			'>': '&gt;',
			'"': '&quot;',
			"'": '&#39;',
			'/': '&#x2F;',
			'`': '&#x60;',
			'=': '&#x3D;'
		};
		
		function escapeHtml (string) {
			return String(string).replace(/[&<>"'`=\/]/g, function (s) {
				return entityMap[s];
			});
		}
		var drawWordCloud = function(data) {
			var width = $('#relevant-content').width();
			$('#relevant-content').html('');
			setTimeout(function() {
				if (data.resultCode != "OK:1") {
					$('#tagcloud').html('Errore nel recupero dei dati.');
					return;
				}
				data = data.content;
				var minCount = data[data.length - 1].count;
				var maxCount = data[0].count;
				data = data.map(function(tag) {
					return {
						text : tag.name,
						size : (((tag.count - minCount) / maxCount) * 50) + 15,
						tagId : tag.id
					};
				});
				var fill = d3.scale.category20();
				var draw = function(words) {
					d3.select("#relevant-content")
						.append("svg")
						.attr("width", width)
						.attr("height", 960)
						.append("g")
						.attr("transform", "translate(450,450)")
						.selectAll("text")
						.data(words)
						.enter()
						.append("text")
						.on("click", function(textObj) {
							window.location = 'Messages?action=getMessagesByTag&t_id=' + textObj.tagId;
						})
						.style("font-size", function(d) { return d.size + "px" })
						.style("cursor", "pointer")
						.style("font-family", "Helvetica")
						.style("fill", function(d, i) { return fill(i); })
						.attr("text-anchor", "middle")
						.attr("transform", function(d) {
							return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
						})
						.text(function(d) { return d.text; });
				};
				d3.layout.cloud()
					.size([width, 960])
					.words(data)
					.rotate(function() { return ~~ (Math.random() * 2) * 90; })
					.font("Helvetica")
					.fontSize(function(d) { return d.size; })
					.on("end", draw)
					.start();
			}, 1);
		};
		var drawList = function(data) {
			$('#relevant-content').html('');
			setTimeout(function() {
				var html = "<ul>";
				for (var index in data.content) {
					var tag = data.content[index];
					html += "<ul><a href='Messages?action=getMessagesByTag&amp;t_id=" + tag.id + "'>"+escapeHtml(tag.name)+ " (" + tag.count +")</a></ul>"
				}
				html += "</ul>";
				$('#relevant-content').html(html);
			}, 1);
		};
		jQuery.get('JSon?action=getAllTags', function(data) {
			window.tagsData = data;
			drawWordCloud(window.tagsData);
		});
	</fdt:delayedScript>
</div>