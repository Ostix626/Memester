package com.memester

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var db: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        db = FirebaseDatabase.getInstance().reference

        val currentUser = FirebaseAuth.getInstance().currentUser
        val usernameTextView = view.findViewById<TextView>(R.id.username_profile_fragment)
        val followingTextView = view.findViewById<TextView>(R.id.following_profile_fragment)
        val followersTextView = view.findViewById<TextView>(R.id.followers_profile_fragment)
        val profileImage = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profilePic)



        userRef.child(currentUser?.uid.toString()).child("username").get().addOnSuccessListener {
            usernameTextView.text = it.value as CharSequence?
        }
        db.child("Follow").child(currentUser?.uid.toString()).child("Following").get().addOnSuccessListener {
            followingTextView.text = it.getChildrenCount().toString()
        }
        db.child("Follow").child(currentUser?.uid.toString()).child("Followers").get().addOnSuccessListener {
            followersTextView.text = it.childrenCount.toString()
        }
        // TODO: posts count and show profile image

        return view
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