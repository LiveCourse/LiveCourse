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
	
	/**
	 * Gets chat information by ID
	 * chat_id - ID of chat
	 * returns - database row of chat specified
	 */
	function get_chat_by_id($chat_id)
	{
		$query = $this->db
				->where('id',$chat_id)
				->from('lc_chats')
				->get()
				->result();
		if (count($query) <= 0)
			return -1;
		else
			return $query[0];
	}
	
	/**
	 * Fetches chats that the specified user is a participant in.
	 * user_id - ID of user
	 * returns - array of chat information
	 */
	function get_subscribed_chats($user_id)
	{
		$query = $this->db
				->where('user_id',$user_id)
				->from('lc_chat_participants')
				->join('lc_chats','lc_chats.id = lc_chat_participants.chat_id')
				->get()
				->result();
		return $query;
	}
	
	/**
	 * Checks if the specified user is subscribed to the specified chat.
	 * user_id - ID of the user
	 * chat_id - integer ID of the chat
	 * returns - TRUE if user is subscribed, FALSE otherwise.
	 */
	function is_user_subscribed($user_id,$chat_id)
	{
		$query = $this->db
				->where('user_id',$user_id)
				->where('chat_id',$chat_id)
				->from('lc_chat_participants')
				->get()
				->result();
		if (count($query) > 0)
			return true;
		return false;
	}
	
	/**
	 * Sends a message from the specified user to the specified chat room.
	 * user_id - ID of the message author
	 * chat_id - ID of the chat room the message is destined for.
	 * message_string - SANITIZED message string.
	 * returns - NULL or FALSE if failed.
	 */
	function send_message($user_id,$chat_id,$message_string)
	{
		$data = array(
				'chat_id'	=> $chat_id,
				'user_id'	=> $user_id,
				'send_time'	=> time(),
				'message_string'=> $message_string
				);
		return $this->db->insert('lc_chat_messages', $data); //Should return NULL or FALSE if failed.
	}
	
	/**
	 * Gets the specified number of latest messages from a chat room
	 * chat_id - ID of chat to fetch from
	 * num_lines - Number of lines to retrieve
	 * returns - array of results
	 */
	function get_num_latest_messages($chat_id,$num_lines = 100)
	{
		$q = 'SELECT * FROM (
				SELECT * FROM `lc_chat_messages`
				WHERE `chat_id` = ' . $chat_id . '
				ORDER BY send_time
				DESC LIMIT ' . $num_lines . '
			) sub
			ORDER BY send_time ASC';
		$query = $this->db->query($q)
				->result();
		return $query;
	}
}
