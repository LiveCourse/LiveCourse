using RestSharp;
using RestSharp.Deserializers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace LiveCourse
{
    class REST
    {
        private String function; //REST method to call
        private Method method; //Method to call REST (GET, POST, etc)
        private Dictionary<String,String> variables; //Key/Value dictionary of variables to send with request

        public delegate void RestSuccess(System.Net.HttpStatusCode code, dynamic data); //Callback (delegate) to call on call success
        public delegate void RestFailure(System.Net.HttpStatusCode code, dynamic data); //Callback (delegate) to call on call failure

        public static String auth_token; //Authentication token
        public static String auth_pass; //User's SHA1 encrypted password

        /**
         * Constructor for REST
         * @myFunction The name of the REST function to call
         * @myMethod Method with which to call function (GET,POST,etc)
         * @myVariables Key/Value dictionary of variables to send with request
         */
        public REST(String myFunction, String myMethod, Dictionary<String,String> myVariables)
        {
            function = myFunction;
            switch (myMethod)
            {
                case "POST":
                    method = Method.POST;
                    break;
                case "GET":
                    method = Method.GET;
                    break;
                case "PUT":
                    method = Method.PUT;
                    break;
                case "DELETE":
                    method = Method.DELETE;
                    break;
                default:
                    method = Method.GET;
                    break;
            }
            variables = myVariables;
        }

        public async void call(RestSuccess successDelegate, RestFailure failureDelegate)
        {
            var client = new RestClient("http://livecourse.net/api/"); //Initialize REST

            var request = new RestRequest(function, method); //Give our function and method
            request.AddHeader(System.Net.HttpRequestHeader.Accept.ToString(), "application/json");

            //Add each variable
            if (variables != null)
            {
                foreach (KeyValuePair<String, String> entry in variables)
                {
                    request.AddParameter(entry.Key, entry.Value);
                }
            }

            //Authentication (if set)
            if (!String.IsNullOrEmpty(REST.auth_token))
            {
                request.AddHeader("Auth", "LiveCourseAuth token="+REST.auth_token+" auth="+SHA1.CalculateSHA1(REST.auth_token+REST.auth_pass.ToLower()+function).ToLower());
            }
            
            var asyncHandle = client.ExecuteAsync(request, response =>
            {
                if (response.StatusCode == System.Net.HttpStatusCode.OK ||
                    response.StatusCode == System.Net.HttpStatusCode.Created ||
                    response.StatusCode == System.Net.HttpStatusCode.Accepted ||
                    response.StatusCode == System.Net.HttpStatusCode.NonAuthoritativeInformation ||
                    response.StatusCode == System.Net.HttpStatusCode.NoContent ||
                    response.StatusCode == System.Net.HttpStatusCode.ResetContent ||
                    response.StatusCode == System.Net.HttpStatusCode.PartialContent)
                {
                    System.Diagnostics.Debug.WriteLine("Begin serialize");
                    if (successDelegate != null)
                        successDelegate(response.StatusCode, JsonConvert.DeserializeObject<dynamic>(response.Content));
                    System.Diagnostics.Debug.WriteLine("End serialize");
                }
                else
                {
                    if (failureDelegate != null)
                        failureDelegate(response.StatusCode, JsonConvert.DeserializeObject<dynamic>(response.Content));
                }
            });
        }
    }
}
