package com.stalex.proxy

import com.google.gson.GsonBuilder
import nolambda.skrape.Skrape
import nolambda.skrape.nodes.ElementBody
import nolambda.skrape.nodes.Page

val gson = GsonBuilder().create()

//---skrape---
inline fun <reified T> Skrape<String>.requestPage(url: String, noinline body: ElementBody): T {
    return request(Page(url, body = body)).let {
        gson.fromJson(it, T::class.java)
    }
}