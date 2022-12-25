package com.aziza.spacexrocketlaunchers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aziza.shared.entity.RocketLaunch
import com.aziza.spacexrocketlaunchers.databinding.ItemLaunchBinding

class LaunchesRvAdapter(var launches: List<RocketLaunch>) : RecyclerView.Adapter<LaunchesRvAdapter.LaunchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchViewHolder {

        return ItemLaunchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            .run(::LaunchViewHolder)
    }

    override fun getItemCount(): Int = launches.count()

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        holder.bindData(launches[position])
    }

    inner class LaunchViewHolder(private val binding: ItemLaunchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(launch: RocketLaunch) {
            val ctx = itemView.context
           binding.missionName.text = ctx.getString(R.string.mission_name_field, launch.missionName)
            binding.launchYear.text = ctx.getString(R.string.launch_year_field, launch.launchYear.toString())
            binding.details.text = ctx.getString(R.string.details_field, launch.details ?: "")
            val launchSuccess = launch.launchSuccess
            if (launchSuccess != null ) {
                if (launchSuccess) {
                    binding.launchSuccess.text = ctx.getString(R.string.successful)
                    binding.launchSuccess.setTextColor((ContextCompat.getColor(itemView.context, R.color.colorSuccessful)))
                } else {
                    binding.launchSuccess.text = ctx.getString(R.string.unsuccessful)
                    binding.launchSuccess.setTextColor((ContextCompat.getColor(itemView.context, R.color.colorUnsuccessful)))
                }
            } else {
                binding.launchSuccess.text = ctx.getString(R.string.no_data)
                binding.launchSuccess.setTextColor((ContextCompat.getColor(itemView.context, R.color.colorNoData)))
            }
        }
    }
}