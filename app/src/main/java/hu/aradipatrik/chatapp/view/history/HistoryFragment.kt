package hu.aradipatrik.chatapp.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.aradipatrik.chatapp.databinding.FragmentHistoryBinding
import hu.aradipatrik.chatapp.di.viewModel
import hu.aradipatrik.chatapp.injector

class HistoryFragment : Fragment() {
    val viewmodel by viewModel { injector.historyViewModel }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }
}