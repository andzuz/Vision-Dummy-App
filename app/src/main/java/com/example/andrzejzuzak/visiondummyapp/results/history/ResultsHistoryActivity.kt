package com.example.andrzejzuzak.visiondummyapp.results.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.andrzejzuzak.visiondummyapp.LotterixApplication
import com.example.andrzejzuzak.visiondummyapp.R
import com.example.andrzejzuzak.visiondummyapp.networking.ResultResponseDto
import kotlinx.android.synthetic.main.activity_results_history.*
import kotlinx.android.synthetic.main.item_history_row.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import timber.log.Timber
import javax.inject.Inject

class ResultsHistoryActivity: AppCompatActivity(), ResultsHistoryPresentation {

    @Inject lateinit var presenter: ResultsHistoryPresenter

    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_history)
        (application as LotterixApplication).daggerGraph.inject(this)
        setupToolbar()
        setupRecyclerView()
        presenter.onCreate(this)
        presenter.getHistory()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "History"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showProgress() {
        progressView.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressView.visibility = View.GONE
    }

    override fun showFailure(strResId: Int) {
        hideProgress()
        alert(messageResource = strResId, titleResource = R.string.error_title) {
            yesButton {}
        }.show()
    }

    override fun showItems(items: List<ResultResponseDto>) {
        Timber.d("List size: ${items.size}")
        adapter.setItems(items)
        hideProgress()
    }

}

class HistoryAdapter: RecyclerView.Adapter<HistoryRowViewHolder>() {

    private var items: ArrayList<ResultResponseDto> = arrayListOf()

    fun setItems(newItems: List<ResultResponseDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: HistoryRowViewHolder?, position: Int) {
        val historyRow = items[position]
        holder?.configureCell(historyRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryRowViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_history_row, null)
        return HistoryRowViewHolder(view)
    }

}

class HistoryRowViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun configureCell(item: ResultResponseDto) {
        itemView.dateTextView.text = item.lotteryDate
        itemView.matchedTextView.text = "${item.matchedNumbersCount} numbers were matched"

        val sb = StringBuilder()
        for (numberRow in item.numbers) {
            sb.append(numberRow.replace("X", "-"))
            sb.append("\n")
        }

        itemView.numbersTextView.text = sb.toString()
    }

}