package moe.nemesiss.plugins

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import moe.nemesiss.di.extension.getInstance
import moe.nemesiss.di.feature.dependencyGraph
import moe.nemesiss.models.authentication.SimpleAuthenticationInfo
import moe.nemesiss.services.authentication.AuthenticationService
import moe.nemesiss.services.user.UserService
import sun.java2d.pipe.SpanShapeRenderer

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/user") {
            val userId = call.request.queryParameters["id"]?.toInt() ?: 0
            val userService = dependencyGraph().getInstance<UserService>()
            call.respond(userService.getUser(userId))
        }
        post("/authenticate") {
            val info = call.receive<SimpleAuthenticationInfo>()
            val authService = dependencyGraph().getInstance<AuthenticationService>()
            if (authService.authenticate(info)) {
                call.respondText { "Hello, world" }
            } else {
                call.respondText(status = HttpStatusCode.Unauthorized) { "Unauthenticated" }
            }
        }
    }
}
