package com.example.shakeib.friendlychat

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.shakeib.friendlychat.RecylerViewMessagePackage.FriendlyMessage
import com.example.shakeib.friendlychat.RecylerViewMessagePackage.RecViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import com.firebase.ui.auth.AuthUI
import java.util.*
import java.util.Arrays.asList
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ChildEventListener
import android.text.method.TextKeyListener.clear
import android.content.Intent




class MainActivity : AppCompatActivity() {

    //firebase objects
    lateinit var firebaseDb: FirebaseDatabase
    lateinit var dbReference: DatabaseReference
    var username: String = ""
    lateinit var firebaseAuth: FirebaseAuth
    var authStateListener: FirebaseAuth.AuthStateListener? = null
    var childEventListener: ChildEventListener? = null

    val RC_SIGN_IN = 1

    //recyclerView list
    var list = ArrayList<FriendlyMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //firebase objects initilize
        firebaseDb = FirebaseDatabase.getInstance()
        dbReference = firebaseDb.reference.child("messages")
        firebaseAuth = FirebaseAuth.getInstance()


        //rec view adapter attach
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = RecViewAdapter(list)

        //send btn
        sendBtn.setOnClickListener {
            dbReference.push().setValue(FriendlyMessage().apply {
                text = editText.text.toString()
                name = username
                photoUrl = null
            })
            editText.setText("")
        }

        //referencing db


        //initializing authstatelistener
        authStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if (user != null) {
                    onSignedInInitialize(user.displayName!!)
                } else {
                    onSignedOutCleanup()
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                asList(
                                    AuthUI.IdpConfig.GoogleBuilder().build(),
                                    AuthUI.IdpConfig.EmailBuilder().build()
                                )
                            )
                            .build(),
                        RC_SIGN_IN
                    )
                }
            }

        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        attachDatabaseReadListener()
        firebaseAuth.removeAuthStateListener(authStateListener!!)
    }

    override fun onResume() {
        super.onResume()
        if (authStateListener != null) {
            firebaseAuth.addAuthStateListener(authStateListener!!)
        }
        detachDatabaseReadListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sign_out, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.signOut -> {
                AuthUI.getInstance().signOut(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)

        }
    }

    private fun onSignedInInitialize(username: String) {
        this.username = username
        attachDatabaseReadListener()
    }

    private fun onSignedOutCleanup() {
        username = "ANONYMOUS"
        list.clear()
        recView.adapter?.notifyDataSetChanged()
        detachDatabaseReadListener()
    }

    private fun attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    list.add(p0.getValue(FriendlyMessage::class.java)!!)
                    recView.adapter?.notifyDataSetChanged()
                }

                override fun onChildRemoved(p0: DataSnapshot) {}
            }
            dbReference.addChildEventListener(childEventListener!!)
        }
    }

    private fun detachDatabaseReadListener() {
        if (childEventListener != null) {
            dbReference.removeEventListener(childEventListener!!)
            childEventListener = null
        }
    }

}
