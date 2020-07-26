package tink.co.weatherforecast.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.city_input
import kotlinx.android.synthetic.main.fragment_login.log_out
import kotlinx.android.synthetic.main.fragment_login.login_name
import kotlinx.android.synthetic.main.fragment_login.weather
import tink.co.weatherforecast.R


/**
 * Created by Tourdyiev Roman on 25.07.2020.
 */
class LoginFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener{

    private lateinit var googleApiClient: GoogleApiClient
    private val auth = FirebaseAuth.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private val SIGN_IN = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        if (!::googleApiClient.isInitialized) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(Scopes.PLUS_LOGIN))
                .requestIdToken("194074741962-dqi4a63tepai0bkocmeemgr7mnvep988.apps.googleusercontent.com")
                .requestEmail()
                .build()
            googleApiClient = GoogleApiClient.Builder(requireActivity())
                .enableAutoManage(requireActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sign_in.setOnClickListener{
            progress.visibility = VISIBLE
            signInWithGoogle()
        }
        var authListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                account_layout.visibility = VISIBLE
                login_layout.visibility = GONE
                login_name.text = user.email
            } else {
                if (googleApiClient.isConnected) {
                    Auth.GoogleSignInApi.signOut(googleApiClient)
                    googleApiClient.disconnect()
                    googleApiClient.connect()
                }
                account_layout.visibility = GONE
                login_layout.visibility = VISIBLE
            }
        }
        auth.addAuthStateListener(authListener)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        weather.setOnClickListener {
            if (sharedPreferences.getString("city", "")?.length!! > 0) {
                view.findNavController().navigate(R.id.action_loginFragment_to_weatherNowFragment)
            } else {
                Toast.makeText(activity, resources.getString(R.string.specify_city), Toast.LENGTH_SHORT).show()
            }
        }

        city_input.setText(sharedPreferences.getString("city", ""))

        city_input.doOnTextChanged { text, start, before, count ->
            val editor = sharedPreferences.edit()
            editor.putString("city", text.toString())
            editor.apply()
        }

        log_out.setOnClickListener {
            auth.signOut()
        }
    }

    protected fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, SIGN_IN)
    }

    override fun onPause() {
        if (::googleApiClient.isInitialized) {
            googleApiClient.stopAutoManage(requireActivity())
            googleApiClient.disconnect()
        }
        super.onPause()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            result?.let {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    firebaseAuthWithGoogle(account)
                    return
                }
            }
            Toast.makeText(activity, resources.getString(R.string.auth_fail), LENGTH_LONG).show()
            progress.visibility = GONE
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(activity, resources.getString(R.string.auth_fail), Toast.LENGTH_SHORT).show()
                    progress.visibility = GONE
                } else {
                    account_layout.visibility = VISIBLE
                    login_layout.visibility = GONE
                }
            }
    }
}