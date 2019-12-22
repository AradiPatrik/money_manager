package hu.aradipatrik.chatapp.view.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import hu.aradipatrik.chatapp.databinding.FragmentHistoryBinding
import hu.aradipatrik.chatapp.di.viewModel
import hu.aradipatrik.chatapp.injector

class HistoryFragment : Fragment() {
    private val viewmodel by viewModel { injector.historyViewModel }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val adapter = HistoryAdapter()
        binding.historyList.adapter = adapter

        viewmodel.historyItemsThisMonth.observe(this) {
            val list = mapCategorisedTransactionsToItems(it)
            Log.d(this::class.java.simpleName, "$it")
            adapter.submitList(list)
        }
        return binding.root
    }
}