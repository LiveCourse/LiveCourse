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
				->select('id, email, display_name, jointime, color_preference')
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
	 * Update a user's color preference
	 * color - color code to set as user's color
	 * returns 
	 */
	function update_user_color($user_id,$color)
	{
		$data = array(
			'color_preference' => $color
		);
		$this->db->where('id', $user_id);
		return $this->db->update('lc_users', $data); 
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
	 * returns - FALSE on failure.
	 */
	function remove_user_by_id($user_id)
	{
		$data = array(
				'id' => $user_id,
				);
		return $this->db->delete('lc_users', $data);
	}
	
	/**
	 *Retrieves an android user from the gcm database
	 *user_id - ID of the user
	 *regid - Registration ID of the device
	 *returns the user(s) with the given credentials, else false.
	 */
	function fetch_android_user($user_id, $gcm_regid)
	{
		$droid_user = $this->db
				->where('user_id', $user_id)
				->where('gcm_regid', $gcm_regid)
				->from('lc_gcm_users')
				->join('lc_users','lc_users.id = lc_gcm_users.user_id')
				->get()
				->result();
				
		if ($droid_user && count($droid_user) == 1)
		{
			return $droid_user;
		}
		else
		{
			return FALSE;
		}
	}
	
	/**
	 *Retrieves all android users from the gcm database
	 *returns the users, else false.
	 */
	function fetch_all_android_user()
	{
		$droid_users = $this->db
				->from('lc_gcm_users')
				->join('lc_users','lc_users.id = lc_gcm_users.user_id')
				->get()
				->result();
				
		if ($droid_users)
		{
			return $droid_users;
		}
		else
		{
			return FALSE;
		}
	}
	
	/**
	 *Adds an android user to the database
	 *user_id - ID of the user
	 *email - Email of the user
	 *name - Display name of the user
	 *gcm_regid - Registration ID of the device
	 *returns inserted information if successful, otherwise null/false.
	 */
	function add_android_user($user_id, $email, $name, $gcm_regid)
	{
		$data = array(
			'user_id' => $user_id,
			'gcm_regid' => $gcm_regid,
			'created_at' => time(),
		);
		
		$this->db->insert('lc_gcm_users', $data);
		
		if ($this->db->affected_rows() > 0)
		{
			return $data;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Removes an android device from the database.
	 * Should also remove ALL RELATED DATA
	 * user_id - identification number of user
	 * reg_id - identification string of the device
	 * returns - number of rows effected
	 */
	function remove_android($user_id, $reg_id)
	{
		$data = array(
				'user_id' => $user_id,
				'gcm_regid' => $reg_id,
				);
		$this->db->delete('lc_gcm_users', $data);
		return $this->db->affected_rows();
	}
	
	/**
	 *Changes the user's password
	 *user_id - the ID of the user whose password we are changing
	 *password - SHA1 hash of the user's password
	 *returns TRUE or FALSE depending on success or failure.
	 */
	function change_user_password($user_id, $password)
	{
		$data = array(
			'id' => $user_id,
			'password' => $password
		);
		$this->db->where('id',$user_id);
		$this->db->update('lc_users', $data);
		if($this->db->affected_rows() <= 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 *Changes the user's name
	 *user_id - the ID of the user whose password we are changing
	 *name - the user's new name
	 *returns TRUE or FALSE depending on success or failure.
	 */
	function change_user_name($user_id, $name)
	{
		$data = array(
			'id' => $user_id,
			'display_name' => $name
		);
		$this->db->where('id',$user_id);
		$this->db->update('lc_users', $data);
		if($this->db->affected_rows() <= 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

}
