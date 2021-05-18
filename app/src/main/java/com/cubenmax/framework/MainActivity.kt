package com.cubenmax.framework

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.cubenmax.framework.datasource.DataSource
import com.cubenmax.framework.model.Task
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    val TAG = "Main Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create observable object - We want to observe a list of Tasks
        val taskObservable: Observable<Task> = Observable
            .fromIterable(DataSource.createTasksList())  // an operator to take the list of tasks and turn it to an observable
            .subscribeOn(Schedulers.io())   //specify a worker thread i.e tell it where the work will be done
            .observeOn(AndroidSchedulers.mainThread()) //specify where results will be seen



        //create an observer to observe the observable object(static methods are overriden)
        taskObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribed : called." )

            }
            override fun onNext(task: Task) { // observer running on the main thread
                Log.d(TAG, "onNext: : " + Thread.currentThread().name)
                Log.d(TAG, "onNext: : " + task.description)

                //simulate main thread busy(freezing the UI)
                //Thread.sleep(1000) //1 miliseconds for each iteration
            }

            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: ", e)

            }
            override fun onComplete() {
                Log.d(TAG, "onComplete: called ")
            }
        })
    }
}