package cz.zcu.kiv.eeg.semweb.model.creator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class TestDataCheckListener implements ActionListener {

    private JCheckBox chckBox;
    private JSpinner spinner;


    public TestDataCheckListener(JCheckBox checkBox, JSpinner sizeSelector) {
        this.chckBox = checkBox;
        this.spinner = sizeSelector;
    }

    public void actionPerformed(ActionEvent e) {
        spinner.setEnabled(chckBox.isSelected());
    }
}
