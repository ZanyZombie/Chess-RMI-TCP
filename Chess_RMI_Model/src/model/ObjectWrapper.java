package model;

import java.io.Serializable;

public class ObjectWrapper implements Serializable{
    private static final long serialVersionUID = 20210811011L;
    public static final int SERVER_INFORM_CLIENT_NUMBER = 1;
    public static final int SERVER_INFORM_ONLINE_PLAYERS = 2;
    public static final int SERVER_INFORM_PLAYER_CONNECTED = 3;
    public static final int SERVER_INFORM_PLAYER_DISCONNECTED = 4;
    public static final int SERVER_INFORM_NEW_FRIEND = 5;
    public static final int SERVER_INFORM_NEW_FRIEND_REQUEST = 6;
    public static final int SERVER_INFORM_NEW_GROUP_MEMBER = 7;
    public static final int SERVER_INFORM_NEW_GROUP_INVITATION = 8;
    public static final int SERVER_INFORM_INGAME_PLAYERS = 9;
    
    public static final int LOGIN_USER = 11;
    public static final int REPLY_LOGIN_USER = 12;
    public static final int ADD_USER = 13;
    public static final int REPLY_ADD_USER = 14;
    public static final int SEARCH_PLAYER =15;
    public static final int REPLY_SEARCH_PLAYER =16;
    public static final int GET_ALL_INVITATION = 17;
    public static final int REPLY_GET_ALL_INVITATION = 18;
    
    public static final int GET_LIST_FRIEND = 201;
    public static final int REPLY_GET_LIST_FRIEND = 202;
    public static final int GET_FRIEND_RELATIONSHIP = 203;
    public static final int REPLY_GET_FRIEND_RELATIONSHIP = 204;
    public static final int ADD_FRIEND = 23;
    public static final int REPLY_ADD_FRIEND = 24;
    public static final int ACCEPT_FRIEND_REQUEST = 25;
    public static final int REPLY_ACCEPT_FRIEND_REQUEST = 26;
    public static final int DECLINE_FRIEND_REQUEST = 27;
    public static final int REPLY_DECLINE_FRIEND_REQUEST = 28;
    
    
    public static final int GET_LIST_GROUP = 301;
    public static final int REPLY_GET_LIST_GROUP = 302;
    public static final int CREATE_GROUP = 303;
    public static final int REPLY_CREATE_GROUP = 304;
    public static final int GET_LIST_GROUP_MEMBER =305;
    public static final int REPLY_GET_LIST_GROUP_MEMBER = 306;
    public static final int ACCEPT_GROUP_REQUEST = 307;
    public static final int REPLY_ACCEPT_GROUP_REQUEST = 308;
    public static final int DECLINE_GROUP_REQUEST = 309;
    public static final int REPLY_DECLINE_GROUP_REQUEST = 310;
    public static final int SENT_GROUP_INVITATION = 311;
    public static final int REPLY_SENT_GROUP_INVITATION = 312;
    
    public static final int GET_STANDINGS = 401;
    public static final int REPLY_GET_STANDINGS = 402;
    public static final int GET_LIST_TOUR = 403;
    public static final int REPLY_GET_LIST_TOUR = 404;
    
    
    public static final int CHALLENGE = 501;
    public static final int REPLY_CHALLENGE = 502;
    public static final int RECEIVE_CHALLENGE = 503;
    public static final int ACCEPT_CHALLENGE = 504;
    public static final int REPLY_ACCEPT_CHALLENGE = 505;
    public static final int DECLINE_CHALLENGE = 506;
    public static final int SERVER_INFORM_DECLINE_CHALLENGE = 507;
    public static final int SERVER_INFORM_MATCH_FOUND = 508;

//    public static final int FIND_MATCH = 503;
//    public static final int REPLY_FIND_MATCH = 504;
    public static final int SURRENDER = 509;

    public static final int QUIT = 512;
    public static final int OPPONENT_QUIT = 513;
    public static final int OFFER_DRAW = 514;
    public static final int OPPONENT_OFFER_DRAW = 515;
    public static final int PLAY_AGAIN = 516;
    public static final int OPPONENT_PLAY_AGAIN = 517;
    public static final int REMATCH = 518;
//    public static final int REMATCH = 509;
//    public static final int REPLY_REMATCH = 510;
    
    //PLAY GAME
    public static final int MAKE_MOVE = 600;
    public static final int REPLY_MAKE_MOVE = 601;
    public static final int SERVER_INFORM_MAKE_MOVE = 602;
    public static final int SERVER_INFORM_GAME_OVER = 603;
    
    public static final int GAME_FRM = 1000;
    
    private int performative;
    private Object data;
    private Object extraData;
    public ObjectWrapper() {
        super();
    }
    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }

    public ObjectWrapper(int performative, Object data, Object extraData) {
        this.performative = performative;
        this.data = data;
        this.extraData = extraData;
    }
    
    public int getPerformative() {
        return performative;
    }
    public void setPerformative(int performative) {
        this.performative = performative;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }   

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }
    
    
}
