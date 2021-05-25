package moe.nemesiss.di.modules.user

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import moe.nemesiss.services.user.UserService

class UserModule: AbstractModule() {

    @Provides
    @Singleton
    fun provideUserService() = UserService()
}