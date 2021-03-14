package com.gmail.iikaliada.enums;

public enum EmojiConstants {
    BACK("\u2B06"),
    CHICKEN("\uD83D\uDC23"),
    DICE_ANIM("\uD83C\uDFB2"),//"🎲"
    DARTS_ANIM("\uD83C\uDFAF"),//"🎯"
    BASKET_ANIM("\uD83C\uDFC0"),//"🏀"
    FOOTBALL_ANIM("⚽"),//"⚽"
    CASINO_ANIM("\uD83C\uDFB0")//"🎰"
    ;

    public final String label;

    private EmojiConstants(String label) {
        this.label = label;
    }
}
