package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.*
import info.jotajoti.jid.participant.*
import info.jotajoti.jid.subscription.*
import org.dataloader.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.graphql.execution.*
import org.springframework.stereotype.*
import reactor.core.publisher.*
import java.util.concurrent.*


@Controller
class FoundJidCodeStatsController(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val subscriptionService: SubscriptionService,
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
                        } += associatedCode.code
                    }

                Mono.just(jidCodesForLocations)
            }
    }

    @SubscriptionMapping
    fun jidCodeStats(@Argument locationId: LocationId) =
        subscriptionService
            .subscribe(JidCodeStatsSubscription::class)
            .map {
                foundJidCodeRepository
                    .findFoundJidCodesByParticipantLocationId(it.locationId)
                    .map { it.code }
                    .toStats()
            }

    @SchemaMapping
    fun jidCodeStats(
        location: Location,
        jidCodesForLocationsLoader: DataLoader<Location, List<JidCode>>
    ) = jidCodesForLocationsLoader.loadStats(location)

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
