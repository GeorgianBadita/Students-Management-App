package utils;

import controllers.AuthenticationInterface;

public class GUIEvent implements Event {
    private AuthenticationInterface element;

    public GUIEvent(AuthenticationInterface element){
        this.element = element;
    }

    public AuthenticationInterface getData(){
        return this.element;
    }
}
