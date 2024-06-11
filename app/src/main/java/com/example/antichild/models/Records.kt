package com.example.antichild.models

class ChildRecord (
    val uid: String = "",
    val title: String = "",
    val body: String = "",
    val date: String = "",
    val fromUid: String = "",
    var isRead: Boolean = false,
)

class ParentRecord(
    val uid: String = "",
    val date: String = "",
    var isRead: Boolean = false,
)

class Notification(
    val title: String,
    val body: String,
)
