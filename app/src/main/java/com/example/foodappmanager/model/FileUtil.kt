package com.example.foodappmanager.model

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.*

object FileUtil {

    fun getPath(context: Context, uri: Uri): String? {
        try {
            val fileName = getFileName(context, uri)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            val file = File(context.cacheDir, fileName)
            val outputStream: OutputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            return file.path
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "temp_file"
        val returnCursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
}