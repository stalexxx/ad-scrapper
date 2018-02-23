package com.stalex.avito

import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import nolambda.skrape.SkrapeLogger

class AvitoEndItemProviderTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        "test" {
            val file: String = javaClass.classLoader.getResource("realty_item.htm").file
            val load = AvitoEndItemProvider()
                .load(AvitoRefItem(file))

            load should {
                it.description?.startsWith("Сдам квартиру") == true &&
                    it.user?.name == "Владимир" &&
                    it.price == "39 000 \u20BD в месяц" &&
                    it.params?.size == 7 &&
                    it.advParams?.size == 6 &&
                    it.subPrice == "залог 39 000 \u20BD" &&
                    it.title == "2-к квартира, 60 м², 21/25 эт." &&
                    it.metro?.size == 3
            }
        }.config(1)

        "url test" {
            "https://www.avito.ru/sankt-peterburg/kvartiry/2-k_kvartira_60_m_2125_et._465016335"
        }
    }
}