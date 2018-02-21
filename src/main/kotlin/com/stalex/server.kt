package com.stalex

import com.google.gson.GsonBuilder
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.OutgoingContent
import io.ktor.content.TextContent
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.response.defaultTextContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.text.DateFormat

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080, module = {
        main()
    })
    server.start(wait = true)
}

fun Application.main() {
    
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(TextContent("${it.value} ${it.description}", ContentType.Text.Plain.withCharset(Charsets.UTF_8), it))
        }
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
    
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
//    install(ConditionalHeaders)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    
    routing {
        get("/") {
            call.respondText("Hello, world!!!!!!!!!!", ContentType.Text.Html)
        }
        
        get("/test") {
            call.respond(1)
        }
        
        get("/items/{limit}") {
            val param = call.parameters["limit"]?.toInt() ?: 10
            call.respondJson(MongoStorer.collection.find().limit(param).toList())
        }
    }
}

val gson = GsonBuilder().setDateFormat(DateFormat.LONG).setPrettyPrinting().create()

suspend fun ApplicationCall.respondJson(obj: Any, status: HttpStatusCode? = null, configure: OutgoingContent.() -> Unit = {}) {
    val message = TextContent(gson.toJson(obj), defaultTextContentType(ContentType.Application.Json), status).apply(configure)
    return respond(message)
}

