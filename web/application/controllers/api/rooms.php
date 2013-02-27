<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
require(APPPATH.'libraries/REST_Controller.php');

/*
 * This is a RESTFUL class. Each method should fall under a RESTful HTTP state
 * (GET, PUT, POST, or DELETE)
 * See http://en.wikipedia.org/wiki/Representational_state_transfer#RESTful_web_services
 * See also https://github.com/philsturgeon/codeigniter-restserver/blob/master/README.md#requirements
 * NOTE: POST Updates a resource. PUT CREATES or COMPLETELY replaces what is already there.
 */
 
class Rooms extends REST_Controller
{
	// Index command is run when no command is specified.
	public function index_get()
	{
		//Return a list of all subscribed rooms, unless a room_id is specified
		$room_id = $this->get('id'); // GET param
	}
	public function join_post()
	{
		//Add a user to a room
		$room_id = $this->post('id'); // GET param
	}
	public function leave_post()
	{
		//Remove a user from a room
		$room_id = $this->post('id'); // GET param
	}
	public function add_put()
	{
		//Gather information (class name, location, etc)
	}
}
