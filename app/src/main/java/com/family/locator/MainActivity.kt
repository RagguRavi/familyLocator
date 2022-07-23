package com.family.locator

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.family.locator.databinding.ActivityMainBinding
import com.family.locator.utitlity.FireBaseDBHelper
import com.family.locator.utitlity.PermissionUtitlity
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val duration = Toast.LENGTH_SHORT
    private var button: Button? =null
    private var testButton:Button? = null
    private var dbHelper:DBHelper?=null
    private var recyclerView:RecyclerView?=null
    private var list:MutableList<String> = ArrayList();
    private var adapter:ListAdaptar? = null;

    private var deleteItemButton:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val permissionUtitlity = PermissionUtitlity(applicationContext,this)
        permissionUtitlity.checkAllPermissions()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        button = findViewById(R.id.addNumberButton)
        button?.setOnClickListener(this)

        testButton = findViewById(R.id.testButton)
        testButton?.setOnClickListener(this)


        dbHelper = DBHelper(this)

        var mapData:Map<Long,String> = dbHelper?.data as Map<Long, String>


        for (entry in mapData) {
            Log.d(entry.key.toString(),entry.value)
            list.add(entry.value)
        }

        recyclerView= findViewById(R.id.addedPhoneNumbers)
        recyclerView?.setHasFixedSize(false)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = ListAdaptar(list,dbHelper,recyclerView,this)
        recyclerView?.refreshDrawableState()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onClick(view: View) {
        var id = view?.id
        when(id) {
            R.id.addNumberButton -> {
                var phoneNumber:TextView = findViewById(R.id.phoneNumberText)
                var phoneText = phoneNumber.text.toString();

                if(TextUtils.isEmpty(phoneText)) {

                    Snackbar.make(view, "Phone Number Can't be empty", Snackbar.LENGTH_SHORT).show();
                    phoneNumber.error = "Please enter phone Number"
                } else {
                    val pattern: Pattern = Pattern.compile("^(\\+\\d{1,3})?\\d{10}$")
                    val matcher: Matcher = pattern.matcher(phoneText)

                    if(matcher.matches()) {
                        val isExist = list.stream().anyMatch { num -> num.contains(phoneText) }
                        if(!isExist) {
                            dbHelper?.addPhoneNumber(phoneText)
                            list?.add(phoneText)

                            adapter?.notifyItemInserted(list.indexOf(phoneText))

                            Snackbar.make(view, "Phone Number Added SuccessFully", Snackbar.LENGTH_SHORT).show();
                            phoneNumber.text = ""

                            val imm: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                        } else {
                            Snackbar.make(view, "Phone Number already added", Snackbar.LENGTH_SHORT).show();
                            phoneNumber.error = "Phone Number already added"
                        }

                    } else {
                        Snackbar.make(view, "Please Enter valid Phone Number", Snackbar.LENGTH_SHORT).show();
                        phoneNumber.error = "Please enter valid phone Number"
                    }
                }


            }

            R.id.testButton -> {
               var intent:Intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(applicationContext,"Doing noting",duration).show()
            }
        }
    }
}