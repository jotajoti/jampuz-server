package info.jotajoti.jid.subscription

import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class SubscriptionService {

    private val sinks = mutableMapOf<String, Sinks.Many<String>>()

    fun publishMessage(key: String, message: String) {
        key.getSink().tryEmitNext(message)
    }

    fun subscribe(key: String) =
        key.getSink().asFlux()

    private fun String.getSink() = sinks.getOrPut(this) {
        Sinks
            .many()
            .multicast()
            .onBackpressureBuffer(20)
    }

}
