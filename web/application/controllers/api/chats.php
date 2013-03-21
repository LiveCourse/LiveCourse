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
		//Return a list of all SUBSCRIBED chats, unless a chat_id is specified
		$chat_id = $this->get('id'); // GET param

		if($chat_id)
		{
			$chats = $this->db
				->where('id', $chat_id)
				->from('lc_chats')
				->get();
		}
		else
		{
			$chats = $this->db
				->from('lc_chats')
				->get();
		}
		$this->response($chats->result());

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
		$user_id = $authenticated_as;

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
		
		$this->Model_Chats->join_chat_by_id($user_id,$chat_id);
		
		$this->response(array(), 200);
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
}
