package model;

import java.io.Serializable;

public class Invitation implements Serializable{
    public static final String FRIEND_REQUEST = "Bạn bè";
    public static final String GROUP_INVITATION = "Nhóm";
    public static final String TOUR_INVITATION = "Giải đấu";

    private String type;
    private Player inviter;
    private Object target;

    public Invitation() {
    }

    public Invitation(String type, Player inviter, Object target) {
        this.type = type;
        this.inviter = inviter;
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Player getInviter() {
        return inviter;
    }

    public void setInviter(Player inviter) {
        this.inviter = inviter;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Invitation) {
            
            Invitation invitation = (Invitation) object;
            if (invitation.getType().equals(FRIEND_REQUEST))
                return (inviter.equals(invitation.getInviter()));
            if (invitation.getType().equals(GROUP_INVITATION))
                return (((Group) target).getId() == ((Group) invitation.getTarget()).getId() );

        }
        return false;
    }
}
