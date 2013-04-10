<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Join extends CI_Controller 
{
	public function _remap($method)
	{
		$this->index($method);
	}
	public function index($chat_id_string)
	{
		$android = strpos($_SERVER['HTTP_USER_AGENT'], 'Android');
		$windows = strpos($_SERVER['HTTP_USER_AGENT'], 'Windows Phone 8');
		if ($android)
		{
			//Place holders
			header('Location: http://google.com');
		}
		else if ($windows)
		{
			//Place holders
			header("Location: http://windowsphone.com");
		}
		else
		{
			header("Location: http://livecourse.net");
		}
		
	}
}

/* End of file join.php */
/* Location: ./application/controllers/join.php */
