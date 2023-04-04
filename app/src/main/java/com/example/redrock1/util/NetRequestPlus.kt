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
 * T 定义返回数据的类型
 */

class NetRequestPlus<T> : ViewModel() {
    private var mHandler : MyHandler? = null
    val homeLiveData : LiveData<ArrayList<T>>
        get() = _mutableHomeLiveDate
    private val _mutableHomeLiveDate = MutableLiveData<ArrayList<T>>()
    //定义返回数据的集合
    val arrayList : ArrayList<T>
        get() = _arrayList
    private val _arrayList = ArrayList<T>()

    fun getHomeData(url : String,jsonDecode : (String,ArrayList<T>) -> ArrayList<T>){
        startConnection(url, jsonDecode)
    }

    private fun startConnection(url : String,jsonDecode : (String,ArrayList<T>) -> ArrayList<T>){
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
                mHandler = MyHandler(jsonDecode)
                mHandler?.sendMessage(message)
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

    private inner class MyHandler(val jsonDecode : (String,ArrayList<T>) -> ArrayList<T>) : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val respondData = msg.obj.toString()
            jsonDecode(respondData,arrayList)
            _mutableHomeLiveDate.value = arrayList
        }
    }
}