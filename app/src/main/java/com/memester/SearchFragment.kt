package com.memester

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.memester.Adapter.UserAdapter
import com.memester.Model.User
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.security.AccessController.getContext


class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.fragment_search, container, false)
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        //show all users on load fragment
        retriveUsers()
        searchUser("")
        recyclerView?.visibility = View.VISIBLE

        view.search_bar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }



            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                if (view.search_bar.text.toString() == "")
                {
                    retriveUsers()
                    searchUser("")
                }
                else
                {
                    recyclerView?.visibility = View.VISIBLE

                    retriveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

     fun searchUser(input: String)
    {
        val query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("usernameLower").startAt(input).endAt(input + "\uf8ff")


        query.addValueEventListener (object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()
                for (snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null)
                    {
                        mUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

     fun retriveUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener (object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (view?.search_bar?.text.toString() == "")
                {
                    mUser?.clear()
                    for (snapshot in dataSnapshot.children)
                    {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null)
                        {
                           mUser?.add(user)
                        }
                    }

                    userAdapter?.notifyDataSetChanged()

                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }


    /*
    val btn = findViewById<Button>(R.id.button)
    btn.setOnClickListener {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    */
}