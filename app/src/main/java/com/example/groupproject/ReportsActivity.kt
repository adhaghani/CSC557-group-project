package com.example.groupproject

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.groupproject.data.CountResult
import com.example.groupproject.databinding.ActivityReportsBinding
import com.example.groupproject.viewmodel.FriendViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private lateinit var viewModel: FriendViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[FriendViewModel::class.java]

        viewModel.genderCounts.observe(this) { counts ->
            setupGenderChart(counts)
        }

        viewModel.stateCounts.observe(this) { counts ->
            setupStateChart(counts)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupGenderChart(counts: List<CountResult>) {
        if (counts.isEmpty()) {
            binding.chartGender.clear()
            binding.chartGender.setNoDataText("No data available")
            return
        }

        val entries = counts.map { result ->
            PieEntry(result.count.toFloat(), result.name)
        }

        val dataSet = PieDataSet(entries, "Gender Distribution").apply {
            colors = listOf(
                Color.rgb(54, 162, 235),
                Color.rgb(255, 99, 132)
            )
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            valueFormatter = PercentFormatter(binding.chartGender)
        }

        val pieData = PieData(dataSet)

        binding.chartGender.apply {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 40f
            setUsePercentValues(true)
            legend.textSize = 14f
            animateY(800)
            invalidate()
        }
    }

    private fun setupStateChart(counts: List<CountResult>) {
        if (counts.isEmpty()) {
            binding.chartState.clear()
            binding.chartState.setNoDataText("No data available")
            return
        }

        val entries = counts.mapIndexed { index, result ->
            BarEntry(index.toFloat(), result.count.toFloat())
        }
        val labels = counts.map { it.name }

        val dataSet = BarDataSet(entries, "Friends by State").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.7f
        }

        binding.chartState.apply {
            data = barData
            description.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textSize = 12f
            }
            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false
            animateY(800)
            invalidate()
        }
    }
}
