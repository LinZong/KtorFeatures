package moe.nemesiss

import com.alibaba.fastjson.ktor.fastjson
import com.alibaba.fastjson.serializer.SerializerFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import moe.nemesiss.di.features.DependencyInjection
import moe.nemesiss.plugins.configureRouting

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
                configureRouting()
                install(ContentNegotiation) {
                    fastjson {
                        features(SerializerFeature.WriteMapNullValue)
                        config {
                            isAsmEnable = false
                        }
                    }
                }
                install(DependencyInjection) {
                    // specify the root package for dependency injection scanning.
                    scanBasePackageClasses += Application::class.java
                }
            }.start(wait = true)
        }
    }
}