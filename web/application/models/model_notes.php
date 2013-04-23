<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Model_Notes extends CI_Model {
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	}
	
	/**
	 * Adds a note to the database.
	 * parameters
	 *	class_id Class to add the note to
	 *	parent_id ID of the parent note. 0 if none
	 *	linked_message_id Message ID from chat to link this note to.
	 *	text Text content of the note
	 *	timeadded (optional) time the note was added
	 */
	function add_note($class_id,$parent_id,$linked_message_id,$text,$timeadded = 0)
	{
		if ($timeadded == 0)
			$timeadded = time();
		$data = array(
			'class_id' => $class_id,
			'parent_note_id' => $parent_id,
			'linked_message_id' => $linked_message_id,
			'text' => $text,
			'timeadded' => $timeadded
		);
		return $this->db->insert('lc_notes', $data);
	}
	
	function _fetch_notes_recursive($class_id,$parent_id)
	{
		date_default_timezone_set ( "America/New_York" );
		$start_time = mktime (0,0,0, date("n"), date("j") , date("Y") );
		$end_time = $start_time+(24*60*60);
		$result = array();
		$query = $this->db
				->from('lc_notes')
				->where('class_id',$class_id)
				->where('parent_note_id',$parent_id)
				->where('timeadded >=',$start_time)
				->where('timeadded <=',$end_time)
				->get()
				->result();
		foreach($query as $r)
		{
			$r->children = $this->_fetch_notes_recursive($class_id,$r->id);
			array_push($result,$r);
		}
		return $result;
	}
	
	function fetch_notes_from_class($class_id)
	{
		return $this->_fetch_notes_recursive($class_id,0);
	}
	
	/**
	 * Checks to see if a note exists
	 * note_id - the ID number of the note to be checked
	 * class_id - (optional) the class ID of the note
	 * returns true if the note exists, or false if it doesnt
	 */
	function note_exists($note_id,$class_id = 0)
	{
		$count = $this->db
				->where('id',$note_id);
		if ($class_id > 0)
		{
			$count = $count->where('class_id',$class_id);
		}
		
		$count = $count->get('lc_notes')
				->result();
		if(count($count)>=1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
