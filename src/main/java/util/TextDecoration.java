package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Kevin
 * @Description
 */
public class TextDecoration {
    public static final String Head="---->>>><<<<----";
    public static final void welcome(){
        System.out.println(Head+"Welcome!"+Head);
        System.out.println("______   ______  _  __ ___________ \n" +
                "\\____ \\ /  _ \\ \\/ \\/ // __ \\_  __ \\\n" +
                "|  |_> >  <_> )     /\\  ___/|  | \\/\n" +
                "|   __/ \\____/ \\/\\_/  \\___  >__|   \n" +
                "|__|                      \\/    ");
        System.out.println(Head+Head);

    }
    public static final String registerAndLogin() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("请输入用户名与密码，未注册用户将自动注册");
        System.out.println("用户名:");
        String userName = bf.readLine();
        System.out.println("密码:");
        String pwd=bf.readLine();
        String input="type=register/login&username="+userName+"&password="+pwd+System.lineSeparator();
        return input;
    }
}
