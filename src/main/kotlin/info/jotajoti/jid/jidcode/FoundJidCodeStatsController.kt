package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.Location
import info.jotajoti.jid.location.LocationId
import info.jotajoti.jid.participant.Participant
import info.jotajoti.jid.subscription.SubscriptionService
import org.dataloader.DataLoader
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import java.util.concurrent.CompletableFuture


@Controller
class FoundJidCodeStatsController(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val subscriptionService: SubscriptionService,
) {

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
        null -> JidCodeStats(0, 0, emptyList(), emptyList())
        else -> JidCodeStats(
            count = size,
            uniqueCount = toSet().size,
            uniqueCountries = map { it.country }.toSet().sorted(),
            uniqueRegions = map { it.region }.toSet().sorted(),
        )
    }
}
