package com.example.redrock1.util

import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 返回字符串，需要自己解析json
 */

class NetRequestPlus : ViewModel() {
    private var mHandler : MyHandler = MyHandler()
    val homeLiveData : LiveData<String>
        get() = _mutableHomeLiveDate
    private val _mutableHomeLiveDate = MutableLiveData<String>()

    fun getHomeData(url: String){
        startConnection(url)
    }

    private fun startConnection(url : String){
        Thread {
            try {
                val mUrl = URL(url)
                val connection = mUrl.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9")
                connection.connect()
                val inputStream = connection.inputStream
                val responseData = streamToString(inputStream)
                val message: Message = Message()
                message.obj = responseData
                Log.d("lx", "responseData: $responseData")
                mHandler.sendMessage(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    private fun streamToString(inputStream: InputStream): String{
        val sb : StringBuilder = StringBuilder()
        var oneLine : String?
        val reader : BufferedReader = BufferedReader(InputStreamReader(inputStream))
        try {
            while ((reader.readLine()).also { oneLine = it } != null){
                sb.append(oneLine).append('\n')
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            try {
                reader.close()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    private inner class MyHandler() : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val respondData = msg.obj.toString()
            _mutableHomeLiveDate.value = respondData
            Log.d("lx", "handleMessage: 设置value的值设置成功")
        }
    }
}