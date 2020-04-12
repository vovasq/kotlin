// PROBLEM: none

internal class ServiceInfo(val port: Int, val host: String, val id: String)

internal class SystemConfig {
    val firstServiceInfo: ServiceInfo
    val secondServiceInfo: ServiceInfo

    init {
        secondServiceInfo = registerSecondService()
        //        uuups
        saveSystemConfig(this)
        firstServiceInfo = registerFirstService()
    }

    fun saveSystemConfig(config: SystemConfig) {
        val first = config.firstServiceInfo
        // some stuff with database NPE should occur...
        println(
            "Service with: id=" + first.id +
                    " host=" + first.host + " port=" + first.port + " WAS SAVED SUCCESSFULLY!"
        )
    }

    private fun registerFirstService(): ServiceInfo {
        return ServiceInfo(7777, "kek.com", "vfhhsadgfbvdlfkjbdfvsl")
    }

    private fun registerSecondService(): ServiceInfo {
        return ServiceInfo(8888, "lol.com", "vfhhsadgfbvdlfkjbdfvsl")
    }

    companion object {
        fun main(args: Array<String>) {
            val config = SystemConfig()
            //        some code with config ....
        }
    }
}