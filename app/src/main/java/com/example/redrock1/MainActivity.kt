package com.example.redrock1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.redrock1.adpter.Viewpager2Adapter
import com.example.redrock1.databinding.ActivityMainBinding
import com.example.redrock1.fragment.Fragment1
import com.example.redrock1.fragment.Fragment2
import com.example.redrock1.fragment.Fragment3


class MainActivity : AppCompatActivity() {

    private val mBinding : ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
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
}