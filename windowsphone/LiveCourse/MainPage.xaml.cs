using System;
using System.Net;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace LiveCourse
{
    public partial class MainPage : PhoneApplicationPage
    {
        private const string Con_String = @"isostore:/livecourse.sdf";

        ProgressIndicator progress_chatlist;

        // Constructor
        public MainPage()
        {
            InitializeComponent();
            BackKeyPress += MainPage_BackKeyPress; //Handle back key press.

            //Load up database
            using (ChatRoomDataContext context = new ChatRoomDataContext(Con_String))
            {
                if (!context.DatabaseExists())
                {
                    context.CreateDatabase();
                }

                list_chatrooms.ItemsSource = context.ChatRooms.ToList();
                updateChatRoomList();
            }
            
        }

        private void updateChatRoomList()
        {
            //Show progress indicator
            progress_chatlist = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Updating Chat Room Listing..."
            };
            SystemTray.SetProgressIndicator(this, progress_chatlist);
            progress_chatlist.IsVisible = true;

            using (ChatRoomDataContext context = new ChatRoomDataContext(Con_String))
            {
                if (!context.DatabaseExists())
                {
                    context.CreateDatabase();
                }
                
                list_chatrooms.ItemsSource = context.ChatRooms.ToList();
                REST chatroomREST = new REST("chats", "GET", null);
                chatroomREST.call(rest_updateChatRoomList_success,rest_updateChatRoomList_failure);
            }
        }

        public void rest_updateChatRoomList_success(System.Net.HttpStatusCode code, dynamic data)
        {
            using (ChatRoomDataContext context = new ChatRoomDataContext(Con_String))
            {
                if (!context.DatabaseExists())
                {
                    context.CreateDatabase();
                }
                context.ChatRooms.DeleteAllOnSubmit(context.ChatRooms);
                context.SubmitChanges();
                foreach (dynamic item in data)
                {
                    MyChatRoom room = new MyChatRoom
                    {
                        C_Name = (String)(item.name),
                        C_ID_String = (String)(item.id_string),
                        C_Course_Number = (int)(item.course_number),
                        C_Start_Time = (int)(item.start_time),
                        C_End_Time = (int)(item.end_time),
                        C_Start_Date = Convert.ToDateTime((String)(item.start_date)),
                        C_End_Date = Convert.ToDateTime((String)(item.end_date)),
                        C_DOW_Monday = ((String)(item.dow_monday)).Equals("1"),
                        C_DOW_Tuesday = ((String)(item.dow_tuesday)).Equals("1"),
                        C_DOW_Wednesday = ((String)(item.dow_wednesday)).Equals("1"),
                        C_DOW_Thursday = ((String)(item.dow_thursday)).Equals("1"),
                        C_DOW_Friday = ((String)(item.dow_friday)).Equals("1"),
                        C_DOW_Saturday = ((String)(item.dow_saturday)).Equals("1"),
                        C_DOW_Sunday = ((String)(item.dow_sunday)).Equals("1")
                    };
                    System.Diagnostics.Debug.WriteLine("Done with item.");
                    context.ChatRooms.InsertOnSubmit(room);
                }
                context.SubmitChanges();
                System.Diagnostics.Debug.WriteLine("Done query");

                list_chatrooms.ItemsSource = context.ChatRooms.ToList();
            }
            progress_chatlist.IsVisible = false;
        }

        public void rest_updateChatRoomList_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            MessageBox.Show("Error updating chat list!");
        }

        // Load data for the ViewModel Items
        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            
        }
        
        private void MainPage_BackKeyPress(object sender, System.ComponentModel.CancelEventArgs e)
        {
            //App.NaviService.BackKeyPress(sender, e);
            //if (NavigationService.CanGoBack)
            //{
            //    NavigationService.GoBack();
            //}
            e.Cancel = true; //Cancel navigation
        }

        private void chat_room_listing_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {

        }
    }
}