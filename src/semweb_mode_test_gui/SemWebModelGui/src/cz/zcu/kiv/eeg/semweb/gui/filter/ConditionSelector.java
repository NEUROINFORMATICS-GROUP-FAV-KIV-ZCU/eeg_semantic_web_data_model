package cz.zcu.kiv.eeg.semweb.gui.filter;

import cz.zcu.kiv.eeg.semweb.model.api.PortalModel;
import cz.zcu.kiv.eeg.semweb.model.api.data.wrapper.UriItem;
import cz.zcu.kiv.eeg.semweb.model.search.Condition;
import cz.zcu.kiv.eeg.semweb.model.search.ConditionList;
import cz.zcu.kiv.eeg.semweb.model.search.HasPropertyCondition;
import cz.zcu.kiv.eeg.semweb.model.search.HasPropertyLikeCondition;
import cz.zcu.kiv.eeg.semweb.model.search.PropertyValEqCondition;
import cz.zcu.kiv.eeg.semweb.model.search.PropertyValLikeCondition;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Filip Markvart filip.marq (at) seznam.cz
 */
public class ConditionSelector extends JFrame {

    private PortalModel model;
    private ConditionList cond;
    private int condIndex;
    private JFrame mw;


    private JComboBox condType;
    private JTextField propertyName;
    private JTextField propertyVal;

    private String [] propTypes = {"Has property", "Has property like", "Has property value", "Has property value like"};

    public ConditionSelector (PortalModel model, JFrame mw, ConditionList c, int condIndex) {

        this.model = model;
        this.cond = c;
        this.mw = mw;
        this.condIndex = condIndex;

        setTitle("Condition setting");
	setSize(420, 180);
        setLocation(790, 350);

        setBackground(Color.gray);
        setResizable(false);

        setLayout(new BorderLayout());
        add(createCondListPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setActual();

        this.setVisible(true);
        mw.setEnabled(false);
    }

    private JPanel createButtonPanel() {

        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setCondition();
            }
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(okBtn);

        return panel;
    }


    private JPanel createCondListPanel() {

        JPanel propertyPanel = new JPanel(new GridLayout(3, 1, 0, 5));

        propertyName = new JTextField();
        propertyName.setPreferredSize(new Dimension(250, 30));
        propertyVal = new JTextField();
        propertyVal.setPreferredSize(new Dimension(250, 30));
        propertyVal.setEnabled(false);

        condType = new JComboBox(propTypes);
        condType.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (condType.getSelectedIndex() < 2) {
                    propertyVal.setEnabled(false);
                }else {
                    propertyVal.setEnabled(true);
                }
            }
        });

        JPanel line = new JPanel(new FlowLayout());
        line.add(new JLabel("Condition type "));
        line.add(condType);
        propertyPanel.add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("Property name"));
        line.add(propertyName);
        propertyPanel.add(line);

        line = new JPanel(new FlowLayout());
        line.add(new JLabel("Property value"));
        line.add(propertyVal);
        propertyPanel.add(line);

        return propertyPanel;
    }


    private void setCondition() {
       
        Condition c;
        
        String selectedType = condType.getSelectedItem().toString();

        if (selectedType.equals(propTypes[0])) {
            c = new HasPropertyCondition(new UriItem(propertyName.getText(), model));
        }else if (selectedType.equals(propTypes[1])) {
            c = new HasPropertyLikeCondition(propertyName.getText());
        }else if (selectedType.equals(propTypes[2])) {
            c = new PropertyValEqCondition(new UriItem(propertyName.getText(), model), propertyVal.getText());
        }else {
            c = new PropertyValLikeCondition(new UriItem(propertyName.getText(), model), propertyVal.getText());
        }

        cond.replaceCondition(c, condIndex);

        mw.setEnabled(true);
        this.dispose();
    }

    private void setActual() {

        Condition actual = cond.getCond(condIndex);

        if (actual.getClass().equals(HasPropertyCondition.class)) {
            condType.setSelectedIndex(0);
            propertyName.setText(((HasPropertyCondition)actual).getPredicate());

        }else if (actual.getClass().equals(HasPropertyLikeCondition.class)) {
            condType.setSelectedIndex(1);
            propertyName.setText(((HasPropertyLikeCondition)actual).getPredicate());

        } else if (actual.getClass().equals(PropertyValEqCondition.class)) {
            condType.setSelectedIndex(2);
            propertyName.setText(((PropertyValEqCondition)actual).getPredicate());
            propertyVal.setText(((PropertyValEqCondition)actual).getObject());
        }else {
            condType.setSelectedIndex(3);
            propertyName.setText(((PropertyValLikeCondition)actual).getPredicate());
            propertyVal.setText(((PropertyValLikeCondition)actual).getObject());
        }


    }

    
}
