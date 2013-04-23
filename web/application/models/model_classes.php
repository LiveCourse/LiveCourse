<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Classes extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}
	
	/**
	 * Fetch a list of available chats by searching for some parameters
	 * query_parameters - Array of parameters to refine search
	 * 	subject_code - Subject code
	 * 	course_number - Course Number
	 * returns a result consisting of matches.
	 */
	function search_classes($query_parameters)
	{
		$query = $this->db
				->select('lc_classes.id_string as class_id_string, lc_subjects.code as subject_code, lc_classes.course_number, lc_classes.name')
				->from('lc_classes')
				->group_by(array('lc_subjects.code','lc_classes.course_number'))
				->join('lc_subjects','lc_subjects.id = lc_classes.subject_id');
		if (isset($query_parameters["subject_code"]))
			$query->like('lc_subjects.code', $query_parameters["subject_code"]);
		if (isset($query_parameters["course_number"]))
			$query->like('lc_classes.course_number', $query_parameters["course_number"]);
		return $query->get()->result();
	}
	
	/**
	 * Fetch a list of subjects
	 * returns a result consisting of matches.
	 */
	function fetch_subjects()
	{
		$query = $this->db
				->from('lc_subjects')
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
				->where('id_string = ',$chat_id_string)
				->from('lc_classes')
				->get()
				->result();
		if (count($query) <= 0)
			return -1;
		else
			return $query[0]->id;
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
				->from('lc_classes')
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
		/*$query = $this->db
				->where('user_id',$user_id)
				->from('lc_chat_participants')
				->join('lc_classes','lc_classes.id = lc_chat_participants.chat_id')
				->get()
				->result();
		*/
		$query = $this->db
				->select('lc_classes.*')
				->distinct()
				->from('lc_sections')
				->join('lc_section_participants','lc_sections.id = lc_section_participants.section_id')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->where('lc_section_participants.user_id',$user_id)
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
				->select('lc_classes.*')
				->from('lc_sections')
				->join('lc_section_participants','lc_sections.id = lc_section_participants.section_id')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->where('lc_section_participants.user_id',$user_id)
				->where('lc_classes.id',$chat_id)
				->get()
				->result();
		if (count($query) > 0)
			return true;
		return false;
	}

	/**
	 *Gets the time of the most recent message sent by a user
	 *user_id - ID of the user in question
	 *returns the time or NULL if no messages in the chat room
	 */
	function get_time_newest($user_id){

		$time = $this->db
				->select()
				->from('lc_chat_messages')
				->where('user_id',$user_id)
				->order_by("send_time", "desc")
				->limit(1)
				->get()
				->result();
		if (count($time) <= 0)
			return NULL;

		return $time[0]->send_time;
	}

	/**
	 * Sends a message from the specified user to the specified chat room.
	 * user_id - ID of the message author
	 * chat_id - ID of the chat room the message is destined for.
	 * message_string - SANITIZED message string.
	 * returns - NULL or FALSE if failed.
	 */
	function send_message($user_id, $chat_id, $message_string, $send_time)
	{
		$this->load->model('Model_Users');
		$this->Model_Users->update_user_focus_time($user_id);
		$data = array(
				'class_id'	=> $chat_id,
				'user_id'	=> $user_id,
				'send_time'	=> $send_time,
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
	function get_num_latest_messages($class_id,$num_lines = 100)
	{
		$q = 'SELECT * FROM (
				SELECT lc_chat_messages.id, lc_classes.id_string, lc_chat_messages.send_time, lc_chat_messages.message_string, lc_chat_messages.user_id, lc_users.email, lc_users.display_name FROM `lc_chat_messages`
				JOIN lc_users ON lc_users.id = lc_chat_messages.user_id
				JOIN lc_classes ON lc_classes.id = lc_chat_messages.class_id
				WHERE lc_chat_messages.class_id = ' . $class_id . '
				ORDER BY lc_chat_messages.send_time
				DESC LIMIT ' . $num_lines . '
			) sub
			ORDER BY send_time ASC';
		$query = $this->db->query($q)
				->result();
		return $query;
	}

	/**
	 * Gets messages in the given time frame from given room.
	 * chat_id - ID of chat to fetch from
	 * start_time - Beginning of time window
	 * end_time - End of time window
	 * returns - array of results
	 */
	function get_time_frame_messages($class_id,$start_time,$end_time)
	{
		$query = $this->db
				->select('lc_chat_messages.id, lc_classes.id_string, lc_chat_messages.send_time, lc_chat_messages.message_string, lc_chat_messages.user_id, lc_users.email, lc_users.display_name')
				->where('lc_chat_messages.class_id',$class_id)
				->where('lc_chat_messages.send_time >=',$start_time)
				->where('lc_chat_messages.send_time <=',$end_time)
				->from('lc_chat_messages')
				->join('lc_users','lc_users.id = lc_chat_messages.user_id')
				->join('lc_classes','lc_classes.id = lc_chat_messages.class_id')
				->order_by("send_time", "asc")
				->get()
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
				->select('lc_chat_messages.id, lc_classes.id_string, lc_chat_messages.send_time, lc_chat_messages.message_string, lc_chat_messages.user_id, lc_users.email, lc_users.display_name')
				->where('lc_chat_messages.class_id',$chat_id)
				->where('lc_chat_messages.id >',$msg_id)
				->from('lc_chat_messages')
				->join('lc_users','lc_users.id = lc_chat_messages.user_id')
				->join('lc_classes','lc_classes.id = lc_chat_messages.class_id')
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
	function flag_message($message_id,$reporter_id,$reason,$time = "")
	{
		if($time == "") $time = time();
		$data = array(
			'message_id'=>$message_id,
			'reporter_id'=>$reporter_id,
			'reason'=>$reason,
			'time_submitted'=>$time
		);

		return $this->db->insert('lc_chat_messages_flagged', $data);
	}

	/**
	 * Removes a user from ALL SECTIONS associated with this class.
	 * class_id - ID of the class to remove the user from
	 * user_id - User who is to be removed
	 * returns - 1 if successful, 0 if not.
	 */
	function leave_class_by_id($class_id,$user_id)
	{
		$count = $this->db->count_all('lc_section_participants');
		//Uh oh, codeigniter doesn't like joins in delete queries... looks like we get to write it ourselves.
		$sql = "DELETE lc_section_participants FROM lc_section_participants
			JOIN lc_sections ON lc_sections.id = lc_section_participants.section_id
			JOIN lc_classes ON lc_classes.id = lc_sections.class_id
			WHERE lc_section_participants.user_id = ? AND lc_classes.id = ?";
		$this->db->query($sql,array($user_id, $class_id));
		return $count - $this->db->count_all('lc_section_participants');

	}

	/**
	 *Adds a new chat to the database
	 *chat_info - Array of chat information to be added into the database.
	 *returns - NULL or FALSE if failed.
	 */
	function add_class($chat_info)
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
			'institution_id' => $chat_info['institution_id']
			);

		return $this->db->insert('lc_classes', $chat_info);
	}
	/**
	 *Checks to see if a user has already reported a message
	 *reporter_id - the ID number of the reporter in question
	 *message_id - the ID number of the message in question
	 *returns true if the reporter has reported this message, false if it hasnt
	 */
	function check_reporter($reporter_id, $message_id){

		$count = $this->db->where('reporter_id',$reporter_id)
								->where('message_id',$message_id)
								->get('lc_chat_messages_flagged')
								->result();

		if(count($count)>=1)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	/**
	 *Checks to see if a message exists
	 *message_id - the ID number of the message to be checked
	 *returns true if the message exists, or false if it doesnt
	 */
	function check_message($message_id)
	{
		$count = $this->db->where('id',$message_id)
					->get('lc_chat_messages')
					->result();
		if(count($count)>=1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks the flagged status of a message
	 * If the message has been flagged more than 5 times, remove it
	 * message_id - the ID number of the message to be checked
	 * returns the count of messages in flagged_messages
	 */

	function check_flagged($message_id)
	{
		$count = $this->db->where('message_id',$message_id)
				->get('lc_chat_messages_flagged')
				->result();
		return count($count);
	}

	/**
	 * Removes a message from a chat room
	 * message_id - the ID number of the message to be removed
	 * returns true if successful, false if failed
	 */
	function remove_message($message_id)
	{
		$count = $this->db->count_all('lc_chat_messages');

		$this->db
			->where('id',$message_id)
			->from('lc_chat_messages')
			->delete();

		$this->db
			->where('message_id',$message_id)
			->from('lc_chat_messages_flagged')
			->delete();

		if($count - $this->db->count_all('lc_chat_messages')<=0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Remove a chat from the database, make sure to clear any sections and participants.
	 * chat_id - the ID number of the chat room to be removed
	 * returns true if successful, false if failed
	 */
	function remove_chat($class_id)
	{
		$count = $this->db->count_all('lc_classes');

		//Clear all participants
		//Uh oh, codeigniter doesn't like joins in delete queries... looks like we get to write it ourselves.
		$sql = "DELETE lc_section_participants FROM lc_section_participants
			JOIN lc_sections ON lc_sections.id = lc_section_participants.section_id
			WHERE lc_sections.class_id = ?";
		$this->db->query($sql,array($class_id));

		//Clear all sections
		$this->db
			->where('class_id', $class_id)
			->from('lc_sections')
			->delete();

		//Clear class
		$this->db
			->where('id', $class_id)
			->from('lc_classes')
			->delete();


		if($count - $this->db->count_all('lc_classes') <= 0)
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
	 *user_id - User ID to determine which users are ignored.
	 *returns an array of users that are subscribed, 0 on first failure, null on second.
	 */
	 /*

	 SELECT lc_users.id,lc_users.display_name,lc_users.jointime,lc_users.color_preference,lc_users.time_lastfocus,lc_users.time_lastrequest, (lc_users_ignored.ignore_id IS NOT NULL) AS ignored FROM `lc_chat_participants`
JOIN `lc_users` on lc_users.id = lc_chat_participants.user_id
LEFT OUTER JOIN `lc_users_ignored` on lc_users_ignored.ignore_id = lc_chat_participants.user_id AND lc_users_ignored.user_id=22
WHERE lc_chat_participants.chat_id = 1

*/
	function get_participants($class_id,$user_id = -1)
	{
		$users = $this->db
				->select('lc_users.id, lc_users.display_name, lc_users.jointime, lc_users.email, lc_users.time_lastfocus, lc_users.time_lastrequest, (lc_users_ignored.ignore_id IS NOT NULL) AS ignored')
				->distinct()
				->from('lc_section_participants')
				->join('lc_users','lc_users.id = lc_section_participants.user_id')
				->join('lc_sections','lc_sections.id = lc_section_participants.section_id')
				->join('lc_users_ignored','lc_users_ignored.ignore_id = lc_section_participants.user_id AND lc_users_ignored.user_id=' . $user_id,'left outer')
				->where('lc_sections.class_id', $class_id)
				->get()
				->result();

		return $users;
	}

	/**
	* DEPRECATED
	*Alters a user administrative permissions
	*chat_id - ID of the chatroom in which they are to receive permissions
	*user_id - ID of the user who will be elevated
	*permissions - The desired state of permissions, 1 being admin, 0 being standard user
	*Returns TRUE on success or FALSE/NULL on failure
	*/
	/*
	function change_user_permissions($chat_id,$user_id,$permissions)
	{
		return $this->db
			->where('chat_id', $chat_id)
			->where('user_id', $user_id)
			->update('lc_chat_participants', array('permissions' => $permissions));
	}
	*/

	/**
	* DEPRECATED
	*Checks the user's current permissions
	*chat_id - ID of the chat in which to check for permissions
	*user_id - ID of the user whose permissions we're looking up
	*Returns TRUE(1) if admin, FALSE(0) if not.
	*/
	/*
	function check_user_permissions($chat_id, $user_id)
	{
		return $this->db
			->select('permissions')
			->where('chat_id', $chat_id)
			->where('user_id', $user_id)
			->from('lc_chat_participants')
			->get()
			->result();
	}
	*/

	function add_file($user_id, $chat_id, $filename, $original, $size, $message_id, $time = '')
	{
		if($time == '')
			$time = time();
		$data = array(
			'user_id' => $user_id,
			'chat_id' => $chat_id,
			'filename' => $filename,
			'original_name' => $original,
			'size' => $size,
			'message_id' => $message_id,
			'uploaded_at' => $time,
		);
		return $this->db->insert('lc_chat_files', $data);

	}

	function get_file_info($message_id)
	{
		return $this->db
			->where('message_id', $message_id)
			->get('lc_chat_files')
			->row_array();
	}

	function remove_file($user_id, $chat_id, $filename)
	{
		return $this->db->delete('lc_chat_files', array(
			'user_id' => $user_id,
			'chat_id' => $chat_id,
			'filename' => $filename,
		));
	}

	/**
	 * Gets the specified number of latest files from a chat room
	 * chat_id - ID of chat to fetch from
	 * num_lines - Number of lines to retrieve
	 * returns - array of results
	 */
	function get_num_latest_files($class_id, $num_lines = 10)
	{
		return $query = $this->db
			->limit($num_lines)
			->order_by('uploaded_at', 'asc')
			->get('lc_chat_files')
			->result();
	}

	/**
	 * Gets files in the given time frame from given room.
	 * chat_id - ID of chat to fetch from
	 * start_time - Beginning of time window
	 * end_time - End of time window
	 * returns - array of results
	 */
	function get_time_frame_files($class_id, $start_time, $end_time)
	{
		$query = $this->db
				->where('uploaded_at >=', $start_time)
				->where('uploaded_at <=', $end_time)
				->where('chat_id', $class_id)
				->from('lc_chat_files')
				->order_by('uploaded_at', 'asc')
				->get()
				->result();
		return $query;
	}

	/**
	 * Gets files from a chat room that were sent AFTER the specified message ID.
	 * chat_id - ID of chat to fetch from
	 * msg_id - msg ID after which to fetch chat messages
	 * returns - array of results
	 */
	function get_files_after_msg_id($chat_id, $msg_id)
	{
		return $query = $this->db
				->where('message_id >', $msg_id)
				->where('chat_id', $chat_id)
				->order_by('uploaded_at', 'asc')
				->get('lc_chat_files')
				->result();
	}

	/**
	 *Retrieves all files by a specific chat
	 *
	 *chat_id - the numerical ID of the chat from which to recover the files
	 *
	 *returns the array of results
	 */
	function get_files_by_class($chat_id)
	{
		return $query = $this->db
				->where('chat_id', $chat_id)
				->get('lc_chat_files')
				->result();
	}
}
