package cafe.adriel.cryp.view.wallet.select

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import cafe.adriel.cryp.R
import cafe.adriel.cryp.SelectCryptocurrencyEvent
import cafe.adriel.cryp.model.entity.Cryptocurrency
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.kbus.KBus
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import kotlinx.android.synthetic.main.activity_select_cryptocurrency.*

class SelectCryptocurrencyActivity : BaseActivity() {

    private val adapter = FastItemAdapter<CryptocurrencyAdapterItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_cryptocurrency)

        setSupportActionBar(vToolbar)
        vToolbar.setNavigationIcon(R.drawable.ic_close)
        supportActionBar?.title = getString(R.string.select_cryptocurrency)

        val cryptocurrencies = Cryptocurrency.values()
                .sortedWith(Comparator { o1, o2 -> o1.name.compareTo(o2.name) })
                .map { CryptocurrencyAdapterItem(it) }

        adapter.setHasStableIds(true)
        adapter.withOnClickListener({ v, adapter, item, position ->
            val cryptocurrency = adapter.getAdapterItem(position).cryptocurrency
            selectCryptocurrency(cryptocurrency)
            true
        })
        adapter.add(cryptocurrencies)

        vCryptocurrencies.setHasFixedSize(true)
        vCryptocurrencies.layoutManager = LinearLayoutManager(this)
        vCryptocurrencies.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun selectCryptocurrency(cryptocurrency: Cryptocurrency){
        KBus.post(SelectCryptocurrencyEvent(cryptocurrency))
        finish()
    }

}