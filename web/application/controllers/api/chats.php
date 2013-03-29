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
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}
		$chats = $this->Model_Chats->get_subscribed_chats($this->authenticated_as);
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
		$chat_id_string = $this->get('id');
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
		$query_string = $this->get('query');

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
	 *Joins a user to a chat
	 *returns the information of the specified chat if successful
	 */
	public function join_post()
	{
		$this->load->model('Model_Chats');

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
		$already_joined = $this->Model_Chats->get_subscribed_chats($user_id);
		foreach ($already_joined as $joined_chats)
		{
			if ($joined_chats->id == $chat_id)
			{
				$this->response($this->rest_error(array("You have already joined that chat.")),403);
				return;
			}
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
		$this->load->model('Model_Users');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		$chat_id = $this->post('chat_id');

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
		$remove = $this->Model_Chats->unsubscribe_user($user_id,$chat_id);
		if ($remove)
		{
			$this->response(true,200);
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

		$this->Model_Chats->send_message($user_id,$chat_id,$message);
		$this->response(NULL,201); //Success
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
}
