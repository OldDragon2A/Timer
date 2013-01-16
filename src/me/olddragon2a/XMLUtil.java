/**
 * 
 */
package me.olddragon2a;

import org.jdom2.Element;

/**
 * @author OldDragon2A
 *
 */
public abstract class XMLUtil {
  static public Element createElement(String element, String val, Element parent) {
    Element result = new Element(element);
    result.addContent(val);
    if (parent != null) { parent.addContent(result); }
    return result;
  }
  
  static public Element createElement(String element, Integer val, Element parent) {
    return createElement(element, val.toString(), parent);
  }
  
  static public Element createElement(String element, Boolean val, Element parent) {
    return createElement(element, val.toString(), parent);
  }
}
