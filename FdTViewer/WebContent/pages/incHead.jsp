<head>
	<%! static final long bootTime = System.currentTimeMillis(); %>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="Pragma" content="no-cache">
	<link href="css/fdt.css?v=<%=bootTime%>" type="text/css" rel="stylesheet"/>
	<script type="text/javascript" src="js/jquery-1.6.3.min.js"></script>
	<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
	<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			initSidebarStatus();
		});
	</script>
	<link rel="icon" href="favicon.ico" type="image/x-icon"/>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	<link href='http://fonts.googleapis.com/css?family=Frijole' rel='stylesheet' type='text/css'> 
</head>