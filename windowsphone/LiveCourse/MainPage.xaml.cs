using System;
using System.Net;
using System.Collections.Generic;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.IO.IsolatedStorage;
using Microsoft.Phone.Notification;
using System.Text;

namespace LiveCourse
{
    public partial class MainPage : PhoneApplicationPage
    {
        public const string Con_String = @"isostore:/livecourse.sdf";

        ProgressIndicator progress_chatlist;

        // Constructor
        public MainPage()
        {
            InitializeComponent();

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
                foreach (dynamic item in data)
                {
                    MyChatRoom room = new MyChatRoom
                    {
                        C_Name = (String)(item.name),
                        C_ID_String = (String)(item.class_id_string),
                        C_Course_Number = (int)(item.course_number),
                        C_Subject_Code = (String)(item.subject_code)
                    };
                    String room_string_id = (String)(item.class_id_string);
                    IEnumerable<MyChatRoom> queryRooms = from MyChatRoom in context.ChatRooms
                                                         where MyChatRoom.C_ID_String == room_string_id
                                                         select MyChatRoom;
                    if (queryRooms.Count() <= 0) //If we don't have this room in the database, add it.
                        context.ChatRooms.InsertOnSubmit(room);
                }
                context.SubmitChanges();

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
        
        /*
        private void MainPage_BackKeyPress(object sender, System.ComponentModel.CancelEventArgs e)
        {
            //App.NaviService.BackKeyPress(sender, e);
            //if (NavigationService.CanGoBack)
            //{
            //    NavigationService.GoBack();
            //}
            e.Cancel = true; //Cancel navigation
        }
        */

        private void list_chatrooms_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            MyChatRoom room = ((list_chatrooms as LongListSelector).SelectedItem as MyChatRoom);
            NavigationService.Navigate(new Uri("/Chat.xaml?id=" + room.C_ID_String, UriKind.Relative));
            App.currentRoom = room;
        }

        private void Join_Click(object sender, EventArgs e)
        {

        }

        /**
         * Signs out of LiveCourse, deleting the database and authentication information from the device.
         */
        private void SignOut_Click(object sender, EventArgs e)
        {
            //Show progress bar
            progress_chatlist = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Signing Out..."
            };
            SystemTray.SetProgressIndicator(this, progress_chatlist);
            progress_chatlist.IsVisible = true;

            //Stop the push notifications.
            //Get device ID
            byte[] myDeviceID = (byte[])Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("DeviceUniqueId");
            string DeviceIDAsString = Convert.ToBase64String(myDeviceID);

            Dictionary<String, String> pushvars = new Dictionary<String, String>();
            pushvars.Add("dev_id", DeviceIDAsString);
            pushvars.Add("channel", "0");
            REST push = new REST("users/wp_remove", "POST", pushvars);
            push.call(rest_signout_success, rest_signout_failure);
        }

        /**
         * Called to sign out after we've successfully disabled PUSH notifications.
         */
        public void rest_signout_success(System.Net.HttpStatusCode code, dynamic data)
        {
            IsolatedStorageSettings appSettings = IsolatedStorageSettings.ApplicationSettings;
            appSettings.Remove("auth_pass");
            appSettings.Remove("auth_token");
            NavigationService.Navigate(new Uri("/LogIn.xaml", UriKind.Relative));
            IsolatedStorageFile storage = IsolatedStorageFile.GetUserStoreForApplication();
            storage.DeleteFile("livecourse.sdf");
            if (App.pushChannel != null)
            {
                App.pushChannel.Close();
                App.pushChannel = null;
            }
            progress_chatlist.IsVisible = false;
        }

        /**
         * Called when there is a failure to disable PUSH notifications.
         */
        public void rest_signout_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            progress_chatlist.IsVisible = false;
            MessageBox.Show("An error occurred contacting the server while attempting to sign out.");
        }
    }
}