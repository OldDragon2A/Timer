/**
 * 
 */
package me.olddragon2a;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * @author OldDragon2A
 *
 */
public class Launcher extends JFrame implements ActionListener, WindowListener {
  private static final long serialVersionUID = 5283999368979262806L;
  protected ArrayList<Timer> timers = new ArrayList<Timer>();
  protected JComboBox<Timer> combobox;
  protected JTextField label;
  
  /**
   * @throws HeadlessException
   */
  public Launcher() throws HeadlessException {
    addWindowListener(this);
    
    setBackground(new Color(0,0,0,0));
    setSize(300, 200);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Timer Control");
    setContentPane(new JPanel());
    setLayout(new GridBagLayout());
    
    GridBagConstraints c = new GridBagConstraints();
    
    c.insets = new Insets(10,10,0,10);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.ipadx = 100;
    label = new JTextField("Label");
    add(label, c);
    
    c.insets = new Insets(10,0,0,10);
    c.gridx = 1;
    c.ipadx = 0;
    JButton btnAdd = new JButton("Add");
    btnAdd.addActionListener(this);
    btnAdd.setActionCommand("add");
    btnAdd.setMnemonic('a');
    add(btnAdd, c);
    
    c.insets = new Insets(10,10,10,10);
    c.gridx = 0;
    c.gridy = 1;
    combobox = new JComboBox<Timer>();
    combobox.setRenderer(new TimerComboBoxRender());
    add(combobox, c);
    
    c.insets = new Insets(10,0,10,10);
    c.gridx = 1;
    JButton btnToggle = new JButton("Toggle");
    btnToggle.addActionListener(this);
    btnToggle.setActionCommand("toggle");
    add(btnToggle, c);
    
    Timer.launcher = this;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("add")) {
      actionAdd();
    } else if (e.getActionCommand().equals("toggle")) {
      actionToggle();
    }
  }
  protected void actionAdd() {
    Timer timer = new Timer();
    if (!label.getText().isEmpty()) { timer.setTitle(label.getText()); }
    timers.add(timer);
    combobox.addItem(timer);
    timer.pack();
    timer.setVisible(true);
  }
  protected void actionToggle() {
    Timer timer = combobox.getItemAt(combobox.getSelectedIndex());
    if (timer != null) { timer.setVisible(! timer.isVisible()); }
  }

  public static void main(String[] args) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Launcher launcher = new Launcher();
        launcher.pack();
        launcher.setVisible(true);
      }
    });
  }
  
  protected void load() {
    File config = new java.io.File("config.xml");
    if (config.exists()) {
      try {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new FileInputStream(config));
        timers = new ArrayList<Timer>();
        for (Element t : doc.getRootElement().getChildren()) { timers.add(new Timer(t)); }
      } catch (JDOMException | IOException e) {
        JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
      }
      
      for (Timer timer : this.timers) {
        combobox.addItem(timer);
        if (timer.started != null) { timer.timer.start(); }
        if (timer.was_visible) { timer.setVisible(true); }
      }
    }
  }
  protected void save() {
    try {
      Element timers = new Element("timers");
      Document doc = new Document(timers);
      for(Timer t : this.timers) { timers.addContent(t.toXML()); }
      
      XMLOutputter xout = new XMLOutputter();
      xout.setFormat(Format.getPrettyFormat());
      xout.output(doc, new FileOutputStream("config.xml"));
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    
  }

  @Override
  public void windowOpened(WindowEvent e) { load(); }
  @Override
  public void windowClosing(WindowEvent e) { save(); }

  @Override
  public void windowClosed(WindowEvent e) {}
  @Override
  public void windowIconified(WindowEvent e) {}
  @Override
  public void windowDeiconified(WindowEvent e) {}
  @Override
  public void windowActivated(WindowEvent e) {}
  @Override
  public void windowDeactivated(WindowEvent e) {}
}
