package info.jotajoti.jid.test

import org.springframework.graphql.test.tester.*

fun <T> GraphQlTester.Path.isEqualTo(value: T) = entity(value!!::class.java).isEqualTo(value)

inline fun <reified T> GraphQlTester.Path.hasSize(size: Int) = entityList(T::class.java).hasSize(size)
