package cz.zcu.kiv.eeg.semweb.model.creator.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * Data model input XML file JFilechooser dialog
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class InputFileFieldListener implements MouseListener {

    private JTextField fileName;

    public InputFileFieldListener(JTextField fileName) {
        this.fileName = fileName;
    }

    /**
     * Open JFile chooser to allow comfort file selecting
     * 
     * @param e
     */
    public void mouseClicked(MouseEvent e) {

        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.getName().toLowerCase().endsWith("xml") || f.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public String getDescription() {
                return "XML file";
            }
        });

        if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {

            fileName.setText(chooser.getSelectedFile().getAbsolutePath());
        } else {
            return;
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
