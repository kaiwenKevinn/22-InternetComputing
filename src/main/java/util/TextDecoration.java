package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Kevin
 * @Description
 */
public class TextDecoration {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String Head="---->>>><<<<----";
    public static final void welcome(){
        printPurple(Head+"Welcome!"+Head);
        System.out.println("______   ______  _  __ ___________ \n" +
                "\\____ \\ /  _ \\ \\/ \\/ // __ \\_  __ \\\n" +
                "|  |_> >  <_> )     /\\  ___/|  | \\/\n" +
                "|   __/ \\____/ \\/\\_/  \\___  >__|   \n" +
                "|__|                      \\/    ");
        printPurple(Head+Head);

    }
    public static final String registerAndLogin() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        printBlue("请输入用户名与密码，未注册用户将自动注册");
        printGreen("用户名:");
        String userName = bf.readLine();
        printGreen("密码:");
        String pwd=bf.readLine();
        String input="type=register/login&username="+userName+"&password="+pwd+System.lineSeparator();
        return input;
    }


    /**
     * 输出特定参数
     * @param message 消息
     */
    public static void printBlue(String message) {
        System.out.println(ANSI_BLUE + message + ANSI_RESET);
    }

    /**
     * 输出特定类型内容，如HTTP响应头
     * @param message 消息
     */
    public static void printYellow(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    /**
     * 输出特定步骤相关消息
     * @param message 消息
     */
    public static void printGreen(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }

    /**
     * 未用
     * @param message 消息
     */
    public static void printPurple(String message) {
        System.out.println(ANSI_PURPLE + message + ANSI_RESET);
    }

    /**
     * 输出错误消息
     * @param message 消息
     */
    public static void printRed(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    /**
     * 输出交互相关消息
     * @param message 消息
     */
    public static void printCyan(String message) {
        System.out.println(ANSI_CYAN + message + ANSI_RESET);
    }
}
