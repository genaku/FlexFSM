package com.genaku.flexfsm.ui.loader

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.genaku.flexfsm.R
import com.genaku.flexfsm.domain.loader.interfaces.ILoaderInteractor
import com.genaku.flexfsm.repository.LoaderRepository
import com.genaku.flexfsm.ui.getViewModel
import com.genaku.flexfsm.ui.observeWith
import com.genaku.flexfsm.viewmodel.LoadingViewModel
import kotlinx.android.synthetic.main.activity_loader.*

class LoaderActivity : AppCompatActivity() {

    private lateinit var interactor: ILoaderInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)
        val viewModel = getViewModel {
            LoadingViewModel(LoaderRepository())
        }.apply {
            progressEvent.observeWith(this@LoaderActivity) {
                Log.d("T", "progress [$it]")
                tvProgress.text = it
                btnStart.isEnabled = false
                btnCancel.isEnabled = true
            }
            dataEvent.observeWith(this@LoaderActivity) {
                Log.d("T", "data [$it]")
                tvProgress.text = "Finished"
                tvData.text = it
                btnStart.isEnabled = true
                btnCancel.isEnabled = false
            }
            hideEvent.observeWith(this@LoaderActivity) {
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
}