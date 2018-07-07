package cafe.adriel.cryp.view.wallet.list

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.cryp.view.custom.RevealAnimationHelper
import cafe.adriel.cryp.view.custom.VerticalSeparatorDecoration
import cafe.adriel.cryp.view.settings.SettingsActivity
import cafe.adriel.cryp.view.wallet.add.AddWalletActivity
import cafe.adriel.cryp.view.wallet.show.ShowWalletActivity
import cafe.adriel.kbus.KBus
import com.arellomobile.mvp.presenter.InjectPresenter
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog
import com.kennyc.view.MultiStateView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback
import com.mikepenz.fastadapter_extensions.utilities.DragDropUtil
import com.tubb.smrv.SwipeHorizontalMenuLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wallet_list.*
import kotlinx.android.synthetic.main.list_item_wallet.view.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class WalletListActivity : BaseActivity(), WalletListView, ItemTouchCallback {
    @InjectPresenter
    lateinit var presenter: WalletListPresenter

    private val adapter = FastItemAdapter<WalletAdapterItem>()
    private var currentTotalBalance = BigDecimal.ZERO
    private var currentTotalBalanceCurrency = 0
    private var currentOpenedMenuPosition = -1
    private var backPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_list)
        setSupportActionBar(vToolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        vRefresh.setOnRefreshListener { refresh() }
        vAddWallet.setOnClickListener { showAddWalletActivity() }
        vTotalBalance.setOnClickListener { toggleTotalBalanceCurrency() }
        vAppBarLayout.addOnOffsetChangedListener { _, offset -> toggleTotalBalanceToolbar(offset) }

        adapter.setHasStableIds(true)
        adapter.withEventHook(object: ClickEventHook<WalletAdapterItem>(){
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder) =
                    listOf<View>(
                        viewHolder.itemView.vSeePublicKey,
                        viewHolder.itemView.vEdit,
                        viewHolder.itemView.vDelete
                    )
            override fun onClick(v: View?, position: Int, fastAdapter: FastAdapter<WalletAdapterItem>?, item: WalletAdapterItem?) {
                v?.parent?.parent?.parent?.let {
                    if(it is SwipeHorizontalMenuLayout){
                        closeSwipeMenu(it)
                        it.postDelayed({
                            item?.wallet?.let {
                                when(v.id){
                                    R.id.vSeePublicKey -> showWalletActivity(it)
                                    R.id.vEdit -> showEditActivity(it)
                                    R.id.vDelete -> showDeleteDialog(it)
                                }
                            }
                        }, 100)
                    }
                }
            }
        })
        adapter.withOnLongClickListener { v, adapter, item, position ->
            // Disable SwipeRefreshLayout to allow drag down
            vRefresh.isEnabled = false
            false
        }

        ItemTouchHelper(SimpleDragCallback(this))
                .attachToRecyclerView(vWallets)
        vWallets.setHasFixedSize(true)
        vWallets.addItemDecoration(VerticalSeparatorDecoration(10))
        vWallets.itemAnimator.changeDuration = 0
        vWallets.layoutManager = LinearLayoutManager(this)
        vWallets.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        refresh()
        // Re-enable Add Wallet button
        vAddWallet.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        KBus.subscribe<SwipeMenuOpenedEvent>(this) {
            currentOpenedMenuPosition = adapter.getPosition(it.itemId)
            closeSwipeMenus(false)
        }
    }

    override fun onStop() {
        super.onStop()
        KBus.unsubscribe(this)
    }

    override fun onBackPressed() {
        if(backPressed) {
            super.onBackPressed()
        } else {
            backPressed = true
            Snackbar.make(vCoordinator, R.string.press_back_exit, Snackbar.LENGTH_SHORT).show()
            Observable.just(Unit)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe { backPressed = false }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
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
        vRefresh.isEnabled = false
        DragDropUtil.onMove(adapter.itemAdapter, oldPosition, newPosition)
        return true
    }

    override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
        val walletIds = adapter.adapterItems.map { it.wallet.id }
        presenter.saveOrder(walletIds)
        vRefresh.isEnabled = true
    }

    override fun remove(wallet: Wallet) {
        val position = getItemPosition(wallet)
        if(position >= 0) {
            adapter.remove(position)
        }
        updateState()
        updateTotalBalance()
    }

    private fun showWalletActivity(wallet: Wallet) {
        if(wallet.publicKey.isNotEmpty()) {
            start<ShowWalletActivity>(Const.EXTRA_WALLET to wallet)
        } else {
            showMessage(R.string.wallet_has_no_public_key, MessageType.INFO)
        }
    }

    private fun showAddWalletActivity() {
        if(presenter.hasWalletSlotRemaining()) {
            // Disable button to avoid be clicked multiple times
            vAddWallet.isEnabled = false
            val revealSettings = RevealAnimationHelper.AnimationSettings(
                (vAddWallet.x + vAddWallet.width / 2).toInt(),
                (vAddWallet.y + vAddWallet.height / 2).toInt(),
                vCoordinator.width,
                vCoordinator.height,
                R.color.colorAccent
            )
            start<AddWalletActivity>(Const.EXTRA_REVEAL_SETTINGS to revealSettings)
        } else {
            showMessage(R.string.you_can_track_ten_wallets, MessageType.INFO)
        }
    }

    private fun showEditActivity(wallet: Wallet) {
        start<AddWalletActivity>(Const.EXTRA_WALLET to wallet)
    }

    private fun showSettingsActivity() {
        start<SettingsActivity>()
    }

    private fun showDeleteDialog(wallet: Wallet) {
        AwesomeSuccessDialog(this)
                .setTitle(wallet.crypto.fullName)
                .setMessage(R.string.are_you_sure_remove_wallet)
                .setColoredCircle(R.color.red)
                .setDialogIconAndColor(R.drawable.ic_delete, R.color.white)
                .setCancelable(true)
                .setNegativeButtonText(getString(R.string.never_mind))
                .setNegativeButtonTextColor(android.R.color.black)
                .setNegativeButtonbackgroundColor(android.R.color.white)
                .setNegativeButtonClick {  }
                .setPositiveButtonText(getString(R.string.yes_please))
                .setPositiveButtonbackgroundColor(R.color.red)
                .setPositiveButtonClick { presenter.delete(wallet) }
                .show()
    }

    private fun refresh() {
        addAll(presenter.loadWallets())
        if(isConnected()) {
            setContentRefreshing(true)
            setTotalBalanceRefreshing(true)
            presenter.updatePrices()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        adapter.notifyAdapterDataSetChanged()
                        vRefresh.isEnabled = true
                        updateState()
                        updateTotalBalance()
                        setContentRefreshing(false)
                        setTotalBalanceRefreshing(false)
                    }, {
                        it.printStackTrace()
                        vRefresh.isEnabled = true
                        closeSwipeMenus(true)
                        setContentRefreshing(false)
                        setTotalBalanceRefreshing(false)
                    })
        } else {
            vRefresh.isEnabled = true
            setContentRefreshing(false)
            setTotalBalanceRefreshing(false)
            showMessage(R.string.connect_internet, MessageType.INFO)
        }
    }

    private fun addAll(wallets: List<Wallet>) {
        wallets.map { WalletAdapterItem(it) }
            .let {
                adapter.clear()
                adapter.add(it)
                updateState()
                updateTotalBalance()
                closeSwipeMenus(true)
                setContentRefreshing(false)
            }
    }

    private fun setContentRefreshing(refreshing: Boolean) {
        vRefresh.isRefreshing = refreshing
    }

    private fun setTotalBalanceRefreshing(refreshing: Boolean) {
        if(refreshing){
            vTotalBalanceProgress.show()
        } else {
            vTotalBalanceProgress.hide()
        }
    }

    private fun updateState(){
        vState.viewState = if(adapter.adapterItemCount == 0) {
            MultiStateView.VIEW_STATE_EMPTY
        } else {
            MultiStateView.VIEW_STATE_CONTENT
        }
        vAddWallet.show()
    }

    private fun updateTotalBalance() {
        var currencySymbol = PreferenceRepository.getCurrency().symbol
        var decimalFormat = getCurrencyFormat()
        var totalBalance = BigDecimal.ZERO
        adapter.adapterItems.forEach {
            var balance = when(currentTotalBalanceCurrency){
                1 -> { // BTC
                    it.wallet.getBalanceBtc()
                }
                2 -> { // ETH
                    it.wallet.getBalanceEth()
                }
                else -> { // Fiat
                    it.wallet.getBalanceCurrency()
                }
            }
            if(balance < BigDecimal.ZERO) balance = BigDecimal.ZERO
            totalBalance += balance
        }
        when(currentTotalBalanceCurrency){
            1 -> { // BTC
                currencySymbol = Const.SYMBOL_BTC
                decimalFormat = getCryptoFormat()
            }
            2 -> { // ETH
                currencySymbol = Const.SYMBOL_ETH
                decimalFormat = getCryptoFormat()
            }
        }
        if(totalBalance < BigDecimal.ZERO) totalBalance = BigDecimal.ZERO
        vTotalBalanceToolbar.text = "$currencySymbol ${decimalFormat.format(totalBalance)}"
        vTotalBalance
            .setPrefix("$currencySymbol ")
            .setDecimalFormat(decimalFormat)
            .startAnimation(currentTotalBalance.toFloat(), totalBalance.toFloat())
        currentTotalBalance = totalBalance
    }

    private fun toggleTotalBalanceCurrency(){
        currentTotalBalanceCurrency = when(currentTotalBalanceCurrency){
            1 -> 2 // BTC -> ETH
            2 -> 0 // ETH -> Fiat
            else -> 1 // Fiat -> BTC
        }
        updateTotalBalance()
    }

    private fun toggleTotalBalanceToolbar(offset : Int){
        val collapsed = Math.abs(offset) == vAppBarLayout.totalScrollRange
        vTotalBalanceToolbar.animate()
            .alpha(if(collapsed) 1F else 0F)
            .setDuration(250)
            .start()
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
        adapter.adapterItems.forEachIndexed { index, item ->
            if (item.wallet.id == wallet.id){
                return index
            }
        }
        return -1
    }
}