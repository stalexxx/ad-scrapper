package com.stalex.avito

import com.github.salomonbrys.kodein.instance
import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.experimental.runBlocking
import nolambda.skrape.SkrapeLogger

class AvitoScrapParserTest : StringSpec() {
    init {
        SkrapeLogger.enableLog = false

        "test" {
            runBlocking {

                val file: String = javaClass.classLoader.getResource("realty_item.htm").file
                val load = AvitoScrapParser(kodein.instance()).parse(AvitoRefItem(file))

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
            }
        }

        "url test" {
            runBlocking {
                val file = "https://www.avito.ru/sankt-peterburg/kvartiry/2-k_kvartira_60_m_2125_et._465016335"
//            val file: String = javaClass.classLoader.getResource("https://www.avito.ru/sankt-peterburg/kvartiry/2-k_kvartira_60_m_2125_et._465016335").file
                val load = AvitoScrapParser(kodein.instance())
                    .parse(AvitoRefItem(file))

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
            }
        }.config(invocations = 1)
    }
}