package com.cubenmax.framework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FlowableActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flowable)

        //create disposable to keep track of all the observables
        compositeDisposable = CompositeDisposable()

        val list = listOf(1, 2, 3, 4, 5, 6, 7)

        //create observable object
        val flowableData = Flowable
            .just(list) // an operator to take the list and turn it to an observable
            .subscribeOn(Schedulers.io()) //specify a worker thread i.e tell it where the work will be done
            .observeOn(AndroidSchedulers.mainThread()) //specify where results will be seen
            .subscribe { value ->    //do the work on thread...
                value.map {
                    it*2  // create a list of even numbers
                }
            }
        //keep track of observables
        compositeDisposable.add(flowableData)
    }

    override fun onDestroy(){
        super.onDestroy()
        compositeDisposable.dispose()  // destroy the observables

        //note: if your are using a viewModel, disposable should track observables there
    }
}