/**
 * 
 */
package me.olddragon2a;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * @author OldDragon2A
 *
 */
public class Timer extends JFrame implements ActionListener {
  static private final long serialVersionUID = -5610732114416563838L;
  static public Launcher launcher;
  static protected PeriodFormatter format_full = new PeriodFormatterBuilder()
    .printZeroIfSupported()
    .minimumPrintedDigits(1)
    .appendHours().appendSeparator(":")
    .minimumPrintedDigits(2)
    .appendMinutes().appendSeparator(":")
    .appendSeconds().appendSeparator(".")
    .minimumPrintedDigits(3)
    .appendMillis()
    .toFormatter();
  static protected PeriodFormatter format_short = new PeriodFormatterBuilder()
    .minimumPrintedDigits(1)
    .appendHours().appendSeparatorIfFieldsBefore(":")
    .minimumPrintedDigits(2)
    .appendMinutes().appendSeparatorIfFieldsBefore(":")
    .printZeroIfSupported()
    .appendSeconds().appendSeparator(".")
    .minimumPrintedDigits(3)
    .appendMillis()
    .toFormatter();
  static protected PeriodType period_type = PeriodType.time();
  static final Color transparent = new Color(0,0,0,0);
  
  public DateTime started;
  public ArrayList<TimeSpan> times = new ArrayList<TimeSpan>();
  public Period period = new Period(0);
  public int update_speed = 250;
  public boolean was_visible = false;
  
  /**
   * Controls
   */
  protected JPanel panel = new JPanel();
  protected JPopupMenu popup;
  protected JLabel total;
  protected JLabel time;
  protected JMenuItem miToggle;
  protected JMenuItem miAdjust;
  protected JMenuItem miForeground;
  protected JMenuItem miBackground;
  protected JMenuItem miTransparent;
  protected JMenuItem miDelete;
  
  protected javax.swing.Timer timer;
  protected MousePopupListener popup_listener = new MousePopupListener();
  
  public Timer() {
    setBackground(Timer.transparent);
    setSize(new Dimension(300,100));
    setLocationRelativeTo(null);
    
    panel.setBackground(Timer.transparent);
    setContentPane(new AlphaContainer(panel));
    
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    
    c.insets = new Insets(5,5,5,5);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    total = new JLabel();
    total.setText("0000:00:00.000");
    total.setHorizontalAlignment(JLabel.RIGHT);
    Font font = total.getFont().deriveFont((float)20);
    total.setFont(font);
    add(total, c);
    
    c.insets = new Insets(0,5,5,5);
    c.gridy = 1;
    time = new JLabel();
    time.setText("0000:00:00.000");
    time.setHorizontalAlignment(JLabel.RIGHT);
    time.setFont(font);
    add(time, c);
    
    popup = new JPopupMenu();
    miToggle = createMenuItem(started == null ? "Start" : "Stop", this, "toggle", 's');
    popup.add(miToggle);
    miAdjust = createMenuItem("Adjust", this, "adjust", 'a');
    popup.add(miAdjust);
    miForeground = createMenuItem("Foreground", this, "foreground", 'f');
    popup.add(miForeground);
    miBackground = createMenuItem("Background", this, "background", 'b');
    popup.add(miBackground);
    miTransparent = createMenuItem("Transparent", this, "transparent", 't');
    popup.add(miTransparent);
    miDelete = createMenuItem("Delete", this, "delete", 'd');
    popup.add(miDelete);
    addMouseListener(popup_listener);

    timer = new javax.swing.Timer(this.update_speed, this);
    timer.setActionCommand("tick");
    if (started != null) { timer.start(); }
  }
  
  protected JMenuItem createMenuItem(String text, ActionListener listener, String command, char mnemonic) {
    JMenuItem mi = new JMenuItem();
    mi.setText(text);
    mi.addActionListener(listener);
    mi.setActionCommand(command);
    mi.setMnemonic(mnemonic);
    return mi;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("tick")) {
      updateTimes();
    } else if (e.getActionCommand().equals("toggle")) {
      actionToggle();
    } else if (e.getActionCommand().equals("adjust")) {
      actionAdjustment();
    } else if (e.getActionCommand().equals("foreground")) {
      actionForeground();
    } else if (e.getActionCommand().equals("background")) {
      actionBackground();
    } else if (e.getActionCommand().equals("transparent")) {
      actionTransparent();
    } else if (e.getActionCommand().equals("delete")) {
      actionDelete();
    }
  }
  protected void updateTimes() {
    Period current = started == null ? new Period(0) : new Period(started, new DateTime());
    total.setText(Timer.format_full.print(period.plus(current).normalizedStandard(period_type)));
    time.setText(Timer.format_short.print(current.normalizedStandard(period_type)));
  }
  protected void actionToggle() {
    if (started == null) {
      miToggle.setText("Stop");
      started = new DateTime();
      timer.start();
    } else {
      miToggle.setText("Start");
      DateTime end = new DateTime();
      TimeSpan ts = new TimeSpan(started, end);
      times.add(ts);
      period = period.plus(ts.getPeriod()).normalizedStandard();
      started = null;
      timer.stop();
      updateTimes();
    }
  }
  protected void actionAdjustment() {
    String result = (String)JOptionPane.showInputDialog(this, "Enter an adjustment", "0 00:00:00.000");
    if (result != null && !result.isEmpty()) {
      try {
        Period adjustment = format_short.parsePeriod(result);
        DateTime start = new DateTime();
        DateTime end = start.plus(adjustment);
        TimeSpan ts = new TimeSpan(start, end);
        times.add(ts);
        period = period.plus(adjustment);
        updateTimes();
      } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
  protected void actionForeground() {
    Color color = JColorChooser.showDialog(this, "Foreground", panel.getForeground());
    if (color != null) { total.setForeground(color); time.setForeground(color); }
  }
  protected void actionBackground() {
    Color color = JColorChooser.showDialog(this, "Background", panel.getBackground());
    if (color != null) { panel.setBackground(color); }
  }
  protected void actionTransparent() {
    dispose();
    panel.setBackground(Timer.transparent);
    setVisible(true);
  }
  protected void actionDelete() {
    int result = JOptionPane.showConfirmDialog(this, "Are you sure?", "Delete Timer: " + getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (result == JOptionPane.YES_OPTION) {
      setVisible(false);
      Timer.launcher.timers.remove(this);
      Timer.launcher.combobox.removeItem(this);
    }
  }
  
  class MousePopupListener extends MouseAdapter implements Serializable {
    private static final long serialVersionUID = -1112142857364855862L;
    public void mousePressed(MouseEvent e) { checkPopup(e); }
    public void mouseClicked(MouseEvent e) { checkPopup(e); }
    public void mouseReleased(MouseEvent e) { checkPopup(e); }
    private void checkPopup(MouseEvent e) {
      if (e.isPopupTrigger()) { popup.show((Component)e.getSource(), e.getX(), e.getY()); }
    }
  }
}
