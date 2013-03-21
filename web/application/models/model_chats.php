<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Chats extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}

	/**
	 * Fetch a list of available chats by searching for some broad parameters
	 * query_string - String to search courses for
	 * returns a result consisting of matches.
	 */
	function search_chats($query_string)
	{
		$query = $this->db
				->like('course_number', $query_string)
				->or_like('name', $query_string)
				->from('lc_chats')
				->get();
		return $query->result();
	}
	
	/**
	 * Returns the integer chat ID of the chat ID string provided
	 * chat_id_string - ID string of chat to find
	 * returns - integer index ID of specified chat.
	 */
	function get_id_from_string($chat_id_string)
	{
		$query = $this->db
				->select('id')
				->where('id_string',$chat_id_string)
				->from('lc_chats')
				->get()
				->result();
		if (count($query) <= 0)
			return -1;
		else
			return $query[0]->id;
	}
	
	/**
	 * Joins a user to a chat specified by ID string
	 * user_id - ID of user to join to specified chat
	 * chat_id - ID integer of chat to join
	 * permissions - TODO allow setting user as admin
	 * returns NULL or FALSE if failed.
	 */
	function join_chat_by_id($user_id,$chat_id,$permissions = 0)
	{
		//Now insert the data
		$data = array(
				'chat_id'	=> $chat_id,
				'user_id'	=> $user_id,
				'permissions'	=> $permissions,
				'jointime'	=> time()
				);
		return $this->db->insert('lc_chat_participants', $data); //Should return NULL or FALSE if failed.
	}

}
