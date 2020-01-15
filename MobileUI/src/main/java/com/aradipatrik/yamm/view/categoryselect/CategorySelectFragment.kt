package com.aradipatrik.yamm.view.categoryselect

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.aradipatrik.yamm.databinding.FragmentCategoryListBinding
import com.aradipatrik.yamm.databinding.ListCategoryBinding
import com.aradipatrik.yamm.di.viewModel
import com.aradipatrik.yamm.injector

class CategorySelectFragment : Fragment() {
    private val viewModel by viewModel { injector.categorySelectViewModel }
    lateinit var binding: FragmentCategoryListBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.categoryPager.adapter =
                CategoryPagerAdapter(activity?.supportFragmentManager!!, viewModel)
        binding.categoryTabLayout.setupWithViewPager(binding.categoryPager)
    }
}

class CategoryPagerAdapter(
        fm: FragmentManager,
        private val viewModel: CategorySelectViewModel
) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        const val IncomeTabPosition = 0
        const val ExpenseTabPosition = 1
    }

    override fun getItem(position: Int): Fragment {
        return Page(position, viewModel)
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return super.getPageTitle(position)
    }

}

class Page(
        private val position: Int,
        private val viewModel: CategorySelectViewModel
) : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return when (position) {
            CategoryPagerAdapter.IncomeTabPosition, CategoryPagerAdapter.ExpenseTabPosition -> {
                Log.d(this::class.java.simpleName, "getItem $position")
                val binding = ListCategoryBinding.inflate(inflater, container, false)
                val categoryAdapter = CategoryListAdapter {}

                binding.categoryRecyclerView.layoutManager =
                        GridLayoutManager(inflater.context, 4)
                binding.categoryRecyclerView.adapter = categoryAdapter
                viewModel.allCategories.observe(this) { items ->
                    if (items != null) {
                        categoryAdapter.submitList(items)
                    }
                }
                binding.root
            }
            else ->
                throw IllegalStateException(
                        "Category select view pager has" +
                                " only 2 items but $position was requested"
                )
        }
    }
}
