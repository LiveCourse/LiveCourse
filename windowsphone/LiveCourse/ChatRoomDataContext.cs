using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.Linq;
using System.Data.Linq.Mapping;

namespace LiveCourse
{
    class ChatRoomDataContext: DataContext
    {
        public ChatRoomDataContext(string connectionString)
            : base(connectionString)
        {
        }

        public Table<MyChatRoom> ChatRooms
        {
            get
            {
                return this.GetTable<MyChatRoom>();
            }   
        }
    }
}
