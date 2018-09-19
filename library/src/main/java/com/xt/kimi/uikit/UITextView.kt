package com.xt.kimi.uikit

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIRange
import com.xt.kimi.KIMIPackage

class UITextView: UINativeTouchView() {

    var text: String?
        get() {
            return systemEditText.text.toString()
        }
        set(value) {
            systemEditText.text.clear()
            systemEditText.text.append(value ?: "")
        }

    var textColor: UIColor? = null
        set(value) {
            field = value
            systemEditText.setTextColor(value?.toInt() ?: Color.BLACK)
            systemEditText.setHintTextColor((value?.colorWithAlphaComponent(0.35)?.toInt() ?: Color.GRAY))
        }

    var font: UIFont? = null
        set(value) {
            field = value
            value?.let { font ->
                systemEditText.textSize = (font.pointSize).toFloat()
                font.fontName?.let { fontName ->
                    systemEditText.typeface = Typeface.create(fontName, kotlin.run {
                        val fontStyle = font.fontStyle ?: return@run Typeface.NORMAL
                        when (fontStyle) {
                            "bold" -> return@run Typeface.BOLD
                            "heavy" -> return@run Typeface.BOLD
                            "black" -> return@run Typeface.BOLD
                            "italic" -> return@run Typeface.ITALIC
                        }
                        return@run Typeface.NORMAL
                    })
                } ?: kotlin.run {
                    systemEditText.typeface = Typeface.defaultFromStyle(kotlin.run {
                        val fontStyle = font.fontStyle ?: return@run Typeface.NORMAL
                        when (fontStyle) {
                            "bold" -> return@run Typeface.BOLD
                            "heavy" -> return@run Typeface.BOLD
                            "black" -> return@run Typeface.BOLD
                            "italic" -> return@run Typeface.ITALIC
                        }
                        return@run Typeface.NORMAL
                    })
                }
            }
        }

    var textAlignment = UITextAlignment.left
        set(value) {
            field = value
            systemEditText.textAlignment = kotlin.run {
                return@run when (value) {
                    UITextAlignment.left -> TEXT_ALIGNMENT_TEXT_START
                    UITextAlignment.center -> TEXT_ALIGNMENT_CENTER
                    UITextAlignment.right -> TEXT_ALIGNMENT_TEXT_END
                }
            }
        }

    var editable: Boolean = true
        set(value) {
            field = value
            systemEditText.isEnabled = false
        }

    var selectable: Boolean = true
        set(value) {
            field = value
            systemEditText.setTextIsSelectable(value)
        }

    val editing: Boolean
        get() { return systemEditText.isFocused }
    
    fun scrollRangeToVisible(range: UIRange) {
        val layout = systemEditText.layout ?: return
        val lineStart = layout.getLineForOffset(range.location.toInt())
        val boundsStart = Rect()
        layout.getLineBounds(lineStart, boundsStart)
        systemEditText.scrollTo(0, boundsStart.top)
    }
    
    fun focus() {
        systemEditText.requestFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(systemEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun blur() {
        systemEditText.clearFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(windowToken, 0)
    }

    var autocapitalizationType: UITextAutocapitalizationType = UITextAutocapitalizationType.sentences
        set(value) {
            field = value
            this.resetInputType()
        }

    var autocorrectionType: UITextAutocorrectionType = UITextAutocorrectionType.default
        set(value) {
            field = value
            this.resetInputType()
        }

    var spellCheckingType: UITextSpellCheckingType = UITextSpellCheckingType.default
        set(value) {
            field = value
            this.resetInputType()
        }

    var keyboardType: UIKeyboardType = UIKeyboardType.default
        set(value) {
            field = value
            this.resetInputType()
        }

    var returnKeyType: UIReturnKeyType = UIReturnKeyType.default
        set(value) {
            field = value
            this.resetInputType()
        }

    var secureTextEntry: Boolean = false
        set(value) {
            field = value
            if (value) {
                this.systemEditText.transformationMethod = PasswordTransformationMethod()
            }
            else {
                this.systemEditText.transformationMethod = null
            }
            this.resetInputType()
        }
    
    // Implementation

    private val systemEditText = EditText(this.context)

    init {
        systemEditText.setTextColor(Color.BLACK)
        systemEditText.background = null
        systemEditText.gravity = Gravity.TOP
        this.edo_backgroundColor = UIColor.white
        addView(systemEditText, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        setupEvents()
    }

    private fun setupEvents() {
        systemEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!this.shouldBeginEditing()) {
                    this.blur()
                    return@OnFocusChangeListener
                }
                this.didBeginEditing()
            }
            else {
                if (!this.shouldEndEditing()) {
                    if (!this.shouldBeginEditing()) {
                        return@OnFocusChangeListener
                    }
                    this.focus()
                    return@OnFocusChangeListener
                }
                this.didEndEditing()
            }
        }
        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            if (dstart != dend) {
                if (!this.shouldChange(UIRange(dend, (dstart - dend)), source?.toString())) {
                    return@InputFilter ""
                }
            }
            else {
                if (!this.shouldChange(UIRange(dstart, end), source?.toString())) {
                    return@InputFilter ""
                }
            }
            return@InputFilter source
        }
        systemEditText.filters = arrayOf(inputFilter)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            this.systemEditText.x = 4 * scale
            this.systemEditText.y = 4 * scale
            this.systemEditText.width = (this.width - 8 * scale).toInt()
            this.systemEditText.height = (this.height - 8 * scale).toInt()
        }
    }

    private fun resetInputType() {
        if (this.secureTextEntry) {
            this.systemEditText.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        }
        else {
            var inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
            when (this.autocapitalizationType) {
                UITextAutocapitalizationType.none -> {}
                UITextAutocapitalizationType.words -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                }
                UITextAutocapitalizationType.sentences -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                }
                UITextAutocapitalizationType.allCharacters -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                }
            }
            when (this.autocorrectionType) {
                UITextAutocorrectionType.yes -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                }
            }
            when (this.spellCheckingType) {
                UITextSpellCheckingType.no -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                }
                UITextSpellCheckingType.default -> {
                    inputType = inputType or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                }
            }
            when (this.keyboardType) {
                UIKeyboardType.default -> {
                    inputType = inputType or InputType.TYPE_CLASS_TEXT
                }
                UIKeyboardType.ASCIICapable -> {
                    inputType = inputType or InputType.TYPE_CLASS_TEXT
                }
                UIKeyboardType.numberPad -> {
                    inputType = inputType or InputType.TYPE_CLASS_NUMBER
                }
                UIKeyboardType.phonePad -> {
                    inputType = inputType or InputType.TYPE_CLASS_PHONE
                }
                UIKeyboardType.numbersAndPunctuation -> {
                    inputType = inputType or InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                }
                UIKeyboardType.emailAddress -> {
                    inputType = inputType or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                }
                UIKeyboardType.decimalPad -> {
                    inputType = inputType or InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                }
            }
            this.systemEditText.inputType = inputType
        }
        when (this.returnKeyType) {
            UIReturnKeyType.default -> {
                this.systemEditText.imeOptions = EditorInfo.IME_ACTION_NONE
            }
            UIReturnKeyType.next -> {
                this.systemEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
            }
            UIReturnKeyType.done -> {
                this.systemEditText.imeOptions = EditorInfo.IME_ACTION_DONE
            }
            UIReturnKeyType.send -> {
                this.systemEditText.imeOptions = EditorInfo.IME_ACTION_SEND
            }
            UIReturnKeyType.go -> {
                this.systemEditText.imeOptions = EditorInfo.IME_ACTION_GO
            }
        }
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        this.tintColor?.let { tintColor ->
            try {
                var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                val drawableResId = field.getInt(systemEditText)
                field = TextView::class.java.getDeclaredField("mEditor")
                field.isAccessible = true
                val editor = field.get(systemEditText)
                val drawable = ContextCompat.getDrawable(systemEditText.context, drawableResId)
                drawable?.setColorFilter(tintColor.toInt(), PorterDuff.Mode.SRC_IN)
                val drawables = arrayOf(drawable, drawable)
                field = editor.javaClass.getDeclaredField("mCursorDrawable")
                field.isAccessible = true
                field.set(editor, drawables)
            } catch (e: Exception) {}
        }
    }

    // Delegates

    open fun shouldBeginEditing(): Boolean {
        return EDOJavaHelper.value(this, "shouldBeginEditing", this) as? Boolean ?: true
    }

    open fun didBeginEditing() {
        EDOJavaHelper.value(this, "didBeginEditing", this)
    }

    open fun shouldEndEditing(): Boolean {
        return EDOJavaHelper.value(this, "shouldEndEditing", this) as? Boolean ?: true
    }

    open fun didEndEditing() {
        EDOJavaHelper.value(this, "didEndEditing", this)
    }

    open fun shouldChange(charactersInRange: UIRange, replacementString: String?): Boolean {
        return EDOJavaHelper.value(this, "shouldChange", this, charactersInRange, replacementString) as? Boolean ?: true
    }

}

fun KIMIPackage.installUITextView() {
    exporter.exportClass(UITextView::class.java, "UITextView", "UIView")
    exporter.exportProperty(UITextView::class.java, "text")
    exporter.exportProperty(UITextView::class.java, "textColor")
    exporter.exportProperty(UITextView::class.java, "font")
    exporter.exportProperty(UITextView::class.java, "textAlignment")
    exporter.exportProperty(UITextView::class.java, "editable")
    exporter.exportProperty(UITextView::class.java, "selectable")
    exporter.exportProperty(UITextView::class.java, "editing", true)
    exporter.exportMethodToJavaScript(UITextView::class.java, "scrollRangeToVisible")
    exporter.exportMethodToJavaScript(UITextView::class.java, "focus")
    exporter.exportMethodToJavaScript(UITextView::class.java, "blur")
    exporter.exportProperty(UITextView::class.java, "autocapitalizationType")
    exporter.exportProperty(UITextView::class.java, "autocorrectionType")
    exporter.exportProperty(UITextView::class.java, "spellCheckingType")
    exporter.exportProperty(UITextView::class.java, "keyboardType")
    exporter.exportProperty(UITextView::class.java, "returnKeyType")
    exporter.exportProperty(UITextView::class.java, "secureTextEntry")
}