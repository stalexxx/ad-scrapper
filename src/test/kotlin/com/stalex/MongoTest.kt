package com.stalex

import com.mongodb.client.FindIterable
import com.stalex.avito.AvitoSourceItem
import io.kotlintest.matchers.should
import io.kotlintest.specs.StringSpec
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteOne
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*

class MongoTest : StringSpec() {

    init {

        val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
        val database = client.getDatabase("test") //normal java driver usage
        val collection = database.getCollection<AvitoSourceItem>() //KMongo extension method

        val url = Random().nextInt().toString()
        "test insert" {
            collection.insertOne(AvitoSourceItem(url))
            val find: FindIterable<AvitoSourceItem> = collection.find(AvitoSourceItem::class.java)
        }

        "test find" {
            collection.findOne("{url:\"$url\"}") should {
                it != null
            }
        }
        "test delete" {
            collection.deleteOne("{url:\"$url\"}")

            collection.findOne("{url:\"$url\"}") should {
                it == null
            }
        }
    }
}