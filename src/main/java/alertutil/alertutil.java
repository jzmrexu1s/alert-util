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
        addRule("default", "TimeLimit", 1000);
    }

    private static Map<String, Rule> rules;
    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>(); // TODO: Only save keys in alerts.

    public boolean addRule(String ruleName, String ruleType, Object... params) {
        if (rules.get(ruleType) != null) return false;
        if (ruleType.equals("TimeLimit")) {
            addTimeLimitRule(ruleName, (int)params[0]);
            return true;
        }
        return false;
    }

    private void addTimeLimitRule(String ruleName, int expireTime) {
        rules.put(ruleName, new TimeLimitRule(expireTime));
    }

    public synchronized void addAlert(String key, String ruleName, String content, boolean refresh) {
        if (alerts.get(key) == null) {
            Alert alert = new Alert(ruleName, content, new Object[]{System.currentTimeMillis()});
            alerts.put(key, alert);
            rules.get(ruleName).action(alert);
            System.out.println("addAlert: key= " + key + " " + "timestamp: " + alert.getParams()[0]);
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
                if (rule.checkRemove(alert.getParams())) {
                    alerts.remove(key);
                }
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
    public abstract boolean checkRemove(Object... params);  // If the alert should be removed from alerts
    public abstract void action(Alert alert);      // How to send email
}

class Alert {
    private String ruleName;
    private String content;
    private Object[] params;

    public Alert(String ruleName, String content, Object[] params) {
        this.ruleName = ruleName;
        this.content = content;
        this.params = params;
    }

    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Object[] getParams() { return params; }
    public void setParams(Object[] params) { this.params = params; }
}

class TimeLimitRule extends Rule {
    private int expireTime;
    public TimeLimitRule(int expireTime) {
        this.expireTime = expireTime;
    }
    private int getExpireTime() {
        return expireTime;
    }
    private void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public boolean checkRemove(Object... params) {
        return (System.currentTimeMillis() - (long)params[0] >= expireTime);
            // TODO: Check if reach n times.
    }

    @Override
    public void action(Alert alert) {
        System.out.println("action:" + "Should send an email right now. Content: " + alert.getContent());
    }
}