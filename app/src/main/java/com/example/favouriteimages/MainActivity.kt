package com.example.favouriteimages

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val artNameList = ArrayList<String>()
        val artImageList = ArrayList<Bitmap>()

        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,artNameList)
        listView.adapter = arrayAdapter

        try {

            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM arts",null)
            val artNameIx = cursor.getColumnIndex("artname")
            val imageIx = cursor.getColumnIndex("image")

            cursor.moveToFirst()

            while (cursor!=null) {
                artNameList.add(cursor.getString(artNameIx))
                val byteArray = cursor.getBlob(imageIx)
                val image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                artImageList.add(image)
                cursor.moveToNext()
                arrayAdapter.notifyDataSetChanged()
            }


            cursor?.close()


        } catch (e: Exception) {
            e.printStackTrace()
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,Main2Activity::class.java)
            intent.putExtra("name",artNameList[position])
            intent.putExtra("info","old")

            val chosen = Globals.Chosen
            chosen.chosenImage = artImageList[position]

            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //Inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_art,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_art_item) {
            val intent = Intent(this,Main2Activity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

}
