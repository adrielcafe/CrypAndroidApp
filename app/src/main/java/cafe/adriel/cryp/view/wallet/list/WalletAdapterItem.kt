package cafe.adriel.cryp.view.wallet.list

import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.view.View
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.CryptoUnit
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import cafe.adriel.kbus.KBus
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter_extensions.drag.IDraggable
import com.tubb.smrv.SwipeMenuLayout
import com.tubb.smrv.listener.SimpleSwipeSwitchListener
import kotlinx.android.synthetic.main.list_item_wallet.view.*
import java.math.BigDecimal

class WalletAdapterItem(var wallet: Wallet) :
        AbstractItem<WalletAdapterItem, WalletAdapterItem.ViewHolder>(),
        IDraggable<WalletAdapterItem, WalletAdapterItem> {

    private var draggable = true

    override fun getIdentifier() = wallet.id.hashCode().toLong()

    override fun getType() = layoutRes

    override fun getLayoutRes() = R.layout.list_item_wallet

    override fun getViewHolder(v: View?) = ViewHolder(v!!)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.itemView?.apply {
            val currencyCode = PreferenceRepository.getCurrency().currencyCode.toUpperCase()
            val convertedBalance = wallet.getFormattedBalanceCurrency()
            val balance: String
            val cryptoUnit: String
            when(PreferenceRepository.getCryptoUnit()){
                CryptoUnit.M_BTC -> {
                    balance = wallet.getFormattedBalanceMBtc()
                    cryptoUnit = "m${wallet.crypto.symbol.toUpperCase()}"
                }
                CryptoUnit.BITS -> {
                    balance = wallet.getFormattedBalanceBits()
                    cryptoUnit = CryptoUnit.BITS.name
                }
                CryptoUnit.SATOSHI -> {
                    balance = wallet.getFormattedBalanceSatoshi()
                    cryptoUnit = CryptoUnit.SATOSHI.name
                }
                else -> {
                    balance = wallet.getFormattedBalanceBtc()
                    cryptoUnit = wallet.crypto.symbol.toUpperCase()
                }
            }

            if(wallet.balance >= BigDecimal.ZERO){
                val balanceSpan = SpannableString("$balance $cryptoUnit")
                val convertedBalanceSpan = SpannableString("$convertedBalance $currencyCode")
                balanceSpan.setSpan(
                    TypefaceSpan("sans-serif-medium"),
                    0, balanceSpan.lastIndexOf(cryptoUnit),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                convertedBalanceSpan.setSpan(
                    TypefaceSpan("sans-serif-medium"),
                    0, convertedBalanceSpan.lastIndexOf(currencyCode),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                vBalance.text = balanceSpan
                vConvertedBalance.text = convertedBalanceSpan
            } else {
                vBalance.text = "- $cryptoUnit"
                vConvertedBalance.text = "- $currencyCode"
            }

            vWalletName.text = if(wallet.name.isNotEmpty()) wallet.name
                                else wallet.crypto.name
            vCryptoLogo.setCrypto(wallet.crypto)
            vSwipeMenu.setSwipeListener(object : SimpleSwipeSwitchListener(){
                override fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {
                    KBus.post(SwipeMenuOpenedEvent(identifier))
                }
            })
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.itemView?.apply {
            vWalletName.text = ""
            vConvertedBalance.text = "-"
            vBalance.text = "-"
            vCryptoLogo.clear()
            vSwipeMenu.smoothCloseMenu(0)
            vSwipeMenu.setSwipeListener(null)
        }
    }

    override fun isDraggable() = draggable

    override fun withIsDraggable(draggable: Boolean): WalletAdapterItem {
        this.draggable = draggable
        return this
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}