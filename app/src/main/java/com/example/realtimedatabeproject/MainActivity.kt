package com.example.realtimedatabeproject

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realtimedatabeproject.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database:DatabaseReference
    private var userList = mutableListOf<User>()
    private var editUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Users")

        binding.saveButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()

            if(name.isEmpty() || email.isEmpty()){
                Toast.makeText(this,"Input all fields!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (editUserId == null){
                val userId = database.push().key!!
                val user = User(userId,name,email)
                database.child(userId).setValue(user)
            } else {
                val updateUser = User(editUserId,name,email)
                database.child(editUserId!!).setValue(updateUser)
                editUserId = null
                binding.saveButton.text = "Save"
                Toast.makeText(this,"Data updated!",Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this@MainActivity, "Data added", Toast.LENGTH_SHORT).show()
            binding.nameInput.text.clear()
            binding.emailInput.text.clear()
            hideKeyboard()

        }

        //Fetch Data

    database.addValueEventListener(object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            userList.clear()
            for(userSnapshot in snapshot.children){
                val user = userSnapshot.getValue(User::class.java)
                user?.let { userList.add(it) }
            }
            binding.userRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.userRecyclerView.adapter = UserAdapter(userList,
                onEditClick = {user ->
                    binding.nameInput.setText(user.name)
                    binding.emailInput.setText(user.email)
                    editUserId = user.id
                    binding.saveButton.text = "Update"
                },
                onDeleteClick = { user ->
                    database.child(user.id!!).removeValue()
                    binding.nameInput.text.clear()
                    binding.emailInput.text.clear()
                    Toast.makeText(this@MainActivity, "data deleted", Toast.LENGTH_SHORT).show()
                })
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
        }

    })

    }
    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // ✅ Close keyboard when tapping outside EditTexts
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}