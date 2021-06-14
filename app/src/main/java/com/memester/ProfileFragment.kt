package com.memester

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.memester.Adapter.MyMemesAdapter
import com.memester.Model.Post
import com.memester.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    private lateinit var db: DatabaseReference
    var postList: List<Post>? = null
    var myMemesAdapter : MyMemesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        db = FirebaseDatabase.getInstance().reference
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        val currentUser = FirebaseAuth.getInstance().currentUser!!

        val usernameTextView = view.findViewById<TextView>(R.id.username_profile_fragment)
        val followingTextView = view.findViewById<TextView>(R.id.following_profile_fragment)
        val followersTextView = view.findViewById<TextView>(R.id.followers_profile_fragment)
        val postsTextView = view.findViewById<TextView>(R.id.posts_profile_fragment)
        val profileImage = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePic)



//        usersRef.child(currentUser?.uid.toString()).child("username").get().addOnSuccessListener {
//            usernameTextView.text = it.value as CharSequence?
//        }
//        db.child("Follow").child(currentUser?.uid.toString()).child("Following").get().addOnSuccessListener {
//            followingTextView.text = it.getChildrenCount().toString()
//        }
//        db.child("Follow").child(currentUser?.uid.toString()).child("Followers").get().addOnSuccessListener {
//            followersTextView.text = it.childrenCount.toString()
//        }

        usersRef.child(currentUser?.uid.toString()).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null) {return}
                if (snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    usernameTextView.text  = user!!.getUsername()
                }
            }
        })


        // TODO: posts count

        db.child("Posts").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    var count = 0
                    for (meme in snapshot.children)
                    {
                        val post = meme.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(FirebaseAuth.getInstance().currentUser!!.uid))
                        {
                            count += 1
                        }
                    }
                    postsTextView.text = count.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })



        db.child("Follow").child(currentUser.uid.toString()).child("Following").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) followingTextView.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        db.child("Follow").child(currentUser.uid.toString()).child("Followers").addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) followersTextView.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        view.followers_layout.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", currentUser.uid)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }

        view.following_layout.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", currentUser.uid)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        var recyclerViewUploadedImages : RecyclerView
        recyclerViewUploadedImages = view.findViewById(R.id.recycler_view_uploaded_pics)
        recyclerViewUploadedImages.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadedImages.layoutManager = linearLayoutManager

        postList = ArrayList()
        myMemesAdapter = context?.let { MyMemesAdapter(it, postList as ArrayList<Post>) }
        recyclerViewUploadedImages.adapter = myMemesAdapter

        myMemes()

        return view
    }



    private fun myMemes()
    {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    (postList as ArrayList<Post>).clear()
                    for (meme in snapshot.children)
                    {
                        val post = meme.getValue(Post::class.java)!!
                        if (post.getPublisher().equals(FirebaseAuth.getInstance().currentUser!!.uid))
                        {
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        myMemesAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetFragment = BottomSheetFragment()
        val profilePicBtn = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePic)

        profilePicBtn.setOnClickListener {
            bottomSheetFragment.show(childFragmentManager, "BottomSheetDialog")
        }



    }
    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    */
}