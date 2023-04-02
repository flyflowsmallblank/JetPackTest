package com.example.redrock1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redrock1.OnItemClickListener
import com.example.redrock1.adpter.RecycleViewAdapter
import com.example.redrock1.databinding.Fragment1Binding
import com.example.redrock1.pojo.MessageInfo
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class Fragment1 : Fragment() {
    private var mHandler : Fragment1.MyHandler? = null
    private var messageList : ArrayList<MessageInfo> = ArrayList()
    private val mFragment1Binding : Fragment1Binding by lazy { Fragment1Binding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragment1Binding.webView.visibility = View.GONE
        mFragment1Binding.rvMain.setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        mFragment1Binding.webView.setPadding(0,getStatusBarHeight(),0,0);  //设置标题栏目远离状态栏,和状态栏分离
        mHandler = MyHandler()
        disposeAndConnection("https://www.wanandroid.com/article/list/1/json")
    }

    private fun initOnItemClickListener(recycleViewAdapter: RecycleViewAdapter) {
        recycleViewAdapter.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startIntent(position)
                mFragment1Binding.webView.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startIntent(position: Int) {
        mFragment1Binding.webView.settings.javaScriptEnabled = true
        mFragment1Binding.webView.webViewClient = WebViewClient()
        mFragment1Binding.webView.loadUrl(messageList[position].link)
    }

    //加载xml布局
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mFragment1Binding.root
    }

    private fun disposeAndConnection(url: String){
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

    private fun jsonDecode(json : String){
        try {
            var jsonObject : JSONObject = JSONObject(json)
            var jsonObject1 = jsonObject.getJSONObject("data")
            var jsonArray = jsonObject1.getJSONArray("datas")
            var messageInfo : MessageInfo? = null
            for (i in 0 until jsonArray.length()){
                var jo2 = jsonArray.getJSONObject(i)
                messageInfo = MessageInfo()
                messageInfo.link = jo2.getString("link")
                messageInfo.title = jo2.getString("title")
                messageList.add(messageInfo)
            }
            Log.d("lx", "jsonDecode: 这个没报错-----109")
        }catch (je:Exception){
            je.printStackTrace()
        }finally {
            mFragment1Binding.rvMain.adapter = RecycleViewAdapter(messageList)
            mFragment1Binding.rvMain.layoutManager = LinearLayoutManager(activity)
            mFragment1Binding.rvMain.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
            initOnItemClickListener(mFragment1Binding.rvMain.adapter as RecycleViewAdapter)
        }
    }

    private inner class MyHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val respondData = msg.obj.toString()
            jsonDecode(respondData)
            Log.d("lx", "responseData: $messageList")
        }
    }

    @SuppressLint("InternalInsetResource")
    private fun getStatusBarHeight() : Int{
        var result : Int = 0
        //获得状态栏高度的id
        val resourceId : Int = resources.getIdentifier("status_bar_height","dimen","android")
        if (resourceId>0){
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}