package io.github.senseopensource.core

import android.util.Log


internal object SenseLog {
    private var isLog = true

    fun setLogVisible(visible: Boolean) {
        isLog = visible
    }

    fun needInit() {
        v("Sense is not initialized")
    }

    fun v(msg: String?) {
        if (isLog) Log.v("Sense SDK ::", msg!!)
    }
}
