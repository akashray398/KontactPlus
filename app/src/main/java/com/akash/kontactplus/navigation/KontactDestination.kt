package com.akash.kontactplus.navigation

sealed class KontactDestination(
    val route: String,
) {
    data object Favourites : KontactDestination("favourites")
    data object Recents : KontactDestination("recents")
    data object Contacts : KontactDestination("contacts")
    data object Dialpad : KontactDestination("dialpad")
    data object Assistant : KontactDestination("assistant")
}
