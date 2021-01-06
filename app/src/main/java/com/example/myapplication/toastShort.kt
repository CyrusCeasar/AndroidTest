package com.example.myapplication

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


fun Context.toastShort(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: String) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
}

inline fun FragmentManager.inTransaction(runnable: Runnable? = null,func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    runnable?.let {
        fragmentTransaction.runOnCommit { it.run() }
    }

    fragmentTransaction.func()
    fragmentTransaction.commit()
}


fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int,runnable: Runnable?=null) {
    supportFragmentManager.inTransaction(runnable) { add(frameId, fragment) }
}

fun AppCompatActivity.show(fragment: Fragment, frameId: Int,runnable: Runnable?=null){
    supportFragmentManager.inTransaction(runnable) {
        var added = false
        supportFragmentManager.fragments.forEach {
            if(it == fragment){
                show(it)
                added = true
            }else{
                hide(it)
            }
        }
        if(!added){
            add(frameId,fragment)
        }
    }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int,runnable: Runnable?= null) {
    supportFragmentManager.inTransaction (runnable){ replace(frameId, fragment) }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}