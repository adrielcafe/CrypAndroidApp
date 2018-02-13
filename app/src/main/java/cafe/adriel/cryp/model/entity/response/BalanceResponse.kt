package cafe.adriel.cryp.model.entity.response

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal

@SuppressLint("ParcelCreator")
data class BalanceResponse(
        val balance: BigDecimal = BigDecimal.ONE.negate()) : AutoParcelable