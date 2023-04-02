package com.example.redrock1

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.viewPager2.setPadding(0,getStatusBarHeight(),0,0)
        initViewpager()
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