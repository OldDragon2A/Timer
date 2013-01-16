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

import org.jdom2.Element;
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
  protected JLabel total = new JLabel();
  protected JLabel time = new JLabel();
  protected JPopupMenu popup = new JPopupMenu();
  protected JMenuItem miToggle = createMenuItem("Start", this, "toggle", 's');
  protected JMenuItem miAdjust = createMenuItem("Adjust", this, "adjust", 'a');
  protected JMenuItem miForeground = createMenuItem("Foreground", this, "foreground", 'f');
  protected JMenuItem miBackground = createMenuItem("Background", this, "background", 'b');
  protected JMenuItem miTransparent = createMenuItem("Transparent", this, "transparent", 't');
  protected JMenuItem miDelete = createMenuItem("Delete", this, "delete", 'd');
  
  protected javax.swing.Timer timer;
  protected MousePopupListener popup_listener = new MousePopupListener();
  
  public Timer() {
    setBackground(Timer.transparent);
    setSize(new Dimension(300,100));
    setLocationRelativeTo(null);
    panel.setBackground(Color.white);
    setContentPane(new AlphaContainer(panel));
    setLayout(new GridBagLayout());
    
    GridBagConstraints c = new GridBagConstraints();
    
    c.insets = new Insets(5,5,5,5);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    total.setText("0000:00:00.000");
    total.setHorizontalAlignment(JLabel.RIGHT);
    Font font = total.getFont().deriveFont((float)20);
    total.setFont(font);
    add(total, c);
    
    c.insets = new Insets(0,5,5,5);
    c.gridy = 1;
    time.setText("0000:00:00.000");
    time.setHorizontalAlignment(JLabel.RIGHT);
    time.setFont(font);
    add(time, c);
    
    popup.add(miToggle);
    popup.add(miAdjust);
    popup.add(miForeground);
    popup.add(miBackground);
    popup.add(miTransparent);
    popup.add(miDelete);
    addMouseListener(popup_listener);

    timer = new javax.swing.Timer(this.update_speed, this);
    timer.setActionCommand("tick");
    if (started != null) { timer.start(); }
  }
  public Timer(Element timer) {
    this();
    
    setTitle(timer.getChildTextNormalize("title"));
    String started = timer.getChildTextNormalize("started");
    this.started = started.isEmpty() ? null : DateTime.parse(started);
    if (this.started != null) { this.timer.start(); }
    period = Period.parse(timer.getChildTextNormalize("total"));
    
    update_speed = Integer.parseInt(timer.getChildTextNormalize("speed"), 10);
    this.timer.setDelay(update_speed);
    if (this.started != null) { this.timer.start(); }
    
    setLocation(Integer.parseInt(timer.getChildTextNormalize("x"), 10), Integer.parseInt(timer.getChildTextNormalize("y"), 10));
    setSize(Integer.parseInt(timer.getChildTextNormalize("width"), 10), Integer.parseInt(timer.getChildTextNormalize("height"), 10));
    
    Color foreground = new Color((int) Long.parseLong(timer.getChildTextNormalize("foreground"), 16), true);
    total.setForeground(foreground);
    time.setForeground(foreground);
    Color background = new Color((int) Long.parseLong(timer.getChildTextNormalize("background"), 16), true);
    panel.setBackground(background);
    
    Element times = timer.getChild("times");
    for(Element ts : times.getChildren()) { this.times.add(new TimeSpan(ts)); }
    
    updateTimes();
    setVisible(Boolean.parseBoolean(timer.getChildText("visible")));
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
  
  public Element toXML() {
    Element result = new Element("timer");
    
    XMLUtil.createElement("title", getTitle(), result);
    XMLUtil.createElement("started", started == null ? "" : started.toString(), result);
    XMLUtil.createElement("total", period.toString(), result);
    XMLUtil.createElement("speed", update_speed, result);
    
    XMLUtil.createElement("x", getX(), result);
    XMLUtil.createElement("y", getY(), result);
    XMLUtil.createElement("width", getWidth(), result);
    XMLUtil.createElement("height", getHeight(), result);
    
    XMLUtil.createElement("foreground", String.format("%08x", total.getForeground().getRGB()), result);
    XMLUtil.createElement("background", String.format("%08x", panel.getBackground().getRGB()), result);
    
    XMLUtil.createElement("visible", isVisible(), result);
    
    Element times = new Element("times");
    for(TimeSpan ts : this.times) { times.addContent(ts.toXML()); }
    result.addContent(times);
    return result;
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
