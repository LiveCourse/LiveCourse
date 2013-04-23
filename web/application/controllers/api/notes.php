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

}
