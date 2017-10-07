package cafe.adriel.bitchecker.view.wallet.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.model.entity.Coin
import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.repository.WalletRepository
import cafe.adriel.bitchecker.view.BaseActivity
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback
import com.tubb.smrv.SwipeHorizontalMenuLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wallet_list.*
import kotlinx.android.synthetic.main.list_item_wallet.view.*
import java.util.*

class WalletListActivity : BaseActivity(), ItemTouchCallback {

    private val adapter = FastItemAdapter<WalletAdapterItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_list)
        setSupportActionBar(vToolbar)

        adapter.withEventHook(object: ClickEventHook<WalletAdapterItem>(){
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                    mutableListOf<View>(
                        viewHolder.itemView.vSeeAddress,
                        viewHolder.itemView.vEditName,
                        viewHolder.itemView.vDelete
                    )
            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<WalletAdapterItem>?, item: WalletAdapterItem?) {
                when(v?.id){
                    R.id.vSeeAddress -> Log.e("CLICK", "SEE ADDRESS")
                    R.id.vEditName -> Log.e("CLICK", "EDIT NAME")
                    R.id.vDelete -> Log.e("CLICK", "DELETE")
                }
                v?.parent?.parent?.let {
                    if(it is SwipeHorizontalMenuLayout){
                        closeSwipeMenu(it)
                    }
                }
            }
        })

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
            val wallet2 = Wallet(Coin.LTC, "LYaCZujLf2Nnt4zQhUoXJfSAT2ffu8DiWA", "", 123)
            val wallet3 = Wallet(Coin.ETH, "0x922847B8781FfbeFbADcC4BE34475521b2990647", "My ETH", 12345678)
            val wallet4 = Wallet(Coin.DASH, "XjyLDq93Q8tr97fkP7REVBZ1DLdztzHgcB", "My DASH", 123456789)
            val wallet5 = Wallet(Coin.DOGE, "DFRSs3NYxn6urg7BVjDzW9PVFbBTRoESTz", "My DOGE", 123456789123)
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
                    closeAllSwipeMenus()
                    vRefresh.isRefreshing = false
                }, {
                    it.printStackTrace()
                    closeAllSwipeMenus()
                    vRefresh.isRefreshing = false
                })
    }

    private fun closeAllSwipeMenus(){
        (0 until adapter.adapterItemCount)
                .map { vWallets.findViewHolderForLayoutPosition(it) }
                .forEach {
                    it?.itemView?.vSwipeMenu?.let {
                        closeSwipeMenu(it)
                    }
                }
    }

    private fun closeSwipeMenu(v: SwipeHorizontalMenuLayout){
        try {
            v.smoothCloseMenu()
        } catch (e: Exception){ }
    }
}
