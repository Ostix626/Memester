package com.memester

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Memes")
        
        save_new_post_btn.setOnClickListener {
            uploadImage()
        }

        image_post.setOnClickListener{
            CropImage.activity().start(this@AddPostActivity)
        }

        CropImage.activity().start(this@AddPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
        else
        {
            Toast.makeText(this, "Only memes allowed!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage()
    {
        if (imageUri == null) Toast.makeText(this, "You didn't select meme to upload", Toast.LENGTH_SHORT).show()
        else
        {
            val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            val progressDialog = ProgressDialog(this,  R.style.MyAlertDialogStyle)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Stealing your personal files...")
            progressDialog.show()

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        progressDialog.dismiss()
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    myUrl = downloadUrl.toString()

                    val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                    val postId = ref.push().key

                    val postMap = HashMap<String, Any>()
                    postMap["postid"] = postId!!
                    postMap["description"] = description_post.text.toString()
                    postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["postimage"] = myUrl

                    ref.child(postId).updateChildren(postMap)

                    Toast.makeText(this, "Your dank fresh meme has been posted", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, NavActivity::class.java)
                    startActivity(intent)
                    finish()
                    progressDialog.dismiss()
                }
                else
                {
                    progressDialog.dismiss()
                }
            } )
        }
    }
}