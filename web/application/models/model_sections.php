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
				->select('lc_classes.id_string as class_id_string, lc_sections.id_string as section_id_string, lc_sections.crn, lc_subjects.code as subject_code, lc_classes.course_number, lc_classes.name, lc_sections.type, lc_buildings.short_name as building_short_name, lc_rooms.room_number, lc_sections.dow_monday, lc_sections.dow_tuesday, lc_sections.dow_wednesday, lc_sections.dow_thursday, lc_sections.dow_friday, lc_sections.dow_saturday, lc_sections.dow_sunday, lc_sections.start_time, lc_sections.end_time, lc_sections.start_date, lc_sections.end_date, lc_sections.capacity, lc_sections.instructor, lc_sections.notes')
				->from('lc_sections')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->join('lc_subjects','lc_subjects.id = lc_classes.subject_id')
				->join('lc_rooms','lc_rooms.id = lc_sections.room_id')
				->join('lc_buildings','lc_buildings.id = lc_rooms.building_id')
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
				->select('lc_classes.id_string as class_id_string, lc_sections.id_string as section_id_string, lc_sections.crn, lc_subjects.code as subject_code, lc_classes.course_number, lc_classes.name, lc_sections.type, lc_buildings.short_name as building_short_name, lc_rooms.room_number, lc_sections.dow_monday, lc_sections.dow_tuesday, lc_sections.dow_wednesday, lc_sections.dow_thursday, lc_sections.dow_friday, lc_sections.dow_saturday, lc_sections.dow_sunday, lc_sections.start_time, lc_sections.end_time, lc_sections.start_date, lc_sections.end_date, lc_sections.capacity, lc_sections.instructor, lc_sections.notes')
				->from('lc_sections')
				->join('lc_section_participants','lc_section_participants.section_id = lc_sections.id')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->join('lc_subjects','lc_subjects.id = lc_classes.subject_id')
				->join('lc_rooms','lc_rooms.id = lc_sections.room_id')
				->join('lc_buildings','lc_buildings.id = lc_rooms.building_id')
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
				->select('lc_classes.id_string as class_id_string, lc_sections.id_string as section_id_string, lc_sections.crn, lc_subjects.code as subject_code, lc_classes.course_number, lc_classes.name, lc_sections.type, lc_buildings.short_name as building_short_name, lc_rooms.room_number, lc_sections.dow_monday, lc_sections.dow_tuesday, lc_sections.dow_wednesday, lc_sections.dow_thursday, lc_sections.dow_friday, lc_sections.dow_saturday, lc_sections.dow_sunday, lc_sections.start_time, lc_sections.end_time, lc_sections.start_date, lc_sections.end_date, lc_sections.capacity, lc_sections.instructor, lc_sections.notes')
				->from('lc_sections')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->join('lc_subjects','lc_subjects.id = lc_classes.subject_id')
				->join('lc_rooms','lc_rooms.id = lc_sections.room_id')
				->join('lc_buildings','lc_buildings.id = lc_rooms.building_id')
				->like('lc_classes.course_number', $query_string)
				->or_like('lc_classes.name', $query_string)
				->get()
				->result();
		return $query;
	}
	
	/**
	 * Fetch a list of available sections by searching for some broad parameters
	 * query_parameters - Array of...
	 * 	crn - CRN number
	 * 	subject_code - Subject code
	 * 	course_number - Course Number
	 * 	building_short_name - Building short name
	 * 	room_number - Room number (requires building)
	 * returns a result consisting of matches.
	 */
	function search_sections_advanced($query_parameters)
	{
		$query = $this->db
				->select('lc_classes.id_string as class_id_string, lc_sections.id_string as section_id_string, lc_sections.crn, lc_subjects.code as subject_code, lc_classes.course_number, lc_classes.name, lc_sections.type, lc_buildings.short_name as building_short_name, lc_rooms.room_number, lc_sections.dow_monday, lc_sections.dow_tuesday, lc_sections.dow_wednesday, lc_sections.dow_thursday, lc_sections.dow_friday, lc_sections.dow_saturday, lc_sections.dow_sunday, lc_sections.start_time, lc_sections.end_time, lc_sections.start_date, lc_sections.end_date, lc_sections.capacity, lc_sections.instructor, lc_sections.notes')
				->from('lc_sections')
				->join('lc_classes','lc_classes.id = lc_sections.class_id')
				->join('lc_subjects','lc_subjects.id = lc_classes.subject_id')
				->join('lc_rooms','lc_rooms.id = lc_sections.room_id')
				->join('lc_buildings','lc_buildings.id = lc_rooms.building_id');
		if (isset($query_parameters["crn"]))
			$query->like('lc_sections.crn', $query_parameters["crn"]);
		if (isset($query_parameters["subject_code"]))
			$query->like('lc_subjects.code', $query_parameters["subject_code"]);
		if (isset($query_parameters["course_number"]))
			$query->like('lc_classes.course_number', $query_parameters["course_number"]);
		if (isset($query_parameters["building_short_name"]))
		{
			$query->like('lc_buildings.short_name', $query_parameters["building_short_name"]);
			if (isset($query_parameters["room_number"]))
				$query->like('lc_rooms.room_number', $query_parameters["room_number"]);
		}
		return $query->get()->result();
	}
}
