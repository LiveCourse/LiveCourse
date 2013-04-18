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

            /// Holds the push channel that is created or found.
            HttpNotificationChannel pushChannel;

            // The name of our push channel.
            string channelName = "ChatNotifications";

            // Try to find the push channel.
            pushChannel = HttpNotificationChannel.Find(channelName);

            // If the channel was not found, then create a new connection to the push service.
            if (pushChannel == null)
            {
                pushChannel = new HttpNotificationChannel(channelName);

                // Register for all the events before attempting to open the channel.
                pushChannel.ChannelUriUpdated += new EventHandler<NotificationChannelUriEventArgs>(PushChannel_ChannelUriUpdated);
                pushChannel.ErrorOccurred += new EventHandler<NotificationChannelErrorEventArgs>(PushChannel_ErrorOccurred);

                // Register for this notification only if you need to receive the notifications while your application is running.
                pushChannel.ShellToastNotificationReceived += new EventHandler<NotificationEventArgs>(PushChannel_ShellToastNotificationReceived);

                pushChannel.Open();

                // Bind this new channel for toast events.
                pushChannel.BindToShellToast();

            }
            else
            {
                // The channel was already open, so just register for all the events.
                pushChannel.ChannelUriUpdated += new EventHandler<NotificationChannelUriEventArgs>(PushChannel_ChannelUriUpdated);
                pushChannel.ErrorOccurred += new EventHandler<NotificationChannelErrorEventArgs>(PushChannel_ErrorOccurred);

                // Register for this notification only if you need to receive the notifications while your application is running.
                pushChannel.ShellToastNotificationReceived += new EventHandler<NotificationEventArgs>(PushChannel_ShellToastNotificationReceived);

                // Display the URI for testing purposes. Normally, the URI would be passed back to your web service at this point.
                byte[] myDeviceID = (byte[])Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("DeviceUniqueId");
                string DeviceIDAsString = Convert.ToBase64String(myDeviceID);
                System.Diagnostics.Debug.WriteLine("Device ID: '" + DeviceIDAsString + "'");
                System.Diagnostics.Debug.WriteLine("PUSH URI: '" + pushChannel.ChannelUri.ToString() + "'");

            }
            
        }

        void PushChannel_ChannelUriUpdated(object sender, NotificationChannelUriEventArgs e)
        {

            Dispatcher.BeginInvoke(() =>
            {
                byte[] myDeviceID = (byte[])Microsoft.Phone.Info.DeviceExtendedProperties.GetValue("DeviceUniqueId");
                string DeviceIDAsString = Convert.ToBase64String(myDeviceID);
                System.Diagnostics.Debug.WriteLine("Device ID: '"+DeviceIDAsString+"'");
                System.Diagnostics.Debug.WriteLine("PUSH URI: '"+e.ChannelUri.ToString()+"'");

                Dictionary<String, String> wpvars = new Dictionary<String, String>();
                wpvars.Add("dev_id", DeviceIDAsString);
                wpvars.Add("url", e.ChannelUri.ToString());
                REST chatroomREST = new REST("users/wp_add", "POST", wpvars);
                chatroomREST.call(null, null);

            });
        }
        void PushChannel_ErrorOccurred(object sender, NotificationChannelErrorEventArgs e)
        {
            // Error handling logic for your particular application would be here.
            Dispatcher.BeginInvoke(() =>
                MessageBox.Show(String.Format("A push notification {0} error occurred.  {1} ({2}) {3}",
                    e.ErrorType, e.Message, e.ErrorCode, e.ErrorAdditionalData))
                    );
        }

        void PushChannel_ShellToastNotificationReceived(object sender, NotificationEventArgs e)
        {
            StringBuilder message = new StringBuilder();
            string relativeUri = string.Empty;

            message.AppendFormat("Received Toast {0}:\n", DateTime.Now.ToShortTimeString());

            // Parse out the information that was part of the message.
            foreach (string key in e.Collection.Keys)
            {
                message.AppendFormat("{0}: {1}\n", key, e.Collection[key]);

                if (string.Compare(
                    key,
                    "wp:Param",
                    System.Globalization.CultureInfo.InvariantCulture,
                    System.Globalization.CompareOptions.IgnoreCase) == 0)
                {
                    relativeUri = e.Collection[key];
                }
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
                    if (!context.ChatRooms.Contains<MyChatRoom>(room)) //If we don't have this room in the database, add it.
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

        private void SignOut_Click(object sender, EventArgs e)
        {
            IsolatedStorageSettings appSettings = IsolatedStorageSettings.ApplicationSettings;
            appSettings.Remove("auth_pass");
            appSettings.Remove("auth_token");
            NavigationService.Navigate(new Uri("/LogIn.xaml", UriKind.Relative));
        }
    }
}