package com.example.antichild.models

open class User (
    var uid: String,
    var username: String,
    var email: String,
    var role: String,
)

class Parent (
    uid: String,
    username: String,
    email: String,
    role: String,
    var accessPassword: String,
) : User(uid, username, email, role)

class Child (
    uid: String,
    username: String,
    email: String,
    role: String,
    var parentEmail: String
) : User (uid, username, email, role)
