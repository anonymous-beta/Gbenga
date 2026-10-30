
package com.gbenga.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.time.LocalDate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val bg = 0xFF101710.toInt()
    private val panel = 0xFF1B281B.toInt()
    private val olive = 0xFFC6D66B.toInt()
    private val white = 0xFFF0F3E8.toInt()
    private val muted = 0xFFB3BDAE.toInt()

    private val executor: ExecutorService =
        Executors.newSingleThreadExecutor()

    private lateinit var root: LinearLayout
    private lateinit var result: TextView
    private lateinit var numberInput: EditText
    private lateinit var scanInput: EditText
    private lateinit var waNumberInput: EditText
    private lateinit var waMessageInput: EditText
    private lateinit var hashInput: EditText
    private lateinit var usageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        refreshUsage()
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(18), dp(20),
                dp(18), dp(28)
            )

            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    0xFF101710.toInt(),
                    0xFF1B2A1B.toInt(),
                    0xFF101710.toInt()
                )
            )
        }

        scroll.addView(root)
        setContentView(scroll)

        root.addView(
            TextView(this).apply {
                text = "GBENGA"
                textSize = 30f
                setTextColor(olive)
                typeface = Typeface.create(
                    "sans-serif-black",
                    Typeface.BOLD
                )
                letterSpacing = 0.12f
            }
        )

        root.addView(
            label(
                "FIELD INTELLIGENCE • SECURE UTILITIES",
                12f, muted, true
            ),
            margin(0, 2, 0, 18)
        )

        val edition = if (BuildConfig.IS_PRO) {
            "PRO EDITION · UNLIMITED LOCAL USAGE"
        } else {
            "FREE EDITION · DAILY USAGE LIMITS"
        }

        val header = card()

        header.addView(
            label(
                "CREATOR / Anonymous-beta",
                14f, white, true
            )
        )

        header.addView(
            label(edition, 12f, olive, true),
            margin(0, 8, 0, 0)
        )

        usageText = label("", 12f, muted, false)

        header.addView(
            usageText,
            margin(0, 8, 0, 0)
        )

        root.addView(header, margin(0, 0, 0, 18))

        // 01: Nigerian number resolver
        root.addView(
            sectionTitle("01 / NIGERIAN NUMBER RESOLVER")
        )

        numberInput = edit("Enter Nigerian phone number")

        root.addView(
            numberInput,
            margin(0, 0, 0, 8)
        )

        root.addView(
            actionButton("RESOLVE NUMBER") {
                runNumberLookup()
            },
            margin(0, 0, 0, 14)
        )

        // 02: Public IP lookup
        root.addView(
            sectionTitle("02 / PUBLIC IP ADDRESS")
        )

        root.addView(
            label(
                "Fetches the public IP reported by ipify.",
                13f, muted, false
            ),
            margin(0, 0, 0, 8)
        )

        root.addView(
            actionButton("CHECK PUBLIC IP") {
                runIpLookup()
            },
            margin(0, 0, 0, 14)
        )

        // 03: Nex-Guard scan
        root.addView(
            sectionTitle("03 / NEX-GUARD URL SCAN")
        )

        scanInput = edit("https://example.com")

        root.addView(
            scanInput,
            margin(0, 0, 0, 8)
        )

        root.addView(
            actionButton("SCAN URL") {
                runScan()
            },
            margin(0, 0, 0, 14)
        )

        // 04: WhatsApp
        root.addView(
            sectionTitle("04 / WHATSAPP MESSAGE COMPOSER")
        )

        root.addView(
            label(
                "Prepare a message and open the official " +
                    "WhatsApp chat. Review and send it yourself.",
                12f, muted, false
            ),
            margin(0, 0, 0, 8)
        )

        waNumberInput =
            edit("Recipient number with country code")

        root.addView(
            waNumberInput,
            margin(0, 0, 0, 8)
        )

        waMessageInput = edit("Write a message")

        root.addView(
            waMessageInput,
            margin(0, 0, 0, 8)
        )

        root.addView(
            actionButton("OPEN WHATSAPP CHAT") {
                openWhatsAppChat()
            },
            margin(0, 0, 0, 14)
        )

        // 05: SHA-256
        root.addView(
            sectionTitle("05 / SHA-256 TEXT HASH")
        )

        root.addView(
            label(
                "Calculate a SHA-256 digest for a text value.",
                12f, muted, false
            ),
            margin(0, 0, 0, 8)
        )

        hashInput = edit("Enter text to hash")

        root.addView(
            hashInput,
            margin(0, 0, 0, 8)
        )

        root.addView(
            actionButton("CALCULATE SHA-256") {
                calculateHash()
            },
            margin(0, 0, 0, 14)
        )

        // Output
        root.addView(sectionTitle("OUTPUT / ACTIVITY"))

        result = label(
            "Ready. Choose a module to begin.",
            13f, white, false
        ).apply {
            gravity = Gravity.TOP
            setPadding(
                dp(14), dp(14),
                dp(14), dp(14)
            )
            background = rounded(panel, 12)
            minHeight = dp(120)
            textIsSelectable = true
        }

        root.addView(
            result,
            margin(0, 0, 0, 14)
        )

        root.addView(
            label(
                "GBENGA • ${BuildConfig.VERSION_NAME}\n" +
                    "Creator: Anonymous-beta",
                11f, muted, false
            )
        )
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(14), dp(14),
                dp(14), dp(14)
            )
            background = rounded(panel, 14)
        }
    }

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)

            if (bold) {
                typeface = Typeface.create(
                    "sans-serif",
                    Typeface.BOLD
                )
            }
        }
    }

    private fun sectionTitle(value: String): TextView {
        return label(value, 13f, olive, true).apply {
            setPadding(0, dp(8), 0, dp(8))
        }
    }

    private fun edit(hintValue: String): EditText {
        return EditText(this).apply {
            hint = hintValue
            setHintTextColor(0xFF879184.toInt())
            setTextColor(white)
            textSize = 14f
            singleLine = true

            setPadding(
                dp(13), dp(10),
                dp(13), dp(10)
            )

            background = rounded(
                0xFF202D20.toInt(),
                10
            )
        }
    }

    private fun actionButton(
        title: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = title
            textSize = 12f
            isAllCaps = false
            setTextColor(0xFF101710.toInt())

            typeface = Typeface.create(
                "sans-serif",
                Typeface.BOLD
            )

            background = rounded(olive, 10)

            setPadding(
                dp(12), dp(6),
                dp(12), dp(6)
            )

            setOnClickListener {
                hideKeyboard()
                action()
            }
        }
    }

    private fun rounded(
        color: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun margin(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                dp(left), dp(top),
                dp(right), dp(bottom)
            )
        }
    }

    private fun dp(value: Int): Int {
        return (
            value * resources.displayMetrics.density
        ).toInt()
    }

    private fun hideKeyboard() {
        val manager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        manager.hideSoftInputFromWindow(
            root.windowToken,
            0
        )
    }

    // API 1: Nigerian number resolver
    private fun runNumberLookup() {
        val number = numberInput.text
            .toString()
            .trim()

        if (number.isBlank()) {
            toast("Enter a Nigerian phone number.")
            return
        }

        if (!consumeQuota("number", 5)) return

        val encoded = URLEncoder.encode(
            number,
            "UTF-8"
        )

        request(
            "https://bank-check-eight.vercel.app/" +
                "api/resolve?number=$encoded",
            "GET",
            null,
            "Nigerian number resolver"
        )
    }

    // API 2: ipify
    private fun runIpLookup() {
        request(
            "https://api.ipify.org?format=json",
            "GET",
            null,
            "Public IP lookup"
        )
    }

    // API 3: Nex-Guard
    private fun runScan() {
        val target = scanInput.text
            .toString()
            .trim()

        val uri = try {
            Uri.parse(target)
        } catch (_: Exception) {
            toast("Invalid URL.")
            return
        }

        if (
            uri.scheme !in listOf("http", "https") ||
            uri.host.isNullOrBlank()
        ) {
            toast("Enter a valid HTTP or HTTPS URL.")
            return
        }

        if (!consumeQuota("scan", 3)) return

        val body = JSONObject()
            .put("url", target)
            .toString()

        request(
            "https://nex-guard-api.vercel.app/api/scan",
            "POST",
            body,
            "Nex-Guard URL scan"
        )
    }

    // Open the official WhatsApp chat flow
    private fun openWhatsAppChat() {
        val number = waNumberInput.text
            .toString()
            .trim()
            .replace(Regex("[\\s()+-]"), "")

        val message = waMessageInput.text
            .toString()
            .trim()

        if (
            number.length !in 7..15 ||
            !number.all { it.isDigit() }
        ) {
            toast(
                "Enter a valid international number " +
                    "including its country code."
            )
            return
        }

        if (message.isBlank()) {
            toast("Write a message first.")
            return
        }

        val encodedMessage = URLEncoder.encode(
            message,
            "UTF-8"
        )

        val uri = Uri.parse(
            "https://wa.me/$number?text=$encodedMessage"
        )

        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, uri)
            )
        } catch (_: Exception) {
            toast(
                "Could not open WhatsApp. " +
                    "Check your internet connection."
            )
        }
    }

    // SHA-256 text utility
    private fun calculateHash() {
        val value = hashInput.text.toString()

        if (value.isEmpty()) {
            toast("Enter text to hash first.")
            return
        }

        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))

        val digest = bytes.joinToString("") {
            "%02x".format(it.toInt() and 0xff)
        }

        showResult(
            "SHA-256 TEXT HASH\n\n$digest"
        )
    }

    // Actual HTTP request implementation
    private fun request(
        endpoint: String,
        method: String,
        body: String?,
        title: String
    ) {
        showResult("$title\nConnecting…")

        executor.execute {
            var connection: HttpURLConnection? = null

            try {
                connection = (
                    URL(endpoint).openConnection()
                        as HttpURLConnection
                ).apply {
                    requestMethod = method
                    connectTimeout = 15000
                    readTimeout = 20000

                    setRequestProperty(
                        "Accept",
                        "application/json"
                    )

                    setRequestProperty(
                        "User-Agent",
                        "GBENGA-Android/1.0"
                    )

                    if (body != null) {
                        doOutput = true

                        setRequestProperty(
                            "Content-Type",
                            "application/json; charset=utf-8"
                        )

                        outputStream.use {
                            it.write(
                                body.toByteArray(
                                    Charsets.UTF_8
                                )
                            )
                        }
                    }
                }

                val status = connection.responseCode

                val stream = if (status in 200..399) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val payload = stream
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?: "(empty response)"

                val formatted = try {
                    JSONObject(payload).toString(2)
                } catch (_: Exception) {
                    payload
                }

                showResult(
                    "$title\nHTTP $status\n\n$formatted"
                )

            } catch (e: Exception) {
                showResult(
                    "$title failed\n" +
                        "${e.javaClass.simpleName}: " +
                        "${e.message ?: "Network error"}\n\n" +
                        "Check your connection and retry."
                )
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun showResult(message: String) {
        runOnUiThread {
            result.text = message
        }
    }

    private fun quotaKey(kind: String): String {
        return "${kind}_${LocalDate.now()}"
    }

    // Local feature limits for Free edition.
    // This is not server-verified Pro entitlement.
    private fun consumeQuota(
        kind: String,
        limit: Int
    ): Boolean {
        if (BuildConfig.IS_PRO) return true

        val prefs = getSharedPreferences(
            "gbenga_usage",
            MODE_PRIVATE
        )

        val key = quotaKey(kind)
        val used = prefs.getInt(key, 0)

        if (used >= limit) {
            toast(
                "Free limit reached for $kind today. " +
                    "Pro edition removes this local limit."
            )
            return false
        }

        prefs.edit()
            .putInt(key, used + 1)
            .apply()

        refreshUsage()
        return true
    }

    private fun refreshUsage() {
        if (!::usageText.isInitialized) return

        if (BuildConfig.IS_PRO) {
            usageText.text =
                "Number lookups: unlimited • " +
                "URL scans: unlimited • " +
                "IP checks: unlimited"
        } else {
            val prefs = getSharedPreferences(
                "gbenga_usage",
                MODE_PRIVATE
            )

            val numbers = prefs.getInt(
                quotaKey("number"), 0
            )

            val scans = prefs.getInt(
                quotaKey("scan"), 0
            )

            usageText.text =
                "Today: number lookups $numbers/5 • " +
                "URL scans $scans/3 • " +
                "IP checks unlimited"
        }
    }

    private fun toast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
