package alertutil;

import java.util.concurrent.TimeUnit;

public class testAddRule {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new ruleAdder();
        t.start();
    }

    static class ruleAdder extends Thread {
        @Override
        public void run() {
            AlertUtil a = AlertUtil.getInstance();
            a.addRule("testNewRule", "TimeLimit", 800);
            a.getTimeLimitRuleAndPrint("testNewRule");
        }
    }
}
