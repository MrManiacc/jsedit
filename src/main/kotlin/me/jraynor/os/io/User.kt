package me.jraynor.os.io

import java.io.Serializable

// User class to represent a user of the filesystem.
data class User(var username: String = "user", var role: Role = Role.USER) : Serializable