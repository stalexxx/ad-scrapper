package com.stalex.test

import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

inline fun <T, R, X> ((T) -> R).andThen(crossinline func: (R) -> X) : (T) -> X  {
    return {it : T -> func(this(it))}
}

fun <T> List<T>.defer(worker: (List<T>) -> Unit): Deferred<T> = Deferred(this, worker)

class Deferred<T>(private val list: List<T>, private val funcList: List<(List<T>) -> Unit>) {
    constructor(list: List<T>, worker: (List<T>) -> Unit) : this(list, listOf(worker))
    fun defer(worker: (List<T>) -> Unit) = Deferred(list, listOf(*funcList.toTypedArray(), worker))
    fun commit() = funcList.forEach { it(list) }
}

fun main(args: Array<String>) {
//    listOf(1, 2, 3).defer { print(it.first()) }.defer { print(it[1]) }.defer { print(it[2]) }.commit()

    data class Jedi(val name: String, val age: Int)

    val client = KMongo.createClient() //get com.mongodb.MongoClient new instance
    val database = client.getDatabase("test") //normal java driver usage
    val collection = database.getCollection<Jedi>() //KMongo extension method
}

