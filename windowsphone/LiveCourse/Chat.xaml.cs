using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Threading;

namespace LiveCourse
{
    public partial class ChatPage : PhoneApplicationPage
    {
        ProgressIndicator progress_history;
        ProgressIndicator progress_participants;
        ObservableCollection<ChatMessage> chat_messages;
        ObservableCollection<Participant> chat_participants;

        DispatcherTimer participants_timer;
        DispatcherTimer chat_timer;

        public ChatPage()
        {
            InitializeComponent();
            
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            chatpage_pivot.Title = App.currentRoom.C_Name.ToUpper();
            chat_messages = new ObservableCollection<ChatMessage>();
            chat_participants = new ObservableCollection<Participant>();
            list_messages.ItemsSource = chat_messages;
            list_participants.ItemsSource = chat_participants;
            loadRecentChatHistory();
            loadParticipants();

            participants_timer = new DispatcherTimer();
            participants_timer.Interval = TimeSpan.FromMilliseconds(10000);
            participants_timer.Tick += new EventHandler(participants_tick);
            participants_timer.Start();

            chat_timer = new DispatcherTimer();
            chat_timer.Interval = TimeSpan.FromMilliseconds(2000);
            chat_timer.Tick += new EventHandler(chat_tick);
            chat_timer.Start();
        }

        void participants_tick(object sender, EventArgs e)
        {
            loadParticipants();
            participants_timer.Interval = TimeSpan.FromMilliseconds(10000);
            participants_timer.Stop();
            participants_timer.Start();
        }

        void chat_tick(object sender, EventArgs e)
        {
            loadNewChat();
        }

        protected void loadParticipants()
        {
            //Show progress indicator
            /*
            progress_participants = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Updating Chat Room Participants..."
            };
            SystemTray.SetProgressIndicator(this, progress_participants);
            progress_participants.IsVisible = true;
            */
            Dictionary<String, String> participantvars = new Dictionary<String, String>();
            participantvars.Add("id", App.currentRoom.C_ID_String);
            REST historyREST = new REST("chats/get_participants", "GET", participantvars);
            historyREST.call(rest_load_participants_success, rest_load_history_failure);
        }

        public void rest_load_participants_success(System.Net.HttpStatusCode code, dynamic data)
        {
            foreach (dynamic item in data)
            {
                Participant usr = new Participant
                {
                    user_id = (int)(item.id),
                    display_name = (String)(item.display_name),
                    time_lastfocused = (int)(item.time_lastfocus),
                    time_lastrequest = (int)(item.time_lastrequest),
                    ignored = ((String)(item.ignored)).Equals("1")
                };
                bool found = false;
                foreach (Participant p in chat_participants)
                {
                    if (p.user_id == (int)(item.id))
                    {
                        //Update their data
                        p.display_name = (String)(item.display_name);
                        p.time_lastfocused = (int)(item.time_lastfocus);
                        p.time_lastrequest = (int)(item.time_lastrequest);
                        p.ignored = ((String)(item.ignored)).Equals("1");
                        found = true;
                        break;
                    }
                }
                //Otherwise add them.
                if (!found)
                    chat_participants.Add(usr);
            }
            //progress_participants.IsVisible = false;
        }

        public void rest_load_participants_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            //progress_participants.IsVisible = false;
            MessageBox.Show("Error updating participant listing!");
        }

        protected void loadNewChat()
        {
            //Show progress indicator
            /*
            progress_history = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Updating Chat Room..."
            };
            SystemTray.SetProgressIndicator(this, progress_history);
            progress_history.IsVisible = true;
             */
            Dictionary<String, String> chatvars = new Dictionary<String, String>();
            chatvars.Add("chat_id", App.currentRoom.C_ID_String);
            chatvars.Add("msg_id", chat_messages.Last().msg_id.ToString());
            REST historyREST = new REST("chats/fetch_since", "GET", chatvars);
            historyREST.call(rest_load_new_chat_success, rest_load_new_chat_failure);
        }

        public void rest_load_new_chat_success(System.Net.HttpStatusCode code, dynamic data)
        {
            if (data != null)
            {
                foreach (dynamic item in data)
                {
                    ChatMessage msg = new ChatMessage
                    {
                        msg_id = (int)(item.id),
                        sender_id = (int)(item.user_id),
                        sender_name = (String)(item.display_name),
                        msg_string = (String)(item.message_string),
                        chatroom_id = (int)(item.chat_id)
                    };
                    chat_messages.Add(msg);
                }
            }
            list_messages.ScrollTo(chat_messages.Last());
            //progress_history.IsVisible = false;
            chat_timer.Interval = TimeSpan.FromMilliseconds(2000);
            chat_timer.Stop();
            chat_timer.Start();
        }

        public void rest_load_new_chat_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            //progress_history.IsVisible = false;
            MessageBox.Show("Error updating chat!");
            chat_timer.Interval = TimeSpan.FromMilliseconds(2000);
            chat_timer.Stop();
            chat_timer.Start();
        }

        protected void loadRecentChatHistory()
        {
            //Show progress indicator
            progress_history = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Updating Chat Room Listing..."
            };
            SystemTray.SetProgressIndicator(this, progress_history);
            progress_history.IsVisible = true;
            Dictionary<String, String> chatvars = new Dictionary<String, String>();
            chatvars.Add("chat_id", App.currentRoom.C_ID_String);
            REST historyREST = new REST("chats/fetch_recent", "GET", chatvars);
            historyREST.call(rest_load_history_success, rest_load_history_failure);
        }

        public void rest_load_history_success(System.Net.HttpStatusCode code, dynamic data)
        {
            foreach (dynamic item in data)
            {
                ChatMessage msg = new ChatMessage
                {
                    msg_id = (int)(item.id),
                    sender_id = (int)(item.user_id),
                    sender_name = (String)(item.display_name),
                    msg_string = (String)(item.message_string),
                    chatroom_id = (int)(item.chat_id)
                };
                chat_messages.Add(msg);
            }
            list_messages.ScrollTo(chat_messages.Last());
            progress_history.IsVisible = false;
        }

        public void rest_load_history_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            progress_history.IsVisible = false;
            MessageBox.Show("Error updating chat history!");
        }

        private void Send_Click(object sender, EventArgs e)
        {
            input_chat_text.IsEnabled = false;
            Dictionary<String, String> sendvars = new Dictionary<String, String>();
            sendvars.Add("chat_id", App.currentRoom.C_ID_String);
            sendvars.Add("message", input_chat_text.Text);
            REST sendREST = new REST("chats/send", "POST", sendvars);
            sendREST.call(rest_send_success, rest_send_failure);
        }

        public void rest_send_success(System.Net.HttpStatusCode code, dynamic data)
        {
            input_chat_text.Text = "";
            input_chat_text.IsEnabled = true;
        }

        public void rest_send_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            MessageBox.Show("Error sending message to chat room!");
            input_chat_text.IsEnabled = true;
        }

        private void chatpage_pivot_LoadingPivotItem(object sender, PivotItemEventArgs e)
        {
            if (e.Item == pivot_chat)
                ApplicationBar.IsVisible = true;
            else
                ApplicationBar.IsVisible = false;  
        }
    }
}