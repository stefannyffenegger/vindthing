package ch.vindthing.controller;

/**
 * This interface is used as an observer for the ActiveUserManager class
 */
public interface ActiveUserChangeListener {
    /**
     * Call when Observable's internal state is changed
     */
    void notifyActiveUserChange();
}