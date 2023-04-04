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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redrock1.OnItemClickListener
import com.example.redrock1.adpter.Frag3RvAdapter
import com.example.redrock1.adpter.Frag1RvAdapter
import com.example.redrock1.databinding.Fragment3Binding
import com.example.redrock1.pojo.MessageInfo
import com.example.redrock1.pojo.WeChat
import com.example.redrock1.util.NetRequest
import com.example.redrock1.util.NetRequestPlus
import org.json.JSONArray
import org.json.JSONObject

class Fragment3 : Fragment() {
    private var weChatList : ArrayList<WeChat> = ArrayList()
    private var webViewNumber = 0
    private val mFragment3Binding : Fragment3Binding by lazy { Fragment3Binding.inflate(layoutInflater) }
    private var messageList : ArrayList<MessageInfo> = ArrayList()
    private var jsonNumber : Int = 1
    private val mViewModel by lazy { ViewModelProvider(this)[NetRequestPlus::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragment3Binding.webView3.visibility = View.GONE
        disposeAndConnection("https://wanandroid.com/wxarticle/chapters/json")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return mFragment3Binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.homeLiveData.observe(viewLifecycleOwner){
            disposeJsonDecode(it)
        }
    }

    private fun disposeAndConnection(url: String){
        mViewModel.getHomeData(url)
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

    private fun disposeJsonDecode(json: String) {
        if(jsonNumber == 1){
            jsonNumber++
            //第一层解析
            jsonDecode(json)
        }else if (jsonNumber == 2){
            //第二层解析
            jsonDecodeDetail(json)
        }
    }

    private fun jsonDecodeDetail(json: String) {
        try {
            val jsonObject : JSONObject = JSONObject(json)
            val jsonObject1 = jsonObject.getJSONObject("data")
            val jsonArray = jsonObject1.getJSONArray("datas")
            var messageInfo : MessageInfo? = null
            for (i in 0 until jsonArray.length()){
                var jo2 = jsonArray.getJSONObject(i)
                messageInfo = MessageInfo()
                messageInfo.link = jo2.getString("link")
                messageInfo.title = jo2.getString("title")
                messageList.add(messageInfo)
            }
            Log.d("lx", "jsonDecode: 这个没报错-----115")
        }catch (je:Exception){
            je.printStackTrace()
        }finally {
            mFragment3Binding.frag3Rv.adapter = Frag1RvAdapter(messageList)
            mFragment3Binding.frag3Rv.layoutManager = LinearLayoutManager(activity)
            mFragment3Binding.frag3Rv.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
            initOnItemClickListenerDetail(mFragment3Binding.frag3Rv.adapter as Frag1RvAdapter)
        }
    }

    private fun initOnItemClickListenerDetail(recycleViewAdapter: Frag1RvAdapter) {
        recycleViewAdapter.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                startIntent(position)
                mFragment3Binding.webView3.visibility = View.VISIBLE
            }
        }
    }

    private fun initOnItemClickListener(frag3RvAdapter: Frag3RvAdapter) {
        frag3RvAdapter.mOnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                var url : StringBuilder = StringBuilder("https://wanandroid.com/wxarticle/list/")
                url.append(weChatList[position].id.toString())
                url.append("/")
                url.append(webViewNumber)
                webViewNumber++
                url.append("/")
                url.append("json")
                url.append("/")
                Log.d("lx", "startIntent: 公众号网络拼接结果，第一层次，马上进入第二层")
                mViewModel.getHomeData(url.toString())
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun startIntent(position: Int) {
        mFragment3Binding.webView3.settings.javaScriptEnabled = true
        mFragment3Binding.webView3.webViewClient = WebViewClient()
        mFragment3Binding.webView3.loadUrl(messageList[position].link.toString())
    }
}