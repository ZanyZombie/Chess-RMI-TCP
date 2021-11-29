package chess;

import java.io.Serializable;

public class Move implements Serializable{
    public static final int NORMAL = 0;
    public static final int CASTLE = 1;
    public static final int ENPASSANT = 2;
    public static final int PROMOTE_TO_QUEEN = 3;
    public static final int PAWN_FIRST_MOVE = 4;

    private int xBefore, yBefore, xAfter, yAfter;
    private int type;

    public Move() {
        this.type = NORMAL;
    }

    
    public Move(int xBefore, int yBefore, int xAfter, int yAfter) {
        this.xBefore = xBefore;
        this.yBefore = yBefore;
        this.xAfter = xAfter;
        this.yAfter = yAfter;
        this.type = NORMAL;
    }

    public Move(int xBefore, int yBefore, int xAfter, int yAfter, int type) {
        this.xBefore = xBefore;
        this.yBefore = yBefore;
        this.xAfter = xAfter;
        this.yAfter = yAfter;
        this.type = type;
    }

    public int getxBefore() {
        return xBefore;
    }

    public void setxBefore(int xBefore) {
        this.xBefore = xBefore;
    }

    public int getyBefore() {
        return yBefore;
    }

    public void setyBefore(int yBefore) {
        this.yBefore = yBefore;
    }

    public int getxAfter() {
        return xAfter;
    }

    public void setxAfter(int xAfter) {
        this.xAfter = xAfter;
    }

    public int getyAfter() {
        return yAfter;
    }

    public void setyAfter(int yAfter) {
        this.yAfter = yAfter;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (this.xBefore != other.xBefore) {
            return false;
        }
        if (this.yBefore != other.yBefore) {
            return false;
        }
        if (this.xAfter != other.xAfter) {
            return false;
        }
        if (this.yAfter != other.yAfter) {
            return false;
        }
        return true;
    }
	
}