package cz.zcu.kiv.eeg.semweb.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Window closing event handler - call close method when window closed
 * 
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class WindowClosingListener implements WindowListener {

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        closeWindow();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public abstract void closeWindow();
}
