package info.jotajoti.jid.subscription

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import kotlin.reflect.KClass

@Service
class SubscriptionService {

    private val sinks = mutableMapOf<KClass<*>, SinkFlux<*>>()

    fun <T : Any> publishMessage(message: T) {
        message.kClass().getSinkFlux().sink.tryEmitNext(message)
    }

    private fun <T : Any> T.kClass(): KClass<T> {
        return javaClass.kotlin
    }

    fun <T : Any> subscribe(key: KClass<T>) =
        key.getSinkFlux().flux

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> KClass<T>.getSinkFlux() = sinks.getOrPut(this) {
        Sinks
            .many()
            .multicast()
            .onBackpressureBuffer<T>(20, false)
            .toSinkFlux()
    } as SinkFlux<T>

    private fun <T> Sinks.Many<T>.toSinkFlux() = SinkFlux(this)

}

private data class SinkFlux<T>(
    val sink: Sinks.Many<T>,
    val flux: Flux<T> = sink.asFlux()
)
