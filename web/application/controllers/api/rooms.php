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
		$room_id = clean($this->post('room_id')); // GET param
		$user_id = clean($this->post('user_id'));
		$room_check_query =
			'SELECT
				*
			FROM
				lc_rooms
			WHERE
				id = ' . $room_id . '
			LIMIT 1';
		if ($room_check_query <= 0)
		{
			echo 'Room does not exist!';
		}
		else
		{
			$room_add_query = 
				'INSERT INTO 
					lc_room_participants
				VALUES
					' . $room_id . '
					,
					' . $user_id . '
					' . 0;
		}
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
	public function clean($str)
	{
		$clean = iconv('UTF-8', 'ASCII//TRANSLIT', $str);
		$clean = preg_replace("/[^a-zA-Z0-9\/_| -]/", '', $clean);
		$clean = strtolower(trim($clean, ''));
		$clean = preg_replace("/[\/_| -]+/", '', $clean);
	
		return $clean;
	}
}
