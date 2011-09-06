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

	// constants
	$PATTERN = "/^(&gt;\\ ?)+/";
	$QUOTE = array(0 => "#007BDF", 1 => "#00AF59", 2 => "#9A00EF", 3 => "#AF6F00");

	session_start();
	if (isset($_GET["noquote"])) {
		$_SESSION['noquote'] = $_GET["noquote"];
	}

	$pageNr = 0;
	if (isset($_GET['pageNr'])) {
		$pageNr = $_GET['pageNr'];
	}

	// by author ? 
	$nick = '';
	if (isset($_GET['nick']) && $_GET['nick'] != '') {
		$nick = $_GET['nick'];
		echo "<h4>Messaggi scritti da <i>".$nick."</i></h4>";
	}
	echo "<h4><a href='". $_SERVER['SCRIPT_NAME']."'>Inizio</a><br/></h4>";

	// contextPath
	$contextPath = substr($_SERVER["SCRIPT_NAME"], 0, strripos($_SERVER["SCRIPT_NAME"], '/'));
	
	$pageSize = 25;
	mysql_connect("localhost", "fdtsucker", "fdtsucker") or die(mysql_error());
	mysql_select_db("fdtsucker") or die(mysql_error());

	if ($nick != '') {
		$query = sprintf("SELECT * FROM messages where author = '%s' ORDER BY date DESC LIMIT %s, %s", mysql_real_escape_string($nick), mysql_real_escape_string($pageSize*$pageNr), mysql_real_escape_string($pageSize));
	} else {
		$query = sprintf("SELECT * FROM messages ORDER BY date DESC LIMIT %s, %s", mysql_real_escape_string($pageSize*$pageNr), mysql_real_escape_string($pageSize));
	}
	$result = mysql_query($query) or die(mysql_error());

	$numberOfRows = mysql_num_rows($result);

	// prev / next page
	if ($pageNr > 0) {
		echo "<a href='". $_SERVER['SCRIPT_NAME']."?pageNr=".($pageNr - 1)."&nick=".$nick."'>&lt;&lt;</a>";
	}
	echo "&nbsp;Pagina ".($pageNr + 1);
	if ($pageSize <= $numberOfRows) {
		echo "&nbsp;<a href='". $_SERVER['SCRIPT_NAME']."?pageNr=".($pageNr + 1)."&nick=".$nick."'>&gt;&gt;</a>";
	}
	
	$index = 0;
	while($msg = mysql_fetch_assoc($result)) {
		$index++;
		
		// background
		echo "<div style='margin: 5px; width:600px;border-bottom:1px solid black;";
		if ($index % 2 == 0) {
			echo "background-color: #F6F6F6";
		} else {
			echo"background-color: #FFFFFF";
		}
		echo "'>";
		
		// author
		echo "<img src='".$contextPath."/avatar.php?nick=".$msg['author']."'/>";
		if ($msg['forum'] != '') {
			echo "<span style='color:#97A28A'><b>".$msg['forum']."</b></span>";
		}
		echo "<br/>";
		echo "Scritto da "; 
		echo "<i>";
		if ($msg['author'] == '') {
			echo "Non autenticato";
		} else {
			echo "<b><a href='".$contextPath."/messages.php?nick=".$msg['author']."'/>".$msg['author']."</a></b><br/>";
		}
		echo "</i>";
		
		// date
		echo " alle ".date("d.m.Y H:i:s", strtotime($msg['date']))."<br/><br/>";
		echo "<b><a href='".$contextPath."/thread.php?threadId=".$msg['threadId']."'/>".$msg['subject']."</a></b><br/>";
		
		// content
		echo "<div style='padding: 15px;'>";
		if (isset($_SESSION["noquote"]) && $_SESSION['noquote'] == "yes") {
			echo $msg['text'];
		} else {
			$text = explode("<BR>", $msg['text']);
			foreach ($text as $txt) {
				preg_match($PATTERN, $txt, $matches);
				if (empty($matches)) {
					echo $txt;
				} else {
					$nrQuotes = strlen(str_replace(" ", "", $matches[0])) / 4;
					$idx = $nrQuotes % count($QUOTE);
					$color = $QUOTE[$idx];
					echo "<span style='color:".$color."'>";
					echo $txt;
					echo "</span>";
				}
				echo "<br/>";
			}
		}
		echo "<!-- close open tags --!>";
		echo "</b></i></u>";
		echo "</div>";
		echo "</div>";
	}
		
	// prev / next page
	if ($pageNr > 0) {
		echo "<a href='". $_SERVER['SCRIPT_NAME']."?pageNr=".($pageNr - 1)."&nick=".$nick."'>&lt;&lt;</a>";
	}
	echo "&nbsp;Pagina ".($pageNr + 1);
	if ($pageSize <= $numberOfRows) {
		echo "&nbsp;<a href='". $_SERVER['SCRIPT_NAME']."?pageNr=".($pageNr + 1)."&nick=".$nick."'>&gt;&gt;</a>";
	}
	?>
	</body>
</html>
