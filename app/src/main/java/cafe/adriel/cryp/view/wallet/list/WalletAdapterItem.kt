package cafe.adriel.cryp.view.wallet.list

import android.content.res.ColorStateList
import android.support.v7.widget.RecyclerView
import android.view.View
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.CoinFormat
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import cafe.adriel.kbus.KBus
import com.mikepenz.fastadapter.items.AbstractItem
import com.tubb.smrv.SwipeMenuLayout
import com.tubb.smrv.listener.SimpleSwipeSwitchListener
import kotlinx.android.synthetic.main.list_item_wallet.view.*

class WalletAdapterItem(var wallet: Wallet) :
        AbstractItem<WalletAdapterItem, WalletAdapterItem.ViewHolder>() {

    override fun getIdentifier() = wallet.id.hashCode().toLong()

    override fun getType() = layoutRes

    override fun getLayoutRes() = R.layout.list_item_wallet

    override fun getViewHolder(v: View?) = ViewHolder(v!!)

    override fun bindView(holder: ViewHolder?, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder?.itemView?.apply {
            var balance = wallet.getFormattedBalanceBtc()
            var coinFormat = wallet.coin.name
            when(PreferenceRepository.getCoinFormat()){
                CoinFormat.M_BTC -> {
                    balance = wallet.getFormattedBalanceMBtc()
                    coinFormat = "m${wallet.coin.name}"
                }
                CoinFormat.BITS -> {
                    balance = wallet.getFormattedBalanceBits()
                    coinFormat = CoinFormat.BITS.name
                }
                CoinFormat.SATOSHI -> {
                    balance = wallet.getFormattedBalanceSatoshi()
                    coinFormat = CoinFormat.SATOSHI.name
                }
            }

            if(wallet.balance >= 0){
                vConvertedBalance.text = "$ ${wallet.getFormattedBalanceCurrency()}"
                vBalance.text = balance
                vCoinFormat.text = coinFormat
            } else {
                vConvertedBalance.text = "-"
                vBalance.text = "-"
                vCoinFormat.text = ""
            }

            vCoinName.text = wallet.coin.fullName
            vCoinLogo.setImageDrawable(wallet.coin.logo)
            vCoinLogo.imageTintList = ColorStateList.valueOf(colorFrom(R.color.colorPrimaryDark))
            vSwipeMenu.setSwipeListener(object : SimpleSwipeSwitchListener(){
                override fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {
                    KBus.post(OpenedSwipeMenuEvent(identifier))
                }
            })
        }
    }

    override fun unbindView(holder: ViewHolder?) {
        super.unbindView(holder)
        holder?.itemView?.apply {
            vCoinName.text = ""
            vConvertedBalance.text = "-"
            vBalance.text = "-"
            vCoinFormat.text = ""
            vCoinLogo.setImageDrawable(null)
            vSwipeMenu.smoothCloseMenu(0)
            vSwipeMenu.setSwipeListener(null)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}