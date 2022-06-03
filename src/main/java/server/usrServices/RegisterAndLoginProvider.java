package server.usrServices;

import server.HttpResponseReturnValue;
import server.ServerMain;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterAndLoginProvider extends UserServiceProvider {
    static private String LOGIN_SUCCESS_DIR = "/loginSuccess.html";
    static private String LOGIN_FAIL_DIR = "/loginFail.html";
    static private String REGISTER_SUCCESS_DIR = "/registerSuccess.html";
    static private String REGISTER_FAIL_DIR = "/registerFail.html";
    HashMap<String, String> userInfo; // (username, password)

    public RegisterAndLoginProvider() {
        bindUri = "/registerOrLogin";
        lock = new ReentrantLock();
        userInfo = new HashMap<>();
    }

    /**
     * 处理登陆或注册的消息
     * @param arg1 "register" or "login"
     * @param arg2 username
     * @param arg3 password
     * @return
     */
    public HttpResponseReturnValue handle(String arg1, String arg2, String arg3) {
        lock.lock();
        String type = arg1, username = arg2, password = arg3;
        HttpResponseReturnValue ret = null;
        if(type.equals("register/login")){
            if(userInfo.containsKey(username))type="login";
            else {
                type="register";
            }
        }
        if ("register".equals(type)) {
            boolean registerRet = register(username, password);
            if (!registerRet) ret = new HttpResponseReturnValue(200, ServerMain.BIND_DIR + REGISTER_FAIL_DIR);
            else ret = new HttpResponseReturnValue(200, ServerMain.BIND_DIR + REGISTER_SUCCESS_DIR);
        } else if ("login".equals(type)) {
            boolean registerRet = login(username, password);
            if (!registerRet) ret = new HttpResponseReturnValue(200, ServerMain.BIND_DIR + LOGIN_FAIL_DIR);
            else ret = new HttpResponseReturnValue(200, ServerMain.BIND_DIR + LOGIN_SUCCESS_DIR);
        } else {
            // impossible
            assert (false);
        }
        lock.unlock();
        return ret;
    }

    /**
     * 尝试注册
     * @param username
     * @param password
     * @return false：用户已存在; true: 成功创建用户
     */
    private boolean register(String username, String password) {
        if (userInfo.containsKey(username)) return false;
        userInfo.put(username, password);
        return true;
    }

    /**
     * 尝试登陆
     * @param username
     * @param password
     * @return 登陆失败 / 成功
     */
    private boolean login(String username, String password) {
        if (!userInfo.containsKey(username)) return false;
        if (!userInfo.get(username).equals(password)) return false;
        return false;
    }
}
