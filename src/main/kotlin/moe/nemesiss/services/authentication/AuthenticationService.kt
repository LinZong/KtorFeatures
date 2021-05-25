package moe.nemesiss.services.authentication

import com.google.inject.Inject
import com.google.inject.Singleton
import moe.nemesiss.models.authentication.SimpleAuthenticationInfo
import moe.nemesiss.services.user.UserService

@Singleton
class AuthenticationService @Inject constructor(val userService: UserService) {

    fun authenticate(info: SimpleAuthenticationInfo): Boolean {
        return userService.getUser(info.id).password == info.password
    }
}