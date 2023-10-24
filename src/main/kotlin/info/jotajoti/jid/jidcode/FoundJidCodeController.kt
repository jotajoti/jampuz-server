package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.Location
import info.jotajoti.jid.participant.Participant
import org.dataloader.DataLoader
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.execution.BatchLoaderRegistry
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture


@Controller
class FoundJidCodeController(
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
                        } += JidCode(associatedCode.code)
                    }

                Mono.just(jidCodesForParticipants)
            }

        batchLoaderRegistry
            .forName<Location, MutableList<JidCode>>("jidCodesForLocationsLoader")
            .withOptions {
                it.setMaxBatchSize(100)
            }
            .registerMappedBatchLoader { locations, _ ->
                val locationLookupMap = locations.associateBy { it.id }
                val jidCodesForLocations = mutableMapOf<Location, MutableList<JidCode>>()

                foundJidCodeRepository
                    .getJidCodesForLocations(locations)
                    .forEach { associatedCode ->
                        val location = locationLookupMap.getValue(associatedCode.id)
                        jidCodesForLocations.getOrPut(location) {
                            mutableListOf()
                        } += JidCode(associatedCode.code)
                    }

                Mono.just(jidCodesForLocations)
            }
    }

    @SchemaMapping
    fun jidCodeStats(
        location: Location,
        jidCodesForEventsLoader: DataLoader<Location, List<JidCode>>
    ) = jidCodesForEventsLoader.loadStats(location)

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
        null -> JidCodeStats(0, 0, emptyList(), emptyList())
        else -> JidCodeStats(
            count = size,
            uniqueCount = toSet().size,
            uniqueCountries = map { it.country }.toSet().sorted(),
            uniqueRegions = map { it.region }.toSet().sorted(),
        )

    }
}
