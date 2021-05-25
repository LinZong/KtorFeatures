package moe.nemesiss

import com.alibaba.fastjson.ktor.fastjson
import com.alibaba.fastjson.serializer.SerializerFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import moe.nemesiss.di.feature.DependencyInjection
import moe.nemesiss.di.modules.user.UserModule
import moe.nemesiss.plugins.configureRouting

fun main() {
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
            modules += arrayOf(UserModule())
        }
    }.start(wait = true)
}
