<?php

	mysql_connect("localhost", "fdtsucker", "fdtsucker") or die(mysql_error());
	mysql_select_db("fdtsucker") or die(mysql_error());
	
	if (isset($_GET['createTagName'])) {
		$tagName = $_GET['createTagName'];
		$query = sprintf("INSERT into tags (tagName, used) VALUES ('%s', %s) ", mysql_real_escape_string(trim($tagName)), 1);
		$result = mysql_query($query) or die(mysql_error());
	}
	
	
	if (isset($_GET['tagName'])) {
		$tagName = $_GET['tagName'];
		$query = sprintf("SELECT tagName FROM tags WHERE tagName like '%s%s' ORDER BY tagname, used ASC", mysql_real_escape_string($tagName), "%");
		$result = mysql_query($query) or die(mysql_error());
		
		while($row = mysql_fetch_assoc($result)) {
			echo $row['tagName']."\n";
		}
	}
?>
