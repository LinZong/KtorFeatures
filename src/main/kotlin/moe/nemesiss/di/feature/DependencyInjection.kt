package moe.nemesiss.di.feature

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import io.ktor.application.*
import io.ktor.util.*

fun Application.dependencyGraph() = feature(DependencyInjection).injector

class DependencyInjection private constructor(configure: Configuration) {

    val injector: Injector = Guice.createInjector(configure.modules)

    public class Configuration {
        val modules = arrayListOf<AbstractModule>()
    }

    public companion object Feature : ApplicationFeature<Application, Configuration, DependencyInjection> {
        override val key: AttributeKey<DependencyInjection> = AttributeKey("DependencyInjection")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): DependencyInjection {
            val configuration = Configuration().apply(configure)
            return DependencyInjection(configuration)
        }
    }
}