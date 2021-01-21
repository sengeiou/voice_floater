package com.gydx.utils;

import com.vdurmont.emoji.EmojiParser;

/**
 * @author 拽小白
 * @createTime 2020-10-30 13:16
 * @description
 */
public class EmojiUtil {

    /** 将emoji转成html */
    public static String emojiToHtml(String emoji) {
        return EmojiParser.parseToHtmlDecimal(emoji);
    }

    /** 将html转成emoji */
    public static String htmlToEmoji(String html) {
        return EmojiParser.parseToUnicode(html);
    }
}
