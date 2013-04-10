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
    class ChatMessage
    {
        [Column(IsPrimaryKey = true, IsDbGenerated = true, DbType = "INT NOT NULL Identity")]
        public int id
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public int msg_id
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public int chatroom_id
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public String sender_name
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public int sender_id
        {
            get;
            set;
        }
        [Column(CanBeNull = false)]
        public String msg_string
        {
            get;
            set;
        }
    }
}
