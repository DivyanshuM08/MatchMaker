package com.example.matchmaker.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.matchmaker.R
import com.example.matchmaker.data.db.ProfileStatus
import com.example.matchmaker.databinding.ItemMatchCardBinding

class MatchAdapter(
    private val onAccept: (MatchCardItem) -> Unit,
    private val onDecline: (MatchCardItem) -> Unit
) : ListAdapter<MatchCardItem, MatchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatchCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onAccept, onDecline)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemMatchCardBinding,
        private val onAccept: (MatchCardItem) -> Unit,
        private val onDecline: (MatchCardItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchCardItem) {
            val e = item.entity
            val ctx = binding.root.context
            binding.textName.text = e.name
            binding.textAgeCity.text = ctx.getString(R.string.age_city_format, e.age, e.city)
            binding.textMatchScore.text = ctx.getString(R.string.match_score_format, item.matchScore)
            binding.textEducationReligion.text = ctx.getString(R.string.education_religion_format, e.education, e.religion)
            binding.textStatus.text = when (e.status) {
                ProfileStatus.PENDING -> ctx.getString(R.string.status_pending)
                ProfileStatus.ACCEPTED -> ctx.getString(R.string.status_accepted)
                ProfileStatus.DECLINED -> ctx.getString(R.string.status_declined)
            }
            binding.buttonAccept.setOnClickListener { onAccept(item) }
            binding.buttonDecline.setOnClickListener { onDecline(item) }
            binding.buttonAccept.isEnabled = e.status == ProfileStatus.PENDING
            binding.buttonDecline.isEnabled = e.status == ProfileStatus.PENDING

            Glide.with(binding.root.context)
                .load(e.imageUrl)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(binding.imageAvatar)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<MatchCardItem>() {
        override fun areItemsTheSame(a: MatchCardItem, b: MatchCardItem) = a.entity.id == b.entity.id
        override fun areContentsTheSame(a: MatchCardItem, b: MatchCardItem) = a == b
    }
}
