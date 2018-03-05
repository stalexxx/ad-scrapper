package com.stalex

import com.mongodb.client.FindIterable
import com.stalex.avito.AvitoScrap
import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOne
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.Random

class MongoTest : StringSpec() {

    init {

        val client = KMongo.createClient() //`-->` com.mongodb.MongoClient new instance
        val database = client.getDatabase("test") //normal java driver usage
        val collection = database.getCollection<AvitoScrap>() //KMongo extension method

        val url = Random().nextInt().toString()
        "test insert" {
            collection.insertOne(AvitoScrap(url))
            val find: FindIterable<AvitoScrap> = collection.find(AvitoScrap::class.java)
        }.config(enabled = false)

        "test find" {
            collection.findOne("{url:\"$url\"}") should {
                it != null
            }
        }.config(enabled = false)

        "test delete" {
            collection.deleteOne("{url:\"$url\"}")

            collection.findOne("{url:\"$url\"}") should {
                it == null
            }
        }.config(enabled = false)
    }
}