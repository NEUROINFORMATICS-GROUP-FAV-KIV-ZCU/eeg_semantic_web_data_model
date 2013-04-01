package cz.zcu.kiv.eeg.semweb.gui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ComponentWrapper {

    public static Component wrapComponent(JLabel label, Component c) {

        if (c.getClass().equals(JTextField.class) || c.getClass().equals(JPasswordField.class)) {
            JPanel wrapper = new JPanel(new BorderLayout(10, 0));

            wrapper.add(label, BorderLayout.WEST);
            wrapper.add(c, BorderLayout.CENTER);
            return wrapper;
        } else {
            JPanel wrapper = new JPanel(new FlowLayout());

            wrapper.add(label, BorderLayout.WEST);
            wrapper.add(c, BorderLayout.EAST);

            JPanel p2 = new JPanel(new BorderLayout());

            p2.add(wrapper, BorderLayout.WEST);
            return p2;
        }
    }
}
