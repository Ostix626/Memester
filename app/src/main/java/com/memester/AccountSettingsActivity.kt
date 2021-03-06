package com.memester

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.memester.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingsActivity : AppCompatActivity()
{
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")


        change_image_text_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }

        save_infor_profile_btn.setOnClickListener {
            onBackPressed()
            //zamijeni fragmente:
//            var fragTrans : FragmentTransaction = supportFragmentManager.beginTransaction()
//            fragTrans.replace(R.id.accSettings, ProfileFragment()).commit()

//            if (checker == "clicked") { uploadImageAndUpdateInfo() }
//            else { updateUserInfoOnly() }
        }

        upload_profile_changes_btn.setOnClickListener {
            if (checker == "clicked") { uploadImageAndUpdateInfo() }
            else { updateUserInfoOnly() }
        }

        delete_account_btn.setOnClickListener {
            Toast.makeText(this, "Sorry, we can't let you do that", Toast.LENGTH_SHORT).show()
        }


        userInfo()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly()
    {
        when {
            //TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
            username_profile_frag.text.toString() == "" -> Toast.makeText(this, "Write your memelord name!", Toast.LENGTH_LONG).show()
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["username"] = username_profile_frag.text.toString()
                userMap["usernameLower"] = username_profile_frag.text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingsActivity, NavActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_view_profile_frag)
                    username_profile_frag.setText(user!!.getUsername())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun uploadImageAndUpdateInfo()
    {
        when
        {
            imageUri == null -> {
                Toast.makeText(this, "You didn't select profile image to upload", Toast.LENGTH_SHORT).show()
                updateUserInfoOnly()
            }
            //TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> Toast.makeText(this, "Please write full name first.", Toast.LENGTH_LONG).show()
            username_profile_frag.text.toString() == "" -> Toast.makeText(this, "Write your memelord name!", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this,  R.style.MyAlertDialogStyle)
                progressDialog.setTitle("Please wait")
                progressDialog.setMessage("Getting your credit card info...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

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
                }).addOnCompleteListener (OnCompleteListener<Uri> {task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["username"] = username_profile_frag.text.toString()
                        userMap["image"] = myUrl
                        userMap["usernameLower"] = username_profile_frag.text.toString().toLowerCase()

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AccountSettingsActivity, NavActivity::class.java)
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

}
