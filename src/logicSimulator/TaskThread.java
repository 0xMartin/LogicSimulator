/*
 * Logic simlator
 * Author: Martin Krcma
 */
package logicSimulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Martin
 */
public class TaskThread {

    private final ActionListener action;

    private Thread thread;

    private boolean run = false;

    private boolean abort = false;

    public TaskThread(ActionListener action) {
        this.action = action;
    }

    public void invokeAction() {
        this.run = true;
    }

    public void run() {
        this.thread = new Thread(() -> {
            while (true) {
                if (this.abort) {
                    break;
                }
                if (this.run) {
                    if (this.action != null) {
                        this.action.actionPerformed(new ActionEvent(this, 0, ""));
                        this.run = false;
                    }
                }
            }
        });
        this.thread.start();
    }

    public void abort() {
        this.abort = true;
    }

}
