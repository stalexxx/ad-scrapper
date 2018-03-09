package com.stalex.server

import com.stalex.avito.AvitoScrap
import com.stalex.pipeline.AdLogger
import mu.KotlinLogging

class ConsoleAdLogger : AdLogger<AvitoScrap> {
    private val logger = KotlinLogging.logger {}

    override suspend fun handle(e: AvitoScrap) {
        logger.info {
            e
        }
    }
}