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
    public class MyChatRoom
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
        public String C_Subject_Code
        {
            get;
            set;
        }
    }
}
