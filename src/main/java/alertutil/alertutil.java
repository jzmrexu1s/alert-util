package alertutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            return (System.currentTimeMillis() - (int)params[0] >= expireTime);
        }

        @Override
        public void action() {
            System.out.println("Should send an email! ");
        }
    }

    private static Map<String, Rule> rules;

    private AlertUtil() {
        rules = new HashMap<String, Rule>();
        System.out.println(addRule("default", "TimeLimit", 1000));
    }

    public boolean addRule(String ruleName, String ruleType, Object... params) {
        try {
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

    private static Map<String, Alert> alerts = new ConcurrentHashMap<String, Alert>();

    public void addAlert(String key, String ruleName, String content) {
        if (alerts.get(key) == null) {
            Alert alert = new Alert(ruleName, content);
            alerts.put(key, alert);
            System.out.println("In thread " + alert.content + " key= " + key + " " + alert.params.get(0));
        }
    }

    public void refresh() {
        for (String key: alerts.keySet()) {
            Alert alert = alerts.get(key);
            String ruleName = alert.getRuleName();
            try {
                Class<?> cl = Class.forName("alertutil.AlertUtil$" + ruleName);
                Method m = cl.getMethod("checkRemove", Object[].class);
//                m.invoke(null, );
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void printAlerts() {
        System.out.println("last size " + alerts.size() + ". Contents: ");
        for (String key: alerts.keySet()) {
            Alert alert = alerts.get(key);
            System.out.println(alert.content + " " + alert.params.get(0));
        }
    }

    public void getTimeLimitRuleAndPrint(String ruleName) {
        TimeLimitRule rule = (TimeLimitRule) rules.get(ruleName);
        System.out.println(rule.getExpireTime());
    }
}
