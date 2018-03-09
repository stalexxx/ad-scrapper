package com.stalex.proxy

import com.github.salomonbrys.kodein.factory
import com.github.salomonbrys.kodein.instance
import com.stalex.pipeline.PageRefImpl
import com.stalex.proxy.hideMyName.PageParser
import com.stalex.proxy.hideMyName.ProxyInfo
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.experimental.runBlocking

class HideMyNameTest : StringSpec() {
    init {

        val fileFactory: (String) -> PageRefImpl = kodein.factory()
        val ref = fileFactory("hidemy.name")

        val parser = PageParser(kodein.instance())

        val parse: List<ProxyInfo> = runBlocking {
            parser.parse(ref)
        }

        print(parse)
    }
}