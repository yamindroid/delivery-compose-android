package com.delivery.compose.android.presentation.deliverylist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.delivery.compose.android.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DeliveryListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        // Wait for initial loading
        composeTestRule.waitForIdle()
    }

    @Test
    fun verifyScreenTitle() {
        // Verify the screen title is displayed
        composeTestRule.onNodeWithText("My Deliveries", useUnmergedTree = true)
            .assertExists()
            .assertIsDisplayed()
    }
}