package com.gmail.iikaliada.constant;

public class CurrencyCommand {
    public static final String START = "/start";
    public static final String KEYBOARD = "/keyboard";
    public static final String HELP = "/help";

    public static String getCommand() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(START).append(" - Начало работы с ботом").append("\n")
                .append(KEYBOARD).append(" - Вывод клавиатуры на экран").append("\n")
                .append(HELP).append(" - Помощь").append("\n");
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }
}
