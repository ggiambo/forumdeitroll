<html>
	<head>
		<style type="text/css"> 
			body {
				font-family: 'Helvetica';
				font-size: 15px;
			 }
		 </style>
	</head>
	
	<body>
<?php

	include "Message.php";
	include "MessageTree.php";

       session_start();
        if (isset($_GET["noquote"])) {
                $_SESSION['noquote'] = $_GET["noquote"];
        }
	
	// constants
	$PATTERN = "/^(&gt;\\ ?)+/";
	$QUOTE = array(0 => "#007BDF", 1 => "#00AF59", 2 => "#9A00EF", 3 => "#AF6F00");

	// contextPath
	$contextPath = substr($_SERVER["SCRIPT_NAME"], 0, strripos($_SERVER["SCRIPT_NAME"], '/'));

	echo "<h4><a href='".$contextPath."/messages.php'>Inizio</a><br/></h4>";
	
	mysql_connect("localhost", "fdtsucker", "fdtsucker") or die(mysql_error());
	mysql_select_db("fdtsucker") or die(mysql_error());

	$query = sprintf("SELECT * FROM messages WHERE threadid = %s ORDER BY date ASC", mysql_real_escape_string($_GET['threadId']));
	$result = mysql_query($query) or die(mysql_error());
	
	// build the thread tree
	$threadTree = array();
	while($row = mysql_fetch_assoc($result)) {
		$threadTree[] = new Message($row);
	}
	$msgTree = new MessageTree($threadTree, $_GET['threadId']);
	$tree = $msgTree->asList();
	
	$index = 0;
	foreach ($tree as $msg) {
		$index++;
		$margin = $msg->indent*15;
		echo "<div style='border:1px solid black; margin: 5px 5px; margin-left:".$margin."px; width:600px;'>";
		for ($i = 1; $i < $msg->indent; $i++) {
			echo "<div style='border:1px solid black; margin: 3px;'>";
		}
		$background;
		if ($msg->indent % 2 == 0) {
			$background = "background-color: #F6F6F6";
		} else {
			$background = "background-color: #FFFFFF";
		}
		echo "<div style='margin: 5px 5px; margin-right: 5px; ".$background."'>";
		echo "<img src='".$contextPath."/avatar.php?nick=".$msg->author."'/>";
		if ($msg->forum != "") {
			echo "<span style='color:#97A28A'><b>".$msg->forum."</b></span>";
		}
		echo "<br/>";
		
		// author & date
		echo "Scritto da "; 
		echo "<i>";
		if ($msg->author == '') {
			echo "Non autenticato";
		} else {
			echo $msg->author;
		}
		echo "</i> alle".date("d.m.Y H:i:s", strtotime($msg->date))."<br/><br/>";
		echo "<b>".$msg->subject."</b><br/>";
		
		// content
                echo "<div style='padding: 15px;'>";
                if (isset($_SESSION['noquote']) && $_SESSION['noquote'] == "yes") {
                        echo $msg->text;
                } else {
			$text = explode("<BR>", $msg->text);
			foreach ($text as $txt) {
				preg_match($PATTERN, $txt, $matches);
				if (empty($matches)) {
					echo $txt;
				} else {
					$nrQuotes = strlen(str_replace(" ", "", $matches[0])) / 4;
					$index = $nrQuotes % count($QUOTE);
					$color = $QUOTE[$index];
					echo "<span style='color:".$color."'>";
					echo $txt;
					echo "</span>";
				}
				echo "<br/>";
			}
		}
		echo "</b></i></u>";
		echo "</div>";
		
		echo "</div>";
	
		for ($i = 1; $i < $msg->indent; $i++) {
			echo "</div>";
		}
		echo "</div>";
	}
	echo "<h4><a href='".$contextPath."/messages.php'>Inizio</a><br/></h4>";
?>
		
	</body>
	
</html>
	
