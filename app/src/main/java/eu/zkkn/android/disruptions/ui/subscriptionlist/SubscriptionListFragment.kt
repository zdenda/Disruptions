package eu.zkkn.android.disruptions.ui.subscriptionlist

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EdgeEffect
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import eu.zkkn.android.disruptions.BuildConfig
import eu.zkkn.android.disruptions.R
import eu.zkkn.android.disruptions.databinding.FragmentSubscriptionsBinding
import eu.zkkn.android.disruptions.ui.AnalyticsFragment
import eu.zkkn.android.disruptions.utils.Analytics


class SubscriptionListFragment : AnalyticsFragment() {

    private val viewModel: SubscriptionListViewModel by viewModels()

    private var _binding: FragmentSubscriptionsBinding? = null
    // Scoped to the lifecycle of the fragment's view (between onCreateView and onDestroyView)
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.refreshAppNotificationsStatus() }

    private val appSettingsLauncher = registerForActivityResult(
        object : ActivityResultContract<Void?, ActivityResult>() {
            override fun createIntent(context: Context, input: Void?): Intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))

            override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
                ActivityResult(resultCode, intent)
        }
    ) { viewModel.refreshAppNotificationsStatus() }

    private val manageUnusedAppRestrictionsLauncher = registerForActivityResult(
        object : ActivityResultContract<Void?, ActivityResult>() {
            override fun createIntent(context: Context, input: Void?): Intent =
                IntentCompat.createManageUnusedAppRestrictionsIntent(
                    context, BuildConfig.APPLICATION_ID
                )

            override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult =
                ActivityResult(resultCode, intent)
        }
    ) { viewModel.refreshAppRestrictionsStatus() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSubscriptionsBinding.inflate(inflater, container, false)
        with(binding) {

            btNotificationsDisabled.setOnClickListener {
                appSettingsLauncher.launch()
            }

            btUnusedAppRestrictions.setOnClickListener {
                manageUnusedAppRestrictionsLauncher.launch()
            }

            viewModel.showNotificationsInfo.observe(viewLifecycleOwner) { showWarning ->
                if (viewModel.notificationPermissionRequested == false && showWarning) {
                    requireNotificationPermission()
                }
                llNotificationsInfoBox.visibility = if (showWarning) View.VISIBLE else View.GONE
            }

            viewModel.showAppHibernationInfo.observe(viewLifecycleOwner) { showWarning ->
                llHibernationInfoBox.visibility = if (showWarning) View.VISIBLE else View.GONE
            }

            val adapter = SubscriptionAdapter()
            adapter.setOnRemoveClickListener { lineName ->
                viewModel.removeSubscription(lineName)
            }
            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    rwSubscriptions.smoothScrollToPosition(positionStart)
                }
            })

            // change color for scroll edge effect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                rwSubscriptions.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                        val context = view.context
                        return EdgeEffect(context).apply {
                            color = if (direction == DIRECTION_BOTTOM) {
                                ContextCompat.getColor(context, R.color.backgroundSecondary)
                            } else {
                                ContextCompat.getColor(context, R.color.colorPrimary)
                            }
                        }
                    }
                }
            }
            rwSubscriptions.adapter = adapter
            rwSubscriptions.setHasFixedSize(true)
            rwSubscriptions.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )

            tiLine.editText?.setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT -> {
                        onSubscribeClick()
                        true
                    }
                    else -> false
                }
            }

            btSubscribe.setOnClickListener { onSubscribeClick() }

            viewModel.subscriptions.observe(viewLifecycleOwner) { subscriptions ->
                empty.visibility = if (subscriptions.isEmpty()) View.VISIBLE else View.GONE
                adapter.submitList(subscriptions)
            }

            viewModel.subscribeStatus.observe(viewLifecycleOwner) { subscribeState ->
                val errorMsgResId = subscribeState.errorMsgResIdIfNotHandled
                tiLine.error = if (errorMsgResId != null) getString(errorMsgResId) else null
                if (!subscribeState.inProgress && errorMsgResId == null) {
                    tiLine.editText?.text?.clear()
                }
                btSubscribe.isEnabled = !subscribeState.inProgress
                tiLine.isEnabled = !subscribeState.inProgress
            }
        }

        return binding.root

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onSubscribeClick() {
        requireNotificationPermission()
        val lineName = binding.tiLine.editText?.text.toString().trim()
        Analytics.logSubscribeForm(lineName)
        if (lineName.isNotBlank()) {
            viewModel.addSubscription(lineName)
        }
    }

    private fun requireNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                viewModel.notificationPermissionRequested = true
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}
