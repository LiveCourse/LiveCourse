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
class Notes extends REST_Controller
{
	/**
	 * Fetches all the notes from a particular chat.
	 *
	 * returns
	 *	401 if not logged in
	 *	404 if no chat specified or no chat found
	 *	200 and the information for all the chats upon success
	 */
	public function index_get()
	{
		$this->load->model('Model_Classes');
		$class_id_string = $this->get('class_id_string');
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		$class_id = $this->Model_Classes->get_id_from_string($class_id_string);
		if ($class_id < 0)
		{
			$this->response($this->rest_error(array("Specified class could not be found.")), 404);
			return;
		}
		
		$this->load->model('Model_Notes');
		$notes = $this->Model_Notes->fetch_notes_from_class($class_id);
		$this->response($notes, 200);
	}
	
	/**
	 * Adds a note to a particular chat under the specified parent node (0 if none)
	 *
	 * parameters
	 *	class_id_string Identifier of chat to add note to.
	 *	parent_note_id ID of parent note. 0 if none.
	 *	text Text contents of the note.
	 * returns
	 *	401 if not logged in or not subscribed to specified chat
	 *	404 if no chat specified or no chat found
	 *	201 upon success
	 */
	public function add_post()
	{
		$class_id_string = $this->post('class_id_string');
		$parent_note_id = $this->post('parent_note_id');
		$text = $this->post('text');
		
		$this->load->model('Model_Classes');
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		$user_id = $this->authenticated_as;
		
		//Make sure the class exists
		$class_id = $this->Model_Classes->get_id_from_string($class_id_string);
		if ($class_id < 0)
		{
			$this->response($this->rest_error(array("Specified class could not be found.")), 404);
			return;
		}
		
		//Check to make sure user is joined to this chat.
		if (!$this->Model_Classes->is_user_subscribed($user_id,$class_id))
		{
			$this->response($this->rest_error(array("You are not subscribed to this class.")), 401);
			return;
		}
		
		$this->load->model('Model_Notes');
		
		//Make sure specified parent note exists
		if ($parent_note_id > 0)
		{
			if (!$this->Model_Notes->note_exists($parent_note_id,$class_id))
			{
				$this->response($this->rest_error(array("Parent note could not be found.")), 404);
				return;
			}
		}
		
		$notes = $this->Model_Notes->add_note($class_id,$parent_note_id,0,$text);
		$this->response(NULL, 201);
	}
	
	/**
	 * EventSource function called by the HTML 5 client to stream note updates from a class.
	 * auth_token - authentication token
	 * auth_code - authentication signature
	 * class_id - chat to stream messages from
	 * msg_id - last message client has received, fetch messages after this one.
	 */
	function eventsource_get()
	{
		// verify authentication first. Can't do this through headers...
		$this->load->model('Model_Auth');
		$this->load->model('Model_Classes');
		$this->load->model('Model_Users');
		$this->load->model('Model_Notes');
		$auth_token = $this->get('auth_token');
		$auth_code = $this->get('auth_code');
		$class_id_string = $this->get('class_id');
		//$last_msg_id = $this->get('msg_id');
		//if (!isset($_COOKIE['lc_last_msg']) || $_COOKIE['lc_last_msg'] == "")
		//{
			$last_note_id = $this->get('lastEventId');
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
		$class_id = $this->Model_Classes->get_id_from_string($class_id_string);

		//Check to make sure user is joined to this chat.
		if (!$this->Model_Classes->is_user_subscribed($user_id,$class_id))
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
		if ($last_note_id == "")
		{
			$last_note = $this->Model_Notes->fetch_last_note($class_id);
			if (count($last_note) > 0)
			{
				$last_note = $last_note[0];
				$last_note_id = $last_note->id;
			} else {
				$last_note_id = 0;
			}
			echo "id: $last_note_id" . PHP_EOL;
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

			$notes = $this->Model_Notes->stream_note_updates_since($class_id,$last_note_id);
			foreach ($notes as $note)
			{
				echo "id: $note->id" . PHP_EOL;
				echo "data: {\n";
				foreach($note as $key => $value) {
					$value = str_replace('\\','\\\\',$value);
					$value = str_replace('"','\\"',$value);
					echo "data: \"$key\": \"$value\",\n";
				}
				echo "data: \"insignificant\": \".\"\n";
				echo "data: }\n\n";
				$last_note_id = $note->id;
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

}
