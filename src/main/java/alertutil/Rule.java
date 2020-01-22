package alertutil;

abstract class Rule {
    public abstract boolean checkRemove(AlertInfo alert);       // If the alert should be removed from alerts
    public abstract boolean checkReachCount(AlertInfo alert);   // If the alert reaches its count limit
    public abstract void action(String content);                // How to send email
    public abstract void setAlert(AlertInfo alert);             // How to update the params in an alert
}