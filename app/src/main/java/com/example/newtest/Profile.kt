package com.example.newtest

import kotlinx.serialization.Serializable

@Serializable
class Profile (
    var ID: String,
    var fio: String,
    var city: String,
    var cityID: String,
    var club: String,
    var clubID: String,
    var birthday: String,
    var countCompetitions: String,
    var countWins: String,
    var category: String,
    var categoryID: String,
    var ageGroup: String,
    var sexID: String,
    val ageGroupID: String,
    var nikname: String,
    val userID: String,
    var password: String,
    var avatar: String
) {

}