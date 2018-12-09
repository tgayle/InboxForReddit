package app.tgayle.inboxforreddit.screens.mainactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R

class MainActivity : AppCompatActivity(), MainActivityModel.Listener {
    lateinit var vmFactory: MainActivityViewModelFactory
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vmFactory = MainActivityViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, vmFactory).get(MainActivityViewModel::class.java)
        viewModel.onIntentOccurred(intent)
    }
}
