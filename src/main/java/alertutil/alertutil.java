package alertutil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class AlertUtil {
    private static class Holder {
        private static AlertUtil instance = new AlertUtil();
    }

    public static AlertUtil getInstance() {
        return Holder.instance;
    }

    private AlertUtil() {
        rules = new HashMap<String, Rule>();
        addRule("default", 1000, 1);
    }

    private static Map<String, Rule> rules;
    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>(); // TODO: Only save keys in alerts.

    public boolean addRule(String ruleName, Object... params) {
        if (params.length == 1) {
            rules.put(ruleName, new LimitRule((int)params[0]));
            return true;
        }
        if (params.length == 2) {
            rules.put(ruleName, new LimitRule((int)params[0], (int)params[1]));
            return true;
        }
        return false;
    }

    public synchronized void addAlert(String key, String ruleName, String content, boolean refresh) {
        if (alerts.get(key) == null) {
            Alert alert = new Alert(ruleName, content);
            alerts.put(key, alert);
            rules.get(ruleName).action(alert);
            System.out.println("addAlert: key= " + key + " " + "timestamp: " + alert.getTimeStamp());
        }
        if (refresh) { refresh(); }
    }

    // TODO: synchronized or not?
    public synchronized void refresh() {
        try {
            for (String key: alerts.keySet()) {
                Alert alert = alerts.get(key);
                String ruleName = alert.getRuleName();
                Rule rule = rules.get(ruleName);
                if (rule.checkRemove(alert)) {
                    alerts.remove(key);
                } else { rule.setAlert(alert); }
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public synchronized void printAllAlerts() {
        System.out.println("↓↓↓↓↓ Lengths of alerts: " + alerts.size() + " ↓↓↓↓↓");
        for (String key: alerts.keySet()) {
            Alert alert = alerts.get(key);
            System.out.println("Key: " + key + " Content: " + alert.getContent() + " Params: " + alert.getContent());
        }
    }
}

abstract class Rule {
    public abstract boolean checkRemove(Alert alert);  // If the alert should be removed from alerts
    public abstract void action(Alert alert);               // How to send email
    public abstract void setAlert(Alert alert);             // How to update the params in an alert
}

class Alert {
    private String ruleName;
    private String content;
    private long timeStamp;
    private int count;

    public Alert(String ruleName, String content) {
        this.ruleName = ruleName;
        this.content = content;
        this.timeStamp = System.currentTimeMillis();
        this.count = 0;
    }

    public String getRuleName() {
        return ruleName;
    }
    public String getContent() { return content; }
    public long getTimeStamp() { return timeStamp; }
    public void addCount() { this.count ++; }
    public int getCount() { return count; }
    public void setContent(String content) {
        this.content = content;
    }
}

class LimitRule extends Rule {
    private int expireTime;
    private int expireCount = 1;

    public LimitRule(int expireTime, int expireCount) {
        this.expireTime = expireTime;
        this.expireCount = expireCount;
    }

    public LimitRule(int expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public boolean checkRemove(Alert alert) {
        return (System.currentTimeMillis() - alert.getTimeStamp() >= expireTime);
            // TODO: Check if reach n times.
    }

    @Override
    public void action(Alert alert) {
        System.out.println("action: Should send an email right now. Content: " + alert.getContent());
    }

    @Override
    public void setAlert(Alert alert) {
        alert.addCount();
    }
}