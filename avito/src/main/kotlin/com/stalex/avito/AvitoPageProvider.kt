package com.stalex.avito

import com.stalex.pipeline.PageRefImpl
import com.stalex.pipeline.RefPageProvider

class AvitoPageProvider() : RefPageProvider<PageRefImpl> {

    val prefix = "https://www.avito.ru/sankt-peterburg/kvartiry/sdam/na_dlitelnyy_srok"
//    val prefix = "https://www.avito.ru/sankt-peterburg/nedvizhimost"

    override fun get(index: Int): PageRefImpl {
        val pfx: String = index.takeIf { it > 0 }?.let { "?p=$it" } ?: ""
        return PageRefImpl("$prefix$pfx")
    }
}
