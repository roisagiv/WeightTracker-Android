package io.roisagiv.github.weighttracker.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.roisagiv.github.weighttracker.R
import io.roisagiv.github.weighttracker.add.AddWeightActivity
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.content_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * HistoryActivity.
 */
class HistoryActivity : AppCompatActivity() {

    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: HistoryRecyclerAdapter? = null
    private val viewModel by viewModel<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)

        linearLayoutManager = LinearLayoutManager(this)
        recycler_history_items.layoutManager = linearLayoutManager
        adapter = HistoryRecyclerAdapter()
        recycler_history_items.adapter = adapter

        fab.setOnClickListener {
            val intent = Intent(this, AddWeightActivity::class.java)
            startActivity(intent)
        }

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is HistoryViewModel.ViewState.Error -> {
                    progressBar.visibility = View.GONE
                }
                is HistoryViewModel.ViewState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is HistoryViewModel.ViewState.Success -> {
                    progressBar.visibility = View.GONE
                    adapter?.submitList(state.list)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }
}
