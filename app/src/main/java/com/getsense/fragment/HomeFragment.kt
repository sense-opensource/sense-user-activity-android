package com.getsense.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.getsense.R
import com.getsense.SharedViewModel
import io.github.senseopensource.Sense
import io.github.senseopensource.SenseOSConfig
import org.json.JSONObject
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class HomeFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var textToCopy: TextView
    private lateinit var senseIdString: String
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.home_layout, container, false)
        val copyButton:ImageView = view.findViewById(R.id.copy_button)

        val signupLink: LinearLayout = view.findViewById(R.id.registerLink)
        signupLink.setOnClickListener {
            openWebsite("http://pro.getsense.co/")
        }
        copyButton.setOnClickListener {
            copyTextToClipboard()
        }

        return view
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val activity = activity
        activity?.let {
            val config = SenseOSConfig(
                allowGeoLocation = true
            )
            Sense.initSDK(activity, config)
            setUpUi()
        }
    }
    private fun setUpUi() {
        context?.let { ProgressDialogManager.show(it) }

        Sense.getSenseDetails(object : Sense.SenseListener {
            override fun onSuccess(data: String) {
                val jsonObject = JSONObject(data)
                val sense_id = jsonObject.getString("sense_id");
                val textView = view?.findViewById<TextView>(R.id.device_id_txt)
                textView?.text  = sense_id

                val jsonView = view?.findViewById<TextView>(R.id.code_view)
                jsonView?.text = beautifyJson(jsonObject.get("device_details").toString())
                val lineView = view?.findViewById<TextView>(R.id.code_line_no)
                lineView?.text = lineNumbersString(beautifyJson(jsonObject.get("device_details").toString()))

                ProgressDialogManager.dismiss()
            }
            override fun onFailure(message: String) {
                ProgressDialogManager.dismiss()
            }
        })
    }
    private fun lineNumbersString(from: String): String =
        (1..from.lines().count())
            .joinToString(separator = "\n") { it.toString() }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
    private fun copyTextToClipboard() {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", senseIdString)
        clipboard.setPrimaryClip(clip)

        // Optional: Show a message to the user
        Toast.makeText(requireContext(), "Sense ID copied!", Toast.LENGTH_SHORT).show()
    }
    fun beautifyJson(jsonString: String): String {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = gson.fromJson(jsonString, Any::class.java)
        return gson.toJson(jsonObject) // Convert back to pretty JSON string
    }

}
