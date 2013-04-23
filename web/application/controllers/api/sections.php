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
class Sections extends REST_Controller
{
	/**
	 * Fetches all the available chats that the user is subscribed to
	 *
	 * returns
	 *	401 if not logged in
	 *	200 and the information for all the chats upon success
	 */
	public function index_get()
	{
		$this->load->model('Model_Sections');
		
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}
		
		$user_id = $this->get('user_id');
		if (!isset($user_id) || $user_id == "")
			$user_id = $this->authenticated_as;
		
		$sections = $this->Model_Sections->get_subscribed_sections($user_id);
		$this->response($sections, 200);
	}
	
	/**
	 * Fetches information on a specific section
	 *
	 * id - String form of section ID
	 *
	 * returns
	 * 	401 if not logged in
	 * 	403 if query was empty
	 * 	404 if the specified section could not be found
	 */
	public function info_get()
	{
		$this->load->model('Model_Sections');
		$section_id_string = $this->get('id');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}

		if (strlen($section_id_string) <= 0)
		{
			$this->response($this->rest_error(array("Empty queries can not be processed.")), 403);
			return;
		}

		$section_id = $this->Model_Sections->get_id_from_string($section_id_string);
		if ($section_id < 0)
		{
			$this->response($this->rest_error(array("Specified section could not be found.")), 404);
			return;
		}

		$section_info = $this->Model_Sections->get_section_by_id($section_id);
		$this->response($section_info);
	}
	
	/**
	 * DEPRECATED
	 * Searches for a specific section based on a keyword query
	 *
	 * query - String based query of what to search for
	 *
	 * returns
	 *	401 if not logged in
	 *	403 if the query was empty
	 *	404 if no matching chats could be found
	 *	Array of chat information if successful
	 */
	public function search_get()
	{
		$this->load->model('Model_Sections');
		$query_string = $this->get('query');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}

		if (strlen($query_string) <= 0)
		{
			$this->response($this->rest_error(array("Empty queries can not be processed.")), 403);
			return;
		}

		$chats = $this->Model_Sections->search_sections($query_string);
		if (count($chats) <= 0)
		{
			$this->response($this->rest_error(array("No matching sections could be found.")), 404);
		}
		else
		{
			$this->response($chats);
		}
	}
	
	/**
	 * Searches for a specific section based on a keyword query
	 *
	 * query - String based query of what to search for
	 *
	 * returns
	 *	401 if not logged in
	 *	403 if the query was empty
	 *	404 if no matching chats could be found
	 *	Array of chat information if successful
	 */
	public function search_advanced_get()
	{
		$this->load->model('Model_Sections');
		$crn = $this->get('crn');
		$subject_code = $this->get('subject_code');
		$course_number = $this->get('course_number');
		$building_short_name = $this->get('building_short_name');
		$room_number = $this->get('room_number');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}

		if (strlen($crn) <= 0 && strlen($subject_code) <= 0 && strlen($course_number) <= 0 && strlen($building_short_name) <= 0 && strlen($room_number) <= 0)
		{
			$this->response($this->rest_error(array("Empty queries can not be processed.")), 403);
			return;
		}
		
		$sections_query = array();
		if (strlen($crn) > 0)
			$sections_query["crn"] = $crn;
		if (strlen($subject_code) > 0)
			$sections_query["subject_code"] = $subject_code;
		if (strlen($course_number) > 0)
			$sections_query["course_number"] = $course_number;
		if (strlen($building_short_name) > 0)
			$sections_query["building_short_name"] = $building_short_name;
		if (strlen($room_number) > 0)
			$sections_query["room_number"] = $room_number;
		
		$sections = $this->Model_Sections->search_sections_advanced($sections_query,$user_id);
		if (count($sections) <= 0)
		{
			$this->response($this->rest_error(array("No matching sections could be found.")), 404);
		}
		else
		{
			$this->response($sections);
		}
	}
	
	/**
	 * Joins a user to a session
	 *
	 * id - The Chat ID String of the chat the user is to be joined to
	 *
	 * returns
	 *	401 if you are not logged in
	 *	403 if no chat ID was specified
	 *	404 if the specified chat doesn't exist
	 *	403 if you've already joined the specified chat
	 *	200 and the Chat Information if successful
	 */
	public function join_post()
	{
		$this->load->model('Model_Sections');

		$session_id_string = $this->post('id');
		
		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")), 401);
			return;
		}

		//Check to make sure we have a chat id string
		if (strlen($session_id_string) <= 0)
		{
			$this->response($this->rest_error(array("No section ID was specified.")), 403);
			return;
		}

		//Try to find the chat
		$section_id = $this->Model_Sections->get_id_from_string($session_id_string);

		if ($section_id < 0)
		{
			$this->response($this->rest_error(array("Specified section does not exist.")), 404);
			return;
		}

		//Verify that the user hasn't already subscribed to this chat room.
		$already_joined = $this->Model_Sections->is_user_subscribed($user_id,$section_id);
		if ($already_joined)
		{
			$this->response($this->rest_error(array("You have already joined that section.")), 403);
			return;
		}

		$this->Model_Sections->join_section_by_id($user_id,$section_id);

		$chatinfo = $this->Model_Sections->get_section_by_id($section_id);

		$this->response($chatinfo, 200);
	}
}
