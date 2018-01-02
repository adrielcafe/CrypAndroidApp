package cafe.adriel.cryp.view.custom

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import cafe.adriel.cryp.px

class SeparatorDecoration(val height: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // First item
        if(itemPosition == 0) {
            outRect.set(0, height.px, 0, 0)
        // Last item
        } else if(itemCount > 0 && itemPosition == itemCount - 1){
            outRect.set(0, 0, 0, height.px)
        }
    }

}