import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Model.Notification
import com.memester.Model.Post
import com.memester.Model.User
import com.memester.PostDetailsActivity
import com.memester.ProfileActivity
import com.memester.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*

class NotificationAdapter(
    private val mContext: Context,
    private val mNotification: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notifications_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int)
    {
        val notification = mNotification[position]


        if (notification.getText().equals("Started following you"))
        {
            holder.text.text = "Started following you"
        }
        else if (notification.getText().equals("Liked your post"))
        {
            holder.text.text = "Liked your post"
        }
        else if (notification.getText().contains("Commented: "))
        {
            holder.text.text = notification.getText().replace("Commented: ", "Commented: ")
        }
        else
        {
            holder.text.text = notification.getText()
        }

        userInfo(holder.profileImage, holder.userName, notification.getUserId())

        if (notification.getIspost())
        {
            holder.postImage.visibility = View.VISIBLE
            getPostImage(holder.postImage, notification.getPostId())
        }
        else
        {
            holder.postImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (notification.getIspost())
            {
                val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId", notification.getPostId())
                editor.apply()
                val intent = Intent(mContext, PostDetailsActivity::class.java)
                mContext.startActivity(intent);
            }
            else
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", notification.getUserId())
                pref.apply()

                val prefs = notification.getUserId()

                val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
                intent.putExtra("uid", prefs)
                mContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val postImage: ImageView
        var profileImage : de.hdodenhof.circleimageview.CircleImageView
        var userName : TextView
        var text: TextView

        init {
            postImage = itemView.findViewById(R.id.notificaiotn_post_image)
            profileImage = itemView.findViewById(R.id.notificaiton_profile_image)
            userName = itemView.findViewById(R.id.username_notification)
            text = itemView.findViewById(R.id.comment_notificaiton)
        }
    }

    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String)
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(imageView)
                    userName.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getPostImage(imageView: ImageView, postID: String)
    {
        val postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID)

        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    val post = snapshot.getValue<Post>(Post::class.java)
                    Picasso.get().load(post?.getPostimage()).placeholder(R.drawable.profile).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}










