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
    class Participant
    {
        [Column(IsPrimaryKey = true, IsDbGenerated = true, DbType = "INT NOT NULL Identity")]
        public int id
        {
            get;
            set;
        }
        [Column]
        public int user_id
        {
            get;
            set;
        }
        [Column]
        public String display_name
        {
            get;
            set;
        }
        [Column]
        public int time_lastfocused
        {
            get;
            set;
        }
        [Column]
        public int time_lastrequest
        {
            get;
            set;
        }
        [Column]
        public bool ignored
        {
            get;
            set;
        }
    }
}
