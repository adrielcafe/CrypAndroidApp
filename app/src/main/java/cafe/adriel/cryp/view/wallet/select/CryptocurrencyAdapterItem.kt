package cafe.adriel.cryp.view.wallet.select

import android.support.v7.widget.RecyclerView
import android.view.View
import cafe.adriel.cryp.R
import cafe.adriel.cryp.drawableFrom
import cafe.adriel.cryp.model.entity.Cryptocurrency
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.list_item_cryptocurrency.view.*

class CryptocurrencyAdapterItem(var cryptocurrency: Cryptocurrency) :
        AbstractItem<CryptocurrencyAdapterItem, CryptocurrencyAdapterItem.ViewHolder>() {

    override fun getIdentifier() = cryptocurrency.name.hashCode().toLong()

    override fun getType() = layoutRes

    override fun getLayoutRes() = R.layout.list_item_cryptocurrency

    override fun getViewHolder(v: View?) = ViewHolder(v!!)

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
        super.bindView(holder, payloads)
        holder.itemView?.apply {
            vName.text = cryptocurrency.name
            vFullName.text = cryptocurrency.fullName
            vLogo.setImageResource(cryptocurrency.logoRes)
            vAutoRefresh.visibility = if(cryptocurrency.autoRefresh) View.VISIBLE else View.GONE
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.itemView?.apply {
            vName.text = ""
            vFullName.text = ""
            vLogo.setImageDrawable(null)
            vAutoRefresh.visibility = View.GONE
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}