package com.mocca_capstone.potlatch.models;

/**
 * Created by nektario on 10/21/2014.
 */
public interface Votable {
    public enum State {
        ON(1), OFF(0);

        private int mDirection;

        State(int dir) {
            mDirection = dir;
        }

        public int getValue() {
            return mDirection;
        }
    }
    public enum Type { FLAG, TOUCH }

    public long getId();

    public long getTouchCount();
    public void addTouch(long userId);
    public void removeTouch(long userId);
    public String getTouchedUsersAsString();

    public long getFlagCount();
    public void addFlag(long userId);
    public void removeFlag(long userId);
    public String getFlaggedByUsersAsString();
}
