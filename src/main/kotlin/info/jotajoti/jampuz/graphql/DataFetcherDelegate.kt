package info.jotajoti.jampuz.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

abstract class DataFetcherDelegate<T>(val dataFetcher: DataFetcher<T>) : DataFetcher<T> {

    override fun get(environment: DataFetchingEnvironment): T {
        return dataFetcher.get(environment)
    }
}