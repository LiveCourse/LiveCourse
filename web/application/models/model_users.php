<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Users extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}

	/**
	 * Fetch a user's information
	 * user_id - User's identification number
	 * returns - array of user objects. Empty array on failure.
	 */
	function fetch_user_by_id($user_id)
	{
		$query = $this->db
				->where('id', $user_id)
				->from('lc_users')
				->get();
		return $query->result();
	}
	
	/**
	 * Fetch a user's information
	 * user_id - User's identification number
	 * fields - array of fields to return
	 * returns - array of user objects. Empty array on failure.
	 */
	function fetch_user_fields($user_id, $fields)
	{
		$query = $this->db
				->select(implode(",", $fields))
				->where('id', $user_id)
				->from('lc_users')
				->get();
		return $query->result();
	}
	/**
	 * Fetch every user in the database
	 * returns - an array of user objects. Empty array on failure.
	 */
	function fetch_all_users()
	{
		$query = $this->db
			->from('lc_users')
			->get();
		return $query->result();
	}
	
	/**
	 * Fetch a user's information
	 * user_email - E-mail address associated with said user
	 * returns - array of user objects. Empty array on failure.
	 */
	function fetch_user_by_email($user_email)
	{
		$query = $this->db
				->like('email', $user_email,'none')
				->from('lc_users')
				->get();
		return $query->result();
	}
	
	/**
	 * Adds a user to the database.
	 * email - e-mail address for the new user
	 * password - SHA1 encrypted password for the new user
	 * display_name - Display Name for the new user
	 * jointime (optional) - join time in UNIX epoch of the new user
	 * returns - NULL or false if failed
	 */
	function add_user($email,$password,$display_name,$jointime = "")
	{
		if ($jointime == "") $jointime = time(); //Set jointime to current time if none specified
		$data = array(
				'email'		=> $email,
				'password'	=> $password,
				'display_name'	=> $display_name,
				'jointime'	=> $jointime
				);
		return $this->db->insert('lc_users', $data); //Should return NULL or FALSE if failed.
	}
	
	/**
	 * Removes a user from the database.
	 * Should also remove ALL RELATED DATA
	 * user_id - identification number of user
	 * returns - FALSE or NULL on failure.
	 */
	function remove_user_by_id($user_id)
	{
		$data = array(
				'id' => $user_id,
				);
		return $this->db->delete('lc_users', $data);
	}

}
