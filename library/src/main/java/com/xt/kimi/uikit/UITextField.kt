package com.xt.kimi.uikit

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.xt.endo.CGRect
import com.xt.endo.EDOJavaHelper
import com.xt.endo.UIRange
import com.xt.kimi.KIMIPackage

enum class UITextFieldViewMode {
    never,
    whileEditing,
    unlessEditing,
    always,
}

enum class UITextAutocapitalizationType {
    none,
    words,
    sentences,
    allCharacters
}

enum class UITextAutocorrectionType {
    default,
    no,
    yes,
}

enum class UITextSpellCheckingType {
    default,
    no,
    yes
}

enum class UIKeyboardType {
    default,
    ASCIICapable,
    numbersAndPunctuation,
    numberPad,
    phonePad,
    emailAddress,
    decimalPad
}

enum class UIReturnKeyType {
    default,
    go,
    next,
    send,
    done
}

private val clearButtonImage = UIImage.fromBase64("iVBORw0KGgoAAAANSUhEUgAAACoAAAAqCAMAAADyHTlpAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABjUExURUdwTKqqqo+OlZGRto+OlZOTmY+OlY+OlJCQlpCQlZGOlpGRnZCQmY+OlJCOlJCOlI+OlI+OlJCOlI+OlJCOlI+OlI+PlI+OlI+OlI+OlI+PlI+OlP///4+OlI+PlY+PlI+OlI/lPb8AAAAgdFJOUwAG4AfzLcP7VS5mFR7Zob+l/Y/eiPd89vXum/AB+HuJvx1C8wAAASxJREFUOMuVlUeigzAMRAWYXkMnEKL7n/LzaRaOKZoVwm+hMpYBjrLHLI2FiNNstOFCdl4gUZGf0VHyQkWvJNKARvlBjarSUMnQwRM54ZH0TTyV6R9IgRcShA1NvJS552A4eCNnq+2LtyrXflb3aLX0N5F/mp6ed638TuZpyhm9rSFwt8ANwHrLuf3PON/D1ppCb2VdbwqsZj/Mp1A6pJvzWdiZhEEmVEzdJ8kFO7uQEJBDH2oSrYDn7h/ksIYMNayGxAxS1LAaElOI8YRVSIxB9dTGqiQKDspIgFEWo1mPRzA+H6xN7dL/2AU6ahdiwubXhO3BhAdrA7X2oFqbXpi2ozX3jXJhIPo8voaMy81ZGYxFxFlvnKXJWcWcBc95NliP0VJfvT1xtVIN/AEiR40jdo0zSQAAAABJRU5ErkJggg==", 3, UIImageRenderingMode.alwaysOriginal)

class UITextField: UINativeTouchView() {

    class NativeEditText: UINativeTouchView() {

        internal val systemEditText = EditText(this.context)

        init {
            systemEditText.setSingleLine()
            systemEditText.setTextColor(Color.BLACK)
            systemEditText.background = null
            addView(systemEditText, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            if (changed) {
                this.systemEditText.width = this.width
                this.systemEditText.height = this.height
            }
        }

    }

    var text: String?
        get() {
            return nativeEditText.systemEditText.text.toString()
        }
        set(value) {
            nativeEditText.systemEditText.text.clear()
            nativeEditText.systemEditText.text.append(value ?: "")
        }

    var textColor: UIColor? = null
        set(value) {
            field = value
            nativeEditText.systemEditText.setTextColor(value?.toInt() ?: Color.BLACK)
            nativeEditText.systemEditText.setHintTextColor((value?.colorWithAlphaComponent(0.35)?.toInt() ?: Color.GRAY))
        }

    var font: UIFont? = null
        set(value) {
            field = value
            value?.let { font ->
                nativeEditText.systemEditText.textSize = (font.pointSize).toFloat()
                font.fontName?.let { fontName ->
                    nativeEditText.systemEditText.typeface = Typeface.create(fontName, kotlin.run {
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
                    nativeEditText.systemEditText.typeface = Typeface.defaultFromStyle(kotlin.run {
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
            nativeEditText.systemEditText.textAlignment = kotlin.run {
                return@run when (value) {
                    UITextAlignment.left -> TEXT_ALIGNMENT_TEXT_START
                    UITextAlignment.center -> TEXT_ALIGNMENT_CENTER
                    UITextAlignment.right -> TEXT_ALIGNMENT_TEXT_END
                }
            }
        }

    var placeholder: String? = null
        set(value) {
            field = value
            nativeEditText.systemEditText.hint = value
        }

    var clearsOnBeginEditing: Boolean = false

    val editing: Boolean
        get() { return nativeEditText.systemEditText.isFocused }

    var clearButtonMode: UITextFieldViewMode = UITextFieldViewMode.never

    var leftView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            reloadExtraContents()
        }

    var leftViewMode: UITextFieldViewMode = UITextFieldViewMode.never
        set(value) {
            field = value
            reloadExtraContents()
        }

    var rightView: UIView? = null
        set(value) {
            field?.removeFromSuperview()
            field = value
            reloadExtraContents()
        }

    var rightViewMode: UITextFieldViewMode = UITextFieldViewMode.never
        set(value) {
            field = value
            reloadExtraContents()
        }

    fun focus() {
        nativeEditText.systemEditText.requestFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(nativeEditText.systemEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun blur() {
        nativeEditText.systemEditText.clearFocus()
        (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(nativeEditText.windowToken, 0)
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
                this.nativeEditText.systemEditText.transformationMethod = PasswordTransformationMethod()
            }
            else {
                this.nativeEditText.systemEditText.transformationMethod = null
            }
            this.resetInputType()
        }

    // Implementation

    private val nativeEditText = NativeEditText()

    private val clearButtonView = object : UIButton(UIButtonType.system) {
        override fun sendEvent(name: String) {
            super.sendEvent(name)
            if (name == "touchUpInside") {
                if (this@UITextField.shouldClear()) {
                    this@UITextField.nativeEditText.systemEditText.text.clear()
                }
            }
        }
    }

    init {
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        addSubview(nativeEditText)
        this.clearButtonView.hidden = true
        this.clearButtonView.setImage(clearButtonImage, UIControlState.normal.rawValue)
        addSubview(this.clearButtonView)
        setupEvents()
        resetInputType()
    }

    private fun setupEvents() {
        nativeEditText.systemEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (!this.shouldBeginEditing()) {
                    this.blur()
                    return@OnFocusChangeListener
                }
                if (this.clearsOnBeginEditing) {
                    nativeEditText.systemEditText.text.clear()
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
            this.reloadExtraContents()
        }
        nativeEditText.systemEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                this@UITextField.reloadExtraContents()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                print(true)
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
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
        nativeEditText.systemEditText.filters = arrayOf(inputFilter)
        nativeEditText.systemEditText.setOnEditorActionListener { _, _, _ ->
            return@setOnEditorActionListener this.shouldReturn()
        }
    }

    private fun reloadExtraContents() {
        val displayClearButton = kotlin.run {
            if (this.clearButtonMode == UITextFieldViewMode.always) {
                return@run true
            }
            else if (!this.editing && this.clearButtonMode == UITextFieldViewMode.unlessEditing) {
                return@run true
            }
            else if (this.editing && this.clearButtonMode == UITextFieldViewMode.whileEditing && this.text?.length ?: 0 > 0) {
                return@run true
            }
            return@run false
        }
        val displayRightView = kotlin.run {
            if (displayClearButton) {
                return@run false
            }
            if (this.rightView == null) { return@run false }
            if (this.rightViewMode == UITextFieldViewMode.always) {
                return@run true
            }
            else if (!this.editing && this.rightViewMode == UITextFieldViewMode.unlessEditing) {
                return@run true
            }
            else if (this.editing && this.rightViewMode == UITextFieldViewMode.whileEditing) {
                return@run true
            }
            return@run false
        }
        val displayLeftView = kotlin.run {
            if (this.leftView == null) { return@run false }
            if (this.leftViewMode == UITextFieldViewMode.always) {
                return@run true
            }
            else if (!this.editing && this.leftViewMode == UITextFieldViewMode.unlessEditing) {
                return@run true
            }
            else if (this.editing && this.leftViewMode == UITextFieldViewMode.whileEditing) {
                return@run true
            }
            return@run false
        }
        this.clearButtonView.hidden = !displayClearButton
        this.clearButtonView.frame = CGRect(this.bounds.width - 36.0, (this.bounds.height - 44.0) / 2.0, 36.0, 44.0)
        if (displayLeftView) {
            this.leftView?.let { leftView ->
                this.addSubview(leftView)
                leftView.frame = CGRect(0.0, (this.bounds.height - leftView.frame.height) / 2.0, leftView.frame.width, leftView.frame.height)
            }
        }
        else {
            this.leftView?.removeFromSuperview()
        }
        if (displayRightView) {
            this.rightView?.let { rightView ->
                this.addSubview(rightView)
                rightView.frame = CGRect(this.bounds.width - rightView.frame.width, (this.bounds.height - rightView.frame.height) / 2.0, rightView.frame.width, rightView.frame.height)
            }
        }
        else {
            this.rightView?.removeFromSuperview()
        }
        this.nativeEditText.frame = CGRect(
                (if(displayLeftView) leftView!!.frame.width else 0.0),
                0.0,
                this.bounds.width - (if(displayLeftView) leftView!!.frame.width else 0.0) - (if (displayRightView) rightView!!.frame.width else 0.0) - (if (displayClearButton) 36.0 else 0.0),
                this.bounds.height)
    }

    private fun resetInputType() {
        if (this.secureTextEntry) {
            this.nativeEditText.systemEditText.inputType = EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        }
        else {
            var inputType = 0
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
            this.nativeEditText.systemEditText.inputType = inputType
        }
        when (this.returnKeyType) {
            UIReturnKeyType.default -> {
                this.nativeEditText.systemEditText.imeOptions = EditorInfo.IME_ACTION_NONE
            }
            UIReturnKeyType.next -> {
                this.nativeEditText.systemEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
            }
            UIReturnKeyType.done -> {
                this.nativeEditText.systemEditText.imeOptions = EditorInfo.IME_ACTION_DONE
            }
            UIReturnKeyType.send -> {
                this.nativeEditText.systemEditText.imeOptions = EditorInfo.IME_ACTION_SEND
            }
            UIReturnKeyType.go -> {
                this.nativeEditText.systemEditText.imeOptions = EditorInfo.IME_ACTION_GO
            }
        }
    }

    override fun tintColorDidChange() {
        super.tintColorDidChange()
        this.tintColor?.let { tintColor ->
            try {
                var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                field.isAccessible = true
                val drawableResId = field.getInt(nativeEditText.systemEditText)
                field = TextView::class.java.getDeclaredField("mEditor")
                field.isAccessible = true
                val editor = field.get(nativeEditText.systemEditText)
                val drawable = ContextCompat.getDrawable(nativeEditText.systemEditText.context, drawableResId)
                drawable.setColorFilter(tintColor.toInt(), PorterDuff.Mode.SRC_IN)
                val drawables = arrayOf(drawable, drawable)
                field = editor.javaClass.getDeclaredField("mCursorDrawable")
                field.isAccessible = true
                field.set(editor, drawables)
            } catch (e: Exception) {}
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        this.nativeEditText.frame = this.bounds
        this.reloadExtraContents()
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

    open fun shouldClear(): Boolean {
        return EDOJavaHelper.value(this, "shouldClear", this) as? Boolean ?: true
    }

    open fun shouldReturn(): Boolean {
        return EDOJavaHelper.value(this, "shouldReturn", this) as? Boolean ?: true
    }

}

fun KIMIPackage.installUITextField() {
    exporter.exportClass(UITextField::class.java, "UITextField", "UIView")
    exporter.exportProperty(UITextField::class.java, "text")
    exporter.exportProperty(UITextField::class.java, "textColor")
    exporter.exportProperty(UITextField::class.java, "font")
    exporter.exportProperty(UITextField::class.java, "textAlignment")
    exporter.exportProperty(UITextField::class.java, "placeholder")
    exporter.exportProperty(UITextField::class.java, "clearsOnBeginEditing")
    exporter.exportProperty(UITextField::class.java, "editing", true)
    exporter.exportProperty(UITextField::class.java, "clearButtonMode")
    exporter.exportProperty(UITextField::class.java, "leftView")
    exporter.exportProperty(UITextField::class.java, "leftViewMode")
    exporter.exportProperty(UITextField::class.java, "rightView")
    exporter.exportProperty(UITextField::class.java, "rightViewMode")
    exporter.exportMethodToJavaScript(UITextField::class.java, "focus")
    exporter.exportMethodToJavaScript(UITextField::class.java, "blur")
    exporter.exportProperty(UITextField::class.java, "autocapitalizationType")
    exporter.exportProperty(UITextField::class.java, "autocorrectionType")
    exporter.exportProperty(UITextField::class.java, "spellCheckingType")
    exporter.exportProperty(UITextField::class.java, "keyboardType")
    exporter.exportProperty(UITextField::class.java, "returnKeyType")
    exporter.exportProperty(UITextField::class.java, "secureTextEntry")
    exporter.exportEnum("UITextFieldViewMode", mapOf(
            Pair("never", UITextFieldViewMode.never),
            Pair("whileEditing", UITextFieldViewMode.whileEditing),
            Pair("unlessEditing", UITextFieldViewMode.unlessEditing),
            Pair("always", UITextFieldViewMode.always)
    ))
    exporter.exportEnum("UITextAutocapitalizationType", mapOf(
            Pair("none", UITextAutocapitalizationType.none),
            Pair("words", UITextAutocapitalizationType.words),
            Pair("sentences", UITextAutocapitalizationType.sentences),
            Pair("allCharacters", UITextAutocapitalizationType.allCharacters)
    ))
    exporter.exportEnum("UITextAutocorrectionType", mapOf(
            Pair("default", UITextAutocorrectionType.default),
            Pair("yes", UITextAutocorrectionType.yes),
            Pair("no", UITextAutocorrectionType.no)
    ))
    exporter.exportEnum("UITextSpellCheckingType", mapOf(
            Pair("default", UITextSpellCheckingType.default),
            Pair("yes", UITextSpellCheckingType.yes),
            Pair("no", UITextSpellCheckingType.no)
    ))
    exporter.exportEnum("UIKeyboardType", mapOf(
            Pair("default", UIKeyboardType.default),
            Pair("ASCIICapable", UIKeyboardType.ASCIICapable),
            Pair("numbersAndPunctuation", UIKeyboardType.numbersAndPunctuation),
            Pair("numberPad", UIKeyboardType.numberPad),
            Pair("phonePad", UIKeyboardType.phonePad),
            Pair("emailAddress", UIKeyboardType.emailAddress),
            Pair("decimalPad", UIKeyboardType.decimalPad)
    ))
    exporter.exportEnum("UIReturnKeyType", mapOf(
            Pair("default", UIReturnKeyType.default),
            Pair("go", UIReturnKeyType.go),
            Pair("next", UIReturnKeyType.next),
            Pair("send", UIReturnKeyType.send),
            Pair("done", UIReturnKeyType.done)
    ))
}