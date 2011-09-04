<?php
	header("Content-type: image/gif");
	
	if (isset($_GET['nick']) && $_GET['nick'] != '') {
		mysql_connect("localhost", "fdtsucker", "fdtsucker") or die(mysql_error());
		mysql_select_db("fdtsucker") or die(mysql_error());
	
		$query = sprintf("SELECT avatar FROM authors where nick = '%s'", mysql_real_escape_string($_GET['nick']));
		$result = mysql_query($query) or die(mysql_error());
		
		$index = 0;
		$avatar = mysql_fetch_assoc($result);
		if ($avatar && $avatar['avatar'] != '') {
			echo $avatar['avatar'];
		} else  {
			readfile("./avatardefault.gif");
		}
	} else {
		readfile("./avataranonimo.gif");
	}
?>
