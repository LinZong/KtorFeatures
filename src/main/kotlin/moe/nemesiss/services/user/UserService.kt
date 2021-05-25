package moe.nemesiss.services.user

import moe.nemesiss.models.user.User

class UserService {

    fun getUser(id: Int) = User(id, "No name", "123456")
}