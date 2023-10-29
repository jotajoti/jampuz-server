package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.Location
import info.jotajoti.jid.participant.Participant
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.BatchLoaderRegistry
import reactor.core.publisher.Mono

@Configuration
class FoundJidCodeBatchLoaderConfig(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val batchLoaderRegistry: BatchLoaderRegistry,
) {

    @PostConstruct
    fun registerDataLoaders() {
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
}