package info.jotajoti.jampuz.jidcode

import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.participant.*
import org.dataloader.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.graphql.execution.*
import org.springframework.stereotype.*
import reactor.core.publisher.*
import java.util.concurrent.*

@Controller
class FoundJidCodeStatsController(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    batchLoaderRegistry: BatchLoaderRegistry,
) {

    init {
        batchLoaderRegistry
            .forName<Participant, MutableList<JidCode>>("jidCodesForParticipantsLoader")
            .withOptions {
                it.setMaxBatchSize(100)
            }
            .registerMappedBatchLoader { participants, _ ->
                val participantLookupMap = participants.associateBy { it.id }
                val jidCodesForParticipants = mutableMapOf<Participant, MutableList<JidCode>>()

                foundJidCodeRepository
                    .getJidCodesForParticipants(participants)
                    .forEach { associatedCode ->
                        val participant = participantLookupMap.getValue(associatedCode.id)
                        jidCodesForParticipants.getOrPut(participant) {
                            mutableListOf()
                        } += associatedCode.code
                    }

                Mono.just(jidCodesForParticipants)
            }

        batchLoaderRegistry
            .forName<Event, MutableList<JidCode>>("jidCodesForEventsLoader")
            .withOptions {
                it.setMaxBatchSize(100)
            }
            .registerMappedBatchLoader { events, _ ->
                val eventLookupMap = events.associateBy { it.id }
                val jidCodesForEvents = mutableMapOf<Event, MutableList<JidCode>>()

                foundJidCodeRepository
                    .getJidCodesForEvents(events)
                    .forEach { associatedCode ->
                        val event = eventLookupMap.getValue(associatedCode.id)
                        jidCodesForEvents.getOrPut(event) {
                            mutableListOf()
                        } += associatedCode.code
                    }

                Mono.just(jidCodesForEvents)
            }
    }

    @SchemaMapping
    fun jidCodeStats(
        event: Event,
        jidCodesForEventsLoader: DataLoader<Event, List<JidCode>>
    ) = jidCodesForEventsLoader.loadStats(event)

    @SchemaMapping
    fun jidCodeStats(
        participant: Participant,
        jidCodesForParticipantsLoader: DataLoader<Participant, List<JidCode>>
    ) = jidCodesForParticipantsLoader.loadStats(participant)

    private fun <T> DataLoader<T, List<JidCode>>.loadStats(target: T): CompletableFuture<JidCodeStats> =
        load(target)
            .thenApply {
                it.toStats()
            }

    private fun List<JidCode>?.toStats() = when (this) {
        null -> JidCodeStats(0, 0, emptyList(), emptyList(), emptyList())
        else -> JidCodeStats(
            count = size,
            uniqueCount = toSet().size,
            uniqueCountries = map { it.country }.toSet().sorted(),
            countryStats = toCountryStats(),
            uniqueRegions = map { it.region }.toSet().sorted(),
        )
    }

    private fun List<JidCode>.toCountryStats() =
        groupBy { it.country }
            .map {
                CountryStat(
                    country = it.key,
                    uniqueCount = it.value.toSet().size,
                )
            }
}
