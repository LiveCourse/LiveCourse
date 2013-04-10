<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Join extends CI_Controller {
	public function index()
	{
		$android = strpos($_SERVER['HTTP_USER_AGENT'], 'Android');
		$windows = strpos($_SERVER['HTTP_USER_AGENT'], 'Windows Phone 8');
		if($android)
		{
			//Place holders
			header('Location: http://google.com');
		}
		if($windows)
		{
			//Place holders
			header("Location: windowsphone.com");
		}
		
	}
}

/* End of file join.php */
/* Location: ./application/controllers/join.php */
