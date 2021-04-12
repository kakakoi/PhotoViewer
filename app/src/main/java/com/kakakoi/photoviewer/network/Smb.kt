package com.kakakoi.photoviewer.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.kakakoi.photoviewer.data.Storage
import com.kakakoi.photoviewer.lib.Event
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import java.net.MalformedURLException
import java.util.*


class Smb(private val storage: Storage) {

    companion object {
        const val TAG = "Smb"
        const val SMB_SCHEME = "smb:\\\\"
    }

    val onTransit = MutableLiveData<Event<String>>()

    private val cifsContext = login()
    val basePath = SMB_SCHEME + storage.address + storage.dir
    val prefixBasePath = convertUrl(SMB_SCHEME + storage.address)
    val baseUrl = convertUrl(basePath)
    val info = "smb info: basePath=${basePath}, user=${storage.user}, pass=${storage.pass}"

    private fun login(): CIFSContext{
        val prop = Properties()
        setProperties(prop)

        //接続情報を作成する
        val baseContext = BaseContext(PropertyConfiguration(prop))
        val authenticator = NtlmPasswordAuthenticator(storage.user, storage.pass)
        return baseContext.withCredentials(authenticator)
    }

    private fun setProperties(p: Properties) {
        p.setProperty("jcifs.smb.client.minVersion", "SMB1")
        p.setProperty("jcifs.smb.client.maxVersion", "SMB311")
    }

    fun connect(uncPath: String = basePath): SmbFile? {
        return try {
            val url = convertUrl(uncPath)
            Log.d(TAG, "connect: path $url")
            SmbFile(url, cifsContext)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    fun convertUrl(uncPath: String = basePath): String {
        return uncPath.replace("\\", "/")
    }
}