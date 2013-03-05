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
	//TODO: Add header
	public function index_get()
	{
		//Return a list of all subscribed rooms, unless a room_id is specified
		$room_id = $this->get('id'); // GET param

		if($room_id)
		{
			$rooms = $this->db
				->where('id', $room_id)
				->from('lc_rooms')
				->get();
		}
		else
		{
			$rooms = $this->db
				->from('lc_rooms')
				->get();
		}
		$this->response($rooms->result());

	}
	
	//TODO: Add header
	public function join_post()
	{
		//Add a user to a room
		$room_id = $this->post('room_id'); // GET param
		$user_id = $this->post('user_id');

		if(!$user_id || !$room_id)
		{
			echo 'Not enough information supplied!';

			$data = null;
			
			$this->response($data, 409);
			
			return;
		}
		
		$room_query = $this->db
				->from('lc_rooms')
				->where('id', $room_id)
				->get()
				->result();

		$user_query = $this->db
				->from('lc_users')
				->where('id', $user_id)
				->get()
				->result();
				
		if(sizeof($user_query) <= 0)
		{
			echo 'User does not exist!';
			
			$data = null;
			
			$this->response($data);
	
			return;
		}		
		
		if (sizeof($room_query) <= 0)
		{
			echo 'Room does not exist!';

			$data = null;

			$this->response($data);
			
			return;
		}
		else
		{
			$data = array(
					'room_id' 	=> $room_id,
					'user_id' 	=> $user_id,
					'permissions' 	=> '0'
					);

			$this->db->insert('lc_room_participants', $data);

			$this->response($data, 201);
			
			return;
		}

	}
	
	//TODO: Add header
	public function add_post()
	{
		$unique_str	= 'R1a2W3r'; //TODO: Generate random string.
		$name 		= $this->post('name');
		$building 	= $this->post('building');
		$room 		= $this->post('room');
		$start_time 	= $this->post('start_time');
		$end_time 	= $this->post('end_time');
		$start_date 	= $this->post('start_date');
		$end_date 	= $this->post('end_date');
		$monday 	= $this->post('dow_monday');
		$tuesday 	= $this->post('dow_tuesday');
		$wednesday 	= $this->post('dow_wednesday');
		$thursday 	= $this->post('dow_thursday');
		$friday 	= $this->post('dow_friday');
		$saturday	= $this->post('dow_saturday');
		$sunday		= $this->post('dow_sunday');
		
		//TODO: Check for valid input
		
		$room_query = $this->db
				->from('lc_rooms')
				->where('building', $building)
				->where('room', $room)
				->where('start_time', $start_time)
				->where('end_time', $end_time)
				->where('start_date', $start_date)
				->where('end_date', $end_date)
				->where('dow_monday', $monday)
				->where('dow_tuesday', $tuesday)
				->where('dow_wednesday', $wednesday)
				->where('dow_thursday', $thursday)
				->where('dow_friday', $friday)
				->where('dow_saturday', $saturday)
				->where('dow_sunday', $sunday)
				->get()
				->result();
				
		if (sizeof($room_query) <= 0)
		{
			$data = array(
					'Id_string'	=> $unique_str,
					'name'		=> $name,
					'building'	=> $building,
					'room'		=> $room,
					'start_time'	=> $start_time,
					'end_time'	=> $end_time,
					'start_date'	=> $start_date,
					'end_date'	=> $end_date,
					'dow_monday'	=> $monday,
					'dow_tuesday'	=> $tuesday,
					'dow_wednesday'	=> $wednesday,
					'dow_thursday'	=> $thursday,
					'dow_friday'	=> $friday,
					'dow_saturday'	=> $saturday,
					'dow_sunday'	=> $sunday
					);
					
			$this->db->insert('lc_rooms', $data);

			$this->response($data, 201);
			
			return;
		}
		else
		{
			echo 'Room already exists!';

			$data = null;

			$this->response($data, 409);
			
			return;
		}	
	}
	
	//TODO: Add header
	public function leave_post()
	{
		//remove a user from a room
		$room_id = $this->post('room_id'); // GET param
		$user_id = $this->post('user_id');

		$room_query = $this->db
				->from('lc_rooms')
				->where('id', $room_id)
				->get()
				->result();

		if ( sizeof($room_query) <= 0)
		{
			echo 'Room does not exist!';

			$data = null;

			$this->response($data);
			
			return;
		}
		//Check to see if a user is in the room
		else
		{
			$data = array(
					'room_id' => $room_id,
					'user_id' => $user_id
					);
			$this->db->delete('lc_room_participants', $data);

			$this->response($data, 200);
			
			return;
		}

	}
}
