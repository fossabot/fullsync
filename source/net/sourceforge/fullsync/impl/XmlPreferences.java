package net.sourceforge.fullsync.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.fullsync.Preferences;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class XmlPreferences implements Preferences
{
	/**
	 * <FullSyncPreferences>
	 *   <General>
	 * 	   <CloseMinimizesToSystemTray>true</CloseMinimizesToSystemTray>
	 * 	   <MinimizeMinimizesToSystemTray>false</CloseMinimizesToSystemTray>
	 * 	   <ConfirmExit>true</ConfirmExit>
	 *     <EnableSystemTray>true</EnableSystemTray>
	 *   </General>
	 * </FullSyncPreferences>
	 */
	
    private String configFile;
    
    private boolean confirmExit = true;
    private boolean enableSystemTray = true;
    private boolean closeMinimizesToSystemTray = false;
    private boolean minimizeMinimizesToSystemTray = true;
    
    public XmlPreferences( String configFile ) 
    {
        this.configFile = configFile;        
        
        try {
            File file = new File( configFile );
            if( file.exists() )
            {
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			Document doc = builder.parse( file );
    			
    			Element rootElement = doc.getDocumentElement();
    			loadPreferences(rootElement);
            } else {
                savePreferences();
            }
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch( ParserConfigurationException e ) {
            e.printStackTrace();
        } catch( SAXException e ) {
            e.printStackTrace();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        
    }
    public void loadPreferences(Element rootElement) 
    {
    	NodeList generalNodeList = rootElement.getElementsByTagName("Interface");
    	if (generalNodeList.getLength() > 0) 
    	{
    		Element generalPreferencesElement = (Element) generalNodeList.item(0);
    		NodeList closeButtonPreferencesNodeList = generalPreferencesElement.getElementsByTagName("CloseButtonBehaviour");
    		if (closeButtonPreferencesNodeList.getLength() > 0) {
    			Element closeButtonPreferencesElement = (Element) closeButtonPreferencesNodeList.item(0);
    			//askOnClosing = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("ask")).booleanValue();
    			//closingButtonMinimizes = Boolean.valueOf(closeButtonPreferencesElement.getAttribute("minimize")).booleanValue();
    		}
    	}
    }
    
	public void savePreferences() {
        try {
	        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        Document doc = docBuilder.newDocument();
	
	        Element rootElement = doc.createElement( "FullSyncPreferences" );
	        doc.appendChild(rootElement);
	        
	        Element generalPreferencesElement = doc.createElement("General");
	        rootElement.appendChild(generalPreferencesElement);
	        
	        Element closeButtonPreferencesElement = doc.createElement("CloseButtonBehaviour");
	        //closeButtonPreferencesElement.setAttribute("ask", String.valueOf(askOnClosing));
	        //closeButtonPreferencesElement.setAttribute("minimize", String.valueOf(closingButtonMinimizes));
	        generalPreferencesElement.appendChild(closeButtonPreferencesElement);
	        
	        OutputStream out = new FileOutputStream( configFile );
	        
	        OutputFormat format = new OutputFormat( doc, "UTF-8", true );
	        XMLSerializer serializer = new XMLSerializer ( out, format);
	        serializer.asDOMSerializer();
	        serializer.serialize(doc);
	        
	        out.close();
        } catch( Exception e ) {
            // TODO messagebox ?
            e.printStackTrace();
        }

	}
    public void save()
    {
        savePreferences();
    }
    
    public boolean closeMinimizesToSystemTray()
    {
        return closeMinimizesToSystemTray;
    }
    public boolean confirmExit()
    {
        return confirmExit;
    }
    public boolean minimizeMinimizesToSystemTray()
    {
        return minimizeMinimizesToSystemTray;
    }
    public boolean systemTrayEnabled()
    {
        return false;
    }
    public void setCloseMinimizesToSystemTray( boolean bool )
    {
        closeMinimizesToSystemTray = bool;
    }
    public void setConfirmExit( boolean bool )
    {
        confirmExit = bool;
    }
    public void setMinimizeMinimizesToSystemTray( boolean bool )
    {
        minimizeMinimizesToSystemTray = bool;
    }
    public void setSystemTrayEnabled( boolean bool )
    {
        enableSystemTray = bool;
    }
    public String getProfileListStyle()
    {
        // TODO Auto-generated method stub
        return "Table";
    }
    public void setProfileListStyle( String profileListStyle )
    {
        // TODO Auto-generated method stub

    }
}