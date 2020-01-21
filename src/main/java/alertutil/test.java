package alertutil;

import java.util.concurrent.TimeUnit;

public class test {
    public static void main(String[] args) throws InterruptedException {
        remove_test();
        // func(id, content, timelimit, quantitylimit)
    }

    public static void remove_test() throws InterruptedException {
//        Thread a1 = new ruleAdder("testRule", "Limit", new Object[]{2000});
//        System.out.println("Run a thread to create new rule: The alert will be removed in 2 sec after it was added to a list of alerts. ");
//        System.out.println("(In the default rule, alert will be removed in 1 sec after it was added to a list of alerts. )");
//        a1.start();
        System.out.println("------0.0 sec------");
        System.out.println("Run thread t1 to add an alert with key t1, using the new rule. ");
        Thread t1 = new alert_invoker("t1", "Content of t1", 2000, 1);
        t1.start();
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("------0.5 sec------");
        System.out.println("Run thread t2 to add an alert with key t2, using the default rule. ");
        Thread t2 = new alert_invoker("t2", "content of t2", 1000, 1);
        t2.start();
        System.out.println("Run thread t3 to add an alert with key t3, using the default rule. ");
        Thread t3 = new alert_invoker("t3", "content of t3", 1000, 1);
        t3.start();
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("------0.6 sec------");
        System.out.println("Run a thread to print all alerts. ");
        Thread o1 = new alert_out();
        o1.start();
        TimeUnit.MILLISECONDS.sleep(400);
        System.out.println("------1.0 sec------");
        System.out.println("Run thread t4 to add an alert with key t2, using the default rule. ");
        Thread t4 = new alert_invoker("t2", "content of t4", 1000, 1);
        t4.start();
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("------1.1 sec------");
        System.out.println("Run a thread to print all alerts. Notice that Params of t2 has not been changed. ");
        Thread o2 = new alert_out();
        o2.start();
        TimeUnit.MILLISECONDS.sleep(400);
        System.out.println("------1.5 sec------");
        System.out.println("Run thread t5 to add an alert with key t5, using the default rule. Notice that t2 and t3 should be removed. ");
        Thread t5 = new alert_invoker("t5", "content of t5", 1000, 1);
        t5.start();
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("------1.6 sec------");
        System.out.println("Run a thread to print all alerts. ");
        Thread o3 = new alert_out();
        o3.start();
        TimeUnit.MILLISECONDS.sleep(900);
        System.out.println("------2.5 sec------");
        System.out.println("Run a thread to directly refresh. Notice that t1 and t5 should be removed. ");
        Thread r = new alert_refresh();
        r.start();
        System.out.println("Run a thread to print all alerts. ");
        Thread o4 = new alert_out();
        o4.start();
    }

    public static void basic_test() throws InterruptedException {
    }

//    static class ruleAdder extends Thread {
//        private String ruleName;
//        private String ruleType;
//        private Object[] params;
//
//        public ruleAdder(String ruleName, String ruleType, Object[] params) {
//            this.ruleName = ruleName;
//            this.ruleType = ruleType;
//            this.params = params;
//        }
//
//        @Override
//        public void run() {
//            AlertUtil a = AlertUtil.getInstance();
//            a.addRule(ruleName, params);
//        }
//    }

    static class alert_invoker extends Thread {
        private String id;
//        private String ruleName;
        private String content;
        private int timeLimit;
        private int countLimit = 1;

        public alert_invoker(String id, String content, int timeLimit, int countLimit) {
            this.id = id;
            this.content = content;
            this.timeLimit = timeLimit;
            this.countLimit = countLimit;
        }

        @Override
        public void run() {
            AlertUtil.addAlert(id, content, timeLimit, countLimit);
        }
    }

    static class alert_out extends Thread {
        @Override
        public void run() {
            AlertUtil.printAllAlerts();
        }
    }

    static class alert_refresh extends Thread {
        @Override
        public void run() {
            AlertUtil.refresh();
        }
    }
}
