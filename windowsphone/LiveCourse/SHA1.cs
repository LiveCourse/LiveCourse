using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Security.Cryptography;

namespace LiveCourse
{
    class SHA1
    {
        internal static string CalculateSHA1(string text)
        {
            SHA1Managed s = new SHA1Managed();
            UTF8Encoding enc = new UTF8Encoding();
            s.ComputeHash(enc.GetBytes(text.ToCharArray()));
            return BitConverter.ToString(s.Hash).Replace("-", "");
        }
    }
}
