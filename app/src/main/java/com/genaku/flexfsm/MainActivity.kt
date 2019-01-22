package com.genaku.flexfsm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.genaku.flexfsm.domain.ILoaderInteractor
import com.genaku.flexfsm.repository.Repository
import com.genaku.flexfsm.viewmodel.LoadingViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var interactor: ILoaderInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = getViewModel {
            LoadingViewModel(Repository())
        }.apply {
            progressEvent.observeWith(this@MainActivity) {
                Log.d("T", "progress [$it]")
                tvProgress.text = it
                btnStart.isEnabled = false
                btnCancel.isEnabled = true
            }
            dataEvent.observeWith(this@MainActivity) {
                Log.d("T", "data [$it]")
                tvProgress.text = "Finished"
                tvData.text = it
                btnStart.isEnabled = true
                btnCancel.isEnabled = false
            }
            hideEvent.observeWith(this@MainActivity) {
                Log.d("T", "hide")
                tvProgress.text = ""
                tvData.text = ""
                btnStart.isEnabled = true
                btnCancel.isEnabled = false
            }
        }
        interactor = viewModel.interactor
        btnStart.setOnClickListener {
            interactor.startLoad()
        }
        btnCancel.setOnClickListener {
            interactor.cancel()
        }
    }

    private inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(crossinline factory: () -> T): T {
        val vmFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
        }

        return ViewModelProviders.of(this, vmFactory)[T::class.java]
    }

    private fun <T> MutableLiveData<T>.observeWith(owner: LifecycleOwner, update: (T) -> Unit) {
        this.observe(owner, Observer { newValue -> newValue?.apply(update) })
    }
}
