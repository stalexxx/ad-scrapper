package com.stalex.avito

import com.stalex.RefPageImpl
import com.stalex.RefPageProvider

class AvitoPageProvider() : RefPageProvider<RefPageImpl> {

    val prefix = "https://www.avito.ru/sankt-peterburg/kvartiry/sdam/na_dlitelnyy_srok"
//    val prefix = "https://www.avito.ru/sankt-peterburg/nedvizhimost"

    override fun get(index: Int): RefPageImpl {
        val pfx: String = index.takeIf { it > 0 }?.let { "?p=$it" } ?: ""
        return RefPageImpl("$prefix$pfx")
    }
}
