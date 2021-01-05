package com.example.myapplication

import android.content.Context
import android.text.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText


class DividerEditText : AppCompatEditText {

    companion object {
        val FORMAT_PATTERN_BANK_CARD = intArrayOf(4)
        val FORMAT_PATTERN_MOBILE_PHONE = intArrayOf(3, 4, 4)
        const val DIVIDER_BLANK = ' '
        const val DIVIDER_HYPHEN = '-'
        const val DEFAULT_MAX_LENGTH = 100
    }


    var dividerCharacter = DIVIDER_HYPHEN
    private var formatPattern = FORMAT_PATTERN_MOBILE_PHONE
    var maxLength = DEFAULT_MAX_LENGTH

    private lateinit var divisionPattern: IntArray

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        val u = Divider()
        addTextChangedListener(u)
        filters = arrayOf<InputFilter>(u)
        divisionPattern = divisionPatter
    }

    /**
     * 字符格式化模式，例如设置为{3,4,4}，字符将要显示成138 3800 3800的形式
     *
     * @param formatPattern
     */
    fun setFormatPattern(maxLength: Int = 100, formatPattern: IntArray= FORMAT_PATTERN_BANK_CARD,dividerChar: Char= DIVIDER_BLANK) {
        this.maxLength = maxLength
        this.formatPattern = formatPattern
        this.dividerCharacter = dividerChar
        init()
    }

    private fun formatText(str: CharSequence): CharSequence {
        var length = str.length
        var lastInsertPos = -1
        // 输入的内容长度较少时，不显示分隔符
        if (length <= formatPattern[0] + 1) {
            return deleteDivideChar(str)
        }
        val tmp = deleteDivideChar(str)
        length = tmp.length
        val builder = StringBuilder()
        var i = 0
        var j = 0
        while (i < length) {
            builder.append(tmp[i])
            if (i - formatPattern[j] - lastInsertPos == 0) {
                lastInsertPos = i
                builder.append(dividerCharacter)
                if (j < formatPattern.size - 1) {
                    j++
                }
            }
            i++
        }
        if (builder[builder.length - 1] == dividerCharacter) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }

    private fun deleteDivideChar(string: CharSequence?): CharSequence {
        val builder = StringBuilder()
        val length = string!!.length
        for (i in 0 until length) {
            if (string[i] != dividerCharacter) {
                builder.append(string[i])
            }
        }
        return builder
    }


    /**
     * 获取去掉分隔符的内容
     *
     * @return 去掉了分隔符的内容
     */
    val realInputText: String
        get() = deleteDivideChar(text).toString()


    private val divisionPatter: IntArray
        get() {
            var len = 0
            var i = 0
            while (len < maxLength) {
                len += if (i < formatPattern.size) {
                    formatPattern[i]
                } else {
                    formatPattern[formatPattern.size - 1]
                }
                i++
            }
            val arrayInt = IntArray(--i)
            arrayInt[0] = formatPattern[0]
            for (j in 1 until i) {
                arrayInt[j] = arrayInt[j - 1]
                if (j < formatPattern.size) {
                    arrayInt[j] += formatPattern[j] + 1
                } else {
                    arrayInt[j] += formatPattern[formatPattern.size - 1] + 1
                }
            }
            return arrayInt
        }

    internal inner class Divider : TextWatcher, InputFilter {
        private var deleteDivider = 0
        override fun afterTextChanged(source: Editable) {
            if (deleteDivider > 1) {
                val tmp = deleteDivider
                deleteDivider = 0
                source.delete(tmp - 1, tmp)
            }
            var j = 0
            var i = 0
            while (i < source.length) {
                val character = source[i]
                if (i == divisionPattern[j]) {
                    if (j < divisionPattern.size - 1) {
                        j++
                    }
                    if (character != dividerCharacter) {
                        source.insert(i, dividerCharacter.toString())
                    }
                } else if (character == dividerCharacter) {
                    source.delete(i, i + 1)
                    i--
                }
                i++
            }
        }

        override fun beforeTextChanged(a: CharSequence,b: Int, c: Int, d: Int) {}
        override fun onTextChanged(a: CharSequence,b: Int, c: Int, d: Int ) {}

        override fun filter(
            source: CharSequence, start: Int,
            vend: Int, dest: Spanned, dstart: Int, dend: Int
        ): CharSequence {
            var end = vend
            if (deleteDivideChar(
                    SpannableStringBuilder(dest).replace(
                        dstart, dend,
                        source, start, end
                    ).toString()
                ).length > maxLength
            ) return ""
            val result = SpannableStringBuilder(source)
            val arrayOfInt = divisionPattern
            val dLength = dend - dstart

            for (i in arrayOfInt.indices) {
                if (source.isEmpty() && dstart == arrayOfInt[i] && dest[dstart] == dividerCharacter) {
                    deleteDivider = arrayOfInt[i]
                }
                var j = 0
                if ((dstart - dLength <= arrayOfInt[i])
                    && (dstart + end - dLength >= arrayOfInt[i])
                    && ((((arrayOfInt[i] - dstart).also { j = it }) == end) || (j in 0 until end && result[j] != dividerCharacter)))
                 {
                    result.insert(j, dividerCharacter.toString())
                    end++
                }
            }
            return result
        }
    }
}