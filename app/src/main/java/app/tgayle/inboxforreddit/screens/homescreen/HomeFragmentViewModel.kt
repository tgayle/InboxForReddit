package app.tgayle.inboxforreddit.screens.homescreen

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import app.tgayle.inboxforreddit.db.repository.DataRepository

class HomeFragmentViewModel(val dataRepository: DataRepository): ViewModel() {

    fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        controller.navigate(destination.id)
    }
}