package com.getsense.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.getsense.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.senseopensource.SenseOSUserActivity
import org.json.JSONObject

class BehaviourFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_behaviour, container, false)
        val myButton = view.findViewById<View>(R.id.get_info_button)
       myButton.setOnClickListener {
           context?.let { it1 ->
                val data = SenseOSUserActivity.getBehaviourData();
                val jsonObject = JSONObject(data)
               val jsonView = view.findViewById<TextView>(R.id.code_view)
               jsonView?.text = beautifyJson(jsonObject.get("user_activity").toString())
               val lineView = view.findViewById<TextView>(R.id.code_line_no)
               lineView?.text = lineNumbersString(beautifyJson(jsonObject.get("user_activity").toString()))
            }
        }

        return view
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { SenseOSUserActivity.initSDK(it) }
        setUpUi()
    }

    private fun setUpUi() {
        context?.let { ProgressDialogManager.show(it) }

        val scrollView = view?.findViewById<ScrollView>(R.id.scrollView)
        val touchView = view?.findViewById<LinearLayout>(R.id.touchView)

        val email = view?.findViewById<EditText>(R.id.public_key)
        val password = view?.findViewById<EditText>(R.id.secret_key)

        if (email != null && password != null) {
            SenseOSUserActivity.initKeyStrokeBehaviour(requireContext(), email, password)
        }
        if (touchView != null) {
            SenseOSUserActivity.initTouchBehaviour(requireContext(), touchView)
        }
        if (scrollView != null) {
            SenseOSUserActivity.initScrollBehaviour(scrollView)
        }

        ProgressDialogManager.dismiss()
    }
    private fun lineNumbersString(from: String): String =
        (1..from.lines().count())
            .joinToString(separator = "\n") { it.toString() }

    fun beautifyJson(jsonString: String): String {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = gson.fromJson(jsonString, Any::class.java)
        return gson.toJson(jsonObject) // Convert back to pretty JSON string
    }

}
