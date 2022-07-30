package watermelon.tobe.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.collectLatest
import watermelon.tobe.databinding.FragmentRegisterBinding
import watermelon.tobe.util.extension.safeLaunch
import watermelon.tobe.util.extension.toast
import watermelon.tobe.viewmodel.UserViewModel

/**
 * description ： TODO:类的作用
 * author : Watermelon02
 * email : 1446157077@qq.com
 * date : 2022/7/22 19:52
 */
class RegisterFragment : DialogFragment() {
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val binding = FragmentRegisterBinding.inflate(layoutInflater).apply {
            fragmentRegisterTextLogin.setOnClickListener {
                dismiss()
            }
            fragmentLoginButton.setOnClickListener {
                val userName = fragmentRegisterTodoAccountEdit.text.toString()
                val password = fragmentRegisterPasswordEdit.text.toString()
                val rePassword = fragmentRegisterRePasswordEdit.text.toString()
                viewModel.register(userName, password, rePassword)
            }
            viewLifecycleOwner.safeLaunch {
                viewModel.user.collectLatest {
                    if (it != null) {
                        if (it.errorCode == 0) {
                            toast("注册成功！")
                            dismiss()
                        } else {
                            fragmentRegisterAccountEditLayout.error = it.errorMsg
                            fragmentRegisterPasswordEditLayout.error = it.errorMsg
                            fragmentRegisterRePasswordEditLayout.error = it.errorMsg
                        }
                    }
                }
            }
        }
        //实现圆角
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

}