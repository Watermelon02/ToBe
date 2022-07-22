package watermelon.tobe.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.util.extension.toast
import watermelon.tobe.base.FRAGMENT_REGISTER
import watermelon.tobe.databinding.FragmentLoginBinding
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.viewmodel.UserViewModel

/**
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 19:48
 */
class LoginFragment : DialogFragment() {
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        binding = FragmentLoginBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()
        binding.apply {
            fragmentLoginTextRegister.setOnClickListener {
                RegisterFragment().show(activity?.supportFragmentManager!!, FRAGMENT_REGISTER)
            }
            fragmentLoginButton.setOnClickListener {
                val userName = fragmentLoginTodoAccountEdit.text.toString()
                val password = fragmentLoginPasswordEdit.text.toString()
                viewModel.login(userName, password)
            }
            safeLaunch {
                viewModel.user.collectLatest {
                    if (it != null) {
                        if (it.errorCode == 0) {
                            toast("登录成功！")
                            dismiss()
                        } else {
                            fragmentLoginAccountEditLayout.error = it.errorMsg
                            fragmentLoginPasswordEditLayout.error = it.errorMsg
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.emitShowState(false)
    }
}