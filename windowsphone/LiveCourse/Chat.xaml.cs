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

namespace LiveCourse
{
    public partial class ChatPage : PhoneApplicationPage
    {
        ProgressIndicator progress_history;
        ObservableCollection<ChatMessage> chat_messages;
        public ChatPage()
        {
            InitializeComponent();
            
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            chatpage_pivot.Title = App.currentRoom.C_Name.ToUpper();
            chat_messages = new ObservableCollection<ChatMessage>();
            list_messages.ItemsSource = chat_messages;
            loadRecentChatHistory();
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
    }
}