<?php
/*
 * This script is an example of using curl in php to log into on one page and 
 * then get another page passing all cookies from the first page along with you.
 * If this script was a bit more advanced it might trick the server into 
 * thinking its netscape and even pass a fake referer, yo look like it surfed 
 * from a local page.
*/

function convertCookiesToString($cookiedb)
{
	$str = '';
	foreach($cookiedb as $cookie)
	{
		$str .= implode("=",$cookie) . "; ";
	}
	$str = substr($str,0,-2);
	return $str;
}

function updateCookies($cookie,$cookiedb)
{
	for($i=0;$i<count($cookiedb);$i++)
	{
		if ($cookiedb[$i][0] == $cookie[0])
		{
			$cookiedb[$i][1] = $cookie[1];
			return $cookiedb;
		}
	}
	//Couldn't find an existing cookie.
	
	array_push($cookiedb,$cookie);
	return $cookiedb;
}

function updateCookiesFromRequest($req,$cookiesdb)
{
	//Grab cookies from the request...
	preg_match_all('|Set-Cookie: (.*)[;' . PHP_EOL . ']|U',$req,$matches);

	foreach ($matches[1] as $c)
	{
		$c_s = explode("=",$c,2);
		$cookiesdb = updateCookies($c_s,$cookiesdb);
	}
	return $cookiesdb;
}


$ch = curl_init();
curl_setopt($ch, CURLOPT_COOKIEJAR, "/tmp/cookieFileName");
curl_setopt($ch, CURLOPT_URL,"https://wl.mypurdue.purdue.edu/cp/home/displaylogin");
curl_setopt($ch, CURLOPT_HEADER, 1); //Get the headers!
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Get the data!
curl_setopt($ch, CURLOPT_SSLVERSION, 3); //SSLv3 for MyPurdue.
curl_setopt($ch, CURLOPT_TIMEOUT,10);

$buf = curl_exec ($ch); // execute the curl command

curl_close ($ch);
unset($ch);

/*
preg_match("/var clientServerDelta = \(new Date\(\)\)\.getTime\(\) - (\d*);/",$buf,$matches);
$purduetime = $matches[1];
$diff = floor(microtime(true)*1000) - $purduetime;
*/

$cookie_db = array();

//Grab cookies from the request...
/*
preg_match_all('|Set-Cookie: (.*);|U',$buf,$matches);
$cookies = implode('; ', $matches[1]);
foreach ($matches[1] as $c)
{
	$c_s = explode("=",$c);
	$cookie_db = updateCookies($c_s,$cookie_db);
}
*/

$cookie_db = updateCookiesFromRequest($buf,$cookie_db);

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,"https://wl.mypurdue.purdue.edu/cp/home/login");
curl_setopt($ch, CURLOPT_POST, true);
$post = "pass=***REMOVED***&user=hmcafee&uuid=0xACA021";
curl_setopt($ch, CURLOPT_POSTFIELDS, $post);
curl_setopt($ch, CURLOPT_SSLVERSION, 3);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HEADER, true);
curl_setopt($ch, CURLOPT_COOKIE, convertCookiesToString($cookie_db)); //Pass the cookies, please.
$buf1 = curl_exec ($ch);
curl_close ($ch);
unset($ch);

if (strstr($buf1,"loginok.html") === false)
{
	print("Log in to MyPurdue failed! D:");
	die();
}

$cookie_db = updateCookiesFromRequest($buf1,$cookie_db);

//Request this in between...
print("Logged in to MyPurdue ...\n");
print("Getting session ID...\n");


$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,"https://selfservice.mypurdue.purdue.edu/prod/bwskfcls.P_GetCrse");
curl_setopt($ch, CURLOPT_SSLVERSION, 3);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HEADER, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 600); //Allow 5 minutes.
curl_setopt($ch, CURLOPT_COOKIE, convertCookiesToString($cookie_db)); //Pass the cookies, please.
$sessid = curl_exec ($ch);
curl_close ($ch);
unset($ch);

$cookie_db = updateCookiesFromRequest($sessid,$cookie_db);

print("Requesting (massive) class list...\n");

//https://selfservice.mypurdue.purdue.edu/prod/bwskfcls.P_GetCrse_Advanced


$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,"https://wl.mypurdue.purdue.edu/cp/ip/login?sys=sctssb&url=https://selfservice.mypurdue.purdue.edu/prod/tzwkwbis.P_CheckAgreeAndRedir?ret_code=STU_LOOKCLASS");
//curl_setopt($ch, CURLOPT_URL,"https://selfservice.mypurdue.purdue.edu/prod/bwckctlg.xml");
//curl_setopt($ch, CURLOPT_REFERER, 'https://selfservice.mypurdue.purdue.edu/prod/bwckctlg.p_display_courses');
//curl_setopt($ch, CURLOPT_POST, true);
//curl_setopt($ch, CURLOPT_POSTFIELDS, $classpost);
curl_setopt($ch, CURLOPT_SSLVERSION, 3);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HEADER, true);
curl_setopt($ch, CURLOPT_NOBODY, true);
//curl_setopt($ch, CURLOPT_TIMEOUT, 600); //Allow 5 minutes.
curl_setopt($ch, CURLOPT_COOKIE, convertCookiesToString($cookie_db)); //Pass the cookies, please.
$buf = curl_exec ($ch);
curl_close ($ch);
unset($ch);

//This page should send us to another...

preg_match('/Location: (.*)' . PHP_EOL . '/',$buf,$result);
$cookie_db = updateCookiesFromRequest($buf,$cookie_db);
echo("\n\nNew Loc: " . $result[1]);

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,$result[1]);
//curl_setopt($ch, CURLOPT_URL,"https://selfservice.mypurdue.purdue.edu/prod/bwckctlg.xml");
//curl_setopt($ch, CURLOPT_REFERER, 'https://selfservice.mypurdue.purdue.edu/prod/bwckctlg.p_display_courses');
//curl_setopt($ch, CURLOPT_POST, true);
//curl_setopt($ch, CURLOPT_POSTFIELDS, $classpost);
curl_setopt($ch, CURLOPT_SSLVERSION, 3);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HEADER, true);
curl_setopt($ch, CURLOPT_NOBODY, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 600); //Allow 5 minutes.
curl_setopt($ch, CURLOPT_COOKIE, convertCookiesToString($cookie_db)); //Pass the cookies, please.
$buf = curl_exec ($ch);
curl_close ($ch);
unset($ch);

$cookie_db = updateCookiesFromRequest($buf,$cookie_db);

$classpost = "rsts=dummy&crn=dummy&term_in=201410&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=AAE&sel_subj=AAS&sel_subj=ABE&sel_subj=AD&sel_subj=AFT&sel_subj=AGEC&sel_subj=AGR&sel_subj=AGRY&sel_subj=AMST&sel_subj=ANSC&sel_subj=ANTH&sel_subj=ARAB&sel_subj=ASAM&sel_subj=ASL&sel_subj=ASM&sel_subj=ASTR&sel_subj=AT&sel_subj=BAND&sel_subj=BCHM&sel_subj=BCM&sel_subj=BIOL&sel_subj=BME&sel_subj=BMS&sel_subj=BTNY&sel_subj=CAND&sel_subj=CE&sel_subj=CEM&sel_subj=CGT&sel_subj=CHE&sel_subj=CHM&sel_subj=CHNS&sel_subj=CLCS&sel_subj=CLPH&sel_subj=CMPL&sel_subj=CNIT&sel_subj=COM&sel_subj=CPB&sel_subj=CS&sel_subj=CSR&sel_subj=DANC&sel_subj=EAPS&sel_subj=ECE&sel_subj=ECET&sel_subj=ECON&sel_subj=EDCI&sel_subj=EDPS&sel_subj=EDST&sel_subj=EEE&sel_subj=ENE&sel_subj=ENGL&sel_subj=ENGR&sel_subj=ENTM&sel_subj=ENTR&sel_subj=EPCS&sel_subj=FNR&sel_subj=FR&sel_subj=FS&sel_subj=FVS&sel_subj=GEP&sel_subj=GER&sel_subj=GRAD&sel_subj=GREK&sel_subj=GS&sel_subj=HDFS&sel_subj=HEBR&sel_subj=HHS&sel_subj=HIST&sel_subj=HK&sel_subj=HONR&sel_subj=HORT&sel_subj=HSCI&sel_subj=HTM&sel_subj=IDE&sel_subj=IDIS&sel_subj=IE&sel_subj=IET&sel_subj=IPPH&sel_subj=IT&sel_subj=ITAL&sel_subj=JPNS&sel_subj=LA&sel_subj=LALS&sel_subj=LATN&sel_subj=LC&sel_subj=LCME&sel_subj=LING&sel_subj=MA&sel_subj=MARS&sel_subj=MCMP&sel_subj=ME&sel_subj=MET&sel_subj=MFET&sel_subj=MGMT&sel_subj=MSE&sel_subj=MSL&sel_subj=MUS&sel_subj=NRES&sel_subj=NS&sel_subj=NUCL&sel_subj=NUPH&sel_subj=NUR&sel_subj=NUTR&sel_subj=OBHR&sel_subj=OLS&sel_subj=PES&sel_subj=PHAD&sel_subj=PHIL&sel_subj=PHPR&sel_subj=PHRM&sel_subj=PHYS&sel_subj=POL&sel_subj=PSY&sel_subj=PTGS&sel_subj=REL&sel_subj=RUSS&sel_subj=SA&sel_subj=SCI&sel_subj=SLHS&sel_subj=SOC&sel_subj=SPAN&sel_subj=STAT&sel_subj=TECH&sel_subj=THTR&sel_subj=USP&sel_subj=VCS&sel_subj=VM&sel_subj=WOST&sel_subj=YDAE&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&SUB_BTN=Section+Search&path=1";

//$classpost = "rsts=dummy&crn=dummy&term_in=201410&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=AAE&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&SUB_BTN=Section+Search&path=1";

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,$result[1]);
curl_setopt($ch, CURLOPT_URL,"https://selfservice.mypurdue.purdue.edu/prod/bwskfcls.P_GetCrse_Advanced");
curl_setopt($ch, CURLOPT_REFERER, 'https://selfservice.mypurdue.purdue.edu/prod/bwckctlg.p_display_courses');
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, $classpost);
curl_setopt($ch, CURLOPT_SSLVERSION, 3);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
//curl_setopt($ch, CURLOPT_HEADER, true);
//curl_setopt($ch, CURLOPT_NOBODY, true);
curl_setopt($ch, CURLOPT_TIMEOUT, 600); //Allow 5 minutes.
curl_setopt($ch, CURLOPT_COOKIE, convertCookiesToString($cookie_db)); //Pass the cookies, please.
$buf = curl_exec ($ch);
curl_close ($ch);
unset($ch);

$cookie_db = updateCookiesFromRequest($buf,$cookie_db);

print_r($buf);

?>

