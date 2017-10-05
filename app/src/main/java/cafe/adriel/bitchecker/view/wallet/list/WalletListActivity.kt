package cafe.adriel.bitchecker.view.wallet.list

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.model.entity.Coin
import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.repository.WalletRepository
import cafe.adriel.bitchecker.view.BaseActivity
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeDragCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_wallet_list.*
import java.util.*

class WalletListActivity : BaseActivity(), ItemTouchCallback {

    private val adapter = FastItemAdapter<WalletAdapterItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_list)
        setSupportActionBar(vToolbar)

        ItemTouchHelper(SimpleDragCallback(this))
                .attachToRecyclerView(vWallets)
        vWallets.setHasFixedSize(true)
        vWallets.layoutManager = LinearLayoutManager(this)
        vWallets.adapter = adapter

        vRefresh.setOnRefreshListener {
            refresh()
        }

        vAddWallet.setOnClickListener {
            // TODO Temp
            val wallet1 = Wallet(Coin.BTC, "14bSgbk7n59DZ8xK6zZcg6MDQR8c9tAoJj", "My BTC")
            val wallet2 = Wallet(Coin.LTC, "LYaCZujLf2Nnt4zQhUoXJfSAT2ffu8DiWA", "My LTC")
            val wallet4 = Wallet(Coin.ETH, "0x922847B8781FfbeFbADcC4BE34475521b2990647", "My ETH")
            val wallet3 = Wallet(Coin.DASH, "XjyLDq93Q8tr97fkP7REVBZ1DLdztzHgcB", "My DASH")
            val wallet5 = Wallet(Coin.DOGE, "DFRSs3NYxn6urg7BVjDzW9PVFbBTRoESTz", "My DOGE")
            WalletRepository.add(wallet1)
            WalletRepository.add(wallet2)
            WalletRepository.add(wallet3)
            WalletRepository.add(wallet4)
            WalletRepository.add(wallet5)
            refresh()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_wallet_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO
        R.id.action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        Collections.swap(adapter.adapterItems, oldPosition, newPosition)
        adapter.notifyAdapterItemMoved(oldPosition, newPosition)
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        // TODO save the new order
    }

    private fun refresh(){
        vRefresh.isRefreshing = true
        WalletRepository.getAll()
                // TODO don't need to update the wallets on pre-alpha phase
//                .map { WalletRepository.updateAll(it) }
//                .flatMap { it.toObservable() }
                .flatMapIterable { it }
                .map { WalletAdapterItem(it) }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.clear()
                    adapter.add(it)
                    vRefresh.isRefreshing = false
                }, {
                    it.printStackTrace()
                    vRefresh.isRefreshing = false
                })
    }
}
