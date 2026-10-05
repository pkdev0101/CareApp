package com.careapp.app

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Device-level navigation walkthrough for UI review. */
@RunWith(AndroidJUnit4::class)
class NavigationScreenshotReviewTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val device = UiDevice.getInstance(instrumentation)
    private var screenshotNumber = 0

    @Before
    fun launchCareApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: error("CareApp does not expose a launcher activity")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        check(device.wait(Until.hasObject(By.textContains("CareApp")), 10_000)) {
            "CareApp did not display its launcher screen"
        }
        findClickable("Senior Mode")?.click()
        device.waitForIdle()
    }

    @Test
    fun captureLabeledNavigationAndInteractionReview() {
        capture("home_initial")

        clickBottomNav(1, 5, "medication_screen")
        click("I Took It", "medication_taken_dialog")
        click("OK", "medication_taken_dialog_closed")
        click("I Took It", "medication_duplicate_warning")
        click("I Understand", "medication_duplicate_warning_closed")
        click("Remind Later", "medication_reminder_dialog")
        click("OK", "medication_reminder_dialog_closed")
        click("Need Help", "medication_help_dialog")
        click("OK", "medication_help_dialog_closed")
        scrollTo("View History")
        click("View History", "medication_history")
        clickBottomNav(1, 5, "medication_history_return")

        clickBottomNav(2, 5, "family_memory_cards")
        clickBottomNav(3, 5, "calm_mode")
        click("What was I doing?", "iforgot_activity_answer")
        click("What do I do next?", "iforgot_next_step_answer")
        click("Did I take my medicine?", "iforgot_medicine_answer")
        scrollDownTo("Where am I?")
        click("Where am I?", "iforgot_location_answer")
        scrollDownTo("Who can I call?")
        click("Who can I call?", "iforgot_caregiver_answer")
        click("Start Calming Breath", "calm_mode_breathing_started")
        click("Stop Breathing Timer", "calm_mode_breathing_stopped")
        scrollTo("Show answer")
        click("Show answer", "calm_mode_common_question_expanded")
        scrollTo("Helped")
        click("Helped", "calm_mode_what_helps_feedback")

        clickBottomNav(4, 5, "help_screen")
        click("Emergency Help", "emergency_screen")
        click("Show My Emergency Card", "emergency_card_opened")
        clickContains("Call 911", "emergency_demo_dialog")
        click("OK", "emergency_dialog_closed")
        click("I Fell / Need Help", "emergency_help_dialog")
        clickContains("I'm OK", "emergency_help_cancelled")
        click("OK", "emergency_help_dialog_closed")

        click("I Am Lost", "lost_mode")
        scrollTo("Show Current Location")
        click("Show Current Location", "lost_mode_location_loaded")
        scrollTo("Show Emergency Card")
        click("Show Emergency Card", "lost_mode_emergency_link")

        click("Caregiver Mode", "caregiver_mode_enabled")
        clickBottomNav(0, 5, "caregiver_routine")
        clickBottomNav(1, 5, "caregiver_medications")
        clickBottomNav(2, 5, "caregiver_notes")
        clickBottomNav(3, 5, "doctor_summary")
        clickBottomNav(4, 5, "caregiver_settings")
        click("Large", "settings_large_text")
        click("Start Demo Mode", "demo_mode")
        click("Open Feature", "demo_mode_first_feature")
        clickBottomNav(4, 5, "settings_after_demo_feature")
        click("Senior Mode", "senior_mode_enabled")
        clickBottomNav(0, 5, "home_final")
    }

    private fun click(label: String, screenshotLabel: String) {
        tap(waitForTextContains(label))
        device.waitForIdle()
        capture(screenshotLabel)
    }

    private fun clickContains(label: String, screenshotLabel: String) = click(label, screenshotLabel)

    private fun clickFirstClickable(label: String, screenshotLabel: String) {
        tap(waitForTextContains(label))
        device.waitForIdle()
        capture(screenshotLabel)
    }

    private fun clickBottomNav(index: Int, itemCount: Int, screenshotLabel: String) {
        val x = ((index + 0.5f) * device.displayWidth / itemCount).toInt()
        val y = device.displayHeight - 400
        check(device.click(x, y)) { "Could not tap bottom navigation item $index" }
        device.waitForIdle()
        SystemClock.sleep(500)
        capture(screenshotLabel)
    }

    private fun tap(objectToTap: UiObject2) {
        val bounds = objectToTap.visibleBounds
        val tapped = runCatching {
            objectToTap.click()
            true
        }.getOrElse { device.click(bounds.centerX(), bounds.centerY()) }
        check(tapped) {
            "Could not tap UI text at ${bounds.centerX()}, ${bounds.centerY()}"
        }
    }

    private fun scrollTo(label: String, fallback: String? = null) {
        repeat(8) {
            if (findClickable(label) != null || device.hasObject(By.text(label))) return
            if (fallback != null && device.hasObject(By.textContains(fallback))) return
            device.swipe(device.displayWidth / 2, device.displayHeight - 900, device.displayWidth / 2, 400, 500)
            device.waitForIdle()
        }
        check(device.hasObject(By.text(label)) || (fallback != null && device.hasObject(By.textContains(fallback)))) {
            "Could not scroll to UI text: $label"
        }
    }

    private fun scrollDownTo(label: String) {
        repeat(8) {
            if (device.hasObject(By.text(label)) || device.hasObject(By.textContains(label))) return
            device.swipe(device.displayWidth / 2, device.displayHeight - 650, device.displayWidth / 2, 550, 350)
            device.waitForIdle()
        }
        check(device.hasObject(By.text(label)) || device.hasObject(By.textContains(label))) {
            "Could not scroll to UI text: $label"
        }
    }

    private fun waitForTextContains(label: String, requireClickable: Boolean = false): UiObject2 {
        val deadline = System.currentTimeMillis() + 10_000
        while (System.currentTimeMillis() < deadline) {
            val match = if (requireClickable) {
                findClickableExact(label) ?: findClickable(label)
            } else {
                device.findObject(By.text(label)) ?: device.findObject(By.textContains(label))
            }
            if (match != null) return match
            device.waitForIdle()
        }
        error("Expected UI text was not found: $label")
    }

    private fun findClickableExact(label: String): UiObject2? =
        device.findObjects(By.text(label)).firstOrNull { it.isClickable }

    private fun findClickable(label: String): UiObject2? =
        device.findObjects(By.textContains(label)).firstOrNull { it.isClickable }

    private fun capture(label: String) {
        val fileName = "CareApp_%02d_%s.png".format(++screenshotNumber, label)
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "image/png")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/CareApp-navigation-review")
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Could not create shared screenshot file: $fileName")
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        context.contentResolver.openOutputStream(uri).use { output ->
            check(output != null && bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                "Could not save screenshot: $fileName"
            }
        }
        println("Navigation review screenshot: content://$uri")
    }
}
