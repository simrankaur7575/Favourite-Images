package com.example.favouriteimages

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.ByteArrayOutputStream

class Main2Activity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")) {
            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.selectimage)
            imageView.setImageBitmap(selectedImageBackground)
            button.visibility = View.VISIBLE
            editText.setText("")
        }
        else{
            val name = intent.getStringExtra("name")
            editText.setText(name)
            val chosen = Globals.Chosen
            val bitmap = chosen.returnImage()
            imageView.setImageBitmap(bitmap)
            button.visibility = View.INVISIBLE
        }

    }

    fun select(view: View) {


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery,2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery,2)
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ( requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

             selectedPicture = data.data

            try {

                if (selectedPicture != null) {

                    if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                        selectedBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(selectedBitmap)
                    } else {
                        selectedBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        imageView.setImageBitmap(selectedBitmap)
                    }
                }
            } catch (e: Exception) {

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }

    fun save(view: View)
    {
        val artName = editText.text.toString()
        val outputStream = ByteArrayOutputStream()
        selectedBitmap?.compress(Bitmap.CompressFormat.PNG,50,outputStream)
        val byteArray = outputStream.toByteArray()

        try {

            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (artname VARCHAR, image BLOB)")

            val sqlString =
                "INSERT INTO arts (artname,image) VALUES (?, ?)"
            val statement = database.compileStatement(sqlString)
            statement.bindString(1, artName)
            statement.bindBlob(2, byteArray)

            statement.execute()

        } catch (e: Exception) {
            e.printStackTrace()
        }


        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        startActivity(intent)

    }

    }


