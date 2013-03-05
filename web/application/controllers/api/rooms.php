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
		if($room_id)
		{
			$this->db->where('room_id', $room_id);
			$this->db->from('lc_rooms');
			$rooms = $this->db->get();
		}
		else
		{
			$this->db->from('lc_rooms');
			$rooms = $this->db->get();
		}
	}
	public function join_post()
	{
		//Add a user to a room
		$room_id = $this->post('room_id'); // GET param
		$user_id = $this->post('user_id');
		$this->db->from('lc_rooms');
		$this->db->where('id', $room_id);
		$room_query = $this->db->get();
		if (isarray($room_check_query) == false || $room_check_query <= 0)
		{
			echo 'Room does not exist!';
		}
		else
		{
			$data = array(
					'room_id' 		=> $room_id,
					'user_id' 		=> $user_id,
					'permissions' 	=> '0');
			$this->db->insert('lc_room_participants', $data)
		}
	}
	public function leave_post()
	{
		//remove a user from a room
		$room_id = $this->post('room_id'); // GET param
		$user_id = $this->post('user_id');
		$this->db->from('lc_rooms');
		$this->db->where('id', $room_id);
		$room_query = $this->db->get();
		if (isarray($room_check_query) == false || $room_check_query <= 0)
		{
			echo 'Room does not exist!';
		}
		else
		{
			$data = array(
					'room_id' 		=> $room_id,
					'user_id' 		=> $user_id
					);
			$this->db->delete('lc_room_participants', $data)
		}
	}
	public function add_put()
	{
		//Gather information (class name, location, etc)
	}
}
