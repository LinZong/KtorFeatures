package moe.nemesiss.di.extension

import com.google.inject.Injector

inline fun <reified T> Injector.getInstance(): T = getInstance(T::class.java)