package com.example.redrock1.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redrock1.OnItemClickListener
import com.example.redrock1.adpter.Frag3RvAdapter
import com.example.redrock1.adpter.RecycleViewAdapter
import com.example.redrock1.databinding.Fragment3Binding
import com.example.redrock1.pojo.MessageInfo
import com.example.redrock1.pojo.WeChat
import com.example.redrock1.util.NetRequest
import org.json.JSONArray
import org.json.JSONObject

class Fragment3 : Fragment() {
    private var mHandler : Fragment3.MyHandler? = null
    private var weChatList : ArrayList<WeChat> = ArrayList()
    private var webViewNumber = 0
    private val mFragment3Binding : Fragment3Binding by lazy { Fragment3Binding.inflate(layoutInflater) }
    private val mNetRequest : NetRequest = NetRequest()
    private var messageList : ArrayList<MessageInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragment3Binding.webView3.visibility = View.GONE
        mHandler = MyHandler()
        disposeAndConnection("https://wanandroid.com/wxarticle/chapters/json")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return mFragment3Binding.root
    }


    private fun disposeAndConnection(url: String){
        mHandler?.let { mNetRequest.startConnection(url.toString(), it) }
    }

    private fun jsonDecode(json : String){
        try {
            val jsonObject : JSONObject = JSONObject(json)
            val jsonArray : JSONArray =jsonObject.getJSONArray("data")
            var newWeChat : WeChat? = null
            for(i in 0 until jsonArray.length()){
                newWeChat = WeChat()
                val jo : JSONObject = jsonArray.getJSONObject(i)
                newWeChat.name = jo.getString("name")
                newWeChat.id = jo.getInt("id")
                weChatList.add(newWeChat)
            }
        }catch (je:Exception){
            je.printStackTrace()
        }finally {
            mFragment3Binding.frag3Rv.adapter = Frag3RvAdapter(weChatList)
            mFragment3Binding.frag3Rv.layoutManager = LinearLayoutManager(activity)
            mFragment3Binding.frag3Rv.addItemDecoration(
                DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL)
            )
            val adapter = mFragment3Binding.frag3Rv.adapter
            if(adapter is Frag3RvAdapter){
                initOnItemClickListener(adapter)
            }
        }
    }

    private inner class MyHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val respondData = msg.obj.toString()
            disposeJsonDecode(respondData)
            Log.d("lx", "responseData: $weChatList")
        }
    }

    private fun disposeJsonDecode(json: String) {
        if(json.matches(".+\"datas\".+".toRegex())){
            jsonDecodeDetail(json)
        }else{
            jsonDecode(json)
        }
    }

    private fun jsonDecodeDetail(json: String) {
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
            mFragment3Binding.frag3Rv.adapter = RecycleViewAdapter(messageList)
            mFragment3Binding.frag3Rv.layoutManager = LinearLayoutManager(activity)
            mFragment3Binding.frag3Rv.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
            initOnItemClickListener(mFragment3Binding.frag3Rv.adapter as Frag3RvAdapter)
        }
    }

    private fun initOnItemClickListener(frag3RvAdapter: Frag3RvAdapter) {
        frag3RvAdapter.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startIntent(position)
                mFragment3Binding.webView3.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startIntent(position: Int) {
        //https://wanandroid.com/wxarticle/list/408/1/json
        var url : StringBuilder = StringBuilder("https://wanandroid.com/wxarticle/list/")
        url.append(weChatList[position].id.toString())
        url.append("/")
        url.append(webViewNumber)
        webViewNumber++
        url.append("/")
        url.append("json")
        url.append("/")
        Log.d("lx", "startIntent: 公众号网络拼接结果")
        mHandler?.let { mNetRequest.startConnection(url.toString(), it) }
        mFragment3Binding.webView3.settings.javaScriptEnabled = true
        mFragment3Binding.webView3.webViewClient = WebViewClient()
        mFragment3Binding.webView3.loadUrl(url.toString())
    }
}