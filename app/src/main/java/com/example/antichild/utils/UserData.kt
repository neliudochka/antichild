package com.example.antichild.utils

open class UserData(
    val uid: String?,
    val username: String?,
    val email: String?,
)

class ChildData(
    childUid: String?,
    childYUsername: String?,
    childEmail: String?,
    parentUid: String?,
    parentUsername: String?,
    parentEmail: String?,
    accessPassword: String?,
): UserData(childUid, childYUsername, childEmail)

class ParentData(
    parentUid: String?,
    parentUsername: String?,
    parentEmail: String?,
    accessPassword: String?,
): UserData(parentUid, parentUsername, parentEmail)
