﻿using System;
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
using Newtonsoft.Json;
using Microsoft.Devices;

namespace LiveCourse
{
    public partial class ChatPage : PhoneApplicationPage
    {
        ProgressIndicator progress_history;
        ProgressIndicator progress_participants;
        public ObservableCollection<ChatMessage> chat_messages;
        public ObservableCollection<Participant> chat_participants;

        DispatcherTimer participants_timer;
        DispatcherTimer chat_timer;

        public ChatPage()
        {
            InitializeComponent();
            
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            string chatidstring;
            if (NavigationContext.QueryString.TryGetValue("id", out chatidstring))
            {
                using (ChatRoomDataContext context = new ChatRoomDataContext(MainPage.Con_String))
                {
                    IEnumerable<MyChatRoom> queryRooms = from MyChatRoom in context.ChatRooms
                                     where MyChatRoom.C_ID_String == chatidstring
                                     select MyChatRoom;
                    App.currentRoom = queryRooms.First();
                }
            }
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
        }

        protected override void OnNavigatingFrom(NavigatingCancelEventArgs e)
        {
        }

        public void pushRawChannel_HttpNotificationReceived(object sender, Microsoft.Phone.Notification.HttpNotificationEventArgs e)
        {
            string content;

            using (System.IO.StreamReader reader = new System.IO.StreamReader(e.Notification.Body))
            {
                content = reader.ReadToEnd();
            }
            dynamic m = JsonConvert.DeserializeObject<dynamic>(content);

            if (!((String)(m.chat_id)).Equals(App.currentRoom.C_ID_String)) //If this isn't for our current room, ignore it.
                return;

            ChatMessage msg = new ChatMessage
            {
                msg_id = (int)(m.message_id),
                sender_id = (int)(m.user_id),
                sender_name = (String)(m.display_name),
                msg_string = (String)(m.message_string),
                chatroom_id = App.currentRoom.C_ID
            };
            Dispatcher.BeginInvoke(() => addMessage(msg));
            using (ChatRoomDataContext context = new ChatRoomDataContext(MainPage.Con_String))
            {
                context.ChatMessages.InsertOnSubmit(msg);
                context.SubmitChanges();
            }
            //throw new NotImplementedException();
        }

        public void addMessage(ChatMessage m)
        {
            chat_messages.Add(m);
            list_messages.ScrollTo(chat_messages.Last());
            VibrateController vib = VibrateController.Default;
            vib.Start(TimeSpan.FromMilliseconds(150));
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
            if (chat_messages.Count <= 0)
                return;
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
                        chatroom_id = (int)(App.currentRoom.C_ID)
                    };
                    chat_messages.Add(msg);
                    using (ChatRoomDataContext context = new ChatRoomDataContext(MainPage.Con_String))
                    {
                        context.ChatMessages.InsertOnSubmit(msg);
                        context.SubmitChanges();
                    }
                }
                list_messages.ScrollTo(chat_messages.Last());
            }
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

            //Grab recent history from database
            System.Diagnostics.Debug.WriteLine("Loading recent chat from DB...");
            using (ChatRoomDataContext context = new ChatRoomDataContext(MainPage.Con_String))
            {
                IEnumerable<ChatMessage> queryMsgs = from ChatMessage in context.ChatMessages
                                                     where ChatMessage.chatroom_id == App.currentRoom.C_ID
                                                     select ChatMessage;
                foreach (ChatMessage c in queryMsgs)
                {
                    chat_messages.Add(c);
                }
            }

            Dictionary<String, String> chatvars = new Dictionary<String, String>();
            chatvars.Add("chat_id", App.currentRoom.C_ID_String);
            if (chat_messages.Count <= 0)
            {
                System.Diagnostics.Debug.WriteLine("No history stored. Fetching from server instead...");
                REST historyREST = new REST("chats/fetch_recent", "GET", chatvars);
                historyREST.call(rest_load_history_success, rest_load_history_failure);
            } else {
                progress_history.IsVisible = false;
                //Switching to PUSH for this.
                //chat_timer = new DispatcherTimer();
                //chat_timer.Interval = TimeSpan.FromMilliseconds(2000);
                //chat_timer.Tick += new EventHandler(chat_tick);
                //chat_timer.Start();
                DispatcherTimer scroll_timer = new DispatcherTimer();
                scroll_timer.Interval = TimeSpan.FromMilliseconds(100);
                scroll_timer.Tick += new EventHandler(scrollChatToBottom);
                scroll_timer.Start();
                loadNewChat();
            }
        }

        public void scrollChatToBottom(object sender, EventArgs e)
        {
            list_messages.ScrollTo(list_messages.ItemsSource[list_messages.ItemsSource.Count - 1]);
            (sender as DispatcherTimer).Stop();
        }

        public void rest_load_history_success(System.Net.HttpStatusCode code, dynamic data)
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
                        chatroom_id = App.currentRoom.C_ID
                    };
                    chat_messages.Add(msg);
                    using (ChatRoomDataContext context = new ChatRoomDataContext(MainPage.Con_String))
                    {
                        context.ChatMessages.InsertOnSubmit(msg);
                        context.SubmitChanges();
                    }
                }
                list_messages.ScrollTo(chat_messages.Last());
            }
            progress_history.IsVisible = false;
            /*
            chat_timer = new DispatcherTimer();
            chat_timer.Interval = TimeSpan.FromMilliseconds(2000);
            chat_timer.Tick += new EventHandler(chat_tick);
            chat_timer.Start();
             */
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