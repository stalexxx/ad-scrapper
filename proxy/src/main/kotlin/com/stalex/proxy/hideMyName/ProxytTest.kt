package com.stalex.proxy.hideMyName

import kz.qwertukg.ChromeDriverSettings
import kz.qwertukg.chromeDriver
import kz.qwertukg.elementVisibilityByLink
import kz.qwertukg.elementsByCssSelector
import kz.qwertukg.elementsByTag
import kz.qwertukg.wait
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeOptions

val settings2 = ChromeDriverSettings(
    pathToDriver = "/Users/alex/Downloads/chromedriver",
    driverOptions = ChromeOptions().apply { setHeadless(false) }
)

fun List<WebElement>.toProxyInfo() = ProxyInfo(get(0).text, get(1).text.toInt())

fun main(args: Array<String>) {

    chromeDriver(settings2) {

        val res = mutableListOf<ProxyInfo>()

        (0..5).map { "https://hidemy.name/ru/proxy-list/?start=${it * 64}#list" }.forEach { url ->
            get(url)

            wait(100, 3000) {

                elementVisibilityByLink("Показать") {
                }

                elementsByCssSelector("table.proxy__t tbody tr")
                    .map {
                        it.elementsByTag("td").toProxyInfo()
                    }.forEach {
                        res.add(it)
                    }
            }
        }
        print(res)
    }
}