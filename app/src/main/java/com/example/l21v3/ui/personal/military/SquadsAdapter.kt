import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.l21v3.databinding.ItemSquadBinding
import com.example.l21v3.model.Squad

class SquadsAdapter(
    private val onSquadClick: (Squad) -> Unit,
    private val showAddSubunitDialog: (Squad) -> Unit // Добавьте этот параметр
) : ListAdapter<Squad, SquadsAdapter.SquadViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadViewHolder {
        val binding = ItemSquadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SquadViewHolder(binding, onSquadClick, showAddSubunitDialog) // Передайте функцию
    }

    override fun onBindViewHolder(holder: SquadViewHolder, position: Int) {
        val squad = getItem(position)
        holder.bind(squad)
    }

    class SquadViewHolder(
        private val binding: ItemSquadBinding,
        private val onSquadClick: (Squad) -> Unit,
        private val showAddSubunitDialog: (Squad) -> Unit // Добавьте этот параметр
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(squad: Squad) {
            with(binding) {
                squadName.text = squad.name
                squadSize.text = "Численность: ${squad.currentSize}/${squad.maxSize}"

                root.setOnClickListener {
                    onSquadClick(squad)
                }
                addSubunitButton.setOnClickListener {
                    showAddSubunitDialog(squad) // Вызовите функцию
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Squad>() {
            override fun areItemsTheSame(oldItem: Squad, newItem: Squad) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Squad, newItem: Squad) = oldItem == newItem
        }
    }
}