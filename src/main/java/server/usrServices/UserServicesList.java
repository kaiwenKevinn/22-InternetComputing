package server.usrServices;

import java.util.ArrayList;

public class UserServicesList {
    private ArrayList<UserServiceProvider> userServiceProviders;

    public UserServicesList() {
        userServiceProviders = new ArrayList<>();
        userServiceProviders.add(new RegisterAndLoginProvider());
        userServiceProviders.add(new FileUploadProvider());
    }

    public ArrayList<UserServiceProvider> getServiceProviders() {
        return userServiceProviders;
    }
}
