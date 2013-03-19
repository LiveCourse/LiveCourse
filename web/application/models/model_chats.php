<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Chats extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}

	function search_chats($query_string)
	{
		$query = $this->db
				->like('course_number', $query_string)
				->or_like('name', $query_string)
				->from('lc_chats')
				->get();
		return $query->result();
	}

}
