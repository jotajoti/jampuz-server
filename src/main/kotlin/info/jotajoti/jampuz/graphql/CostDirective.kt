package info.jotajoti.jampuz.graphql

import graphql.schema.*
import graphql.schema.idl.*
import graphql.schema.idl.RuntimeWiring.*
import org.springframework.graphql.server.*

fun Builder.registerCostDirective(): Builder =
    directive("Cost", CostDirective)

val WebGraphQlResponse.calculatedCost: Int get() = executionInput.graphQLContext.getOrDefault(COST_KEY, 0)

private object CostDirective : SchemaDirectiveWiring {
    override fun onField(environment: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition =
        environment.setFieldDataFetcher(
            CostDataFetcher(
                environment.appliedDirective,
                environment.fieldDataFetcher
            )
        )
}

private class CostDataFetcher<T>(val appliedDirective: GraphQLAppliedDirective, dataFetcher: DataFetcher<T>) :
    DataFetcherDelegate<T>(dataFetcher) {

    override fun get(environment: DataFetchingEnvironment): T {
        val cost = appliedDirective.getArgument("value").getValue<Int>()
        environment.graphQlContext.compute<Int>(COST_KEY) { t, u ->
            u?.let { u + cost } ?: cost
        }
        return super.get(environment)
    }
}

private const val COST_KEY = "cost"
