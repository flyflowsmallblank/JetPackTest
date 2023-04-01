package com.example.redrock1

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.redrock1.adpter.RecycleViewAdapter
import com.example.redrock1.adpter.Viewpager2Adapter
import com.example.redrock1.databinding.ActivityMainBinding
import com.example.redrock1.fragment.Fragment1
import com.example.redrock1.fragment.Fragment2
import com.example.redrock1.fragment.Fragment3
import com.example.redrock1.pojo.MessageInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private val mBinding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var mHandler : MyHandler? = null
    private var messageList : ArrayList<MessageInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        initViewpager()
        mHandler = MyHandler()
        disposeAndConnection("https://www.wanandroid.com/article/list/1/json")
    }

    private fun initViewpager() {
        // TODO: 需要传递数据
        var fragments = ArrayList<BackInterface>()
        fragments.add(object : BackInterface {
            override fun back(): Fragment {
                return Fragment1()
            }
        })
        fragments.add(object : BackInterface {
            override fun back(): Fragment {
                return Fragment2()
            }
        })
        fragments.add(object : BackInterface {
            override fun back(): Fragment {
                return Fragment3()
            }
        })
        mBinding.viewPager2.adapter = Viewpager2Adapter(fragments,this)
        mBinding.bottomNavi.setOnItemSelectedListener {
            when(it.itemId){
                //todo 需要等到fragment整好之后再来处理跳转
                R.id.bottom_nav_home -> mBinding.viewPager2.currentItem = 0
                R.id.bottom_nav_find -> mBinding.viewPager2.currentItem = 1
                R.id.bottom_nav_wechat -> mBinding.viewPager2.currentItem = 2
            }
            return@setOnItemSelectedListener true
        }
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
            messageList = ArrayList()
            var messageInfo : MessageInfo? = null
            for (i in 0 until jsonArray.length()){
                var jo2 = jsonArray.getJSONObject(i)
                messageInfo = MessageInfo()
                messageInfo.link = jo2.getString("link")
                messageInfo.title = jo2.getString("title")
                messageList?.add(messageInfo)
            }
        }catch (je:Exception){
            je.printStackTrace()
        }
    }

    private fun setAdapter(){
        mBinding.viewPager2.adapter = messageList?.let { RecycleViewAdapter(it) }
        mBinding.viewPager2.addItemDecoration(DividerItemDecoration( this, DividerItemDecoration.VERTICAL))
    }

    private inner class MyHandler : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val respondData = msg.obj.toString()
            jsonDecode(respondData)
            Log.d("lx", "responseData: $messageList")
            setAdapter()
        }
    }
}