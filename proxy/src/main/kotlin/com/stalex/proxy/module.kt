package com.stalex.proxy

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.factory
import com.github.salomonbrys.kodein.provider
import com.stalex.pipeline.PageRefImpl
import nolambda.skrape.Skrape
import nolambda.skrape.processor.jsoup.JsoupDocumentParser

val skrapeModule = Kodein.Module {

    bind<Int>() with factory { i: Int ->
        i
    }

    bind<Skrape<String>>() with provider {
        Skrape(JsoupDocumentParser())
    }

    bind<PageRefImpl>() with factory { fileName: String ->
        PageRefImpl(javaClass.classLoader.getResource(fileName).file)
    }
}

val kodein = Kodein {
    import(skrapeModule)
}
