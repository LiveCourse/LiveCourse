using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.Linq;
using System.Data.Linq.Mapping;
namespace LiveCourse
{
    [Table]
    class MyChatRoom
    {
        [Column(IsPrimaryKey = true, IsDbGenerated = true, DbType = "INT NOT NULL Identity")]
        public int C_ID
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public String C_ID_String
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public String C_Name
        {
            get;
            set;
        }
        [Column]
        public int C_Course_Number
        {
            get;
            set;
        }
        [Column]
        public int C_Start_Time
        {
            get;
            set;
        }
        [Column]
        public int C_End_Time
        {
            get;
            set;
        }
        [Column(CanBeNull = true)]
        public DateTime C_Start_Date
        {
            get;
            set;
        }
        [Column(CanBeNull = true)]
        public DateTime C_End_Date
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Monday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Tuesday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Wednesday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Thursday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Friday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Saturday
        {
            get;
            set;
        }
        [Column]
        public bool C_DOW_Sunday
        {
            get;
            set;
        }
    }
}
