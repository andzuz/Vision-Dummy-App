package com.example.andrzejzuzak.visiondummyapp.extensions

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class PreferenceHelper @Inject constructor(context: Context) {

    val defaultPrefs: SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(context)

    fun getAuthToken(): String {
        return defaultPrefs[AUTH_TOKEN] ?: ""
    }

    fun putAuthToken(token: String) {
        defaultPrefs[AUTH_TOKEN] = token
    }

    fun cleanupWhenLoggingOut() {
        defaultPrefs[AUTH_TOKEN] = null
    }

    fun hasAuthToken(): Boolean {
        val token: String? = defaultPrefs[AUTH_TOKEN]
        return token != null
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    private operator fun SharedPreferences.set(key: String, value: Any?) {
        if (value == null) {
            edit { it.remove(key) }
        } else {
            when (value) {
                is String? -> edit { it.putString(key, value) }
                is Int -> edit { it.putInt(key, value) }
                is Boolean -> edit { it.putBoolean(key, value) }
                is Float -> edit { it.putFloat(key, value) }
                is Long -> edit { it.putLong(key, value) }
                else -> throw UnsupportedOperationException("Not yet implemented")
            }
        }
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    private operator inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }

}