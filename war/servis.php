<?php
		/*
				echo 		"{ 
					ekstenzija:\"jpg\",
					brojac:\"001\",
					broj_strana:6,
					podrzan_tekst:false,
					podrzane_slike:true,
					podrzan_slajd_prikaz:true,
					podrzan_neprekidni_prikaz:true, 
					podrzan_sadrzaj:false,
					neprekidne_default:true,
					kvalitet:
						[
							{
								naziv:\"мањи квалитет слике\",
								url:\"http://localhost/sofija/small/\",
								podrazumevana_dimenzija:{v:300, s:400},
								pojedinacne_dimenzije:
									[{v:100,s:100},{v:200,s:200},{v:300,s:300},{v:400,s:400},{v:500,s:500},{v:600,s:600}]

							},
							{
								naziv:\"већи квалитет слике\",
								url:\"http://localhost/sofija/big\",
								podrazumevana_dimenzija:{s:800, v:600},
								pojedinacne_dimenzije:
									[{v:100,s:100},{v:100,s:100},{v:100,s:100},{v:100,s:100},{v:100,s:100},{v:600,s:600}]
							}
						],
					minijaturne:
						{
							url:\"http://localhost/sofija/thumb\",
							podrazumevana_dimenzija:{v:60,s:80}
						},
					imenovane_strane:
						[
							{
								i:2,
								naziv:\"Порука!\"
							},
							{
								i:3,
								naziv:\"strana cetvrta\"
							}
						],
					numeracija:
						[
							{
								i:0,
								pocetni_br:1,
								preskoci:false,
								narednih:2
							},
							{
								i:2,
								pocetni_br:3,
								preskoci:true,
								narednih:1
							},
							{
								i:3,
								pocetni_br:4,
								preskoci:false,
								narednih:2
							}
						]
				}"	*/

	if(isset($_POST['id_knjige']))
	{
		echo 		"{ 
						prefix:\"1904-01-12-\",
						ekstenzija:\"jpg\",
						brojac:\"001\",
						broj_strana:6,
						podrzan_tekst:false,
						podrzane_slike:true, 
						podrzan_slajd_prikaz:true,
						podrzan_sadrzaj:true,
						podrzan_neprekidni_prikaz:true, 
						neprekidne_default:true,
						kvalitet:
							[
								{
									naziv:\"мањи квалитет слике\",
									url:\"http://www.digital.nbs.bg.ac.yu/novine/politika/1904/min/\",
									podrazumevana_dimenzija:{v:800, s:600},
									pojedinacne_dimenzije:
										[{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600}]
	
								},
								{
									naziv:\"већи квалитет слике\",
									url:\"http://www.digital.nbs.bg.ac.yu/novine/politika/1904/mid/\",
									podrazumevana_dimenzija:{v:800, s:600},
									pojedinacne_dimenzije:
										[{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600},{v:800, s:600}]
								}
							],
						minijaturne:
							{
								url:\"http://www.digital.nbs.bg.ac.yu/novine/politika/1904/thu/\",
								podrazumevana_dimenzija:{v:80,s:60}
							},
						imenovane_strane:
							[
								{
									i:2,
									naziv:\"Прескочена страна\"
								},
								{
									i:3,
									naziv:\"Веома занимљива страна\"
								}
							],
						numeracija:
							[
								{
									i:0,
									pocetni_br:1,
									preskoci:false,
									narednih:2
								},
								{
									i:2,
									pocetni_br:3,
									preskoci:true,
									narednih:1
								},
								{
									i:3,
									pocetni_br:4,
									preskoci:false,
									narednih:2
								}
							]
					}";
	return;
	}
	
	if(isset($_POST['sadrzaj']))
	{
		echo "[
				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				

				{txt:\"Наслов\", 
				i:0,
				sub:[{txt:\"Прва после наслова\", i:1}]},
				
				{txt:\"Прескочена\", 
				i:2}
			]";
		return;
	}
?>
			
