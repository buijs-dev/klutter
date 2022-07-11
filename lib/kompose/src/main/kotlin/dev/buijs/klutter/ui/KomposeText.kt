package dev.buijs.klutter.ui

import dev.buijs.klutter.core.KlutterException

class Text(
    /**
     * The text to display.
     */
    val data: String,

    /**
     * How the text should be aligned horizontally.
     */
    val textAlign: TextAlign? = null,

    /**
     * The directionality of the text.
     *
     * This decides how [textAlign] values like [TextAlign.start] and
     * [TextAlign.end] are interpreted.
     *
     * This is also used to disambiguate how to render bidirectional text. For
     * example, if the [data] is an English phrase followed by a Hebrew phrase,
     * in a [TextDirection.ltr] context the English phrase will be on the left
     * and the Hebrew phrase to its right, while in a [TextDirection.rtl]
     * context, the English phrase will be on the right and the Hebrew phrase on
     * its left.
     */
    val textDirection: TextDirection? = null,

    /**
     * How visual overflow should be handled.
     */
    val overflow: TextOverflow? = null,

    /** Whether the text should break at soft line breaks.
     *
     * If false, the glyphs in the text will be positioned as if there was unlimited horizontal space.
     */
    val softWrap: Boolean? = null,

    /**
     * The number of font pixels for each logical pixel.
     *
     * For example, if the text scale factor is 1.5, text will be 50% larger than
     * the specified font size.
     *
     */
    val textScaleFactor: Double? = null,

    /**
     * An optional maximum number of lines for the text to span, wrapping if necessary.
     * If the text exceeds the given number of lines, it will be truncated according
     * to [overflow].
     */
    val maxLines: Int? = null,

    /**
     * An alternative semantics label for this text.
     */
    val semanticsLabel: String? = null,

    /**
     * The different ways of measuring the width of one or more lines of text.
     */
    val textWidthBasis: TextWidthBasis? = null,
): Kompose {

    override fun hasChild(): Boolean = false

    override fun hasChildren(): Boolean = false

    override fun child(): Kompose {
        throw KlutterException("Text has no child")
    }

    override fun children(): List<Kompose> = emptyList()

    override fun print(): String {
        return """PlatformText('$data')"""
    }

}

/**
 * Whether and how to align text horizontally.
 * The order of this enum must match the order
 * of the values in RenderStyleConstants.h's ETextAlign.
 */
enum class TextAlign {

    /**
     * Align the text on the left edge of the container.
     */
    left,

    /**
     * Align the text on the right edge of the container.
     */
    right,

    /**
     * Align the text in the center of the container.
     */
    center,

    /**
     * Stretch lines of text that end with a soft line break to fill the width of the container.
     *
     * Lines that end with hard line breaks are aligned towards the [start] edge.
     */
    justify,

    /**
     * Align the text on the leading edge of the container.
     *
     * For left-to-right text, this is the left edge.
     *
     * For right-to-left text, this is the right edge.
     */
    start,

    /**
     * Align the text on the trailing edge of the container.
     *
     * For left-to-right text, this is the right edge.
     *
     * For right-to-left text, this is the left edge.
     */
    end
}

/**
 *  A direction in which text flows.
 *
 *  Some languages are written from the left to the right (for example, English,
 *  Tamil, or Chinese), while others are written from the right to the left (for
 *  example Aramaic, Hebrew, or Urdu). Some are also written in a mixture, for
 *  example Arabic is mostly written right-to-left, with numerals written
 *  left-to-right.
 *
 *  The text direction must be provided to APIs that render text or lay out
 *  boxes horizontally, so that they can determine which direction to start in:
 *  either right-to-left, [TextDirection.rtl]; or left-to-right,
 *  [TextDirection.ltr].
 */
enum class TextDirection {

    /**
     * The text flows from right to left (e.g. Arabic, Hebrew).
     */
    rtl,

    /**
     * The text flows from left to right (e.g., English, French).
     */
    ltr
}

/**
 * How overflowing text should be handled.
 *
 * A [TextOverflow] can be passed to [Text] and [RichText] via their
 * [Text.overflow] and [RichText.overflow] properties respectively.
 */
enum class TextOverflow {
    /** Clip the overflowing text to fix its container. */
    clip,

    /** Fade the overflowing text to transparent. */
    fade,

    /** Use an ellipsis to indicate that the text has overflowed. */
    ellipsis,

    /** Render overflowing text outside of its container. */
    visible
}

/**
 * The different ways of measuring the width of one or more lines of text.
 */
enum class TextWidthBasis {

    /**
     * multiline text will take up the full width given by the parent. For single
     * line text, only the minimum amount of width needed to contain the text
     * will be used. A common use case for this is a standard series of
     * paragraphs.
     */
    parent,

    /**
     * The width will be exactly enough to contain the longest line and no
     * longer. A common use case for this is chat bubbles.
     */
    longestLine
}