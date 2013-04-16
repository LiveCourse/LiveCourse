<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
require(APPPATH.'libraries/REST_Controller.php');

/*
 * This is a RESTFUL class. Each method should fall under a RESTful HTTP state
 * (GET, PUT, POST, or DELETE)
 * See http://en.wikipedia.org/wiki/Representational_state_transfer#RESTful_web_services
 * See also https://github.com/philsturgeon/codeigniter-restserver/blob/master/README.md#requirements
 * NOTE: POST Updates a resource. PUT CREATES or COMPLETELY replaces what is already there.
 */

setlocale(LC_ALL, 'en_US.UTF8');
class Chats extends REST_Controller
{
	/**
	 *Fetches all the available chats that the user is subscribed to
	 *returns - All of the chats and their information
	 */
	public function index_get()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		
		$user_id = $this->get('user_id');
		if (!isset($user_id) || $user_id == "")
			$user_id = $this->authenticated_as;
		
		$chats = $this->Model_Chats->get_subscribed_chats($user_id);
		$this->response($chats,200);
	}

	/**
	 * Fetches chat information on a specific chat
	 * id - String form of chat ID
	 * returns - chat information
	 */
	public function info_get()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');
		$chat_id_string = $this->get('id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("Empty queries can not be processed.")),403);
			return;
		}
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat could not be found.")),404);
			return;
		}
		$chat_info = $this->Model_Chats->get_chat_by_id($chat_id);
		$this->response($chat_info);
	}

	/**
	 *Searches for a specific chat based on a keyword query
	 *returns - the chats that match the query
	 */
	public function search_get()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');
		$query_string = $this->get('query');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		if (strlen($query_string) <= 0)
		{
			$this->response($this->rest_error(array("Empty queries can not be processed.")),403);
			return;
		}

		$chats = $this->Model_Chats->search_chats($query_string);
		if (count($chats) <= 0)
		{
			$this->response($this->rest_error(array("No matching chats could be found.")),404);
		} else {
			$this->response($chats);
		}

	}

	/**
	 *Flags a message as inapropriate
	 */
	public function flag_message_post()
	{
		
		$reporter_id = $this->authenticated_as;
		if ($reporter_id <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		
		$this->load->model('Model_Chats');
	
		$message_id = $this->post('message_id');
		$reason = $this->post('reason');
		$time = time();
		
		if($this->Model_Chats->check_reporter($reporter_id,$message_id)==false)
		{		
			if($this->Model_Chats->check_message($message_id)==true)
			{
				$this->Model_Chats->flag_message($message_id,$reporter_id,$reason,$time);		
				if($this->Model_Chats->check_flagged($message_id)>=5)
				{
					$this->Model_Chats->remove_message($message_id);
				}
			}
			else
			{
			$this->response($this->rest_error(array("Only messages that exist my be deleted.")),403);
			return;
			}
		}
		else
		{
		$this->response($this->rest_error(array("You have already reported this message.")),403);
		return;
		}
	}
	
	/**
	 *Joins a user to a chat
	 *returns the information of the specified chat if successful
	 */
	public function join_post()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');

		$chat_id_string = $this->post('id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Check to make sure we have a chat id string
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")),403);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")),404);
			return;
		}

		//Verify that the user hasn't already subscribed to this chat room.
		$already_joined = $this->Model_Chats->is_user_subscribed($user_id,$chat_id);
		if($already_joined)
		{
			$this->response($this->rest_error(array("You have already joined that chat.")),403);
			return;
		}

		$this->Model_Chats->join_chat_by_id($user_id,$chat_id);

		$chatinfo = $this->Model_Chats->get_chat_by_id($chat_id);

		$this->response($chatinfo, 200);
	}

	/**
	 *Adds a chat to the database
	 *id_string - the generated unique string for the chat
	 *subject_id - The ID for the subject of the class
	 *course_number - self explanatory
	 *name - Name of the chat room
	 *institution_id - ID for the university, organization, etc using the room
	 *room_id - The Room # for the class
	 *start_time - The time that the class starts
	 *end_time - The time at which the class ends
	 *dow_monday - Whether or not the class is held on a monday
	 *dow_tuesday - Whether or not the class is held on a tuesday
	 *dow_wednesday - Whether or not the class is held on a wednesday
	 *dow_thursday - Whether or not the class is held on a thursday
	 *dow_friday - Whether or not the class is held on a friday
	 *dow_saturday - Whether or not the class is held on a saturday
	 *dow_sunday - Whether or not the class is held on a sunday
	 */
	public function add_post()
	{
		$this->load->model('Model_Auth');
		$this->load->model('Model_Chats');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		$id_string 		= $this->Model_Auth->_random_string(16);
		$subject_id 		= $this->post('subject_id');
		$course_number		= $this->post('course_number');
		$name			= $this->post('name');
		$institution_id 	= $this->post('institution_id');
		$room_id 		= $this->post('room_id');
		$start_time 		= $this->post('start_time');
		$end_time 		= $this->post('end_time');
		$dow_monday 		= $this->post('dow_monday');
		$dow_tuesday 		= $this->post('dow_tuesday');
		$dow_wednesday 		= $this->post('dow_wednesday');
		$dow_thursday 		= $this->post('dow_thursday');
		$dow_friday		= $this->post('dow_friday');
		$dow_saturday 		= $this->post('dow_saturday');
		$dow_sunday		= $this->post('dow_sunday');

		if($subject_id <= 0)
		{
			$this->response($this->rest_error(array("No subject ID was specified.")),403);
			return;
		}
		if($course_number <= 0)
		{
			$this->response($this->rest_error(array("No course number was specified.")),403);
			return;
		}
		if(strlen($name) <= 0)
		{
			$this->response($this->rest_error(array("No name was specified.")),403);
			return;
		}
		if($institution_id <= 0)
		{
			$this->response($this->rest_error(array("No institution id was specified.")),403);
			return;
		}
		if($room_id <= 0)
		{
			$this->response($this->rest_error(array("No room id was specified.")),403);
			return;
		}
		if($start_time <= 0)
		{
			$this->response($this->rest_error(array("No start time was specified.")),403);
			return;
		}
		if($end_time <= 0)
		{
			$this->response($this->rest_error(array("No end time was specified.")),403);
			return;
		}
		if($dow_monday != 0 && $dow_monday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on a Monday.")),403);
			return;
		}
		if($dow_tuesday != 0 && $dow_tuesday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on a Tuesday.")),403);
			return;
		}
		if($dow_wednesday != 0 && $dow_wednesday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on a Wednesday.")),403);
			return;
		}
		if($dow_thursday != 0 && $dow_thursday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on a Thursday.")),403);
			return;
		}
		if($dow_friday != 0 && $dow_friday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on Friday.")),403);
			return;
		}
		if($dow_saturday != 0 && $dow_saturday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on a Saturday.")),403);
			return;
		}
		if($dow_sunday != 0 && $dow_sunday != 1)
		{
			$this->response($this->rest_error(array("Did not specify if class is on Sunday.")),403);
			return;
		}
		$chat_info['id_string'] = $id_string;
		$chat_info['subject_id'] = $subject_id;
		$chat_info['course_number'] = $course_number;
		$chat_info['name'] = $name;
		$chat_info['institution_id'] = $institution_id;
		$chat_info['room_id'] = $room_id;
		$chat_info['start_time'] = $start_time;
		$chat_info['end_time'] = $end_time;
		$chat_info['dow_monday'] = $dow_monday;
		$chat_info['dow_tuesday'] = $dow_tuesday;
		$chat_info['dow_wednesday'] = $dow_wednesday;
		$chat_info['dow_thursday'] = $dow_thursday;
		$chat_info['dow_friday'] = $dow_friday;
		$chat_info['dow_saturday'] = $dow_saturday;
		$chat_info['dow_sunday'] = $dow_sunday;
		
		if($this->Model_Chats->add_chat($chat_info))
		{
			$this->response('Successfully added a chat room!', 200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("Failed to add the chat!")),403);
			return;
		}

	}

	/**
	 *Removes a user from the chat
	 *chat_id - the ID for the chat that the user is to be removed from
	 *user_id - user id of the user who is to be removed
	 *returns the user id and chat id on success, else a 404 error is generated
	 */
	public function leave_post()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		$chat_id_string = $this->post('id');

		//Make sure they gave us a user id.
		if ($user_id <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Check to make sure we have a chat id string
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")),403);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")),404);
			return;
		}

		//Check to see if the user is subscribed
		$subscribed = $this->Model_Chats->is_user_subscribed($user_id,$chat_id);
		if ($subscribed == false)
		{
			$this->response($this->rest_error(array("User was not subscribed to the chat!")),403);
			return;
		}

		//Well, all that error checking done, lets unsubscribe the user.
		$remove = $this->Model_Chats->leave_chat_by_id($chat_id,$user_id);
		
		if ($remove)
		{
			$this->response('User successfully removed!',200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("Error removing user from room!")),500);
			return;
		}
	}

	/**
	 * Sends a message to the specified room
	 * chat_id - string ID of room to send to.
	 * message - message string
	 * returns - NULL or FALSE on failure.
	 */
	public function send_post()
	{
		$this->load->model('Model_Chats');

		$chat_id_string = $this->post('chat_id');
		$message = $this->post('message');
		$user_id = $this->authenticated_as;

		//Make sure we're signed in and we requested a chat ID
		if ($user_id <= 0 || strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("You are not authorized, or no chat ID was specified.")),403);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")),404);
			return;
		}
		
		//Fetch chat info
		$chat_info = $this->Model_Chats->get_chat_by_id($chat_id);

		//Check to make sure user is joined to this chat.
		if (!$this->Model_Chats->is_user_subscribed($user_id,$chat_id))
		{
			$this->response($this->rest_error(array("You are not subscribed to this chat.")),401);
			return;
		}

		//TODO: Sanitize this text

		//Check that there is a message provided.
		if (strlen($message) < 1)
		{
			$this->response($this->rest_error(array("You must provide a message.")),403);
			return;
		}
		
		$message_data = $this->Model_Chats->send_message($user_id,$chat_id,$message);
		
		if(!$message_data)
		{
			$this->response($this->rest_errror(array("ERROR adding the message!")), 403);
			return;
		}
		
		$message_id = $this->db->insert_id();
		
		$this->load->model('Model_S3');
		$this->load->model('Model_Auth');
		
		//Randomly generate the file name.
		$file_name = $this->Model_Auth->_random_string(16);
		
		//Check if there was an uploaded file.
		if(isset($_FILES['file']))
		{
			//Generate an inputFile for S3, part of the function
			$file = $this->Model_S3->inputFile($_FILES['file']);
			
			//Redundancy is key.
			if(!$file)
			{
				$this->response($this->rest_error(array("Invalid file uploaded!")), 404);
				return;
			}
			
			//Upload the file to S3
			$upload = $this->Model_Chats->add_file($user_id, $chat_id, $file_name, $message_id);
			if(!$upload)
			{
				$this->response($this->rest_error(array("Error adding file entry to database!")), 404);
				return;
			}
			
			//Set the authentication keys and SSL mode for S3
			$this->Model_S3->setAuth($this->config->item['access_key'], $this->config->item['secret_key']);
			$this->Model_S3->setSSL('false');
			
			//Upload the file to S3
			if ($this->Model_S3->putObject($file_name, 'livecourse', $file, 'public-read-write'))
			{
				$this->response(null, 201);
				return;
			}
			else
			{
				$this->response($this->rest_error(array("Error uploading the file!")), 500);
				return;
			}
			
		}
		
		//Sending push notifications to the android users who are subscribed to this chat
		$users = $this->Model_Users->fetch_all_subscribed_android_user($chat_id); 
		$time = time();
		
		$user_info = $this->Model_Users->fetch_user_by_id($user_id);
		
		
		
		if (count($users) > 0)
		{
			$registration_ids = null;
			foreach ($users as $user)
			{
				$registration_ids[] = $user->gcm_regid;
			}
			$url = 'https://android.googleapis.com/gcm/send';
		
			$fields = array(
			    'registration_ids' => $registration_ids,
			    'data' => array('message_id' => $message_id,
			    		'chat_id' => $chat_id_string,
			    		'send_time' => $time,
			    		'message_string' => $message,
			    		'user_id' => $user_id,
			    		'email' => $user_info[0]->email,
			    		'display_name' => $user_info[0]->display_name,)
			);
			
			$headers = array(
			    'Authorization: key=' . $this->config->item('api_key'),
			    'Content-Type: application/json'
			);
			// Open connection
			$ch = curl_init();
			
			// Set the url, number of POST vars, POST data
			curl_setopt($ch, CURLOPT_URL, $url);
			
			curl_setopt($ch, CURLOPT_POST, true);
			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
			
			// Disabling SSL Certificate support temporarly
			curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
			
			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
			
			// Execute post
			$result = curl_exec($ch);
			
			// Close connection
			curl_close($ch);
			if(!$result)
			{
				$this->response($this->rest_error(array("Could not send android messages!")), 404);
				return;
			}
			
		}
		
		include_once("misc/windowsphonepushclient.php");
		
		//Send push notifications to all Windows Phone users
		$wp_users = $this->Model_Users->fetch_all_subscribed_wp_user($chat_id); 
		foreach ($wp_users as $wp)
		{
			$wpp = new WindowsPhonePushClient($wp->push_url);
			$wpp->send_toast($chat_info->name,$message);
		}
		
		$this->response(null,201); //Success
	}

	/**
	 * Retrieves a number of recent messages from the specified chat.
	 * chat_id - Chat ID to fetch from
	 * num_messages - Number of messages to fetch (100 if unspecified)
	 * returns - messages
	 */
	function fetch_recent_get()
	{
		$this->load->model('Model_Chats');
		
		$chat_id_string = $this->get('chat_id');
		
		$num_messages = $this->get('num_messages');
		
		if (!isset($num_messages) || $num_messages == "")
			$num_messages = 100;
			
		$user_id = $this->authenticated_as;

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		
		//Check to make sure user is joined to this chat.
		if (!$this->Model_Chats->is_user_subscribed($user_id,$chat_id))
		{
			$this->response($this->rest_error(array("You are not subscribed to this chat.")),401);
			return;
		}

		$messages = $this->Model_Chats->get_num_latest_messages($chat_id,$num_messages);
		$this->response($messages,200); //Success
	}
	
	/**
	 * Retrieves all messages past a certain defined message id
	 * chat_id - Chat ID to fetch from
	 * msg_id - Message ID after which to grab
	 * returns - messages
	 */
	function fetch_since_get()
	{
		$this->load->model('Model_Chats');
		
		$chat_id_string = $this->get('chat_id');
		$msg_id = $this->get('msg_id');
		
		$num_messages = $this->get('num_messages');
		
		if (!isset($num_messages) || $num_messages == "")
			$num_messages = 100;
			
		$user_id = $this->authenticated_as;

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		
		//Check to make sure user is joined to this chat.
		if (!$this->Model_Chats->is_user_subscribed($user_id,$chat_id))
		{
			$this->response($this->rest_error(array("You are not subscribed to this chat.")),401);
			return;
		}

		$messages = $this->Model_Chats->get_messages_after_msg_id($chat_id,$msg_id);
		$this->response($messages,200); //Success
	}
	
	/**
	 * Retrieves all messages from a given room between specified time and specified time + 24 hours.
	 * NOTE: This operates in UTC time, effectively. Client should do some conversions before requesting
	 *       if they expect results to be arranged like their local time zone.
	 * chat_id - Chat ID to fetch from
	 * start_epoch - UNIX epoch of start of the day from which to retrieve 24 hours worth of messages for.
	 * returns - messages
	 */
	function fetch_day_get()
	{
		$this->load->model('Model_Chats');
		
		$chat_id_string = $this->get('chat_id');
		$start_epoch = $this->get('start_epoch');
		
		if (!isset($start_epoch) || $start_epoch == "")
		{
			$this->response($this->rest_error(array("No time frame given.")),403);
			return;
		}
		
		$user_id = $this->authenticated_as;

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		
		//Check to make sure user is joined to this chat.
		if (!$this->Model_Chats->is_user_subscribed($user_id,$chat_id))
		{
			$this->response($this->rest_error(array("You are not subscribed to this chat.")),401);
			return;
		}

		$messages = $this->Model_Chats->get_time_frame_messages($chat_id,$start_epoch,$start_epoch+(24*60*60));
		$this->response($messages,200); //Success
	}
	
	/**
	 * EventSource function called by the HTML 5 client to stream chat updates from a room.
	 * auth_token - authentication token
	 * auth_code - authentication signature
	 * chat_id - chat to stream messages from
	 * msg_id - last message client has received, fetch messages after this one.
	 */
	function eventsource_get()
	{
		// verify authentication first. Can't do this through headers...
		$this->load->model('Model_Auth');
		$this->load->model('Model_Chats');
		$this->load->model('Model_Users');
		$auth_token = $this->get('auth_token');
		$auth_code = $this->get('auth_code');
		$chat_id_string = $this->get('chat_id');
		//$last_msg_id = $this->get('msg_id');
		//if (!isset($_COOKIE['lc_last_msg']) || $_COOKIE['lc_last_msg'] == "")
		//{
			$last_msg_id = $this->get('lastEventId');
		//} else {
		//	$last_msg_id = $_COOKIE['lc_last_msg'];
		//}
		
		$users = $this->Model_Auth->fetch_user_by_token($auth_token);
		if (count($users) > 0) //Auth token was valid... now let's verify their request.
		{
			$method = substr($this->uri->uri_string,4); //Strip api/ out of their request string
			//Generate the expected code
			$expected_code = sha1($auth_token.$users[0]->password.$method);
			if ($expected_code == $auth_code)
			{
				$this->authenticated_as = $users[0]->id;
			}
		}
		
		$user_id = $this->authenticated_as;
		
		//Make sure they gave us a user id.
		if ($user_id <= 0)
		{
			$this->response(NULL,401);
			return;
		}
		
		//Update their request activity
		$this->Model_Users->update_user_request_time($user_id);
		
		//Make sure they gave us a message id.
		//if ($last_msg_id <= 0)
		//{
		//	$this->response(NULL,401);
		//	return;
		//}
		
		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);
		
		//Check to make sure user is joined to this chat.
		if (!$this->Model_Chats->is_user_subscribed($user_id,$chat_id))
		{
			$this->response(NULL,401);
			return;
		}
		
		header('Content-Type: text/event-stream');
		header('Cache-Control: no-cache');
		header('Access-Control-Allow-Origin: *');

		// prevent bufferring
		if (function_exists('apache_setenv')) {
			@apache_setenv('no-gzip', 1);
		}
		@ini_set('zlib.output_compression', 0);
		@ini_set('implicit_flush', 1);
		for ($i = 0; $i < ob_get_level(); $i++) { ob_end_flush(); }
		ob_implicit_flush(1);

		$startedAt = time();
		echo ':' . str_repeat(' ', 2048) . "\n"; //2KB header for IE
		echo "retry: 2000\n";
		
		//Give IE an event ID to send us back if they didn't specify one:
		if ($last_msg_id == "")
		{
			$last_msg = $this->Model_Chats->get_num_latest_messages($chat_id,1);
			if (count($last_msg) > 0)
			{
				$last_msg = $last_msg[0];
				$last_msg_id = $last_msg->id;
			} else {
				$last_msg_id = 0;
			}
			echo "id: $last_msg_id" . PHP_EOL;
			echo "event: ie_sucks\n";
			echo "data: {\n";
			echo "data: \"compatibility\":\"thing.\"\n";
			echo "data: }\n\n";
		}
		
		do {
			// Cap connections at 15 seconds. The browser will reopen the connection on close (update lost msgs)
			if ((time() - $startedAt) > 15) {
				die();
			}

			$msgs = $this->Model_Chats->get_messages_after_msg_id($chat_id,$last_msg_id);
			foreach ($msgs as $msg)
			{
				echo "id: $msg->id" . PHP_EOL;
				echo "data: {\n";
				foreach($msg as $key => $value) {
					$value = str_replace('\\','\\\\',$value);
					$value = str_replace('"','\\"',$value);
					echo "data: \"$key\": \"$value\",\n";
				}
				echo "data: \"insignificant\": \".\"\n";
				echo "data: }\n\n";
				$last_msg_id = $msg->id;
			}
			echo PHP_EOL;
			//ob_flush();
			flush();
			sleep(1);

			// If we didn't use a while loop, the browser would essentially do polling
			// every ~3seconds. Using the while, we keep the connection open and only make
			// one request.
		} while(true);
	}
	
	/**
	 *Remove a chat room
	 *chat_id_string - The string of the Chat to be removed
	 *returns 200 on success, 404 on failure
	 */
	function delete_post()
	{
		return; //TODO: PERMISSIONS !!
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');
		
		$chat_id_string = $this->post('id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")),403);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")),404);
			return;
		}
		
		if($this->Model_Chats->remove_chat($chat_id))
		{
			$this->response('Successfully removed the chat!', 200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("Failed to remove the chat!")),404);
			return;
		}
	}
	
	/**
	 *Gets all participants in a selected chat
	 *id - Chat ID String of the chat to get subscribed users
	 *returns an array of users on success or an error on failure  
	 */
	function get_participants_get()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Auth');
		
		$chat_id_string = $this->get('id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")),403);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")),404);
			return;
		}
		$users = null;
		$users = $this->Model_Chats->get_participants($chat_id,$user_id);
		
		if(!$users)
		{
			$this->response($this->rest_error(array("Error finding users!")),500);
			return;
		}
		
		if(count($users) <= 0)
		{
			$this->response($this->rest_error(array("No subscribed users")),404);
			return;
		}
		
		$this->response($users,200);
	}
	
	/**
	*This function grants a user administrative permissions in a specific chat room.
	*chat_id_string - The Unique String associated with the chat room
	*user_id - ID of the user who is to be given administrative permissions
	*Returns 200 if successful, 401 if not logged in, 401 if the requesting user isn't an admin,
	*400 if the chat id or user id isn't proper or supplied, and 404 if the user or chat doesn't exist,
	*400 if the user isn't subscribed to the chat.
	*and finally a 409 if the user is already an admin
	*/
	function promote_user_post()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Users');
		
		$chat_id_string = $this->post('chat_id');
		$target_id = $this->post('user_id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		//Make sure they gave us a proper target
		if ($target_id == NULL || $target_id <= 0)
		{
			$this->response($this->rest_error(array("User ID not supplied!")), 400);
			return;
		}
		
		//Make sure the target is a user
		$target_user = $this->Model_Users->fetch_user_by_id($target_id);
		
		if (count($target_user) <= 0)
		{
			$this->response($this->rest_error(array("The target user does not exist!")), 404);
			return;
		}
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")), 400);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")), 404);
			return;
		}
		
		//Make sure the target is subscribed to the specified chat room
		$target_in_chat = $this->Model_Chats->is_user_subscribed($target_id, $chat_id);
		
		if (!$target_in_chat)
		{
			$this->response($this->rest_error(array("The target User ID is not subscribed to this chat room!")), 404);
			return;
		}
		
		//Check to make sure the requesting user has permissions
		$admin_user = $this->Model_Chats->check_user_permissions($chat_id, $user_id);
		//print_r($admin_user[0]->permissions);
		if (!$admin_user[0]->permissions)
		{
			$this->response($this->rest_error(array("You do not have the proper permissions to do that!")), 401);
			return;
		}
		
		//After extensive checking, we can update the users permissions.		
		$update_user = $this->Model_Chats->change_user_permissions($chat_id, $target_id, 1);
		if ($update_user)
		{
			$this->response(NULL, 200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("The server encountered an error while processing your request!")),500);
			return;
		}
	}
	
	/**
	*This function removes a user's administrative permissions in a specific chat room.
	*chat_id_string - The Unique String associated with the chat room
	*user_id - ID of the user who is to lose administrative permissions
	*Returns 200 if successful, 401 if not logged in, 401 if the requesting user isn't an admin,
	*400 if the chat id or user id isn't proper or supplied, and 404 if the user or chat doesn't exist,
	*and 400 if the user isn't subscribed to the chat. 500 only on the rare possibility the server couldn't proceed.
	*/
	function demote_user_post()
	{
		$this->load->model('Model_Chats');
		$this->load->model('Model_Users');
		
		$chat_id_string = $this->post('chat_id');
		$target_id = $this->post('user_id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		//Make sure they gave us a proper target
		if ($target_id == NULL || $target_id <= 0)
		{
			$this->response($this->rest_error(array("User ID not supplied!")), 400);
			return;
		}
		
		//Make sure the target is a user
		$target_user = $this->Model_Users->fetch_user_by_id($target_id);
		
		if (count($target_user) <= 0)
		{
			$this->response($this->rest_error(array("The target user does not exist!")), 404);
			return;
		}
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")), 400);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")), 404);
			return;
		}
		
		//Make sure the target is subscribed to the specified chat room
		$target_in_chat = $this->Model_Chats->is_user_subscribed($target_id, $chat_id);
		
		if (!$target_in_chat)
		{
			$this->response($this->rest_error(array("The target User ID is not subscribed to this chat room!")), 404);
			return;
		}
		
		//Check to make sure the requesting user has permissions
		$admin_user = $this->Model_Chats->check_user_permissions($chat_id, $user_id);
		
		if (!$admin_user[0]->permissions)
		{
			$this->response($this->rest_error(array("You do not have the proper permissions to do that!")), 401);
			return;
		}
		
		//After extensive checking, we can update the users permissions.		
		$update_user = $this->Model_Chats->change_user_permissions($chat_id, $target_id, 0);
		if ($update_user)
		{
			$this->response(NULL, 200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("The server encountered an error while processing your request!")),500);
			return;
		}
	}
	/**
	*This function will generate a QR code and return the URL to be embedded as an image.
	*id - ID of the chat to join
	*returns a URL and 200 on success, 400 if no chat id provided, 404 on chat not existing,
	*401 if not logged in, 500 if error on processing.
	*/
	function generate_qr_get()
	{
		$this->load->model('Model_Chats');
		
		$chat_id_string = $this->get('id');
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")), 400);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")), 404);
			return;
		}
		
		$url = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=livecourse.net/join/" . $chat_id_string;
		header("Location: " . $url);
	}
	
	function retrieve_file_get()
	{
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		$this->load->model('Model_Chats');
		$this->load->model('Model_S3');
		$this->load->model('Model_Auth');
		
		//Get Variables
		$chat_id_string = $this->get('chat_id_string');
		$file_id = $this->get('file_id');
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")), 400);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")), 404);
			return;
		}
		
		//Look up the name of the file by the unique file ID
		$fileinfo = $this->Model_Chats->get_file_info($file_id);
		
		$filename = $fileinfo->filename;
		
		//Make sure there was a specified file
		if(strlen($filename) <= 0)
		{
			$this->response($this->rest_error(array("Specified file could not be found!")), 404);
			return;
		}
		
		//Set the authentication keys and SSL mode for S3
		$this->Model_S3->setAuth($this->config->item['access_key'], $this->config->item['secret_key']);
		$this->Model_S3->setSSL('false');
		
		//Get the file from S3
		$file = $this->Model_S3->getObjectInfo('livecourse', $filename);
		if(!file)
		{
			$this->response($this->rest_error(array("Error finding file!")), 404);
			return;
		}
		
		$file['chat_id'] = $chat_id;
		$file['user_id'] = $user_id;
		$file['time'] = $fileinfo->uploaded_at;
		$file['message_id'] = $fileinfo->message_id;
		
		$this->response($file, 200);
		return;
		
	}
	
	function remove_file_post()
	{
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		$this->load->model('Model_Chats');
		$this->load->model('Model_S3');
		$this->load->model('Model_Auth');
		
		//Get Post Variables
		$chat_id_string = $this->post('chat_id_string');
		$file_id = $this->post('file_id');
		
		//Make sure we requested a chat ID
		if (strlen($chat_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No chat ID was specified.")), 400);
			return;
		}

		//Try to find the chat
		$chat_id = $this->Model_Chats->get_id_from_string($chat_id_string);

		if ($chat_id < 0)
		{
			$this->response($this->rest_error(array("Specified chat does not exist.")), 404);
			return;
		}
		
		//Look up the name of the file by the unique file ID
		$filename = $this->Model_Chats->get_filename($file_id);
		
		//Make sure there was a specified file
		if(strlen($filename) <= 0)
		{
			$this->response($this->rest_error(array("Specified file could not be found!")), 404);
			return;
		}
		
		//Set the authentication keys and SSL mode for S3
		$this->Model_S3->setAuth($this->config->item['access_key'], $this->config->item['secret_key']);
		$this->Model_S3->setSSL('false');
		
		//Get the file from S3
		$file = $this->Model_S3->getObject('livecourse', $filename);
		if(!file)
		{
			$this->response($this->rest_error(array("Error finding file!")), 404);
			return;
		}
		
		$delete = $this->Model_Chats->remove_file($user_id, $chat_id, $filename);
		
		if(!$delete)
		{
			$this->response($this->rest_error(array("Error removing file entry!")), 404);
			return;
		}
		
		//Delete the file from S3
		if ($this->Model_S3->deleteObject('livecourse', $filename))
		{
			$this->response(null, 200);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("Error deleting the file!")), 500);
			return;
		}
		
	}
}
