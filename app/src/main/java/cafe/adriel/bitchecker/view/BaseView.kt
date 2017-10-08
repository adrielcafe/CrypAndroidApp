package cafe.adriel.bitchecker.view

import cafe.adriel.bitchecker.model.entity.MessageType
import com.arellomobile.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(message: String, type: MessageType)
}