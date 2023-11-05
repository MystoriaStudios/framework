package net.revive.framework.maven

import com.google.common.base.Supplier
import com.google.common.base.Suppliers
import net.revive.framework.Framework
import net.revive.framework.flavor.annotation.Inject
import net.revive.framework.flavor.service.Service
import net.revive.framework.server.IMinecraftPlatform
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.*


/**
 * Resolves [MavenLibrary] annotations for a class, and loads the dependency
 * into the classloader.
 */
@Service
object MavenLibraryLoader {
    private val URL_INJECTOR: Supplier<URLClassLoaderAccess> = Suppliers.memoize {
        URLClassLoaderAccess.create(
            IMinecraftPlatform::class.java.getClassLoader() as URLClassLoader
        )
    }

    @Inject
    lateinit var minecraftPlatform: IMinecraftPlatform

    /**
     * Resolves all [MavenLibrary] annotations on the given object.
     *
     * @param object the object to load libraries for.
     */
    fun loadAll(`object`: Any) {
        loadAll(`object`.javaClass)
    }

    /**
     * Resolves all [MavenLibrary] annotations on the given class.
     *
     * @param clazz the class to load libraries for.
     */
    fun loadAll(clazz: Class<*>) {
        val libs: Array<MavenLibrary> = clazz.getDeclaredAnnotationsByType(MavenLibrary::class.java) ?: return
        for (lib in libs) {
            load(lib.groupId, lib.artifactId, lib.version, lib.repo.url)
        }
    }

    @JvmOverloads
    fun load(groupId: String, artifactId: String, version: String, repoUrl: String = "https://repo1.maven.org/maven2") {
        load(Dependency(groupId, artifactId, version, repoUrl))
    }

    fun load(d: Dependency) {
        val name = d.artifactId + "-" + d.version
        val saveLocation = File(libFolder, "$name.jar")
        if (!saveLocation.exists()) {
            try {
                val url = d.url
                url.openStream().use { `is` -> Files.copy(`is`, saveLocation.toPath()) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!saveLocation.exists()) {
            throw RuntimeException("Unable to download dependency: $d")
        }
        try {
            URL_INJECTOR.get().addURL(saveLocation.toURI().toURL())
        } catch (e: Exception) {
            throw RuntimeException("Unable to load dependency: $saveLocation", e)
        }
    }

    private val libFolder: File
        private get() {
            val pluginDataFolder: File = minecraftPlatform.getDataFolder()
            val pluginsDir = pluginDataFolder.parentFile
            val helperDir = File(pluginsDir, "Framework")
            val libs = File(helperDir, "libraries")
            libs.mkdirs()
            return libs
        }

    class Dependency(groupId: String, artifactId: String, version: String, repoUrl: String) {
        val groupId: String
        val artifactId: String
        val version: String
        val repoUrl: String

        init {
            this.groupId = Objects.requireNonNull(groupId, "groupId")
            this.artifactId = Objects.requireNonNull(artifactId, "artifactId")
            this.version = Objects.requireNonNull(version, "version")
            this.repoUrl = Objects.requireNonNull(repoUrl, "repoUrl")
        }

        @get:Throws(MalformedURLException::class)
        val url: URL
            get() {
                var repo = repoUrl
                if (!repo.endsWith("/")) {
                    repo += "/"
                }
                repo += "%s/%s/%s/%s-%s.jar"
                val url = String.format(
                    repo, groupId.replace(".", "/"),
                    artifactId, version, artifactId, version
                )
                return URL(url)
            }

        override fun equals(o: Any?): Boolean {
            if (o === this) return true
            if (o !is Dependency) return false
            val other = o
            return groupId == other.groupId && artifactId == other.artifactId && version == other.version && repoUrl == other.repoUrl
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            result = result * PRIME + groupId.hashCode()
            result = result * PRIME + artifactId.hashCode()
            result = result * PRIME + version.hashCode()
            result = result * PRIME + repoUrl.hashCode()
            return result
        }

        override fun toString(): String {
            return "LibraryLoader.Dependency(" +
                    "groupId=" + groupId + ", " +
                    "artifactId=" + artifactId + ", " +
                    "version=" + version + ", " +
                    "repoUrl=" + repoUrl + ")"
        }
    }
}

