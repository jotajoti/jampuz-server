package info.jotajoti.jid.test

import kotlin.random.*

fun Random.Default.nextGaussian(mean: Double = 0.0, stdDev: Double = 1.0) =
    asJavaRandom().nextGaussian(mean, stdDev)
