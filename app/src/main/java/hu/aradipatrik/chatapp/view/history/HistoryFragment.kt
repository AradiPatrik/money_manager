package hu.aradipatrik.chatapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.aradipatrik.chatapp.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
}