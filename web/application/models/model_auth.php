<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Auth extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
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
	
	/**
	 * Generates a random authentication token for use in verifying users' passwords,
	 * stores it in the authentication database for later verification.
	 * userid - identification number of the user
	 * returns - random authentication token string
	 * TODO: check for duplicates
	 */
	function generate_token($userid,$deviceid = 0)
	{
		$str = $this->_random_string(16);
		$data = array(
				'user_id'	=> $userid,
				'token'		=> $str,
				'lastused'	=> 0,
				'device'	=> $deviceid
				);
		$this->db->insert('lc_authentication', $data);
		return $str;
	}
	
	/**
	 * Fetches a user by their authentication token.
	 * token - authentication token
	 * returns - user row
	 */
	function fetch_user_by_token($token)
	{
		$this->db->select('lc_users.*');
		$this->db->from('lc_authentication');
		$this->db->where('token',$token);
		$this->db->join('lc_users', 'lc_users.id = lc_authentication.user_id');
		$query = $this->db->get();
		return $query->result();
	}

}
