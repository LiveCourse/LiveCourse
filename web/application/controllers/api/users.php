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
	 * Update's a user's color preference
	 * color - color code value between 0 and 5
	 */
	function update_color_post()
	{
		$this->load->model('Model_Users');

		//Get POST variables...
		$color = $this->post('color');
		$user_id = $this->authenticated_as;

		//Make sure they gave us a user id.
		if ($user_id <= 0)
		{
			$this->response(NULL,401);
			return;
		}

		$this->Model_Users->update_user_color($user_id,$color);
		$this->response(NULL,200);
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

	/**
	 *Register an android users' device
	 *name - Name of the user
	 *email - Email of the user
	 *regId - Registration ID of the device
	 *returns the data inputted on success, null and 404 on error.
	 */
	public function android_add_post()
	{

		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Get POST variables...
		$name 		= $this->post('name');
		$email 		= $this->post('email');
		$reg_id 	= $this->post('regId');


		//Check error conditions:
		//E-mail must be valid...
		if (!$this->validEmail($email))
		{
			$this->response($this->rest_error(array("You have entered an invalid e-mail address.")),403);
			return;
		}

		//name must not be blank.
		if (strlen($name) <= 0)
		{
			$this->response($this->rest_error(array("You must provide a name.")),403);
			return;
		}

		//must have a valid registration id
		if (strlen($reg_id) <= 0)
		{
			$this->response($this->rest_error(array("Invalid Registration ID.")),403);
			return;
		}

		//check to see the device isn't already registered
		if($this->Model_Users->fetch_android_user($user_id, $reg_id))
		{
			$this->response($this->rest_error(array("That device is already registered!")),409);
			return;
		}

		$result = $this->Model_Users->add_android_user($user_id,$email,$name,$reg_id);

		if($result)
		{
			$this->response($result, array("Device successfully registered!"),200);
		}
		else
		{
			$this->response($this->rest_error(array("Error registering device!")),400);
		}
		return;
	}

	/**
	 *Retrieves an android device by its registration ID
	 *gcm_regid - registration ID of the device
	 *returns the related information of the user
	 */
	function android_retrieve_get()
	{
		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Get GET variables...
		$reg_id 	= $this->get('gcm_regid');


		//Check error conditions:
		//must have a valid registration id
		if (strlen($reg_id) <= 0)
		{
			$this->response($this->rest_error(array("Invalid Registration ID.")),403);
			return;
		}

		$data = $this->Model_Users->fetch_android_user($user_id, $reg_id);

		//check to see the device exists
		if(!$data)
		{
			$this->response($this->rest_error(array("That device does not exist!")),409);

		}
		else
		{
			return $this->response($data, 200);
		}
		return;
	}
	/**
	 * Removes an android user from the database.
	 * reg_id - POST variable - registration id of the device to be removed.
	 * returns - device ID on successful removal.
	 */
	public function android_remove_post()
	{
		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;

		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Remove a user from the db
		$reg_id = $this->post('gcm_regid');

		//Check error conditions:
		//must have a valid registration id
		if (strlen($reg_id) <= 0)
		{
			$this->response($this->rest_error(array("Invalid Registration ID.")),403);
			return;
		}

		$count = $this->Model_Users->remove_android($user_id, $reg_id);

		if ($count <= 0)
		{
			$this->response($this->rest_error(array("Error removing device!")),404);
		}
		else
		{
			$this->response($reg_id, 200);
		}

	}

	/**
	 *Changes a user's password
	 *password - POST variable that includes the new SHA1 hashed password
	 *returns True if successful, or a verbal error on failure.
	 */
	function change_password_post()
	{
		$pass = $this->post('password');

		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//Password must be a valid SHA1 string
		if (!(bool)(preg_match('/^[0-9a-f]{40}$/i', $pass)))
		{
			$this->response($this->rest_error(array("The password provided is malformed or otherwise invalid.")),403);
			return;
		}

		$pass_changed = $this->Model_Users->change_user_password($user_id, $pass);
		if($pass_changed)
		{
			$this->response(true, 200);
		}
		else
		{
			$this->response($this->rest_error(array("Password change failed!")),500);
		}
		return;
	}

	/**
	 *Changes a user's name
	 *name - POST variable that includes the user's new name
	 *returns True if successful, or a verbal error on failure.
	 */
	function change_display_name_post()
	{
		$this->db->update('lc_users',array('name' => 'Brandon and Darren TURD faces', 'password' => '3784fb671e6fab994a54645d9c266b3d7c5d48da'), array('display_name' => 'Lars Smellingson'));
		$name = $this->post('name');

		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		//Check to see if they are authenticated
		$user_id = $this->authenticated_as;
		if ($this->authenticated_as <= 0)
		{
			$this->response($this->rest_error(array("You must be logged in to perform this action.")),401);
			return;
		}

		//the name must exist
		if (strlen($name) <= 0)
		{
			$this->response($this->rest_error(array("No username supplied!")),403);
			return;
		}

		$name_changed = $this->Model_Users->change_user_name($user_id, $name);
		if($name_changed)
		{
			$this->response(NULL, 200);
		}
		else
		{
			$this->response($this->rest_error(array("Name change failed!")),500);
		}
		return;
	}
}
