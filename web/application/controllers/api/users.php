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
class Users extends REST_Controller
{
	// Index command is run when no command is specified.
	//TODO: Add header
	public function index_get()
	{
		//Get a list of all users unless a specific one is requested
		$user_id = $this->get('id'); // GET parameter
		
		if($user_id)
		{
			$users = $this->db
				->where('id', $user_id)
				->from('lc_users')
				->get();
		}
		else
		{
			$users = $this->db
				->from('lc_users')
				->get();
		}
		
		$this->response($users->result());
			
		return;

	}

	//TODO: Add header
	public function add_post()
	{
		//Add a user to the database
		$email = $this->post('email');
		$pass = sha1($this->post('password'));
		$display = $this->post('display_name');
		
		$user_query = $this->db
				->from('lc_users')
				->where('email', $email)
				->get()
				->result();
				
		if (sizeof($user_query) <= 0)
		{
			$data = array(
					'email'		=> $email,
					'password'	=> $pass,
					'display_name'	=> $display,
					'jointime'	=> time()
					);
					
			$this->db->insert('lc_users', $data);

			$this->response($data, 201);
			
			return;
		}
		else
		{
			echo 'Email already exists!';

			$data = null;

			$this->response($data, 409);
			
			return;
		}

	}

	//TODO: Add header
	public function remove_post()
	{
		//Remove a user from the db
		$user_id = $this->post('id');

		$user_query = $this->db
				->from('lc_users')
				->where('id', $user_id)
				->get()
				->result();
				
		if (sizeof($user_query) <= 0)
		{
			echo 'User does not exist!';

			$data = null;

			$this->response($data);
			
			return;
		}
		else
		{
			$data = array(
					'id' => $user_id,
					);
			$this->db->delete('lc_users', $data);

			$this->response($data, 200);
			
			return;
		}

	}
}
