package app.kobuggi.hyuabot.utils

import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable

class NativeTemplateStyle {
    var callToActionTextTypeface: Typeface? = null
        private set
    var callToActionTextSize = 0f
        private set
    var callToActionTypefaceColor = 0
        private set
    var callToActionBackgroundColor: ColorDrawable? = null
        private set
    var primaryTextTypeface: Typeface? = null
        private set
    var primaryTextSize = 0f
        private set
    var primaryTextTypefaceColor = 0
        private set
    var primaryTextBackgroundColor: ColorDrawable? = null
        private set
    var secondaryTextTypeface: Typeface? = null
        private set
    var secondaryTextSize = 0f
        private set
    var secondaryTextTypefaceColor = 0
        private set
    var secondaryTextBackgroundColor: ColorDrawable? = null
        private set
    var tertiaryTextTypeface: Typeface? = null
        private set
    var tertiaryTextSize = 0f
        private set
    var tertiaryTextTypefaceColor = 0
        private set
    var tertiaryTextBackgroundColor: ColorDrawable? = null
        private set
    var mainBackgroundColor: ColorDrawable? = null
        private set

    class Builder {
        private val styles: NativeTemplateStyle = NativeTemplateStyle()
        fun withCallToActionTextTypeface(callToActionTextTypeface: Typeface?): Builder {
            styles.callToActionTextTypeface = callToActionTextTypeface
            return this
        }

        fun withCallToActionTextSize(callToActionTextSize: Float): Builder {
            styles.callToActionTextSize = callToActionTextSize
            return this
        }

        fun withCallToActionTypefaceColor(callToActionTypefaceColor: Int): Builder {
            styles.callToActionTypefaceColor = callToActionTypefaceColor
            return this
        }

        fun withCallToActionBackgroundColor(callToActionBackgroundColor: ColorDrawable?): Builder {
            styles.callToActionBackgroundColor = callToActionBackgroundColor
            return this
        }

        fun withPrimaryTextTypeface(primaryTextTypeface: Typeface?): Builder {
            styles.primaryTextTypeface = primaryTextTypeface
            return this
        }

        fun withPrimaryTextSize(primaryTextSize: Float): Builder {
            styles.primaryTextSize = primaryTextSize
            return this
        }

        fun withPrimaryTextTypefaceColor(primaryTextTypefaceColor: Int): Builder {
            styles.primaryTextTypefaceColor = primaryTextTypefaceColor
            return this
        }

        fun withPrimaryTextBackgroundColor(primaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.primaryTextBackgroundColor = primaryTextBackgroundColor
            return this
        }

        fun withSecondaryTextTypeface(secondaryTextTypeface: Typeface?): Builder {
            styles.secondaryTextTypeface = secondaryTextTypeface
            return this
        }

        fun withSecondaryTextSize(secondaryTextSize: Float): Builder {
            styles.secondaryTextSize = secondaryTextSize
            return this
        }

        fun withSecondaryTextTypefaceColor(secondaryTextTypefaceColor: Int): Builder {
            styles.secondaryTextTypefaceColor = secondaryTextTypefaceColor
            return this
        }

        fun withSecondaryTextBackgroundColor(secondaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.secondaryTextBackgroundColor = secondaryTextBackgroundColor
            return this
        }

        fun withTertiaryTextTypeface(tertiaryTextTypeface: Typeface?): Builder {
            styles.tertiaryTextTypeface = tertiaryTextTypeface
            return this
        }

        fun withTertiaryTextSize(tertiaryTextSize: Float): Builder {
            styles.tertiaryTextSize = tertiaryTextSize
            return this
        }

        fun withTertiaryTextTypefaceColor(tertiaryTextTypefaceColor: Int): Builder {
            styles.tertiaryTextTypefaceColor = tertiaryTextTypefaceColor
            return this
        }

        fun withTertiaryTextBackgroundColor(tertiaryTextBackgroundColor: ColorDrawable?): Builder {
            styles.tertiaryTextBackgroundColor = tertiaryTextBackgroundColor
            return this
        }

        fun withMainBackgroundColor(mainBackgroundColor: ColorDrawable?): Builder {
            styles.mainBackgroundColor = mainBackgroundColor
            return this
        }

        fun build(): NativeTemplateStyle {
            return styles
        }
    }
}