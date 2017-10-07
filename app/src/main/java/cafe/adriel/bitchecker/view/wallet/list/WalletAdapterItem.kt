package cafe.adriel.bitchecker.view.wallet.list

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.View
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.getColor
import cafe.adriel.bitchecker.model.entity.Wallet
import com.mikepenz.fastadapter.IDraggable
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.list_item_wallet.view.*

class WalletAdapterItem(private val wallet: Wallet) :
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
            vName.text = wallet.name
            vBalance.text = if(wallet.balance < 0) "-" else wallet.prettyBalance
            vCoinName.text = wallet.coin.name
            vCoinLogo.background = wallet.coin.logo
            vCoinLayout.backgroundTintList = ColorStateList.valueOf(wallet.coin.color)
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
            vCoinLayout.backgroundTintList = ColorStateList.valueOf(getColor(R.color.silver))
            smMenuViewLeft.setBackgroundColor(getColor(R.color.silver))
        }
    }

    override fun withIsDraggable(draggable: Boolean): WalletAdapterItem {
        this.draggable = draggable
        return this
    }

    override fun isDraggable() = draggable

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}