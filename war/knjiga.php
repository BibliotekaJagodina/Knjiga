<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">                                                           
    <link type="text/css" rel="stylesheet" href="Knjiga.css">
   
    <?php
		$user="root";
		$password="";
		$database="biblioteka";
		
		mb_internal_encoding("UTF-8");
		if(! isset($_GET['id']))
			die("Није наведена ниједна књига");
		$link = mysql_connect('localhost',$user,$password);
		@mysql_select_db($database, $link) or die( "Приступ бази није могућ");
	//neophodno da bi prikazivao cirilicu
		mysql_query("SET NAMES utf8");
		mysql_query("SET CHARACTER SET utf8");
		mysql_query("SET COLLATION_CONNECTION='utf8_general_ci'");
		$sql = "SET NAMES 'utf8'";
		mysql_query($sql, $link);
		$query="SELECT  id, jsonDesc, jsonSadrzaj, autor, izdanje, invBr, naslov, cobiss, dan, mesec, godina, brPregleda FROM Knjiga WHERE id=".intval($_GET['id']);
		$result = mysql_query($query, $link);
		if(!$result)
			die("Упит није вратио податке из базе ".myslq_error());
		
		if (mysql_num_rows($result) == 0) 
			die("Тражена књига не постоји!");
		$row = mysql_fetch_assoc($result);	
		$idKnjige = $row['id'];
                if(isset( $row['naslov']))
                    $naslovKnjige = $row['naslov'];
                else
                    $naslovKnjige = '';
                if(isset($row['autor']))
                    $autorKnjige = $row['autor'];
                else
                    $autorKnjige = '';
                if(isset($row['izdanje']))
                    $izdanjeKnjige = $row['izdanje'];
                else
                    $izdanjeKnjige = '';
                if(isset($row['invBr']))
                    $invBrKnjige = $row['invBr'];
                else
                    $invBrKnjige = '';
                $cobiss = $row['cobiss'];
                $dan = $row['dan'];
                $mesec = $row['mesec'];
                $godina = $row['godina'];

                $brPregleda = $row['brPregleda'];

		$jsonKnjiga = str_replace(array("\n","\r"), '', $row['jsonDesc']);//brise nove redove
		$jsonKnjiga = str_replace('"','\"', $jsonKnjiga);//menja string " stringom \"
		
		$jsonSadrzaj = null;
		if(isset($row['jsonSadrzaj']))
                    $jsonSadrzaj = $row['jsonSadrzaj'];
		mysql_query('UPDATE Knjiga SET brPregleda=brPregleda+1 WHERE id='.$idKnjige, $link);
		//mysql_close();
	?>
         <title>Читанка - <?php echo $naslovKnjige ?></title>
  	<script type="text/javascript" language="javascript">
			var knjiga1={
				id_knjige: "<?php echo $idKnjige ?>",
                                naslov:"<?php echo $naslovKnjige ?>",
                                autor:"<?php echo $autorKnjige ?>",
                                izdanje:"<?php echo $izdanjeKnjige ?>",
                                invBr: "<?php echo $invBrKnjige ?>",
                                cobiss: "<?php echo $cobiss ?>",
                                brPregleda:"<?php echo $brPregleda ?>"
			};			
			var knjiga_meta={
				jsonString: "<?php echo $jsonKnjiga ?>"
			};
			<?php if($jsonSadrzaj !== null)
				echo	"var sadrzaj_meta={jsonString:\"".$jsonSadrzaj."\"};";
			?>			
	</script>
 
    <script type="text/javascript" language="javascript" src="knjiga/knjiga.nocache.js"></script>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-27844300-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>    
  </head>

  <body tabindex="0">
	<div id=ucitavanje>Учитавање странице је у току. Уколико имате спору везу операција може да потраје...</div>	
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Морате укључити Јава скрипт у Вашем програму за преглед Интернет страница (веб браузеру) како би ова апликација могла да функционише!
      </div>
    </noscript>
  </body>
</html>
