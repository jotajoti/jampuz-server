package info.jotajoti.jid.build

import java.io.FileReader
import java.io.FileWriter
import java.util.*

// Used to update version.properties during releases
class CopyVersion(
    private val releaseProperty: String
) {

    fun execute() {
        val releaseProperties = Properties()
        releaseProperties.load(FileReader("release.properties"))

        val versionProperties = Properties()
        versionProperties.setProperty("serverVersion", releaseProperties.getProperty(releaseProperty))
        versionProperties.store(FileWriter("src/main/resources/version.properties"), "Auto-generated from release process")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            CopyVersion(args[0]).execute()
        }
    }
}
