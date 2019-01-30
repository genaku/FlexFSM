package com.genaku.flexfsm

import com.genaku.flexfsm.domain.RepoException
import com.genaku.flexfsm.domain.loader.LoaderUseCase
import com.genaku.flexfsm.domain.loader.interfaces.ILoaderPresenter
import com.genaku.flexfsm.domain.loader.interfaces.ILoaderRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class LoaderTest {

    private val presenter = TestPresenter()

    @Test
    fun shouldGoThroughWholeCycle() {
        val repo = mockk<ILoaderRepository>()
        every { repo.loadConfig() } returns ""
        every { repo.loadData("") } returns "data"

        presenter.init()
        val useCase = LoaderUseCase(presenter, repo)
        useCase.startLoad()
        Assert.assertEquals(
            "init-hide-progress:1/5 Configuration loading...-progress:2/5 Data loading...-show:data",
            presenter.log
        )
    }

    @Test
    fun shouldHideOnConfigurationLoad() {
        val repo = mockk<ILoaderRepository>()
        every { repo.loadConfig() } throws RepoException()

        presenter.init()
        val useCase = LoaderUseCase(presenter, repo)
        useCase.startLoad()
        Assert.assertEquals("init-hide-progress:1/5 Configuration loading...-hide", presenter.log)
    }

    @Test
    fun shouldHideOnDataLoad() {
        val repo = mockk<ILoaderRepository>()
        every { repo.loadConfig() } returns ""
        every { repo.loadData("") } throws RepoException()

        presenter.init()
        val useCase = LoaderUseCase(presenter, repo)
        useCase.startLoad()
        Assert.assertEquals("init-hide-progress:1/5 Configuration loading...-progress:2/5 Data loading...-hide", presenter.log)
    }

    private class TestPresenter : ILoaderPresenter {

        var log = ""
            private set

        override fun hide() {
            log("hide")
        }

        override fun show(data: String) {
            log("show:$data")
        }

        override fun showProgress(current: Int, total: Int, description: String) {
            log("progress:$current/$total $description")
        }

        override fun init() {
            log = ""
            log("init")
        }

        private fun log(msg: String) {
            if (log.isNotEmpty()) {
                log += "-"
            }
            log += msg
        }
    }
}