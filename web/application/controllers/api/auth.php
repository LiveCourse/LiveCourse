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
class Auth extends REST_Controller
{
	// Index command is run when no command is specified.

	/**
	 * Request authentication.
	 * email - e-mail of user to authenticate as
	 * returns - challenge string needed to complete authentication handshake.
	 */
	public function index_get()
	{
		$email = $this->get('email'); // GET parameter
		$device = $this->get('device'); // GET parameter
		$this->load->model('Model_Users');
		$this->load->model('Model_Auth');

		$user = $this->Model_Users->fetch_user_by_email($email);
		if (count($user) > 0)
		{
			$user = $user[0];
			$auth = $this->Model_Auth->generate_token($user->id,$device);
			$response = array();
			$response['authentication'] = array('token' => $auth);
			$this->response($response);
			return;
		} else {
			$this->response($this->rest_error(array("The e-mail provided could not be found.")),404);
			return;
		}
	}

	/**
	 * Verifies authorization status
	 */
	public function verify_get()
	{
		if ($this->authenticated_as > 0)
		{
			$response['authentication'] = array('success' => 'true', 'user_id' => $this->authenticated_as);
			$this->response($response);
			return;
		}
		$this->response($this->rest_error(array("Your authentication code was not valid.")),401);
	}
}
