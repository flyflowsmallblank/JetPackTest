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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redrock1.OnItemClickListener
import com.example.redrock1.adpter.Frag1RvAdapter
import com.example.redrock1.databinding.Fragment1Binding
import com.example.redrock1.pojo.MessageInfo
import com.example.redrock1.util.NetRequest
import com.example.redrock1.util.NetRequestPlus
import org.json.JSONObject


class Fragment1 : Fragment() {
    private var messageList : ArrayList<MessageInfo> = ArrayList()
    private val mFragment1Binding : Fragment1Binding by lazy { Fragment1Binding.inflate(layoutInflater) }
    private var webViewNumber = 0
    private val mViewModel by lazy { ViewModelProvider(this)[NetRequestPlus::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFragment1Binding.webView.visibility = View.GONE
        disposeAndConnection("https://www.wanandroid.com/article/list/")
    }



    private fun initOnItemClickListener(recycleViewAdapter: Frag1RvAdapter) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.homeLiveData.observe(viewLifecycleOwner){
            Log.d("lx", "onCreate: 观察到了")
            jsonDecode(it.toString())
        }
    }

    private fun disposeAndConnection(url: String){
        var str : StringBuilder = StringBuilder(url)
        str.append(webViewNumber.toString())
        webViewNumber++
        str.append("/")
        str.append("json")
        str.append("/")
        mViewModel.getHomeData(str.toString())
    }

    private fun jsonDecode(json : String){
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
            Log.d("lx", "jsonDecode: 这个没报错-----109")
        }catch (je:Exception){
            je.printStackTrace()
        }finally {
            mFragment1Binding.rvMain.adapter = Frag1RvAdapter(messageList)
            mFragment1Binding.rvMain.layoutManager = LinearLayoutManager(activity)
            mFragment1Binding.rvMain.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
            initOnItemClickListener(mFragment1Binding.rvMain.adapter as Frag1RvAdapter)
        }
    }
}