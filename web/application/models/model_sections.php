<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Sections extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}
	
	/**
	 * Gets section information by ID
	 * section_id - ID of chat
	 * returns - database row of section specified
	 */
	function get_section_by_id($section_id)
	{
		$query = $this->db
				->select('lc_classes.id_string as class_id_string, lc_classes.subject_id, lc_classes.course_number, lc_classes.name, lc_sections.*')
				->from('lc_sections')
				->join('lc_section_participants','lc_section_participants.section_id = lc_sections.id')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->where('lc_sections.id',$section_id)
				->get()
				->result();
		if (count($query) <= 0)
			return -1;
		else
			return $query[0];
	}
	
	/**
	 * Returns the integer section ID of the section ID string provided
	 * chat_id_string - ID string of chat to find
	 * returns - integer index ID of specified chat.
	 */
	function get_id_from_string($section_id_string)
	{
		$query = $this->db
				->select('id')
				->where('id_string = ',$section_id_string)
				->from('lc_sections')
				->get()
				->result();
		if (count($query) <= 0)
			return -1;
		else
			return $query[0]->id;
	}
	
	/**
	 * Joins a user to a section specified by integer ID
	 * user_id - ID of user to join to specified chat
	 * section_id - ID integer of chat to join
	 * permissions - TODO allow setting user as admin
	 * returns NULL or FALSE if failed.
	 */
	function join_section_by_id($user_id,$section_id,$permissions = 0)
	{
		//Now insert the data
		$data = array(
				'section_id'	=> $section_id,
				'user_id'	=> $user_id,
				'permissions'	=> $permissions,
				'jointime'	=> time()
				);
		return $this->db->insert('lc_section_participants', $data); //Should return NULL or FALSE if failed.
	}
	
	/**
	 * Fetches sections that the specified user is a participant in.
	 * user_id - ID of user
	 * returns - array of section information
	 */
	function get_subscribed_sections($user_id)
	{
		$query = $this->db
				->select('lc_classes.id_string as class_id_string, lc_classes.subject_id, lc_classes.course_number, lc_classes.name, lc_sections.*')
				->from('lc_sections')
				->join('lc_section_participants','lc_section_participants.section_id = lc_sections.id')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->where('lc_section_participants.user_id',$user_id)
				->get()
				->result();
		return $query;
	}
	
	/**
	 * Checks if the specified user is subscribed to the specified section.
	 * user_id - ID of the user
	 * section_id - integer ID of the section
	 * returns - TRUE if user is subscribed, FALSE otherwise.
	 */
	function is_user_subscribed($user_id,$section_id)
	{
		$query = $this->db
				->from('lc_section_participants')
				->where('lc_section_participants.user_id',$user_id)
				->where('lc_section_participants.section_id',$section_id)
				->get()
				->result();
		if (count($query) > 0)
			return true;
		return false;
	}
	
	/**
	 * Fetch a list of available sections by searching for some broad parameters
	 * query_string - String to search courses for
	 * returns a result consisting of matches.
	 */
	function search_sections($query_string)
	{
		$query = $this->db
				->select('lc_classes.id_string as class_id_string, lc_classes.subject_id, lc_classes.course_number, lc_classes.name, lc_sections.*')
				->from('lc_sections')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->like('lc_classes.course_number', $query_string)
				->or_like('lc_classes.name', $query_string)
				->get()
				->result();
		return $query;
	}
}
