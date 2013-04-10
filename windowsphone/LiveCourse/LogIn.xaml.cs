using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace LiveCourse
{
    public partial class LogIn : PhoneApplicationPage
    {
        ProgressIndicator progress;

        public LogIn()
        {
            InitializeComponent();
        }

        private void button_login_Click(object sender, RoutedEventArgs e)
        {
            //Disable form
            input_login.IsEnabled = false;
            input_password.IsEnabled = false;
            button_login.IsEnabled = false;
            button_register.IsEnabled = false;

            //Show progress indicator
            progress = new ProgressIndicator
            {
                IsVisible = true,
                IsIndeterminate = true,
                Text = "Authenticating..."
            };
            SystemTray.SetProgressIndicator(this, progress);
            progress.IsVisible = true;
            //Make a rest call to get an authentication token
            Dictionary<String,String> authvars = new Dictionary<String,String>();
            authvars.Add("email", input_login.Text);
            authvars.Add("device", "2");

            REST auth = new REST("auth", "GET", authvars);
            auth.call(auth_success, auth_failure);
        }

        public async void auth_success(System.Net.HttpStatusCode code, dynamic data)
        {
            progress.IsVisible = false;
            REST.auth_token = data.authentication.token;
            REST.auth_pass = SHA1.CalculateSHA1(input_password.Password);
            //Let's verify our password
            REST verify = new REST("auth/verify", "GET", null);
            verify.call(verify_success, verify_failure);
        }

        public async void auth_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            progress.IsVisible = false;
            if (code == HttpStatusCode.NotFound)
                MessageBox.Show("Invalid e-mail address, this account does not exist.");
            else
                MessageBox.Show("Error contacting LiveCourse. Please try again later.");
            //Enable form
            input_login.IsEnabled = true;
            input_password.IsEnabled = true;
            button_login.IsEnabled = true;
            button_register.IsEnabled = true;
        }

        public async void verify_success(System.Net.HttpStatusCode code, dynamic data)
        {
            progress.IsVisible = false;
            //Enable form
            input_login.IsEnabled = true;
            input_password.IsEnabled = true;
            button_login.IsEnabled = true;
            button_register.IsEnabled = true;
            NavigationService.Navigate(new Uri("/MainPage.xaml", UriKind.Relative));
        }

        public async void verify_failure(System.Net.HttpStatusCode code, dynamic data)
        {
            progress.IsVisible = false;
            if (code == HttpStatusCode.Unauthorized)
                MessageBox.Show("Your password was not valid.");
            else
                MessageBox.Show("Error contacting LiveCourse to verify your token. Please try again later.");
            //Enable form
            input_login.IsEnabled = true;
            input_password.IsEnabled = true;
            button_login.IsEnabled = true;
            button_register.IsEnabled = true;
        }
    }
}