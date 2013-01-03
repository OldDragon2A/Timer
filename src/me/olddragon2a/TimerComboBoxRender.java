/**
 * 
 */
package me.olddragon2a;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author OldDragon2A
 * 
 */
public class TimerComboBoxRender extends JLabel implements ListCellRenderer<Timer> {
  private static final long serialVersionUID = -5588726377513491637L;

  public TimerComboBoxRender() {
    setOpaque(true);
    setHorizontalAlignment(LEFT);
    setVerticalAlignment(CENTER);
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends Timer> list, Timer value, int index, boolean isSelected, boolean cellHasFocus) {
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    if (value == null) {
      setText("No Timers");
    } else if (value.getTitle().isEmpty()) {
      setText("Unlabeled");
    } else {
      setText(value.getTitle());
    }
    return this;
  }
}
