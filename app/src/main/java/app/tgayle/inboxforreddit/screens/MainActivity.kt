package app.tgayle.inboxforreddit.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.tgayle.inboxforreddit.R
import dagger.android.AndroidInjection
import retrofit2.Retrofit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
