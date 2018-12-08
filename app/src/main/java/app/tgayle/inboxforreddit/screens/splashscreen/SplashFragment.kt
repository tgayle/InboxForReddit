package app.tgayle.inboxforreddit.screens.splashscreen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.tgayle.inboxforreddit.BaseFragment
import app.tgayle.inboxforreddit.R

class SplashFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}
