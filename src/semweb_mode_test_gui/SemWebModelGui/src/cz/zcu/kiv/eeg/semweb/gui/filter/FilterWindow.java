package cz.zcu.kiv.eeg.semweb.gui.filter;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.ConditionList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public abstract class FilterWindow extends JFrame {

    protected PortalModel model;
    protected ConditionList cond;
    protected JFrame mw;

    public FilterWindow (PortalModel model, JFrame mw, ConditionList c, String name, boolean moveWindow) {

        this.model = model;
        this.cond = c;
        this.mw = mw;

        setTitle(name);
	setSize(420, 160 );

        if (moveWindow) {
            setLocation(760, 320);
        }else {
            setLocation(740, 300);
        }
	setBackground(Color.gray);
        setResizable(false);

        setLayout(new BorderLayout());
        add(createCondListPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        this.setVisible(true);
        mw.setEnabled(false);
    }

    private JPanel createButtonPanel() {

        JButton addCondBtn = new JButton("Add condtion");
        addCondBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addCondition();
            }
        });

        JButton removeAllBtn = new JButton("Remove all");
        removeAllBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeAllCondition();
            }
        });


        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(addCondBtn);
        panel.add(removeAllBtn);
        panel.add(closeBtn);

        return panel;
    }


    private JScrollPane createCondListPanel() {

        JLabel condLabel;
        JButton updBtn;
        JButton deleteBtn;

        JPanel line;


        JPanel propertyPanel = new JPanel(new GridLayout(cond.getConditionList().size(), 1, 0, 5));

        for (int i = 0; i < cond.getConditionList().size(); i++) {

            condLabel = new JLabel("Condition " + i);

            updBtn = new JButton("Update");
            updBtn.addActionListener(new UpdateBtnListener(i));

            deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(new DeleteBtnListener(i));

            line = new JPanel(new FlowLayout());
            line.add(condLabel);
            line.add(updBtn);
            line.add(deleteBtn);

            propertyPanel.add(line);
        }

        JScrollPane mainPanel = new JScrollPane();
        mainPanel.getViewport().add(propertyPanel);

        return mainPanel;
    }

    protected abstract void updateCondition(int index);

    private void deleteCondition(int index) {
        cond.removeCondition(index);
        updateView();

    }

    protected abstract void addCondition();

    private void removeAllCondition() {
        cond.removeAll();
        updateView();
    }

    private void closeWindow() {
        mw.setEnabled(true);
        this.dispose();
    }

    protected void updateView() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(createCondListPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);

        validate();
        repaint();
    }


    private class UpdateBtnListener implements ActionListener {

        private int index;

        public UpdateBtnListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            updateCondition(index);
        }
    }

    private class DeleteBtnListener implements ActionListener {

        private int index;

        public DeleteBtnListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            deleteCondition(index);
        }

    }

}
