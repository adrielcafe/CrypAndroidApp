package cafe.adriel.bitchecker.view.wallet.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.model.entity.Coin
import cafe.adriel.bitchecker.model.entity.MessageType
import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.repository.WalletRepository
import cafe.adriel.bitchecker.view.BaseActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.github.ajalt.timberkt.e
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

class WalletListActivity : BaseActivity(), WalletListView, ItemTouchCallback {
    @InjectPresenter
    lateinit var presenter: WalletListPresenter

    private val adapter = FastItemAdapter<WalletAdapterItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_list)
        setSupportActionBar(vToolbar)

        vRefresh.setOnRefreshListener { refresh() }
        vAddWallet.setOnClickListener { showAddActivity() }

        adapter.setHasStableIds(true)
        adapter.withEventHook(object: ClickEventHook<WalletAdapterItem>(){
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                    listOf<View>(
                        viewHolder.itemView.vSeeAddress,
                        viewHolder.itemView.vEditName,
                        viewHolder.itemView.vDelete
                    )
            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<WalletAdapterItem>?, item: WalletAdapterItem?) {
                item?.wallet?.let {
                    when(v?.id){
                        R.id.vSeeAddress -> showAddressDialog(it)
                        R.id.vEditName -> showEditDialog(it)
                        R.id.vDelete -> showDeleteDialog(it)
                    }
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
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_wallet_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.action_settings -> {
                    showSettingsActivity()
                    true
                }
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

    override fun showAddActivity() {
        // TODO Temp
        val wallet1 = Wallet(Coin.BTC, "14bSgbk7n59DZ8xK6zZcg6MDQR8c9tAoJj", "My BTC")
        val wallet2 = Wallet(Coin.LTC, "LYaCZujLf2Nnt4zQhUoXJfSAT2ffu8DiWA", "", 123)
        val wallet3 = Wallet(Coin.ETH, "0x922847B8781FfbeFbADcC4BE34475521b2990647", "My ETH", 12345678)
        val wallet4 = Wallet(Coin.DASH, "XjyLDq93Q8tr97fkP7REVBZ1DLdztzHgcB", "My DASH", 123456789)
        val wallet5 = Wallet(Coin.DOGE, "DFRSs3NYxn6urg7BVjDzW9PVFbBTRoESTz", "My DOGE", 123456789123)
        WalletRepository.addOrUpdate(wallet1)
        WalletRepository.addOrUpdate(wallet2)
        WalletRepository.addOrUpdate(wallet3)
        WalletRepository.addOrUpdate(wallet4)
        WalletRepository.addOrUpdate(wallet5)
        refresh()
    }

    override fun showSettingsActivity() {
        // TODO
    }

    override fun showAddressDialog(wallet: Wallet) {
        // TODO
    }

    override fun showEditDialog(wallet: Wallet) {
        // TODO
    }

    override fun showDeleteDialog(wallet: Wallet) {
        // TODO
    }

    override fun addOrUpdate(wallet: Wallet) {
        val position = getItemPosition(wallet)
        if(position < 0) {
            adapter.add(WalletAdapterItem(wallet))
        } else {
            adapter.set(position, WalletAdapterItem(wallet))
        }
    }

    override fun remove(wallet: Wallet) {
        val position = getItemPosition(wallet)
        if(position >= 0) {
            adapter.remove(position)
        }
    }

    override fun refresh() {
        setRefreshing(true)
        presenter.loadAll()
                .flatMapIterable { it }
                .map { WalletAdapterItem(it) }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.clear()
                    adapter.add(it)
                    closeAllSwipeMenus()
                    setRefreshing(false)
                }, {
                    e(it)
                    showMessage(it.localizedMessage, MessageType.ERROR)
                    closeAllSwipeMenus()
                    setRefreshing(false)
                })
    }

    override fun setRefreshing(refreshing: Boolean) {
        vRefresh.isRefreshing = refreshing
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

    private fun getItemPosition(wallet: Wallet): Int {
        (0 until adapter.adapterItemCount)
                .forEach {
                    val holder = adapter.getItem(it)
                    if (holder.wallet.id == wallet.id){
                        return it
                    }
                }
        return -1
    }
}
