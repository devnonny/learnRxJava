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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.function.Predicate


class MainActivity : AppCompatActivity() {

    val TAG = "Main Activity"
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //create disposable to keep track of all the observables
        compositeDisposable = CompositeDisposable()

        //create observable object - We want to observe a list of Tasks
        val taskObservable: Observable<Task> = Observable
            .fromIterable(DataSource.createTasksList())  // an operator to take the list of tasks and turn it to an observable
            .subscribeOn(Schedulers.io())   //specify a worker thread i.e tell it where the work will be done
            //do the work on a background thread
            .filter{task ->    //operator to show complete task, when task is complete, Observers will receive it

                Log.d(TAG, "onNext: : " + Thread.currentThread().name) // this should show that it is working in a background thread
                try{
                    Thread.sleep(1000); // mimick freezing the thread
                }catch(e: InterruptedException) {
                    e.printStackTrace()
                }
                //task.isComplete()
                task.isComplete
            }
            .observeOn(AndroidSchedulers.mainThread()) //specify where results will be seen




        //create an Observer to observe the observable object(static methods are overriden)
        //this observer is observing on the main thread
        taskObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribed : called." )

                //add the the disposable to the list of disposable
                compositeDisposable.add(d)

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

    override fun onDestroy(){  // when the activity is no longer available
        super.onDestroy()

        compositeDisposable.dispose()  // destroy the observables

        //note: if your are using a viewModel, disposable should track observables there
    }

}


