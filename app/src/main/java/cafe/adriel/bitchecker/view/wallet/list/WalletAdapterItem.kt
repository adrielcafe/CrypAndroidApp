package cafe.adriel.bitchecker.view.wallet.list

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.getColor
import cafe.adriel.bitchecker.model.entity.Wallet
import com.mikepenz.fastadapter.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.list_item_wallet.view.*


class WalletAdapterItem(val wallet: Wallet) :
        AbstractItem<WalletAdapterItem, WalletAdapterItem.ViewHolder>(),
        IDraggable<WalletAdapterItem, WalletAdapterItem> {

    private var draggable = true

    override fun getIdentifier() = wallet.id.hashCode().toLong()

    override fun getType() = layoutRes

    override fun getLayoutRes() = R.layout.list_item_wallet

    override fun getViewHolder(v: View?) = ViewHolder(v!!)

    override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder?.itemView?.apply {
            val balance = formatBalance(if(wallet.balance < 0) "-" else wallet.prettyBalance)

            vName.text = wallet.name
            vBalance.text = balance
            vCoinName.text = wallet.coin.name
            vCoinLogo.background = wallet.coin.logo
            vCoinLayout.setBackgroundColor(wallet.coin.color)
            vCircle.backgroundTintList = ColorStateList.valueOf(wallet.coin.color)
            smMenuViewLeft.setBackgroundColor(wallet.coin.color)
        }
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)
        holder?.itemView?.apply {
            vName.text = ""
            vBalance.text = ""
            vCoinName.text = ""
            vCoinLogo.background = null
            vCoinLayout.setBackgroundColor(getColor(R.color.silver))
            vCircle.backgroundTintList = ColorStateList.valueOf(getColor(R.color.silver))
            smMenuViewLeft.setBackgroundColor(getColor(R.color.silver))
        }
    }

    override fun withIsDraggable(draggable: Boolean): WalletAdapterItem {
        this.draggable = draggable
        return this
    }

    override fun isDraggable() = draggable

    private fun formatBalance(balance: String): Spannable {
        return if(balance.length >= 8){
            val span1 = SpannableString(balance.substring(0, balance.length - 5))
            val span2 = SpannableString(balance.substring(balance.length - 5, balance.length - 1))
            span1.setSpan(ForegroundColorSpan(Color.BLACK), 0, span1.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            span2.setSpan(ForegroundColorSpan(getColor(R.color.grey_dark)), 0, span2.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            SpannableString(TextUtils.concat(span1, span2))
        } else {
            SpannableString(balance)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}