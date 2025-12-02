package com.shadowcheck.mobile.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureApiKeyManager {
    
    private var encryptedPrefs: SharedPreferences? = null
    
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        if (encryptedPrefs == null) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            encryptedPrefs = EncryptedSharedPreferences.create(
                context,
                "shadowcheck_api_keys",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        return encryptedPrefs!!
    }
    
    fun saveApiKey(context: Context, keyName: String, keyValue: String) {
        getEncryptedPrefs(context).edit()
            .putString(keyName, keyValue)
            .apply()
    }
    
    fun getApiKey(context: Context, keyName: String): String? {
        return getEncryptedPrefs(context).getString(keyName, null)
    }
    
    fun deleteApiKey(context: Context, keyName: String) {
        getEncryptedPrefs(context).edit()
            .remove(keyName)
            .apply()
    }
    
    fun getAllKeys(context: Context): Set<String> {
        return getEncryptedPrefs(context).all.keys
    }
    
    fun clearAll(context: Context) {
        getEncryptedPrefs(context).edit()
            .clear()
            .apply()
    }
}
