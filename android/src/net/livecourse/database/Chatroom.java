package net.livecourse.database;

public class Chatroom {
	private String id_string;
	private String subject_id;
	private String course_number;
	private String name;
	private String institution_id;
	private String room_id;
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
	
	public String toString()
	{
		String temp = "ID String: "+id_string+" Course Number: "+course_number+" Name: "+name
				+"Institution ID: "+institution_id+" Room ID: "+room_id+" Start Time: "+start_time+" End Time: "
				+end_time+" Start Date: "+start_date+" End Date: "+end_date+" Dow Monday: "+dow_monday+" Dow Monday: "+dow_monday
				+" Dow Tuesday: "+dow_tuesday+" Dow Wednesday: "+dow_wednesday+" Dow Thursday: "+dow_thursday+" Dow Friday: "+dow_friday
				+" Dow Sunday: "+dow_sunday;
		return temp;
	}
	public String getEndTime() {
		return end_time;
	}
	public void setEndTime(String end_time) {
		this.end_time = end_time;
	}
	public String getStartDate() {
		return start_date;
	}
	public void setStartDate(String start_date) {
		this.start_date = start_date;
	}
	public String getEndDate() {
		return end_date;
	}
	public void setEndDate(String end_date) {
		this.end_date = end_date;
	}
	public String getDowMonday() {
		return dow_monday;
	}
	public void setDowMonday(String dow_monday) {
		this.dow_monday = dow_monday;
	}
	public String getDowTuesday() {
		return dow_tuesday;
	}
	public void setDowTuesday(String dow_tuesday) {
		this.dow_tuesday = dow_tuesday;
	}
	public String getDowWednesday() {
		return dow_wednesday;
	}
	public void setDowWednesday(String dow_wednesday) {
		this.dow_wednesday = dow_wednesday;
	}
	public String getDowThursday() {
		return dow_thursday;
	}
	public void setDowThursday(String dow_thursday) {
		this.dow_thursday = dow_thursday;
	}
	public String getDowFriday() {
		return dow_friday;
	}
	public void setDowFriday(String dow_friday) {
		this.dow_friday = dow_friday;
	}
	public String getDowSaturday() {
		return dow_saturday;
	}
	public void setDowSaturday(String dow_saturday) {
		this.dow_saturday = dow_saturday;
	}
	public String getDowSunday() {
		return dow_sunday;
	}
	public void setDowSunday(String dow_sunday) {
		this.dow_sunday = dow_sunday;
	}
	public String getIdString() {
		return id_string;
	}
	public void setIdString(String id_string) {
		this.id_string = id_string;
	}
	public String getSubjectId() {
		return subject_id;
	}
	public void setSubjectId(String subject_id) {
		this.subject_id = subject_id;
	}
	public String getCourseNumber() {
		return course_number;
	}
	public void setCourseNumber(String course_number) {
		this.course_number = course_number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInstitutionId() {
		return institution_id;
	}
	public void setInstitutionId(String institution_id) {
		this.institution_id = institution_id;
	}
	public String getRoomId() {
		return room_id;
	}
	public void setRoomId(String room_id) {
		this.room_id = room_id;
	}
	public String getStartTime() {
		return start_time;
	}
	public void setStartTime(String start_time) {
		this.start_time = start_time;
	}
}
