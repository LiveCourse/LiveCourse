package net.livecourse.android.classlist;

import net.livecourse.R;
import net.livecourse.database.ChatroomLoader;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

public class ChatroomDialog extends DialogFragment implements DialogInterface.OnClickListener, LoaderCallbacks<Cursor>
{
	public final int 	DATA_FROM_DATABASE 	= 1;
	public final int 	DATA_FROM_OBJECT	= 2;
	
	private String 		sectionId;
	private int			flag;

	Chatroom			chatroom;
	
	private TextView 	chatroomCrn;
	private TextView 	chatroomInstructor;
	private TextView 	chatroomType;
	private TextView 	chatroomSection;
	private TextView 	chatroomLocation;
	private TextView 	chatroomTime;
	private TextView 	chatroomStart;
	private TextView 	chatroomEnd;
	private TextView 	chatroomCapacity;
	private TextView 	chatroomNotes;
	

	
	public ChatroomDialog(String sectionId)
	{
		this.sectionId 		= sectionId;
		this.flag 			= this.DATA_FROM_DATABASE;
	}
	
	public ChatroomDialog(String sectionId, Chatroom object)
	{
		this.sectionId 		= sectionId;
		this.flag			= this.DATA_FROM_OBJECT;
		this.chatroom		= object;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = this.getActivity().getLayoutInflater();	    
	    	    
	    builder.setView(inflater.inflate(R.layout.classlist_dialog_layout, null))
	           .setNegativeButton(R.string.classlist_dialog_button_negative, this)
	           .setTitle(this.sectionId);
	    
	    return builder.show();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
	    this.chatroomCrn 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_crn_text_view			);
	    this.chatroomInstructor = (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_instructor_text_view	);
	    this.chatroomType 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_type_text_view		);
	    this.chatroomSection 	= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_section_text_view		);
	    this.chatroomLocation 	= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_location_text_view	);
	    this.chatroomTime 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_time_text_view		);
	    this.chatroomStart 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_start_text_view		);
	    this.chatroomEnd 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_end_text_view			);
	    this.chatroomCapacity 	= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_capacity_text_view	);
	    this.chatroomNotes 		= (TextView) this.getDialog().findViewById(R.id.classlist_dialog_item_notes_text_view		);
	    
	    if(this.flag == this.DATA_FROM_DATABASE)
	    	this.getActivity().getSupportLoaderManager().restartLoader(Globals.CHATROOM_LOADER, null, this);
	    else if(this.flag == this.DATA_FROM_OBJECT)
	    	this.populateDialogFromObject();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		this.getDialog().cancel();
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		return new ChatroomLoader(this.getActivity(), Globals.appDb, this.sectionId);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		this.populateDialogFromDatabase(cursor);
	}
	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		// TODO Auto-generated method stub
		
	}
	
	private void populateDialogFromObject()
	{
		String className 			= this.chatroom.getName();
		String classSubjectCode 	= this.chatroom.getSubjectCode();
		String classCourseNumber 	= this.chatroom.getCourseNumber();
		String classRoomNumber 		= this.chatroom.getRoomNumber();
		String classBuilding 		= this.chatroom.getBuildingName();
		String classType 			= this.chatroom.getClassType();
		String classCrn 			= this.chatroom.getCrn();
		String classSection 		= this.chatroom.getSection();
		String classStartTime		= this.chatroom.getStartTime();
		String classEndTime 		= this.chatroom.getEndTime();
		String classStartDate 		= this.chatroom.getStartDate();
		String classEndDate 		= this.chatroom.getEndDate();
		String classInstructor 		= this.chatroom.getInstructor();
		String classNotes 			= this.chatroom.getNotes();
		String classCapacity 		= this.chatroom.getCapacity();
		
		String classTitle			= classSubjectCode + classCourseNumber + " " + className;
		String classLocation 		= classBuilding + " " + classRoomNumber;
		
		String classTime = "";
    	
    	if(this.chatroom.getDowMonday()		.equals("1"))
    		classTime += "M";
    	if(this.chatroom.getDowTuesday()	.equals("1"))
    		classTime += "T";
    	if(this.chatroom.getDowWednesday()	.equals("1"))
    		classTime += "W";
    	if(this.chatroom.getDowThursday()	.equals("1"))
    		classTime += "R";
    	if(this.chatroom.getDowFriday()		.equals("1"))
    		classTime += "F";
    	if(this.chatroom.getDowSaturday()	.equals("1"))
    		classTime += "S";
    	if(this.chatroom.getDowSunday()		.equals("1"))
    		classTime += "U";
    	classTime += " " + Utility.convertMinutesTo24Hour(this.chatroom.getStartTime()) + " - " + Utility.convertMinutesTo24Hour(this.chatroom.getEndTime());
		
    	Log.d("Chatroom dialog", 	" ClassName: " 			+ className 		+
				" ClassSubjectCode: " 	+ classSubjectCode 	+
				" ClassCourseNumber: " 	+ classCourseNumber +
				" ClassRoomNumber: " 	+ classRoomNumber 	+
				" ClassBuilding: " 		+ classBuilding 	+
				" ClassType: " 			+ classType 		+
				" ClassCrn: " 			+ classCrn 			+
				" ClassSection: " 		+ classSection 		+
				" ClassStartTime: " 	+ classStartTime 	+
				" ClassEndTime: " 		+ classEndTime 		+
				" ClassStartDate: " 	+ classStartDate 	+
				" ClassEndDate: " 		+ classEndDate 		+
				" ClassInstructor: " 	+ classInstructor 	+
				" ClassNotes: " 		+ classNotes		+
				" ClassCapacity:" 		+ classCapacity);
    	
    	this.getDialog()			.setTitle(classTitle	);
		
		this.chatroomCrn			.setText(classCrn		);
		this.chatroomInstructor		.setText(classInstructor);
		this.chatroomType			.setText(classType		);
		this.chatroomLocation		.setText(classLocation	);
		this.chatroomSection		.setText(classSection	);
		this.chatroomTime			.setText(classTime		);
		this.chatroomStart			.setText(classStartDate	);
		this.chatroomEnd			.setText(classEndDate	);
		this.chatroomCapacity		.setText(classCapacity	);
		this.chatroomNotes			.setText(classNotes		);
	}
	private void populateDialogFromDatabase(Cursor cursor)
	{
		if(!cursor.moveToFirst())
		{
			Log.e("Chatroom Dialog", "cursor is null");
			return;
		}		
		String className 			= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_NAME			));
		String classSubjectCode 	= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_SUBJECT_CODE	));
		String classCourseNumber 	= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_COURSE_NUMBER	));
		String classRoomNumber 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_ROOM_NUMBER	));
		String classBuilding 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_BUILDING_NAME	));
		String classType 			= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_TYPE			));
		String classCrn 			= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_CRN			));
		String classSection 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_SECTION		));
		String classStartTime		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_START_TIME	));
		String classEndTime 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_END_TIME		));
		String classStartDate 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_START_DATE	));
		String classEndDate 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_END_DATE		));
		String classInstructor 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_INSTRUCTOR	));
		String classNotes 			= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_NOTES			));
		String classCapacity 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CLASS_CAPACITY		));
		
		String classTitle			= classSubjectCode + classCourseNumber + " " + className;
		String classLocation 		= classBuilding + " " + classRoomNumber;
		
		String classTime 			= "";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_monday"	)).equals("1"))
			classTime += "M";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_tuesday"	)).equals("1"))
			classTime += "T";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_wednesday")).equals("1"))
			classTime += "W";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_thursday"	)).equals("1"))
			classTime += "R";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_friday"	)).equals("1"))
			classTime += "F";	
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_saturday"	)).equals("1"))
			classTime += "S";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_sunday"	)).equals("1"))
			classTime += "U";
		classTime += " " + Utility.convertMinutesTo24Hour(cursor.getString(cursor.getColumnIndexOrThrow("start_time"))) + " - " + Utility.convertMinutesTo24Hour(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));		
		
		Log.d("Chatroom dialog", 	" ClassName: " 			+ className 		+
									" ClassSubjectCode: " 	+ classSubjectCode 	+
									" ClassCourseNumber: " 	+ classCourseNumber +
									" ClassRoomNumber: " 	+ classRoomNumber 	+
									" ClassBuilding: " 		+ classBuilding 	+
									" ClassType: " 			+ classType 		+
									" ClassCrn: " 			+ classCrn 			+
									" ClassSection: " 		+ classSection 		+
									" ClassStartTime: " 	+ classStartTime 	+
									" ClassEndTime: " 		+ classEndTime 		+
									" ClassStartDate: " 	+ classStartDate 	+
									" ClassEndDate: " 		+ classEndDate 		+
									" ClassInstructor: " 	+ classInstructor 	+
									" ClassNotes: " 		+ classNotes		+
									" ClassCapacity:" 		+ classCapacity);
		
		this.getDialog()			.setTitle(classTitle	);
		
		this.chatroomCrn			.setText(classCrn		);
		this.chatroomInstructor		.setText(classInstructor);
		this.chatroomType			.setText(classType		);
		this.chatroomLocation		.setText(classLocation	);
		this.chatroomSection		.setText(classSection	);
		this.chatroomTime			.setText(classTime		);
		this.chatroomStart			.setText(classStartDate	);
		this.chatroomEnd			.setText(classEndDate	);
		this.chatroomCapacity		.setText(classCapacity	);
		this.chatroomNotes			.setText(classNotes		);

		cursor.close();
	}
}
