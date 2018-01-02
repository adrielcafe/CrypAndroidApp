package cafe.adriel.cryp.view.wallet.list

import android.os.Bundle
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.CoinFormat
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.cryp.view.custom.SeparatorDecoration
import cafe.adriel.cryp.view.qrcode.show.ShowQrCodeActivity
import cafe.adriel.cryp.view.wallet.add.AddWalletActivity
import cafe.adriel.kbus.KBus
import com.arellomobile.mvp.presenter.InjectPresenter
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog
import com.kennyc.view.MultiStateView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback
import com.tubb.smrv.SwipeHorizontalMenuLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wallet_list.*
import kotlinx.android.synthetic.main.list_item_wallet.view.*
import java.math.BigDecimal
import java.util.*

class WalletListActivity : BaseActivity(), WalletListView, ItemTouchCallback {
    @InjectPresenter
    lateinit var presenter: WalletListPresenter

    private val adapter = FastItemAdapter<WalletAdapterItem>()
    private var currentTotalBalance = BigDecimal.ZERO
    private var currentOpenedMenuPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_list)
        setSupportActionBar(vToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        vRefresh.setOnRefreshListener { refresh() }
        vAddWallet.setOnClickListener { showAddWalletActivity() }

        adapter.setHasStableIds(true)
        adapter.withEventHook(object: ClickEventHook<WalletAdapterItem>(){
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                    listOf<View>(
                        viewHolder.itemView.vSeeAddress,
                        viewHolder.itemView.vDelete
                    )
            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<WalletAdapterItem>?, item: WalletAdapterItem?) {
                v?.parent?.parent?.parent?.let {
                    if(it is SwipeHorizontalMenuLayout){
                        closeSwipeMenu(it)
                        it.postDelayed({
                            item?.wallet?.let {
                                when(v.id){
                                    R.id.vSeeAddress -> showPublicKey(it)
                                    R.id.vDelete -> showDeleteDialog(it)
                                }
                            }
                        }, 100)
                    }
                }
            }
        })

        ItemTouchHelper(SimpleDragCallback(this))
                .attachToRecyclerView(vWallets)
        vWallets.setHasFixedSize(true)
        vWallets.addItemDecoration(SeparatorDecoration(10))
        vWallets.layoutManager = LinearLayoutManager(this)
        vWallets.adapter = adapter
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        KBus.subscribe<OpenedSwipeMenuEvent>(this, {
            currentOpenedMenuPosition = adapter.getPosition(it.itemId)
            closeSwipeMenus(false)
        })
        KBus.subscribe<RefreshWalletListEvent>(this, {
            refresh()
        })
    }

    override fun onResume() {
        super.onResume()
        refresh()
        // Re-enable add wallet button
        vAddWallet.isEnabled = true
        // Fix missing logo of selected item
        if(currentOpenedMenuPosition >= 0){
            adapter.notifyAdapterItemChanged(currentOpenedMenuPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        KBus.unsubscribe(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_wallet_list, menu)

        val vCoinFormats = menu.findItem(R.id.action_coin_formats).actionView as AppCompatSpinner
        initCoinFormatSpinner(vCoinFormats)

        return true
    }

    // TODO temp
//    override fun onOptionsItemSelected(item: MenuItem?) =
//            when (item.itemId) {
//                R.id.action_settings -> {
//                    showSettingsActivity()
//                    true
//                }
//                else -> super.onOptionsItemSelected(item)
//            }

    override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
        Collections.swap(adapter.adapterItems, oldPosition, newPosition)
        adapter.notifyAdapterItemMoved(oldPosition, newPosition)
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        val walletIds = mutableListOf<String>()
        (0 until adapter.adapterItemCount).forEach {
            val holder = adapter.getItem(it)
            walletIds.add(holder.wallet.id)
        }
        presenter.saveOrder(walletIds)
    }

    override fun addAll(wallets: List<Wallet>) {
        wallets.toObservable()
                .map { WalletAdapterItem(it) }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.clear()
                    if(it.isNotEmpty()) {
                        adapter.add(it)
                    }
                    updateState()
                    updateTotalBalance()
                    closeSwipeMenus(true)
                    setRefreshing(false)
                }, {
                    it.printStackTrace()
                })
    }

    override fun addOrUpdate(wallet: Wallet) {
        val position = getItemPosition(wallet)
        if(position < 0) {
            if(presenter.exists(wallet)) {
                adapter.add(WalletAdapterItem(wallet))
            }
        } else {
            adapter.getAdapterItem(position).wallet = wallet
            adapter.notifyAdapterItemChanged(position)
        }
    }

    override fun remove(wallet: Wallet) {
        val position = getItemPosition(wallet)
        if(position >= 0) {
            adapter.remove(position)
        }
        updateState()
        updateTotalBalance()
    }

    private fun initCoinFormatSpinner(vSpinner: AppCompatSpinner){
        val currentCoinFormat = presenter.getCoinFormat().fullName
        val currentPosition = CoinFormat.values().indexOf(CoinFormat.getByName(currentCoinFormat))
        val formatsAdapter = ArrayAdapter(this, R.layout.spinner_item_coin_format, CoinFormat.values())
        formatsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_coin_format)
        vSpinner.adapter = formatsAdapter
        vSpinner.setSelection(currentPosition)
        vSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCoinFormat = CoinFormat.values()[position]
                presenter.saveCoinFormat(selectedCoinFormat)
                adapter.notifyAdapterDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun showAddWalletActivity() {
        vAddWallet.isEnabled = false
        startActivity<AddWalletActivity>()
    }

    private fun showSettingsActivity() {
        // TODO
    }

    private fun showPublicKey(wallet: Wallet) {
        startActivity<ShowQrCodeActivity>(
                Const.EXTRA_WALLET to wallet)
    }

    private fun showDeleteDialog(wallet: Wallet) {
        AwesomeSuccessDialog(this)
                .setTitle(wallet.coin.toString())
                .setMessage(R.string.are_you_sure_remove_wallet)
                .setColoredCircle(R.color.red)
                .setDialogIconAndColor(R.drawable.ic_delete, R.color.white)
                .setCancelable(true)
                .setNegativeButtonText(getString(R.string.nevermind))
                .setNegativeButtonTextColor(android.R.color.black)
                .setNegativeButtonbackgroundColor(android.R.color.white)
                .setNegativeButtonClick {  }
                .setPositiveButtonText(getString(R.string.yes_please))
                .setPositiveButtonbackgroundColor(R.color.red)
                .setPositiveButtonClick { presenter.delete(wallet) }
                .show()
    }

    private fun refresh() {
        if(isConnected()) {
            setRefreshing(true)
            presenter.loadAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.forEach { addOrUpdate(it) }
                        updateState()
                        updateTotalBalance()
                        setRefreshing(false)
                    }, {
                        it.printStackTrace()
                        closeSwipeMenus(true)
                        setRefreshing(false)
                    })
        } else {
            setRefreshing(false)
            showMessage(R.string.connect_internet, MessageType.INFO)
        }
    }

    private fun setRefreshing(refreshing: Boolean) {
        vRefresh.isRefreshing = refreshing
    }

    private fun updateState(){
        vState.viewState = if(adapter.adapterItemCount == 0) {
            MultiStateView.VIEW_STATE_EMPTY
        } else {
            MultiStateView.VIEW_STATE_CONTENT
        }
    }

    private fun updateTotalBalance() {
        var totalBalance = BigDecimal.ZERO
        adapter.adapterItems.forEach {
            totalBalance += it.wallet.getBalanceCurrency()
        }
        if(totalBalance < BigDecimal.ZERO){
            totalBalance = BigDecimal.ZERO
        }
        vTotalBalance.postDelayed({
            vTotalBalance.setDecimalFormat(currencyFormat)
                    .startAnimation(currentTotalBalance.toFloat(), totalBalance.toFloat())
            currentTotalBalance = totalBalance
        }, 500)
    }

    private fun closeSwipeMenus(closeCurrentOpenedMenu: Boolean){
        (0 until adapter.adapterItemCount)
            .map { vWallets.findViewHolderForAdapterPosition(it) }
            .forEachIndexed { index, holder ->
                holder?.itemView?.vSwipeMenu?.let {
                    if((index == currentOpenedMenuPosition && closeCurrentOpenedMenu)
                            || index != currentOpenedMenuPosition)
                    closeSwipeMenu(it)
                }
            }
    }

    private fun closeSwipeMenu(vMenu: SwipeHorizontalMenuLayout){
        try {
            vMenu.smoothCloseMenu()
        } catch (e: Exception){ }
    }

    private fun getItemPosition(wallet: Wallet): Int {
        (0 until adapter.adapterItemCount).forEach {
            val holder = adapter.getItem(it)
            if (holder.wallet.id == wallet.id){
                return it
            }
        }
        return -1
    }
}