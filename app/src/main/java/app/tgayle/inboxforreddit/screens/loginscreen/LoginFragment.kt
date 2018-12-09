package app.tgayle.inboxforreddit.screens.loginscreen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.tgayle.inboxforreddit.AppSingleton
import app.tgayle.inboxforreddit.R

class LoginFragment : Fragment() {
    lateinit var viewModelFactory: LoginFragmentViewModelFactory
    lateinit var viewModel: LoginFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModelFactory = LoginFragmentViewModelFactory(AppSingleton.dataRepository)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginFragmentViewModel::class.java)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        return view
    }
}
