package com.example.antichild.utils

open class UserData(
    val uid: String?,
    val username: String?,
    val email: String?,
    val role: String?,
)

class ChildData(
    childUid: String?,
    childYUsername: String?,
    childEmail: String?,
    val parentUid: String?,
    parentUsername: String?,
    parentEmail: String?,
    childRole: String?,
    val accessPassword: String?,
): UserData(childUid, childYUsername, childEmail, childRole)

class ParentData(
    parentUid: String?,
    parentUsername: String?,
    parentEmail: String?,
    parentRole: String?,
    val accessPassword: String?,
): UserData(parentUid, parentUsername, parentEmail, parentRole) {
}
