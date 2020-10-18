package com.techyourchance.testdrivendevelopment.exercise8.networking

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact

class ContactSchema(val id: String, val fullName: String, val fullPhoneNumber: String, val imageUrl: String, val age: Double)

fun ContactSchema.toContact() = Contact(id, fullName, imageUrl)