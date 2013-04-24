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
	 * Update a user's last focus time to NOW.
	 * returns 
	 */
	function update_user_focus_time($user_id)
	{
		$data = array(
			'time_lastfocus' => time()
		);
		$this->db->where('id', $user_id);
		return $this->db->update('lc_users', $data); 
	}
	
	/**
	 * Update a user's last request time to NOW.
	 * returns 
	 */
	function update_user_request_time($user_id)
	{
		$data = array(
			'time_lastrequest' => time()
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
	 
	function remove_user_by_id($user_id)
	{
		$data = array(
				'id' => $user_id,
				);
		
		$data2 = array(
				'user_id' => $user_id,
				);
		
		$this->db->delete('lc_chat_participants', $data2);
		
		$this->db->delete('lc_gcm_users', $data2)
		
		$this->db->delete('lc_authentication', $data2)
		
		return $this->db->delete('lc_users', $data);
	}*/
	
	/**
	 *Retrieves an android user from the gcm database
	 *user_id - ID of the user
	 *dev_id - Device ID of the device
	 *returns the user(s) with the given credentials, else false.
	 */
	function fetch_android_user($user_id, $dev_id)
	{
		$droid_user = $this->db
				->where('user_id', $user_id)
				->where('device_id', $dev_id)
				->from('lc_gcm_users')
				->join('lc_users','lc_users.id = lc_gcm_users.user_id')
				->get()
				->result();
				
		return $droid_user;
	}
	
	/**
	 *Retrieves an android user from the gcm database by their device ID
	 *dev_id - Device ID of the device
	 *returns the user(s) with the given credentials, else false.
	 */
	function fetch_android_user_by_device($dev_id)
	{
		$droid_user = $this->db
				->where('device_id', $dev_id)
				->from('lc_gcm_users')
				->get()
				->result();
				
		return $droid_user;
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
				
		return $droid_users;
	}
	
	/**
	 *Adds an android user to the database
	 *user_id - ID of the user
	 *email - Email of the user
	 *name - Display name of the user
	 *gcm_regid - Registration ID of the device
	 *returns inserted information if successful, otherwise null/false.
	 */
	function add_android_user($user_id, $gcm_regid,$dev_id, $jointime = "")
	{
		if($jointime == "") $jointime = time();
		$data = array(
			'user_id' => $user_id,
			'gcm_regid' => $gcm_regid,
			'created_at' => $jointime,
			'device_id' => $dev_id,
		);
		
		return $this->db->insert('lc_gcm_users', $data);
		
	}
	
	/**
	 * Removes an android device from the database.
	 * Should also remove ALL RELATED DATA
	 * user_id - identification number of user
	 * dev_id - unique identification string of the device
	 * returns - number of rows effected
	 */
	function remove_android($user_id, $dev_id)
	{
		$data = array(
				'user_id' => $user_id,
				'device_id' => $dev_id,
				);
		return $this->db->delete('lc_gcm_users', $data);
		
	}
	
	/**
	 * Removes an android device from the database.
	 * Should also remove ALL RELATED DATA
	 * user_id - identification number of user
	 * dev_id - unique identification string of the device
	 * returns - number of rows effected
	 */
	function remove_android_by_device($dev_id)
	{
		$data = array(
				'device_id' => $dev_id,
				);
		return $this->db->delete('lc_gcm_users', $data);
		
	}
	
	/**
	 * Adds a windows phone user to the database
	 * user_id - ID of the user
	 * dev_id - ID of device
	 * url - Push URL
	 * jointime - time added
	 * returns inserted information if successful, otherwise null/false.
	 */
	function add_wp_user($user_id, $dev_id,$channel,$url, $jointime = "")
	{
		if($jointime == "") $jointime = time();
		$data = array(
			'user_id' => $user_id,
			'device_id' => $dev_id,
			'push_url' => $url,
			'channel' => $channel,
			'timeadded' => $jointime
		);
		
		return $this->db->insert('lc_wp_users', $data);
		
	}
	
	/**
	 * Updates a windows phone row
	 * dev_id - ID of device
	 * url - Push URL
	 * returns inserted information if successful, otherwise null/false.
	 */
	function update_wp_user($dev_id,$channel,$url)
	{
		if($jointime == "") $jointime = time();
		$data = array(
			'push_url' => $url
		);
		$this->db->where('device_id',$dev_id);
		$this->db->where('channel',$channel);
		return $this->db->update('lc_wp_users', $data);
	}
	
	/**
	 * Retrieves a windows phone user by their device ID
	 * dev_id - Device ID of the device
	 * returns the user(s) with the given credentials, else false.
	 */
	function fetch_wp_by_device($dev_id,$channel)
	{
		$wp_user = $this->db
				->where('device_id', $dev_id)
				->where('channel', $channel)
				->from('lc_wp_users')
				->get()
				->result();
				
		return $wp_user;
	}
	
	/**
	 * Removes a windows phone device from the database.
	 * dev_id - unique identification string of the device
	 * returns - number of rows effected
	 */
	function remove_wp_by_device($dev_id,$channel)
	{
		$data = array(
			'device_id' => $dev_id,
			'channel' => $channel
			);
		return $this->db->delete('lc_wp_users', $data);
		
	}
	
	/**
	* This function will grab all of the windows phone devices that are subscribed to the given chat
	* chat_id - ID, numerical, of the chat that we want windows phone users notified of
	* returns the array of people who are registered with a windows phone device to this chat
	*/
	function fetch_all_subscribed_wp_user($class_id,$channel)
	{
		$users = $this->db
				->select('lc_wp_users.user_id, lc_wp_users.device_id, lc_wp_users.push_url')
				->distinct()
				->from('lc_wp_users')
				->join('lc_section_participants', 'lc_section_participants.user_id = lc_wp_users.user_id' )
				->join('lc_sections','lc_sections.id = lc_section_participants.section_id')
				->where('lc_sections.class_id',$class_id)
				->where('lc_wp_users.channel',$channel)
				->get()
				->result();
		return $users;
	}
	
	/**
	 * Changes the user's password
	 * user_id - the ID of the user whose password we are changing
	 * password - SHA1 hash of the user's password
	 * returns TRUE or FALSE depending on success or failure.
	 */
	function change_user_password($user_id, $password)
	{
		$data = array(
			'id' => $user_id,
			'password' => $password
		);
		$this->db->where('id',$user_id);
		return $this->db->update('lc_users', $data);
	}
	
	/**
	 *Changes the user's name
	 *user_id - the ID of the user whose password we are changing
	 *name - the user's new name
	 *returns TRUE or FALSE depending on success or failure.
	 */
	function change_display_name($user_id, $name)
	{
		$data = array(
			'id' => $user_id,
			'display_name' => $name
		);
		$this->db->where('id',$user_id);
		return $this->db->update('lc_users', $data);
	}
	
	/**
	 *Ignores a user
	 *user_id - ID of the user who is ignoring
	 *ignore_id - ID of the user being ignored
	 *returns TRUE on success, FALSE on failure
	 */
	function ignore_user($user_id, $ignore_id)
	{
		$data = array(
			'user_id' => $user_id,
			'ignore_id' => $ignore_id,
		);
		return $this->db->insert('lc_users_ignored', $data);
	}
	
	/**
	 *Unignores a user
	 *user_id - ID of the user who is ignoring
	 *ignore_id - ID of the user being unignored
	 *returns TRUE on success, FALSE on failure
	 */
	function unignore_user($user_id, $unignore_id)
	{
		$this->db->where('user_id', $user_id);
		$this->db->where('ignore_id', $unignore_id);
		return $this->db->delete('lc_users_ignored'); 
	}
	
	/**
	 *Checks to see if a user has been ignored by another
	 *user_id - ID of the user who is ignoring
	 *ignore_id - ID of the user being ignored
	 *returns TRUE on success, FALSE on failure
	 */
	function check_if_ignored($user_id, $ignore_id)
	{
		
		$data = array(
			'user_id' => $user_id,
			'ignore_id' => $ignore_id,
		);
		
		$existing = $this->db
				->where('user_id', $user_id)
				->where('ignore_id', $ignore_id)
				->from('lc_users_ignored')
				->get();
				
		if($existing->num_rows >= 1)
			return true;
		
		return false;
	}
	
	/**
	*This function will grab all of the android devices that are subscribed to the given chat
	*chat_id - ID, numerical, of the chat that we want android users notified of
	*returns the array of people who are registered with an android device to this chat
	*/
	function fetch_all_subscribed_android_user($class_id)
	{
		$users = $this->db
				->select('lc_gcm_users.user_id, lc_gcm_users.gcm_regid')
				->from('lc_gcm_users')
				->join('lc_section_participants', 'lc_section_participants.user_id = lc_gcm_users.user_id' ) // AND lc_chat_participants.chat_id = ' . $chat_id
				->join('lc_sections','lc_sections.id = lc_section_participants.section_id')
				->where('lc_sections.class_id',$class_id)
				->get()
				->result();
		return $users;
	}

}
