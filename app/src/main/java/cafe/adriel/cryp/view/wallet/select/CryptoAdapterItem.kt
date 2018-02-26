package cafe.adriel.cryp.view.wallet.select

import android.support.v7.widget.RecyclerView
import android.view.View
import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.Crypto
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.list_item_crypto.view.*

class CryptoAdapterItem(val crypto: Crypto) :
        AbstractItem<CryptoAdapterItem, CryptoAdapterItem.ViewHolder>() {

    override fun getIdentifier() = crypto.symbol.hashCode().toLong()

    override fun getType() = layoutRes

    override fun getLayoutRes() = R.layout.list_item_crypto

    override fun getViewHolder(v: View?) = ViewHolder(v!!)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.itemView?.apply {
            vCryptoSymbol.text = crypto.symbol
            vCryptoName.text = crypto.name
            vCryptoLogo.setImageResource(crypto.logoResId)
//            vAutoRefresh.visibility = if(crypto.autoRefresh) View.VISIBLE else View.GONE
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.itemView?.apply {
            vCryptoSymbol.text = ""
            vCryptoName.text = ""
            vCryptoLogo.setImageDrawable(null)
            vAutoRefresh.visibility = View.GONE
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}