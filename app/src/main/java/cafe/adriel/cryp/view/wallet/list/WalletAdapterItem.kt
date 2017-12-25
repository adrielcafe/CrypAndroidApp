package cafe.adriel.cryp.view.wallet.list

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

class WalletAdapterItem(val wallet: Wallet) :
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
                CoinFormat.U_BTC -> {
                    balance = wallet.getFormattedBalanceUBtc()
                    coinFormat = "μ${wallet.coin.name}"
                }
                CoinFormat.SATOSHI -> {
                    balance = wallet.getFormattedBalanceSatoshi()
                    coinFormat = CoinFormat.SATOSHI.fullName.toUpperCase()
                }
            }

            if(wallet.balance > 0){
                // TODO temp
                vConvertedBalance.text = "\$ 1.345,20"
                vBalance.text = balance
                vCoinFormat.text = coinFormat
            } else {
                // TODO temp
                vConvertedBalance.text = "-"
                vBalance.text = "-"
                vCoinFormat.text = ""
            }

            vName.text = wallet.coin.fullName
            vCoinLogo.background = wallet.coin.logo
            vCircle.setBackgroundColor(wallet.coin.color)
            smMenuViewLeft.setBackgroundColor(wallet.coin.color)
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
            vName.text = ""
            vConvertedBalance.text = ""
            vBalance.text = ""
            vCoinFormat.text = ""
            vCoinLogo.background = null
            vCircle.setBackgroundColor(colorFrom(R.color.silver_sand))
            smMenuViewLeft.setBackgroundColor(colorFrom(R.color.silver_sand))
            vSwipeMenu.setSwipeListener(null)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}