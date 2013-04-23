package net.livecourse.android.classlist;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the object that holds the information for Chatrooms.
 * 
 * @author Darren Cheng
 */
public class Chatroom 
{
	private String chatIdString;
	private String sectionString;
	private String subjectCode;
	private String courseNumber;
	private String name;
	private String roomNumber;
	private String buildingName;
	private String classType;
	private String crn;
	private String section;
	private String startTime;
	private String endTime;
	private String startDate;
	private String endDate;
	private String dowMonday;
	private String dowTuesday;
	private String dowWednesday;
	private String dowThursday;
	private String dowFriday;
	private String dowSaturday;
	private String dowSunday;
	private String instructor;
	private String notes;
	private String capacity;
	
	public Chatroom(JSONObject ob)
	{
    	try 
    	{
			this.setChatIdString	(ob.getString("class_id_string"		));
			this.setSectionIdString	(ob.getString("section_id_string"	));
	    	this.setCrn				(ob.getString("crn"					));
	    	this.setSubjectCode		(ob.getString("subject_code"		));
	    	this.setCourseNumber	(ob.getString("course_number"		));
	    	this.setName			(ob.getString("name"				));
	    	this.setClassType		(ob.getString("type"				));
	    	this.setSection			(ob.getString("section"				));
	    	this.setBuildingName	(ob.getString("building_short_name"	));
	    	this.setRoomNumber		(ob.getString("room_number"			));	
	    	this.setDowMonday		(ob.getString("dow_monday"			));
	    	this.setDowTuesday		(ob.getString("dow_tuesday"			));
	    	this.setDowWednesday	(ob.getString("dow_wednesday"		));
	    	this.setDowThursday		(ob.getString("dow_thursday"		));
	    	this.setDowFriday		(ob.getString("dow_friday"			));
	    	this.setDowSaturday		(ob.getString("dow_saturday"		));
	    	this.setDowSunday		(ob.getString("dow_sunday"			));
	    	this.setStartTime		(ob.getString("start_time"			));	            	
	    	this.setStartTime		(ob.getString("start_time"			));
	    	this.setEndTime			(ob.getString("end_time"			));
	    	this.setStartDate		(ob.getString("start_date"			));
	    	this.setEndDate			(ob.getString("end_date"			));
	    	this.setCapacity		(ob.getString("capacity"			));   	
	    	this.setInstructor		(ob.getString("instructor"			));
	    	this.setNotes			(ob.getString("notes"				));
		} 
    	catch (JSONException e) 
    	{
			e.printStackTrace();
		}
	}
	public String toString()
	{
		String temp = 	"ID String: " 		+ chatIdString 	+
						" SectionString: "	+ sectionString	+
						" Course Number: " 	+ courseNumber 	+ 
						" Name: " 			+ name 			+ 
						" Room ID: " 		+ roomNumber 	+
						" Class ID: "		+ buildingName	+
						" Class Type: "		+ classType 	+
						" CRN: "			+ crn 			+
						" Section: "		+ section 		+
						" Start Time: " 	+ startTime 	+
						" End Time: " 		+ endTime 		+ 
						" Start Date: " 	+ startDate 	+ 
						" End Date: " 		+ endDate 		+ 
						" Dow Monday: " 	+ dowMonday 	+ 
						" Dow Tuesday: " 	+ dowTuesday 	+ 
						" Dow Wednesday: " 	+ dowWednesday 	+ 
						" Dow Thursday: " 	+ dowThursday 	+ 
						" Dow Friday: " 	+ dowFriday 	+
						" Dow Saturday: " 	+ dowSaturday 	+
						" Dow Sunday: " 	+ dowSunday		+
						" Instructor: "		+ instructor	+
						" Notes: "			+ notes			+
						" Capacity: "		+ capacity;
		return temp;
	}
	public String getSectionIdString()
	{
		return sectionString;
	}
	public void setSectionIdString(String sectionIdString)
	{
		this.sectionString = sectionIdString;
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
		return endTime;
	}
	public void setEndTime(String endTime) 
	{
		this.endTime = endTime;
	}
	public String getStartDate() 
	{
		return startDate;
	}
	public void setStartDate(String startDate) 
	{
		this.startDate = startDate;
	}
	public String getEndDate() 
	{
		return endDate;
	}
	public void setEndDate(String endDate) 
	{
		this.endDate = endDate;
	}
	public String getDowMonday() 
	{
		return dowMonday;
	}
	public void setDowMonday(String dowMonday) 
	{
		this.dowMonday = dowMonday;
	}
	public String getDowTuesday() 
	{
		return dowTuesday;
	}
	public void setDowTuesday(String dowTuesday) 
	{
		this.dowTuesday = dowTuesday;
	}
	public String getDowWednesday() 
	{
		return dowWednesday;
	}
	public void setDowWednesday(String dowWednesday) 
	{
		this.dowWednesday = dowWednesday;
	}
	public String getDowThursday() 
	{
		return dowThursday;
	}
	public void setDowThursday(String dowThursday) 
	{
		this.dowThursday = dowThursday;
	}
	public String getDowFriday() 
	{
		return dowFriday;
	}
	public void setDowFriday(String dowFriday) 
	{
		this.dowFriday = dowFriday;
	}
	public String getDowSaturday() 
	{
		return dowSaturday;
	}
	public void setDowSaturday(String dowSaturday) 
	{
		this.dowSaturday = dowSaturday;
	}
	public String getDowSunday() 
	{
		return dowSunday;
	}
	public void setDowSunday(String dowSunday) 
	{
		this.dowSunday = dowSunday;
	}
	public String getChatIdString() 
	{
		return chatIdString;
	}
	public void setChatIdString(String chatIdString) 
	{
		this.chatIdString = chatIdString;
	}
	public String getSubjectCode() 
	{
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) 
	{
		this.subjectCode = subjectCode;
	}
	public String getCourseNumber() 
	{
		return courseNumber;
	}
	public void setCourseNumber(String courseNumber) 
	{
		this.courseNumber = courseNumber;
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public String getRoomNumber() 
	{
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) 
	{
		this.roomNumber = roomNumber;
	}
	public String getStartTime() 
	{
		return startTime;
	}
	public void setStartTime(String startTime) 
	{
		this.startTime = startTime;
	}
	public String getBuildingName() 
	{
		return buildingName;
	}
	public void setBuildingName(String buildingName) 
	{
		this.buildingName = buildingName;
	}
	public String getClassType() 
	{
		return classType;
	}
	public void setClassType(String classType) 
	{
		this.classType = classType;
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
	public String getCapacity() 
	{
		return capacity;
	}
	public void setCapacity(String capacity) 
	{
		this.capacity = capacity;
	}
}
