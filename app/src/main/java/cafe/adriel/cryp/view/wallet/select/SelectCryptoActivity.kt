package cafe.adriel.cryp.view.wallet.select

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import cafe.adriel.cryp.R
import cafe.adriel.cryp.SelectedCryptoEvent
import cafe.adriel.cryp.model.entity.Crypto
import cafe.adriel.cryp.model.repository.CryptoRepository
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.kbus.KBus
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import com.kennyc.view.MultiStateView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_crypto.*


class SelectCryptoActivity : BaseActivity() {

    private val adapter = FastItemAdapter<CryptoAdapterItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_crypto)

        setSupportActionBar(vToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.choose_one)
        }

        adapter.setHasStableIds(true)
        adapter.withOnClickListener({ v, adapter, item, position ->
            val crypto = adapter.getAdapterItem(position).crypto
            selectCrypto(crypto)
            true
        })
        adapter.itemFilter.withFilterPredicate { item, constraint ->
            item.crypto.symbol.startsWith(constraint!!, true) ||
                    item.crypto.name.startsWith(constraint, true)
        }
        adapter.itemFilter.withItemFilterListener(object : ItemFilterListener<CryptoAdapterItem>{
            override fun itemsFiltered(constraint: CharSequence?, results: MutableList<CryptoAdapterItem>?) {
                updateState()
            }
            override fun onReset() {
                updateState()
            }
        })

        vCryptos.setHasFixedSize(true)
        vCryptos.layoutManager = LinearLayoutManager(this)
        vCryptos.adapter = adapter
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        CryptoRepository.getAll()
            .flatMapIterable { it }
            .sorted { o1, o2 -> o1.symbol.compareTo(o2.symbol) }
            .map { CryptoAdapterItem(it) }
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                adapter.add(it)
                updateState()
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryTextChanges()
            .skipInitialValue()
            .subscribe { adapter.filter(it) }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun selectCrypto(crypto: Crypto){
        KBus.post(SelectedCryptoEvent(crypto))
        finish()
    }

    private fun updateState(){
        vState.viewState = if(adapter.adapterItemCount == 0) {
            MultiStateView.VIEW_STATE_EMPTY
        } else {
            MultiStateView.VIEW_STATE_CONTENT
        }
    }

}