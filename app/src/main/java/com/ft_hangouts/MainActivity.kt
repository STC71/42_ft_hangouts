package com.ft_hangouts

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.ft_hangouts.data.repository.ContactRepository
import com.ft_hangouts.domain.model.Contact

class MainActivity : Activity() {

    private lateinit var repository: ContactRepository
    private lateinit var contactList: LinearLayout
    private lateinit var contactCount: TextView
    private lateinit var emptyMessage: TextView

    private val blue = 0xFF24364B.toInt()
    private val screenBackground = 0xFFF4F6F8.toInt()
    private val white = 0xFFFFFFFF.toInt()
    private val gray = 0xFF667085.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ContactRepository(this)
        createScreen()
    }

    override fun onResume() {
        super.onResume()
        refreshContacts()
    }

    override fun onDestroy() {
        repository.close()
        super.onDestroy()
    }

    private fun createScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(screenBackground)
        }

        val header = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 24f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            setBackgroundColor(blue)
        }

        contactCount = TextView(this).apply {
            textSize = 16f
            setTextColor(blue)
            setPadding(dp(20), dp(20), dp(20), dp(12))
        }

        emptyMessage = TextView(this).apply {
            text = getString(R.string.empty_contacts)
            textSize = 16f
            setTextColor(gray)
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
        }

        contactList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(16))
        }

        val scroll = ScrollView(this).apply {
            addView(contactList)
        }

        val addButton = Button(this).apply {
            text = getString(R.string.add_contact)
            setOnClickListener { showContactForm() }
        }

        root.addView(header)
        root.addView(contactCount)
        root.addView(
            emptyMessage,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(16), dp(8), dp(16), dp(12))
            }
        )

        setContentView(root)
    }

    private fun refreshContacts() {
        val contacts = repository.getAll()

        contactCount.text = getString(
            R.string.contact_count,
            contacts.size
        )

        contactList.removeAllViews()
        emptyMessage.visibility =
            if (contacts.isEmpty()) View.VISIBLE else View.GONE

        for (contact in contacts) {
            contactList.addView(createContactRow(contact))
        }
    }

    private fun createContactRow(contact: Contact): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(white)
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }

        val name = TextView(this).apply {
            text = "${contact.firstName} ${contact.lastName}".trim()
            textSize = 18f
            setTextColor(blue)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val phone = TextView(this).apply {
            text = contact.phone
            textSize = 15f
            setTextColor(gray)
            setPadding(0, dp(5), 0, 0)
        }

        row.addView(name)
        row.addView(phone)

        if (contact.email.isNotBlank()) {
            val email = TextView(this).apply {
                text = contact.email
                textSize = 14f
                setTextColor(gray)
                setPadding(0, dp(3), 0, 0)
            }
            row.addView(email)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, dp(10))
        }

        row.layoutParams = params
        return row
    }

    private fun showContactForm() {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(8), dp(24), dp(8))
        }

        val firstName = createInput(
            getString(R.string.first_name)
        )
        val lastName = createInput(
            getString(R.string.last_name)
        )
        val phone = createInput(
            getString(R.string.phone),
            InputType.TYPE_CLASS_PHONE
        )
        val email = createInput(
            getString(R.string.email),
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        val address = createInput(
            getString(R.string.address)
        )
        val notes = createInput(
            getString(R.string.notes)
        )

        form.addView(firstName)
        form.addView(lastName)
        form.addView(phone)
        form.addView(email)
        form.addView(address)
        form.addView(notes)

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.new_contact)
            .setView(form)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val nameValue = firstName.text.toString().trim()
                    val phoneValue = phone.text.toString().trim()

                    if (nameValue.isEmpty() || phoneValue.isEmpty()) {
                        Toast.makeText(
                            this,
                            R.string.required_fields,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    try {
                        repository.insert(
                            Contact(
                                firstName = nameValue,
                                lastName = lastName.text.toString().trim(),
                                phone = phoneValue,
                                email = email.text.toString().trim(),
                                address = address.text.toString().trim(),
                                notes = notes.text.toString().trim()
                            )
                        )
                        refreshContacts()
                        dialog.dismiss()
                        Toast.makeText(
                            this,
                            R.string.contact_saved,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (exception: Exception) {
                        Toast.makeText(
                            this,
                            R.string.save_error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        dialog.show()
    }

    private fun createInput(
        hint: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT
    ): EditText {
        return EditText(this).apply {
            this.hint = hint
            this.inputType = inputType
            setSingleLine(true)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

