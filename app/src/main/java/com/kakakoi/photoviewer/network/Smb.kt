package com.kakakoi.photoviewer.network

import android.util.Log
import com.kakakoi.photoviewer.data.Storage
import jcifs.CIFSContext
import jcifs.CloseableIterator
import jcifs.SmbResource
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import java.net.MalformedURLException
import java.util.*

class Smb(val storage: Storage) {

    companion object {
        const val TAG = "Smb"
        const val SMB_SCHEME = "smb:\\\\"
    }

    private val cifsContext = login()
    private val basePath = SMB_SCHEME + storage.address + storage.dir
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
            val url = uncPath?.replace("\\", "/")
            SmbFile(url, cifsContext)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }

    fun createIndex(smbFile: SmbFile) {
        var count = 0
        val iterator = smbFile.children()
        while (iterator.hasNext()) {
            val resource = iterator.next() as SmbFile
            if (resource.isDirectory()) {
                Log.d(TAG, "createIndex: ${resource.name}")
            } else {

            }
        }
    }
}