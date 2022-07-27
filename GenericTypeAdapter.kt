import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

fun <T, V : View> RecyclerView.bindAdapter(
    list: List<T>,
    key: (Int) -> Int,
    create: (Int) -> V,
    onBindOnce: ((Int) -> Unit)? = null,
    update: (V, Int) -> Unit
) {
    adapter = GenericTypeAdapter(list, key, create, update, onBindOnce)
}

private class GenericTypeAdapter<T, V : View>(
    private val list: List<T>,
    private val key: (Int) -> Int,
    private val create: (Int) -> V,
    private val bind: (V, Int) -> Unit,
    private val onBindOnce: ((Int) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val alreadyBindOnce = mutableSetOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GenericSingleViewHolder(create.invoke(viewType))

    override fun getItemViewType(position: Int): Int = key(position)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(holder.itemView as V, position)
        if (!alreadyBindOnce.contains(key(position))) {
            onBindOnce?.let { it(position) }
        } else
            alreadyBindOnce.add(key(position))
    }

    override fun getItemCount(): Int = list.count()
    inner class GenericSingleViewHolder<V : View>(view: V) : RecyclerView.ViewHolder(view)
}
