package ru.merkulyevsasha.userinfo.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_userinfo.imageViewAvatar
import kotlinx.android.synthetic.main.fragment_userinfo.layoutButtonCamera
import kotlinx.android.synthetic.main.fragment_userinfo.layoutButtonGallery
import kotlinx.android.synthetic.main.fragment_userinfo.layoutSources
import kotlinx.android.synthetic.main.fragment_userinfo.layoutSwitchTheme
import kotlinx.android.synthetic.main.fragment_userinfo.switchTheme
import kotlinx.android.synthetic.main.fragment_userinfo.toolbar
import kotlinx.android.synthetic.main.fragment_userinfo.userName
import ru.merkulyevsasha.core.models.ThemeEnum
import ru.merkulyevsasha.core.models.UserInfo
import ru.merkulyevsasha.core.models.UserProfile
import ru.merkulyevsasha.core.presentation.OnThemeChangedCallback
import ru.merkulyevsasha.coreandroid.common.AvatarShower
import ru.merkulyevsasha.coreandroid.common.BaseFragment
import ru.merkulyevsasha.coreandroid.common.ImageFileHelper
import ru.merkulyevsasha.coreandroid.common.KbUtils
import ru.merkulyevsasha.coreandroid.common.observe
import ru.merkulyevsasha.userinfo.R
import java.io.IOException

@AndroidEntryPoint
class UserInfoFragment : BaseFragment<UserInfoViewModel>(R.layout.fragment_userinfo) {

    companion object {
        @JvmStatic
        val TAG: String = "UserInfoFragment"

        private const val KEY_FILE_NAME = "key_file_name"

        private const val GALLERY_TAKE_IMAGE_REQUEST = 1001
        private const val CAMERA_TAKE_IMAGE_REQUEST = 1003

        @JvmStatic
        fun newInstance(): Fragment {
            val fragment = UserInfoFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }

    private val avatarShower = AvatarShower()

    private var profileFileName: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val savedState = savedInstanceState ?: arguments
        savedState?.apply {
            profileFileName = savedState.getString(KEY_FILE_NAME, "")
        }
        toolbar.setTitle(R.string.fragment_user_title)
        toolbar.setTitleTextColor(colorThemeResolver.getThemeAttrColor(R.attr.actionBarTextColor))
        combinator.bindToolbar(toolbar)
        if (savedInstanceState == null) {
            model.onFirstLoad()
        }
        layoutButtonCamera.setOnClickListener { takeCameraPicture() }
        layoutButtonGallery.setOnClickListener { takeGalleryPicture() }
        userName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                KbUtils.hideKeyboard(requireActivity())
                model.onSaveButtonClicked(userName.text.toString())
            }
            true
        }
        layoutSwitchTheme.setOnClickListener {
            val activity = requireActivity()
            if (activity is OnThemeChangedCallback) {
                val isChecked = !switchTheme.isChecked
                val newTheme = if (isChecked) ThemeEnum.ClassicNight else ThemeEnum.Classic
                activity.onThemeChanged(newTheme)
                model.onThemeChanged(newTheme)
            }
        }
        observeOnUserProfileChanged()
        observeOnUserInfoChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentState(outState)
    }

    override fun onDestroy() {
        saveFragmentState(arguments ?: Bundle())
        super.onDestroy()
    }

    private fun observeOnUserProfileChanged() {
        observe(model.userProfile) {
            showUserProfile(it)
        }
    }

    private fun observeOnUserInfoChanged() {
        observe(model.userInfo) {
            showUserInfo(it)
        }
    }

    private fun showUserInfo(userInfo: UserInfo) {
        if (userInfo.avatarUrl.isNotEmpty()) {
            avatarShower.showWithoutCache(requireContext(), R.drawable.ic_avatar_empty, userInfo.avatarUrl, userInfo.authorization, imageViewAvatar)
        }
        userName.setText(userInfo.name)
    }

    private fun showUserProfile(userProfile: UserProfile) {
        showUserInfo(userProfile.userInfo)
        switchTheme.isChecked = userProfile.theme == ThemeEnum.ClassicNight
        layoutSources.removeAllViews()
        userProfile.sources.forEach { source ->
            val view = CheckBox(requireContext())
            view.text = source.sourceName
            view.isChecked = source.checked
            view.setTextColor(colorThemeResolver.getThemeAttrColor(R.attr.activeColor))
            view.setOnClickListener {
                model.onSourceChecked(view.isChecked, source.sourceId)
            }
            layoutSources.addView(view)
        }
    }

    private fun takeGalleryPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.userinfo_select_image_label)), GALLERY_TAKE_IMAGE_REQUEST)
    }

    private fun takeCameraPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            profileFileName = ImageFileHelper.getTempFileName()
            val helper = ImageFileHelper(requireContext(), profileFileName)
            try {
                helper.file().createNewFile()
                if (helper.file() != null) {
                    val photoURI = FileProvider.getUriForFile(requireActivity(), getString(R.string.APPLICATION_ID) + ".fileprovider", helper.file())
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(helper.file()));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_TAKE_IMAGE_REQUEST)
                }
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CAMERA_TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val helper = ImageFileHelper(requireContext(), profileFileName)
            helper.compress()
            profileFileName = helper.file().absolutePath
            Glide.with(this).load(helper.file()).into(imageViewAvatar)
            model.onChangedAvatar(profileFileName)
        }
        if (requestCode == GALLERY_TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            intent?.let {
                it.data?.let { uri ->
                    profileFileName = ImageFileHelper.getTempFileName()
                    val helper = ImageFileHelper(requireContext(), profileFileName)
                    helper.createImageFile(requireActivity().contentResolver.openInputStream(uri))
                    helper.compress()
                    profileFileName = helper.file().absolutePath
                    Glide.with(this).load(helper.file()).into(imageViewAvatar)
                    model.onChangedAvatar(profileFileName)
                }
            }
        }
    }

    private fun saveFragmentState(state: Bundle) {
        state.putString(KEY_FILE_NAME, profileFileName)
    }

}