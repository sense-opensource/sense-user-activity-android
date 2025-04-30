<h1>Sense User Activity - Android</h1>

<p style="width:100%;">
    <a href="https://github.com/sense-opensource/sense-user-activity-android/blob/main/LICENSE">
        <img width="9%" src="https://custom-icon-badges.demolab.com/github/license/denvercoder1/custom-icon-badges?logo=law">
    </a> 
    <img width="12.6%" src="https://badge-generator.vercel.app/api?icon=Github&label=Last%20Commit&status=May&color=6941C6"/> 
    <a href="https://discord.gg/hzNHTpwt">
        <img width="10%" src="https://badge-generator.vercel.app/api?icon=Discord&label=Discord&status=Live&color=6941C6"> 
    </a>
</p>

<h2>Welcome to Sense's open source repository</h2>

<p style="width:100%;">  
    <img width="4.5%" src="https://custom-icon-badges.demolab.com/badge/Fork-orange.svg?logo=fork"> 
    <img width="4.5%" src="https://custom-icon-badges.demolab.com/badge/Star-yellow.svg?logo=star"> 
    <img width="6.5%" src="https://custom-icon-badges.demolab.com/badge/Commit-green.svg?logo=git-commit&logoColor=fff"> 
</p>

<p> Sense is a client side library that enables you to identify users by pinpointing their hardware and software characteristics. This is done by computing a token that stays consistent in spite of any manipulation.</p>                           
<p> This tracking method works even in the browser's incognito mode and is not cleared by flushing the cache, closing the browser or restarting the operating system, using a VPN or installing AdBlockers. Sense is available as SenseOS for every open source requirement and is different from Sense PRO, our extremely accurate and detailed product.</p>

**Try visiting the same page in an incognito mode or switch on the VPN and 
notice how the visitor identifier remains the same in spite of all these changes!** 

<h3>Getting started with Sense </h3>


#### Requirements

```html
* Use Android 5.1 (API level 21) and above.
* Use Kotlin version 1.6.10 and above.
* Add READ_PHONE_STATE Permission in Android Manifest for deivce information(Optional)
```

Note: If the application does not have the listed permissions, the values collected using those permissions will be ignored. To provide a valid device details, we recommend employing as much permission as possible based on your use-case.

#### Step 1 - Add Dependency

Add the dependency in the app level build.gradle:

```bash
dependencies {
    implementation 'io.github.sense-opensource:SenseOSUserActivity:0.0.1'
}
```

#### Step 2 - Import SDK

```kotlin
import io.github.senseopensource.SenseOSUserActivity
```

#### Step 3 - Initialize SDK

Add the following line of code to initialize it.

```kotlin
SenseOSUserActivity.initSDK(activity)

SenseOSUserActivity.initKeyStrokeBehaviour(context, email, password) // Here email and password are id of EditText Fields

SenseOSUserActivity.initTouchBehaviour(context, touchView) // Here touchView is id of Layout which needs to capture touch metrics

SenseOSUserActivity.initScrollBehaviour(scrollView) // Here scrollView is id of Layout which needs to capture scroll metrics
```

#### Step 4 - Get User Activity Details

Use the below code to get the User Activity Details

```kotlin
SenseOSUserActivity.getBehaviourData();
```

#### Sample Program

Here you can find the demonstration to do the integration.

```kotlin
import io.github.senseopensource.SenseOSUserActivity
import io.github.senseopensource.SenseOSUserActivityConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize SDK
        SenseOSUserActivity.initSDK(this)

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

        // Fetch device details
        getUserActivityDetails();
    }
    private fun getUserActivityDetails() {
        val details = SenseOSUserActivity.getBehaviourData();
        println(details);
    }
}
```

<h4>Plug and play, in just 3 steps</h3>  

1️⃣ Visit the Git hub repository for the desired function : Validate your desired repository  
2️⃣ Download the code as a ZIP file : Clone the code in your local system  
3️⃣ Run the installer : Start testing the accuracy of your desired metrics 

#### With Sense, you can  

✅ Predict user intent : Identify the good from the bad visitors with precision  
✅ Create user identities : Tokenise events with a particular user and device  
✅ Custom risk signals : Developer specific scripts that perform unique functions  
✅ Protect against Identity spoofing : Prevent users from impersonation  
✅ Stop device or browser manipulation : Detect user behaviour anomalies 

### Resources 

#### MIT license : 

Sense OS is available under the <a href="https://github.com/sense-opensource/sense-device-identity-android/blob/main/LICENSE"> MIT license </a>

#### Contributors code of conduct : 

Thank you for your interest in contributing to this project! We welcome all contributions and are excited to have you join our community. Please read these <a href="https://github.com/sense-opensource/sense-device-identity-android/blob/main/code_of_conduct.md"> code of conduct </a> to ensure a smooth collaboration.

#### Where you can get support :     
![Gmail](https://img.shields.io/badge/Gmail-D14836?logo=gmail&logoColor=white)       product@getsense.co 

Public Support:

For questions, bug reports, or feature requests, please use the Issues and Discussions sections on our repository. This helps the entire community benefit from shared knowledge and solutions.

Community Chat:

Join our Discord server (link) to connect with other developers, ask questions in real-time, and share your feedback on Sense.

Interested in contributing to Sense?

Please review our <a href="https://github.com/sense-opensource/sense-device-identity-android/blob/main/CONTRIBUTING.md"> Contribution Guidelines </a> to learn how to get started, submit pull requests, or run the project locally. We encourage you to read these guidelines carefully before making any contributions. Your input helps us make Sense better for everyone!