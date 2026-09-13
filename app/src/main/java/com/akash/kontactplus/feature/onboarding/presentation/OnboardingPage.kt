package com.akash.kontactplus.feature.onboarding.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.akash.kontactplus.R

sealed class OnboardingPage(
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val points: List<Int> = emptyList()
) {
    data object Welcome : OnboardingPage(
        titleRes = R.string.onboarding_welcome_title,
        descriptionRes = R.string.onboarding_welcome_description,
        icon = Icons.Default.Star,
        points = listOf(
            R.string.onboarding_welcome_point1,
            R.string.onboarding_welcome_point2,
            R.string.onboarding_welcome_point3,
            R.string.onboarding_welcome_point4
        )
    )

    data object LocalPrivacy : OnboardingPage(
        titleRes = R.string.onboarding_privacy_title,
        descriptionRes = R.string.onboarding_privacy_description,
        icon = Icons.Default.Lock,
        points = listOf(
            R.string.onboarding_privacy_point1,
            R.string.onboarding_privacy_point2,
            R.string.onboarding_privacy_point3,
            R.string.onboarding_privacy_point4
        )
    )

    data object PhoneAndContacts : OnboardingPage(
        titleRes = R.string.onboarding_phone_title,
        descriptionRes = R.string.onboarding_phone_description,
        icon = Icons.Default.Phone
    )

    data object OptionalAi : OnboardingPage(
        titleRes = R.string.onboarding_ai_title,
        descriptionRes = R.string.onboarding_ai_description,
        icon = Icons.Default.AutoAwesome
    )

    data object Ready : OnboardingPage(
        titleRes = R.string.onboarding_ready_title,
        descriptionRes = R.string.onboarding_ready_description,
        icon = Icons.Default.PrivacyTip
    )

    companion object {
        val pages = listOf(Welcome, LocalPrivacy, PhoneAndContacts, OptionalAi, Ready)
    }
}
