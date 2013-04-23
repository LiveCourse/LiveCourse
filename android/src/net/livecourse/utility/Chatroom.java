package net.livecourse.utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the object that holds the information for Chatrooms.
 * 
 * @author Darren Cheng
 */
public class Chatroom 
{
	private String id_string;
	private String section_string;
	private String subject_id;
	private String course_number;
	private String name;
	private String room_id;
	private String class_id;
	private String class_type;
	private String crn;
	private String section;
	private String start_time;
	private String end_time;
	private String start_date;
	private String end_date;
	private String dow_monday;
	private String dow_tuesday;
	private String dow_wednesday;
	private String dow_thursday;
	private String dow_friday;
	private String dow_saturday;
	private String dow_sunday;
	private String instructor;
	private String notes;
	private String capacity;
	
	public Chatroom(JSONObject ob)
	{
    	try 
    	{
		this.setIdString(ob.getString(		"class_id_string"));
		this.setSectionString(ob.getString(	"id_string"));
    	this.setSubjectId(ob.getString(		"subject_id"));
    	this.setCourseNumber(ob.getString(	"course_number"));
    	this.setName(ob.getString(			"name"));
    	this.setStartTime(ob.getString(		"start_time"));	            	
    	this.setRoomId(ob.getString(		"room_id"));	
    	this.setClassId(ob.getString(		"class_id"));
    	this.setClassType(ob.getString(		"type"));
    	this.setCrn(ob.getString(			"crn"));
    	this.setSection(ob.getString(		"section"));
    	this.setStartTime(ob.getString(		"start_time"));
    	this.setEndTime(ob.getString(		"end_time"));
    	this.setStartDate(ob.getString(		"start_date"));
    	this.setEndDate(ob.getString(		"end_date"));
    	this.setDowMonday(ob.getString(		"dow_monday"));
    	this.setDowTuesday(ob.getString(	"dow_tuesday"));
    	this.setDowWednesday(ob.getString(	"dow_wednesday"));
    	this.setDowThursday(ob.getString(	"dow_thursday"));
    	this.setDowFriday(ob.getString(		"dow_friday"));
    	this.setDowSaturday(ob.getString(	"dow_saturday"));
    	this.setDowSunday(ob.getString(		"dow_sunday"));
    	this.setInstructor(ob.getString(	"instructor"));
    	this.setNotes(ob.getString(			"notes"));
    	this.setCapacity(ob.getString(		"capacity"));   	
		} 
    	catch (JSONException e) 
    	{
			e.printStackTrace();
		}
	}
	public String toString()
	{
		String temp = 	"ID String: " 		+ id_string 	+
						" SectionString: "	+ section_string+
						" Course Number: " 	+ course_number + 
						" Name: " 			+ name 			+ 
						" Room ID: " 		+ room_id 		+
						" Class ID: "		+ class_id		+
						" Class Type: "		+ class_type 	+
						" CRN: "			+ crn 			+
						" Section: "		+ section 		+
						" Start Time: " 	+ start_time 	+
						" End Time: " 		+ end_time 		+ 
						" Start Date: " 	+ start_date 	+ 
						" End Date: " 		+ end_date 		+ 
						" Dow Monday: " 	+ dow_monday 	+ 
						" Dow Tuesday: " 	+ dow_tuesday 	+ 
						" Dow Wednesday: " 	+ dow_wednesday + 
						" Dow Thursday: " 	+ dow_thursday 	+ 
						" Dow Friday: " 	+ dow_friday 	+
						" Dow Saturday: " 	+ dow_saturday 	+
						" Dow Sunday: " 	+ dow_sunday	+
						" Instructor: "		+ instructor	+
						" Notes: "			+ notes			+
						" Capacity: "		+ capacity;
		return temp;
	}
	public String getSectionString()
	{
		return section_string;
	}
	public void setSectionString(String sectionString)
	{
		this.section_string = sectionString;
	}
	public String getInstructor() 
	{
		return instructor;
	}
	public void setInstructor(String instructor) 
	{
		this.instructor = instructor;
	}
	public String getNotes() 
	{
		return notes;
	}
	public void setNotes(String notes) 
	{
		this.notes = notes;
	}
	public String getEndTime() 
	{
		return end_time;
	}
	public void setEndTime(String end_time) 
	{
		this.end_time = end_time;
	}
	public String getStartDate() 
	{
		return start_date;
	}
	public void setStartDate(String start_date) 
	{
		this.start_date = start_date;
	}
	public String getEndDate() 
	{
		return end_date;
	}
	public void setEndDate(String end_date) 
	{
		this.end_date = end_date;
	}
	public String getDowMonday() 
	{
		return dow_monday;
	}
	public void setDowMonday(String dow_monday) 
	{
		this.dow_monday = dow_monday;
	}
	public String getDowTuesday() 
	{
		return dow_tuesday;
	}
	public void setDowTuesday(String dow_tuesday) 
	{
		this.dow_tuesday = dow_tuesday;
	}
	public String getDowWednesday() 
	{
		return dow_wednesday;
	}
	public void setDowWednesday(String dow_wednesday) 
	{
		this.dow_wednesday = dow_wednesday;
	}
	public String getDowThursday() 
	{
		return dow_thursday;
	}
	public void setDowThursday(String dow_thursday) 
	{
		this.dow_thursday = dow_thursday;
	}
	public String getDowFriday() 
	{
		return dow_friday;
	}
	public void setDowFriday(String dow_friday) 
	{
		this.dow_friday = dow_friday;
	}
	public String getDowSaturday() 
	{
		return dow_saturday;
	}
	public void setDowSaturday(String dow_saturday) 
	{
		this.dow_saturday = dow_saturday;
	}
	public String getDowSunday() 
	{
		return dow_sunday;
	}
	public void setDowSunday(String dow_sunday) 
	{
		this.dow_sunday = dow_sunday;
	}
	public String getIdString() 
	{
		return id_string;
	}
	public void setIdString(String id_string) 
	{
		this.id_string = id_string;
	}
	public String getSubjectId() 
	{
		return subject_id;
	}
	public void setSubjectId(String subject_id) 
	{
		this.subject_id = subject_id;
	}
	public String getCourseNumber() 
	{
		return course_number;
	}
	public void setCourseNumber(String course_number) 
	{
		this.course_number = course_number;
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public String getRoomId() 
	{
		return room_id;
	}
	public void setRoomId(String room_id) 
	{
		this.room_id = room_id;
	}
	public String getStartTime() 
	{
		return start_time;
	}
	public void setStartTime(String start_time) 
	{
		this.start_time = start_time;
	}
	public String getClassId() 
	{
		return class_id;
	}
	public void setClassId(String class_id) 
	{
		this.class_id = class_id;
	}
	public String getClassType() 
	{
		return class_type;
	}
	public void setClassType(String class_type) 
	{
		this.class_type = class_type;
	}
	public String getCrn() 
	{
		return crn;
	}
	public void setCrn(String crn) 
	{
		this.crn = crn;
	}
	public String getSection() 
	{
		return section;
	}
	public void setSection(String section) 
	{
		this.section = section;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
}
