package com.memester.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.memester.Model.Post
import com.memester.R
import com.squareup.picasso.Picasso

class MyMemesAdapter(private val mContext: Context, mPost: List<Post>) : RecyclerView.Adapter<MyMemesAdapter.ViewHolder?>()
{
    private var mPost: List<Post>? = null

    init {
        this.mPost = mPost
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postImage: ImageView

        init
        {
            postImage = itemView.findViewById(R.id.post_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.my_posts_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post:Post = mPost!![position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }
}