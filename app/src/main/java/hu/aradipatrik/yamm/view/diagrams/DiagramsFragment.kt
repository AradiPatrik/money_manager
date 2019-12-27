package hu.aradipatrik.yamm.view.diagrams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.aradipatrik.yamm.databinding.FragmentDiagramsBinding

class DiagramsFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDiagramsBinding.inflate(inflater, container, false)
        return binding.root
    }
}