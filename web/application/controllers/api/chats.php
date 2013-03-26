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
	// Index command is run when no command is specified.
	//TODO: Add header
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
	 * Fetches chat room information on a specific room
	 * id - String form of chat ID
	 * returns - Room information
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
	
	//TODO: Add header
	//TODO: There should be no user_id argument. This should only function for the authenticated user ($authenticated_as)
	public function join_post()
	{
		$this->load->model('Model_Chats');
		
		//Add a user to a chat
		$chat_id_string = $this->post('id');
		$user_id = $this->authenticated_as;

		if($user_id <= 0 || strlen($chat_id_string) <= 0)
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
		
		//TODO: Verify that they are not already joined before adding them!
		$this->Model_Chats->join_chat_by_id($user_id,$chat_id);
		$chatinfo = $this->Model_Chats->get_chat_by_id($chat_id);
		$this->response($chatinfo, 200);
	}
	
	//TODO: Add header
	public function add_post()
	{
		//TODO: This needs to be re-written.
	}
	
	//TODO: Add header
	public function leave_post()
	{
		//remove a user from a chat
		$chat_id = $this->post('chat_id'); // GET param
		$user_id = $this->post('user_id');

		$chat_query = $this->db
				->from('lc_chats')
				->where('id', $chat_id)
				->get()
				->result();
		if ( sizeof($chat_query) <= 0)
		{
			echo 'Room room does not exist!';
			$data = null;
			$this->response($data);
			return;
		}
		//Check to see if a user is in the chat
		else
		{
			$data = array(
					'chat_id' => $chat_id,
					'user_id' => $user_id
					);
			$this->db->delete('lc_chat_participants', $data);
			$this->response($data, 200);
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
		if($user_id <= 0 || strlen($chat_id_string) <= 0)
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
