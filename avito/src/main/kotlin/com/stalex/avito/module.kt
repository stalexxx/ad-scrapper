package com.stalex.avito

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.factory
import com.github.salomonbrys.kodein.provider
import nolambda.skrape.Skrape
import nolambda.skrape.processor.jsoup.JsoupDocumentParser

val apiModule = Kodein.Module {

    bind<Int>() with factory { i: Int ->
        i
    }

    bind<Skrape<String>>() with provider {
        Skrape(JsoupDocumentParser())
    }
}

val kodein = Kodein {
    import(apiModule)
}
