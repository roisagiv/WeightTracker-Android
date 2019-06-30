package io.roisagiv.github.weighttracker.add

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.prolificinteractive.materialcalendarview.CalendarDay
import io.roisagiv.github.weighttracker.R
import kotlinx.android.synthetic.main.activity_add_weight.*
import kotlinx.android.synthetic.main.content_add_weight.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.OffsetDateTime

class AddWeightActivity : AppCompatActivity() {

    private val viewModel by viewModel<AddWeightViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weight)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        calendar_date.selectedDate = CalendarDay.today()

        button_save.setOnClickListener {
            viewModel.add(
                OffsetDateTime.now(),
                edittext_weight.text.toString().toDouble(),
                null
            )
        }

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                AddWeightViewModel.ViewState.Idle -> {
                    button_save.isEnabled = true
                    button_save.text = getString(R.string.button_save)
                }
                AddWeightViewModel.ViewState.Loading -> {
                    button_save.isEnabled = false
                    button_save.text = getString(R.string.button_loading)
                }
                is AddWeightViewModel.ViewState.Error -> TODO()
                AddWeightViewModel.ViewState.Success -> finish()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
