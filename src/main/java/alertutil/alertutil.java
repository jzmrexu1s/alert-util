package alertutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.ScatteringByteChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class AlertUtil {
    private static class Holder {
        private static AlertUtil instance = new AlertUtil();
    }

    public static AlertUtil getInstance() {
        return Holder.instance;
    }

    public static abstract class Rule {
        public abstract boolean checkRemove(Object... params);  // If the alert should be removed from alerts
        public abstract void action();      // How to send email
    }
    public static class TimeLimitRule extends Rule {
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
            if (System.currentTimeMillis() - (long)params[0] >= expireTime) {
                // TODO: Check if reach n times.
                // TODO: Update the list and variables here.
                action();
                return true;
            }
            return false;
        }

        @Override
        public void action() {
            System.out.println("action: Should send an email right now. ");
        }
    }

    private static Map<String, Rule> rules;

    private AlertUtil() {
        rules = new HashMap<String, Rule>();
        addRule("default", "TimeLimit", 1000);
    }

    public boolean addRule(String ruleName, String ruleType, Object... params) {
        try {
            // TODO: cancel reflects.
            Class<?> cl = Class.forName("alertutil.AlertUtil$" + ruleType + "Rule");
            Constructor<?>[] cons = cl.getConstructors();
            Rule rule = (Rule) cons[0].newInstance(params);
            rules.put(ruleName, rule);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Alert {
        private String ruleName;
        private String content;
        private ArrayList<Object> params;

        public Alert(String ruleName, String content) {
            this.ruleName = ruleName;
            this.content = content;
            this.params = new ArrayList<>();
            this.params.add(System.currentTimeMillis());
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
    }

    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>(); // TODO: Only save keys in alerts.

    public synchronized void addAlert(String key, String ruleName, String content, boolean refresh) {
        if (alerts.get(key) == null) {
            Alert alert = new Alert(ruleName, content);
            alerts.put(key, alert);
            System.out.println("addAlert: key= " + key + " " + "timestamp: " + alert.params.get(0));
        }
        if (refresh) { refresh(); }
    }
    // TODO: synchronized or not?
    public synchronized void refresh() {
        for (String key: alerts.keySet()) {
            Alert alert = alerts.get(key);
            String ruleName = alert.getRuleName();
            Rule rule = rules.get(ruleName);
            Object[] l = new Object[alert.params.size()];
            try {
                if (rule.checkRemove(alert.params.toArray(l))) {
                    System.out.println("refresh: Alert with key " + key + " has been removed");
                    alerts.remove(key);
                }
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public synchronized void printAllAlerts() {
        System.out.println("↓↓↓↓↓ Lengths of alerts: " + alerts.size() + " ↓↓↓↓↓");
        for (String key: alerts.keySet()) {
            Alert alert = alerts.get(key);
            System.out.println("Key: " + key + " Content: " + alert.content + " Params: " + alert.params);
        }
    }
}
