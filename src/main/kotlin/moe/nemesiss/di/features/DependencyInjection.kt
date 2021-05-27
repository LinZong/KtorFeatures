package moe.nemesiss.di.features

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import io.ktor.application.*
import io.ktor.util.*
import mu.KotlinLogging
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

inline val Application.dependencyGraph: Injector get() = feature(DependencyInjection).injector

class DependencyInjection private constructor(private val configure: Configuration) {

    private val log = KotlinLogging.logger("DependencyInjection")

    val injector: Injector by lazy { Guice.createInjector(dependencyModules()) }

    public class Configuration {
        val modules = arrayListOf<AbstractModule>()
        val scanBasePackages = linkedSetOf<String>()
        val scanBasePackageClasses = linkedSetOf<Class<*>>()

        internal val shouldScanPackages get() = scanBasePackages.isNotEmpty() || scanBasePackageClasses.isNotEmpty()
    }

    public companion object Feature : ApplicationFeature<Application, Configuration, DependencyInjection> {
        override val key: AttributeKey<DependencyInjection> = AttributeKey("DependencyInjection")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): DependencyInjection {
            val configuration = Configuration().apply(configure)
            return DependencyInjection(configuration)
        }
    }

    private fun dependencyModules(): Set<AbstractModule> {
        // modules
        val result = LinkedHashSet<AbstractModule>()
        result += configure.modules
        // fast path
        if (configure.shouldScanPackages) {
            // scanBasePackages + scanBasePackageClasses
            val scannedModules = scanner.getSubTypesOf(AbstractModule::class.java)
            result += scannedModules.map { module -> module.newInstance() }
        }
        if (log.isInfoEnabled) {
            log.info { "Dependency modules found: [${result.joinToString { r -> r.javaClass.canonicalName }}]" }
        }
        return result
    }

    private val scanner by lazy(::preparePackageScanner)

    private fun preparePackageScanner(): Reflections {
        val scannerConfiguration = ConfigurationBuilder().apply {
            val scanPackages = LinkedHashSet(configure.scanBasePackages)
            scanPackages.addAll(configure.scanBasePackageClasses.map { clz -> clz.`package`.name })
            setUrls(scanPackages.map { pkgName -> ClasspathHelper.forPackage(pkgName) }.toSet().flatten())
            setScanners(SubTypesScanner())
            filterInputsBy { input -> !input.contains("kotlin") }
        }
        return Reflections(scannerConfiguration)
    }
}