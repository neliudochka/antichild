package com.example.antichild.models

class ChildRecord (
    val uid: String = "",
    val title: String = "",
    val body: String = "",
    val date: String = "",
    val fromUid: String = "",
    var read: Boolean = false,
)

class ParentRecord(
    val uid: String = "",
    val date: String = "",
    var read: Boolean = false,
)

class Notification(
    val title: String,
    val body: String,
)
