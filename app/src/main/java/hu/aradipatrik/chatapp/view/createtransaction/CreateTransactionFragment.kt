package hu.aradipatrik.chatapp.view.createtransaction

import androidx.fragment.app.Fragment
import hu.aradipatrik.chatapp.di.viewModel
import hu.aradipatrik.chatapp.injector

class CreateTransactionFragment : Fragment() {
  val viewModel by viewModel { injector.createTransactionViewModel }

}
