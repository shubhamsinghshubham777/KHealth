package app.khealth.sample.cmpkoin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform