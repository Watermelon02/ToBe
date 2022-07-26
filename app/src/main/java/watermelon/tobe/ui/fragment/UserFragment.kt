package watermelon.tobe.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.base.FRAGMENT_LOGIN
import watermelon.tobe.databinding.FragmentUserBinding
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.viewmodel.UserViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 15:05
 */
class UserFragment : DialogFragment() {
    private lateinit var viewModel: UserViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val binding = FragmentUserBinding.inflate(layoutInflater).apply {
            fragmentUserExit.setOnClickListener {
                viewModel.exit()
            }
        }
        binding.fragmentUserExit.setOnClickListener {
            viewModel.exit()
            LoginFragment().show(activity?.supportFragmentManager!!, FRAGMENT_LOGIN)
        }
        binding.fragmentUserNameEdit.text =
            Editable.Factory.getInstance().newEditable(viewModel.user.value?.data?.publicName)
        binding.fragmentUserTodoAccountEdit.text =
            Editable.Factory.getInstance().newEditable(viewModel.user.value?.data?.username)
        binding.fragmentUserAccountPieChart.start()
        safeLaunch {
            viewModel.finishPercent.collectLatest {
                binding.fragmentUserAccountPieChartPercent.text = (it*100).toString()+"%"
            }
        }
        viewModel.queryTodoFinishState()
        return AlertDialog.Builder(requireContext()).setView(binding.root).create()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //实现圆角
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.emitShowState(false)
    }
}