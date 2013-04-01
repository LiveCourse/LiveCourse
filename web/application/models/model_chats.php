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
				SELECT lc_chat_messages.id, lc_chat_messages.chat_id, lc_chat_messages.send_time, lc_chat_messages.message_string, lc_users.email, lc_users.display_name FROM `lc_chat_messages`
				JOIN lc_users ON lc_users.id = lc_chat_messages.user_id 
				WHERE lc_chat_messages.chat_id = ' . $chat_id . ' 
				ORDER BY lc_chat_messages.send_time 
				DESC LIMIT ' . $num_lines . ' 
			) sub
			ORDER BY send_time ASC';
		$query = $this->db->query($q)
				->result();
		return $query;
	}
	
	/**
	 * Gets messages from a chat room that were sent AFTER the specified message ID.
	 * chat_id - ID of chat to fetch from
	 * msg_id - msg ID after which to fetch chat messages
	 * returns - array of results
	 */
	function get_messages_after_msg_id($chat_id,$msg_id)
	{
		$query = $this->db
				->select('lc_chat_messages.id, lc_chat_messages.chat_id, lc_chat_messages.send_time, lc_chat_messages.message_string, lc_users.email, lc_users.display_name')
				->where('lc_chat_messages.chat_id',$chat_id)
				->where('lc_chat_messages.id >',$msg_id)
				->from('lc_chat_messages')
				->join('lc_users','lc_users.id = lc_chat_messages.user_id')
				->order_by("send_time", "asc")
				->get()
				->result();
		return $query;
	}
	
	/**
	 *Adds a message to the flagged message table
	 *message_id - ID of message that has been flagged
	 *reporter_id - ID of user that reported message
	 *reason - string that describes why message was reported
	 *time - time message was reported
	 *returns - NULL or FALSE if failed.
	 */
	function flag_message($message_id,$reporter_id,$reason,$time)
	{
		$data = array(
			'message_id'=>$message_id,
			'reporter_id'=>$reporter_id,
			'reason'=>$reason,
			'time_submitted'=>$time	
		);
		
		return $this->db->insert('lc_chat_messages_flagged', $data);
	}
	
	/**
	 *Removes a user from a specific chat
	 *chat_id - ID of the chat to remove the user from
	 *user_id - User who is to be removed
	 *returns - 1 if successful, 0 if not.
	 */
	function unsubscribe_user($chat_id,$user_id)
	{
		$count = $this->db->count_all('lc_chat_participants');
		$this->db
			->where('chat_id', $chat_id)
			->where('user_id', $user_id)
			->from('lc_chat_participants')
			->delete();
		return $count - $this->db->count_all();
	}
	
	/**
	 *Adds a new chat to the database
	 *chat_info - Array of chat information to be added into the database. 
	 *returns - NULL or FALSE if failed.
	 */
	function add_chat($chat_info)
	{
		
		if($chat_info == NULL)
		{
			return false;
		}
		
		$data = array(
			'id_string' => $chat_info['id_string'],
			'subject_id' => $chat_info['subject_id'],
			'course_number' => $chat_info['course_number'],
			'name' => $chat_info['name'],
			'institution_id' => $chat_info['institution_id'],
			'room_id' => $chat_info['room_id'],
			'start_time' => $chat_info['start_time'],
			'end_time' => $chat_info['end_time'],
			'dow_monday' => $chat_info['dow_monday'],
			'dow_tuesday' => $chat_info['dow_tuesday'],
			'dow_wednesday' => $chat_info['dow_wednesday'],
			'dow_thursday' => $chat_info['dow_thursday'],
			'dow_friday' => $chat_info['dow_friday'],
			'dow_saturday' => $chat_info['dow_saturday'],
			'dow_sunday' => $chat_info['dow_sunday'],
			);
		
		return $this->db->insert('lc_chats', $chat_info);
	}
	
	/**
	 *Remove a chat from the database, make sure to clear any participants.
	 *chat_id - the ID number of the chat room to be removed
	 *returns true if successful, false if failed
	 */
	function remove_chat($chat_id)
	{
		
		$count = $this->db->count_all('lc_chats');
		
		$this->db
			->where('id', $chat_id)
			->from('lc_chats')
			->delete();

		
		$this->db
			->where('chat_id', $chat_id)
			->from('lc_chat_participants')
			->delete();
				
						
		if($count - $this->db->count_all('lc_chats') <= 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	/**
	 *Get all of the users subscribed to a chat room
	 *chat_id - ID of the chat to get participants
	 *returns an array of users that are subscribed, 0 on first failure, null on second.
	 */
	function get_participants($chat_id)
	{
		$user_ids = $this->db
				->select('user_id')
				->where('chat_id', $chat_id)
				->from('lc_chat_participants')
				->get()
				->result();
		
		if(!$user_ids)
		{
			return 0;
		}
		
		$users = null;
		
		foreach ($user_ids as $user_id)
		{
			$users[] = $this->db
					->select('id, email, display_name')
					->where('id', $user_id->user_id)
					->from('lc_users')
					->get()
					->result();
		}
		return $users;
	}
}
