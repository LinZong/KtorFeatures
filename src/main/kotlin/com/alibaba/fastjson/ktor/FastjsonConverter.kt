package com.alibaba.fastjson.ktor

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import com.alibaba.fastjson.serializer.SerializerFeature
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import io.ktor.request.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.jvm.jvmErasure

public class FastjsonConverter(private val configuration: Configuration) :
    ContentConverter {

    public class Configuration {
        internal val serializeConfig = SerializeConfig()
        internal val features = linkedSetOf<SerializerFeature>()
        internal val freezeFeatures by lazy { features.toTypedArray() }

        fun config(block: SerializeConfig.() -> Unit) {
            serializeConfig.apply(block)
        }

        fun features(vararg f: SerializerFeature) {
            features += f
        }

        internal fun freeze(): Configuration {
            // touch lazy fields to ensure its creation.
            freezeFeatures
            return this
        }
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val type = request.typeInfo
        val javaType = type.jvmErasure

        return withContext(Dispatchers.IO) {
            val reader = channel
                .toInputStream()
                .reader(context.call.request.contentCharset() ?: Charsets.UTF_8)
            JSON.parseObject(reader.readText(), javaType.javaObjectType)
        }
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any {
        return TextContent(
            JSON.toJSONString(value, configuration.serializeConfig, *configuration.freezeFeatures),
            contentType.withCharset(context.call.suitableCharset())
        )
    }
}


public fun ContentNegotiation.Configuration.fastjson(
    contentType: ContentType = ContentType.Application.Json,
    block: FastjsonConverter.Configuration.() -> Unit = {}
) {
    val configuration = FastjsonConverter.Configuration().apply(block).freeze()
    val converter = FastjsonConverter(configuration)
    register(contentType, converter)
}