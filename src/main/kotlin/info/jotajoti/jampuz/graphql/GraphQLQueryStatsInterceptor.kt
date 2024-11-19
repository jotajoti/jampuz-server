package info.jotajoti.jampuz.graphql

import org.springframework.graphql.server.*
import org.springframework.graphql.server.WebGraphQlInterceptor.*
import org.springframework.stereotype.*
import reactor.core.publisher.*

@Component
class GraphQLQueryStatsInterceptor : WebGraphQlInterceptor {

    override fun intercept(request: WebGraphQlRequest, chain: Chain): Mono<WebGraphQlResponse> {
        val start = System.currentTimeMillis()

        return chain.next(request).map { response ->
            val end = System.currentTimeMillis()
            response.transform {
                it.extensions(
                    mapOf(
                        "executionStats" to ExecutionStats(
                            duration = end - start,
                            cost = response.calculatedCost
                        )
                    )
                )
            }
        }
    }
}

data class ExecutionStats(
    val duration: Long,
    val cost: Int,
)
