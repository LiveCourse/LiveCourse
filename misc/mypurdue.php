<?php
$YEAR = 2013;
$INSTITUTION_ID = 1;
date_default_timezone_set("America/New_York");
$stdin = fopen("php://stdin","r");
function mysql_insert($table, $inserts) {
	$values = array_map('mysql_real_escape_string', array_values($inserts));
	$keys = array_keys($inserts);

	if (mysql_query('INSERT INTO `'.$table.'` (`'.implode('`,`', $keys).'`) VALUES (\''.implode('\',\'', $values).'\')'))
		return mysql_insert_id();
	else
		return false;
}

/**
 * Generates a random alphanumeric string of specified length.
 * length - length of string
 * returns - random string of specified length
 */
function _random_string($length) {
	$key = '';
	$keys = array_merge(range(0, 9), range('a', 'z'), range('A','Z'));
	for ($i = 0; $i < $length; $i++) {
		$key .= $keys[array_rand($keys)];
	}
	return $key;
}

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

function fetchSubjectID($subj)
{
	//Add this as a class.
	//Make sure a subject exists for this class.
	$subject_q = mysql_query("SELECT * 
		FROM  `lc_subjects` 
		WHERE  `code` LIKE  '" . $subj . "'") or die("Error fetching subject: " . mysql_error());
	if (mysql_num_rows($subject_q) > 0)
	{
		$result_q = mysql_fetch_array($subject_q);
		return $result_q["id"];
	} else {
		$subject_q = mysql_query("INSERT INTO `lc_subjects` (
			`id` ,
			`name` ,
			`code`
			)
			VALUES (
			NULL ,  '',  '" . $subj . "');") or die("Error adding subject: " . mysql_error());
		return mysql_insert_id();
	}
}

function fetchBuildingID($bldg)
{
	global $INSTITUTION_ID;
	//Add this as a class.
	//Make sure a subject exists for this class.
	$building_q = mysql_query("SELECT * 
		FROM  `lc_buildings` 
		WHERE  `short_name` LIKE  '" . $bldg . "'") or die("Error fetching building: " . mysql_error());
	if (mysql_num_rows($building_q) > 0)
	{
		$result_q = mysql_fetch_array($building_q);
		return $result_q["id"];
	} else {
		$sql = "INSERT INTO  `lc_buildings` (
		`id` ,
		`institution_id` ,
		`name` ,
		`short_name`
		)
		VALUES (
		NULL ,  '" . $INSTITUTION_ID . "',  '',  '" . $bldg . "'
		);";
		$building_q = mysql_query($sql) or die("Error adding building: " . mysql_error() . "\nSQL: \n" . $sql);
		return mysql_insert_id();
	}
}

function fetchRoomID($bldg_id,$room)
{
	//Add this as a class.
	//Make sure a subject exists for this class.
	$room_q = mysql_query("SELECT * 
		FROM  `lc_rooms` 
		WHERE  `building_id` =  " . $bldg_id . " AND `room_number` LIKE '" . $room . "'") or die("Error fetching room: " . mysql_error());
	if (mysql_num_rows($room_q) > 0)
	{
		$result_q = mysql_fetch_array($room_q);
		return $result_q["id"];
	} else {
		$room_q = mysql_query("INSERT INTO `lc_rooms` (
			`id` ,
			`building_id` ,
			`room_number` , 
			`room_name`
			)
			VALUES (
			NULL ,  " . $bldg_id . ", '" . $room . "', '');") or die("Error adding room: " . mysql_error());
		return mysql_insert_id();
	}
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

print("Creating session ...\n");

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

print("Retrieving class list ...\n");

//Get EVERY class!!!
$classpost = "rsts=dummy&crn=dummy&term_in=201320&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=AAE&sel_subj=AAS&sel_subj=ABE&sel_subj=AD&sel_subj=AFT&sel_subj=AGEC&sel_subj=AGR&sel_subj=AGRY&sel_subj=AMST&sel_subj=ANSC&sel_subj=ANTH&sel_subj=ARAB&sel_subj=ASAM&sel_subj=ASL&sel_subj=ASM&sel_subj=ASTR&sel_subj=AT&sel_subj=BAND&sel_subj=BCHM&sel_subj=BCM&sel_subj=BIOL&sel_subj=BME&sel_subj=BMS&sel_subj=BTNY&sel_subj=CAND&sel_subj=CE&sel_subj=CEM&sel_subj=CGT&sel_subj=CHE&sel_subj=CHM&sel_subj=CHNS&sel_subj=CLCS&sel_subj=CLPH&sel_subj=CMPL&sel_subj=CNIT&sel_subj=COM&sel_subj=CPB&sel_subj=CS&sel_subj=CSR&sel_subj=DANC&sel_subj=EAPS&sel_subj=ECE&sel_subj=ECET&sel_subj=ECON&sel_subj=EDCI&sel_subj=EDPS&sel_subj=EDST&sel_subj=EEE&sel_subj=ENE&sel_subj=ENGL&sel_subj=ENGR&sel_subj=ENTM&sel_subj=ENTR&sel_subj=EPCS&sel_subj=FNR&sel_subj=FR&sel_subj=FS&sel_subj=FVS&sel_subj=GEP&sel_subj=GER&sel_subj=GRAD&sel_subj=GREK&sel_subj=GS&sel_subj=HDFS&sel_subj=HEBR&sel_subj=HHS&sel_subj=HIST&sel_subj=HK&sel_subj=HONR&sel_subj=HORT&sel_subj=HSCI&sel_subj=HTM&sel_subj=IDE&sel_subj=IDIS&sel_subj=IE&sel_subj=IET&sel_subj=IPPH&sel_subj=IT&sel_subj=ITAL&sel_subj=JPNS&sel_subj=LA&sel_subj=LALS&sel_subj=LATN&sel_subj=LC&sel_subj=LCME&sel_subj=LING&sel_subj=MA&sel_subj=MARS&sel_subj=MCMP&sel_subj=ME&sel_subj=MET&sel_subj=MFET&sel_subj=MGMT&sel_subj=MSE&sel_subj=MSL&sel_subj=MUS&sel_subj=NRES&sel_subj=NS&sel_subj=NUCL&sel_subj=NUPH&sel_subj=NUR&sel_subj=NUTR&sel_subj=OBHR&sel_subj=OLS&sel_subj=PES&sel_subj=PHAD&sel_subj=PHIL&sel_subj=PHPR&sel_subj=PHRM&sel_subj=PHYS&sel_subj=POL&sel_subj=PSY&sel_subj=PTGS&sel_subj=REL&sel_subj=RUSS&sel_subj=SA&sel_subj=SCI&sel_subj=SLHS&sel_subj=SOC&sel_subj=SPAN&sel_subj=STAT&sel_subj=TECH&sel_subj=THTR&sel_subj=USP&sel_subj=VCS&sel_subj=VM&sel_subj=WOST&sel_subj=YDAE&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&SUB_BTN=Section+Search&path=1";

//Just get CS
//$classpost =  "rsts=dummy&crn=dummy&term_in=201410&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=CS&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&SUB_BTN=Section+Search&path=1";

//Just get ALL
//$classpost = "rsts=dummy&crn=dummy&term_in=201410&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=AAE&sel_subj=AAS&sel_subj=ABE&sel_subj=AD&sel_subj=AFT&sel_subj=AGEC&sel_subj=AGR&sel_subj=AGRY&sel_subj=AMST&sel_subj=ANSC&sel_subj=ANTH&sel_subj=ARAB&sel_subj=ASAM&sel_subj=ASL&sel_subj=ASM&sel_subj=ASTR&sel_subj=AT&sel_subj=BAND&sel_subj=BCHM&sel_subj=BCM&sel_subj=BIOL&sel_subj=BME&sel_subj=BMS&sel_subj=BTNY&sel_subj=CAND&sel_subj=CE&sel_subj=CEM&sel_subj=CGT&sel_subj=CHE&sel_subj=CHM&sel_subj=CHNS&sel_subj=CLCS&sel_subj=CLPH&sel_subj=CMPL&sel_subj=CNIT&sel_subj=COM&sel_subj=CPB&sel_subj=CS&sel_subj=CSR&sel_subj=DANC&sel_subj=EAPS&sel_subj=ECE&sel_subj=ECET&sel_subj=ECON&sel_subj=EDCI&sel_subj=EDPS&sel_subj=EDST&sel_subj=EEE&sel_subj=ENE&sel_subj=ENGL&sel_subj=ENGR&sel_subj=ENTM&sel_subj=ENTR&sel_subj=EPCS&sel_subj=FNR&sel_subj=FR&sel_subj=FS&sel_subj=FVS&sel_subj=GEP&sel_subj=GER&sel_subj=GRAD&sel_subj=GREK&sel_subj=GS&sel_subj=HDFS&sel_subj=HEBR&sel_subj=HHS&sel_subj=HIST&sel_subj=HK&sel_subj=HONR&sel_subj=HORT&sel_subj=HSCI&sel_subj=HTM&sel_subj=IDE&sel_subj=IDIS&sel_subj=IE&sel_subj=IET&sel_subj=IPPH&sel_subj=IT&sel_subj=ITAL&sel_subj=JPNS&sel_subj=LA&sel_subj=LALS&sel_subj=LATN&sel_subj=LC&sel_subj=LCME&sel_subj=LING&sel_subj=MA&sel_subj=MARS&sel_subj=MCMP&sel_subj=ME&sel_subj=MET&sel_subj=MFET&sel_subj=MGMT&sel_subj=MSE&sel_subj=MSL&sel_subj=MUS&sel_subj=NRES&sel_subj=NS&sel_subj=NUCL&sel_subj=NUPH&sel_subj=NUR&sel_subj=NUTR&sel_subj=OBHR&sel_subj=OLS&sel_subj=PES&sel_subj=PHAD&sel_subj=PHIL&sel_subj=PHPR&sel_subj=PHRM&sel_subj=PHYS&sel_subj=POL&sel_subj=PSY&sel_subj=PTGS&sel_subj=REL&sel_subj=RUSS&sel_subj=SA&sel_subj=SCI&sel_subj=SLHS&sel_subj=SOC&sel_subj=SPAN&sel_subj=STAT&sel_subj=TECH&sel_subj=THTR&sel_subj=USP&sel_subj=VCS&sel_subj=VM&sel_subj=WOST&sel_subj=YDAE&sel_crse=&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a&SUB_BTN=Section+Search&path=1";

//Just get aero
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

//Let's parse this BABY

print("Parsing class list ...\n");

$dom = new DOMDocument('1.0', 'UTF-8');
error_reporting(E_ALL ^ E_WARNING); // Don't show warnings for the HTML parsing

$dom->loadHTML('<meta http-equiv="content-type" content="text/html; charset=utf-8">' . $buf);

// dirty fix
//foreach ($dom->childNodes as $item)
//	if ($item->nodeType == XML_PI_NODE)
//		$dom->removeChild($item); // remove hack
$dom->encoding = 'UTF-8'; // insert proper

error_reporting(E_ALL ^ E_NOTICE);
$xpath = new DOMXPath($dom);

$xquery = "//table[@class='datadisplaytable']//tr";

$classes = $xpath->query($xquery);

$class_db = array();

mysql_connect("localhost","livecourse","fart4fun");
mysql_select_db("livecourse_import");
//Empty cache
mysql_query("TRUNCATE `sections_tmp`;") or die("Could not empty cache: " . mysql_error());
foreach ($classes as $class)
{
	//Make sure this isn't a header.
	$headercheck = $xpath->query(".//th",$class);
	if ($headercheck->length > 0)
	{
		continue;
	}
	
	//Now let's get the class data.
	$c = array();
	$classdata = $xpath->query(".//td",$class);
	if (strlen($classdata->item(1)->nodeValue) < 5)
	{
		//This CRN exists. Let's update the relevant info
		print_r($class_db[count($class_db) - 1]);
		$dates = $classdata->item(20)->nodeValue;
		if (strlen($dates) > 0)
		{
			$dates = explode("-",$dates);
			$start_month = substr($dates[0],0,2);
			$start_day = substr($dates[0],3,2);
			$end_month = substr($dates[1],0,2);
			$end_day = substr($dates[1],3,2);
		
			$year = date($YEAR);
		
			//$c["date_start"] = $year . "-" . $start_month . "-" . $start_day;
			$class_db[count($class_db) - 1]["date_end"] = $year . "-" . $end_month . "-" . $end_day;
		}
		continue; //Next CRN
	}
	$c["crn"] = $classdata->item(1)->nodeValue;
	$c["subj"] = $classdata->item(2)->nodeValue;
	$c["course_number"] = $classdata->item(3)->nodeValue;
	$c["section"] = $classdata->item(4)->nodeValue;
	$c["name"] = $classdata->item(7)->nodeValue;
	
	//Get days of week
	$c["dow_monday"] = 0;
	$c["dow_tuesday"] = 0;
	$c["dow_wednesday"] = 0;
	$c["dow_thursday"] = 0;
	$c["dow_friday"] = 0;
	$c["dow_saturday"] = 0;
	$c["dow_sunday"] = 0;
	
	$dow = $classdata->item(8)->nodeValue;
	if (strpos($dow,"M") !== false)
		$c["dow_monday"] = 1;
	if (strpos($dow,"T") !== false)
		$c["dow_tuesday"] = 1;
	if (strpos($dow,"W") !== false)
		$c["dow_wednesday"] = 1;
	if (strpos($dow,"R") !== false)
		$c["dow_thursday"] = 1;
	if (strpos($dow,"F") !== false)
		$c["dow_friday"] = 1;
	if (strpos($dow,"S") !== false)
		$c["dow_saturday"] = 1;
	if (strpos($dow,"U") !== false)
		$c["dow_sunday"] = 1;
	
	//Get start and end times.
	$c["time_start"] = 0;
	$c["time_end"] = 0;
	$time = $classdata->item(9)->nodeValue;
	if (strlen($time) > 0)
	{
		$times = explode("-",$time);
		$start_hour = intval(substr($times[0],0,2));
		$start_minute = intval(substr($times[0],3,2));
		if (strpos($times[0],"am") !== false && $start_hour == 12)
			$start_hour -= 12;
		if (strpos($times[0],"pm") !== false && $start_hour != 12)
			$start_hour += 12;
		$end_hour = intval(substr($times[1],0,2));
		$end_minute = intval(substr($times[1],3,2));
		if (strpos($times[1],"am") !== false && $end_hour == 12)
			$end_hour -= 12;
		if (strpos($times[1],"pm") !== false  && $end_hour != 12)
			$end_hour += 12;
	
		$c["time_start"] = ($start_hour*60)+$start_minute;
		$c["time_end"] = ($end_hour*60)+$end_minute;
	}
	
	$c["capacity"] = $classdata->item(10)->nodeValue;
	$c["instructor"] = $classdata->item(19)->nodeValue;
	//YYYY-MM-DD
	$dates = $classdata->item(20)->nodeValue;
	if (strlen($dates) > 0)
	{
		$dates = explode("-",$dates);
		$start_month = substr($dates[0],0,2);
		$start_day = substr($dates[0],3,2);
		$end_month = substr($dates[1],0,2);
		$end_day = substr($dates[1],3,2);
		
		$year = date($YEAR);
		
		$c["date_start"] = $year . "-" . $start_month . "-" . $start_day;
		$c["date_end"] = $year . "-" . $end_month . "-" . $end_day;
	}
	
	
	$c["location"] = $classdata->item(21)->nodeValue;
	$c["type"] = $classdata->item(22)->nodeValue;
	$c["notes"] = $classdata->item(26)->nodeValue;
	$links = explode("|",$classdata->item(23)->nodeValue);
	if (count($links) == 2)
	{
		$c["link_self"] = $links[0];
		$c["link_other"] = $links[1];
	}
	array_push($class_db,$c);
}

//Insert to MySQL temp DB
foreach ($class_db as $c)
{
	mysql_insert('sections_tmp', $c);
}

//Now we need to process 
function addSections($class_id,$section_info)
{
	global $stdin;
	//Add the first section
	$s = array();
	//Process location:
	$loc = explode(" ",$section_info["location"]);
	if (count($loc) > 1)
	{
		$bldg_id = fetchBuildingID($loc[0]);
		$room_id = fetchRoomID($bldg_id,$loc[1]);
	} else {
		$bldg_id = 0;
		$room_id = 0;
	}
	
	$s["id_string"] = _random_string(16);
	$s["class_id"] = $class_id;
	$s["type"] = $section_info["type"];
	$s["crn"] = $section_info["crn"];
	$s["section"] = $section_info["section"];
	$s["room_id"] = $room_id;
	$s["dow_monday"] = $section_info["dow_monday"];
	$s["dow_tuesday"] = $section_info["dow_tuesday"];
	$s["dow_wednesday"] = $section_info["dow_wednesday"];
	$s["dow_thursday"] = $section_info["dow_thursday"];
	$s["dow_friday"] = $section_info["dow_friday"];
	$s["dow_saturday"] = $section_info["dow_saturday"];
	$s["dow_sunday"] = $section_info["dow_sunday"];
	$s["start_time"] = $section_info["time_start"];
	$s["end_time"] = $section_info["time_end"];
	$s["start_date"] = $section_info["date_start"];
	$s["end_date"] = $section_info["date_end"];
	$s["capacity"] = $section_info["capacity"];
	$s["instructor"] = $section_info["instructor"];
	$s["notes"] = $section_info["notes"];
	
	echo("\tProcessing ID " . $section_info["id"] . " CRN " . $s["crn"] . " (" . $section_info["subj"] . $section_info["course_number"] . ")...");
	//fgets($stdin);
	$section_id = mysql_insert('lc_sections', $s) or die(mysql_error());
	
	//Delete it
	mysql_query("DELETE FROM `sections_tmp`
		WHERE id = " . $section_info["id"]) or die(mysql_error());
	
	//If no links, stop here.
	if (strlen($section_info["link_self"]) < 2)
	{
		echo("\t\tNo linked sections.\n");
		return;
	}
	
	//Find any linked sections:
	$sql = "SELECT * 
	FROM  `sections_tmp` 
	WHERE  `subj` =  '" . $section_info["subj"] . "'
	AND  `course_number` =" . $section_info["course_number"] . "
	AND  `link_other` =  '" . $section_info["link_self"] . "'";
	$sub_q = mysql_query($sql) or die("Error querying for linked classes: " . mysql_error());
	echo("\t\t Found " . mysql_num_rows($sub_q) . " linked sections.\n");
	while ($row = mysql_fetch_array($sub_q))
	{
		addSections($class_id,$row);
	}
}

echo("Processing class links...\n");
$q = mysql_query("SELECT * FROM `sections_tmp`
	WHERE STRCMP(link_self,link_other) <= 0
	LIMIT 0,1");
while (mysql_num_rows($q) > 0)
{
	$section_info = mysql_fetch_array($q);
	
	//TODO: If the section exists, update it and all of its chrilden.
	
	echo("Processing class " . $section_info["subj"] . $section_info["course_number"] . "...\n");
	
	//Add this as a class.
	//Make sure a subject exists for this class.
	$subject_id = fetchSubjectID($section_info["subj"]);
	
	
	$class = array();
	$class["id_string"] = _random_string(16);
	$class["subject_id"] = $subject_id;
	$class["course_number"] = $section_info["course_number"];
	$class["name"] = $section_info["name"];
	$class["institution_id"] = $INSTITUTION_ID;
	
	//Add this class
	$class_id = mysql_insert('lc_classes', $class) or die(mysql_error());
	echo("Class ID " . $class_id . "\n");
	
	//Add all the sections for it (recursion!!!)
	addSections($class_id,$section_info);
	
	//Delete it (this should be handled by addSections
	//mysql_query("DELETE FROM `sections_tmp`
	//	WHERE id = " . $section_info["id"]) or die(mysql_error());
	
	//Look for more
	$q = mysql_query("SELECT * FROM `sections_tmp`
		WHERE STRCMP(link_self,link_other) <= 0
		LIMIT 0,1") or die(mysql_error());
}

?>

