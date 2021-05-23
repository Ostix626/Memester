package com.memester

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.activity_add_post.processing
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class AddPostActivity : AppCompatActivity() {
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var postBitmap: Bitmap? = null
    private var storagePostPicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Memes")
        
        save_new_post_btn.setOnClickListener {
            if(postBitmap != null)
            {
                imageUri = postBitmap?.let { it1 -> getImageUriFromBitmap(this, it1) }
            }
            uploadImage()
        }

        image_post.setOnClickListener{
            CropImage.activity().start(this@AddPostActivity)
        }

        processing.setOnClickListener {
            imageUri?.let {
                val processedBitmap = ProcessingBitmap()
//                imageUri = processedBitmap.toURI()
//                imageUri = Uri.fromFile(processedBitmap);
//                postBitmap = processedBitmap?.let { it1 -> getImageUriFromBitmap(this, it1) }
                postBitmap = processedBitmap
                if (processedBitmap != null)
                {
                    image_post.setImageBitmap(processedBitmap)
                    Toast.makeText(applicationContext,"Done", Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(
                        applicationContext,
                        "Something wrong in processing!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
            Toast.makeText(this, "Pick the dankest meme to upload", Toast.LENGTH_SHORT).show()
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

    private fun ProcessingBitmap(): Bitmap?
    {
        var bitmap: Bitmap? = null

        var newBitmap: Bitmap? = null

        try
        {
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))

            var config: Bitmap.Config? = bitmap!!.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }

            newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, config!!)
            val newCanvas = Canvas(newBitmap)

            newCanvas.drawBitmap(bitmap, 0f, 0f, null)

            val captionString = caption.getText().toString() //caption je id od edittext
            if (captionString != null) {

                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.setColor(ContextCompat.getColor(this, R.color.white))
                paintText.setTextSize(bitmap.width.toFloat()/10)
                paintText.setStyle(Paint.Style.FILL)
                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK)


                var rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString!!.length, rectText)

                var div: Int = 10
                while(rectText.width() >= (bitmap.width*0.95)) //smanjuje text ne stane u sliku
                {
                    div += 1
                    paintText.setTextSize(bitmap.width.toFloat()/div)
                    rectText = Rect()
                    paintText.getTextBounds(captionString, 0, captionString!!.length, rectText)
                }

//                if (rectText.width() >= (bitmap.width*0.95))
//                {
////                    TODO: splitat string u vise redova ako izlazi iz slike
////                    https://stackoverflow.com/questions/11100428/add-text-to-image-in-android-programmatically/52356614#52356614
//                    paintText.setTextSize(bitmap.width.toFloat()/div)
//                    rectText = Rect()
//                    paintText.getTextBounds(captionString, 0, captionString!!.length, rectText)
//                }

                newCanvas.drawText(captionString,10f, rectText.height().toFloat(), paintText)

                //Toast.makeText(applicationContext, "drawText: " + captionString!!, Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(applicationContext, "Dank meme text is empty!", Toast.LENGTH_LONG).show()
            }

        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return newBitmap
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }
}