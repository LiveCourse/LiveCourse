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
	
	/**
	 * Retrieves a list of all the users, filterable by variables
	 * id - User ID to retrieve specific profile
	 * returns - information for each matching user
	 */
	public function index_get()
	{
		$this->load->model('Model_Users','',TRUE);
		//Get a list of all users unless a specific one is requested
		$user_id = $this->get('id'); // GET parameter
		//TODO: Allow filtering by e-mail.
		
		//TODO: Limit returned fields. Should not show password, etc.
		if($user_id)
		{
			$users = $this->Model_Users->fetch_user_by_id($user_id);
			if (count($users) > 0)
				$this->response($users);
			else
				$this->response($this->rest_error(array("The user id provided is invalid.")),404);
		}
		return;
	}
	
	/**
	 * Validate an email address.
	 * Provide email address (raw input)
	 * Returns true if the email address has the email 
	 * address format and the domain exists.
	 * Credit: Douglas Lovell, http://www.linuxjournal.com/article/9585?page=0,3
	*/
	function validEmail($email)
	{
		$isValid = true;
		$atIndex = strrpos($email, "@");
		if (is_bool($atIndex) && !$atIndex)
		{
			$isValid = false;
		}
		else
		{
			$domain = substr($email, $atIndex+1);
			$local = substr($email, 0, $atIndex);
			$localLen = strlen($local);
			$domainLen = strlen($domain);
			if ($localLen < 1 || $localLen > 64)
			{
				// local part length exceeded
				$isValid = false;
			}
			else if ($domainLen < 1 || $domainLen > 255)
			{
				// domain part length exceeded
				$isValid = false;
			}
			else if ($local[0] == '.' || $local[$localLen-1] == '.')
			{
				// local part starts or ends with '.'
				$isValid = false;
			}
			else if (preg_match('/\\.\\./', $local))
			{
				// local part has two consecutive dots
				$isValid = false;
			}
			else if (!preg_match('/^[A-Za-z0-9\\-\\.]+$/', $domain))
			{
				// character not valid in domain part
				$isValid = false;
			}
			else if (preg_match('/\\.\\./', $domain))
			{
				// domain part has two consecutive dots
				$isValid = false;
			}
			else if
			(!preg_match('/^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$/',str_replace("\\\\","",$local)))
			{
				// character not valid in local part unless 
				// local part is quoted
				if (!preg_match('/^"(\\\\"|[^"])+"$/',str_replace("\\\\","",$local)))
				{
					$isValid = false;
				}
			}
			if ($isValid && !(checkdnsrr($domain,"MX") || checkdnsrr($domain,"A")))
			{
				// domain not found in DNS
				$isValid = false;
			}
		}
		return $isValid;
	}
	
	/**
	 * Adds a user to the database - effectively registration
	 * email - POST variable, properly formatted e-mail of user
	 * pass - POST variable, SHA1 hash of user's password
	 * display_name - POST variable, chosen name of user to be publicly displayed
	 * returns - User's information on success, error on failure.
	 */
	public function add_post()
	{
		$this->load->model('Model_Users');
		
		//Get POST variables...
		$email = $this->post('email');
		$pass = $this->post('password');
		$display = $this->post('display_name');
		
		
		//Check error conditions:
		//E-mail must be valid...
		if (!$this->validEmail($email))
		{
			$this->response($this->rest_error(array("You have entered an invalid e-mail address.")),403);
			return;
		}
		//Password must be a valid SHA1 string
		if (!(bool)(preg_match('/^[0-9a-f]{40}$/i', $pass)))
		{
			$this->response($this->rest_error(array("The password provided is malformed or otherwise invalid.")),403);
			return;
		}
		//Display name must not be blank.
		if (strlen($display) <= 0)
		{
			$this->response($this->rest_error(array("You must provide a display name.")),403);
			return;
		}
		
		//Check for duplicates...
		$duplicates = $this->Model_Users->fetch_user_by_email($email);
		if (count($duplicates) <= 0) //No duplicates detected.
		{
			$this->Model_Users->add_user($email,$pass,$display);
			$this->response(array(), 201);
			return;
		}
		else
		{
			$this->response($this->rest_error(array("This e-mail address has already been used.")), 409);
			return;
		}

	}

	/**
	 * Removes a user from the database.
	 * id - POST variable - user id of the user to be removed. 
	 * returns - user ID on successful removal. 
	 */
	public function remove_post()
	{
		//TODO: Require some sort of authentication here.
		
		//Remove a user from the db
		$user_id = $this->post('id');

		$user_query = $this->db
				->from('lc_users')
				->where('id', $user_id)
				->get()
				->result();
				
		if (sizeof($user_query) <= 0)
		{
			$this->response($this->rest_error(array("The specified user does not exist.")),404);
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
