/*
 * Created on 16.10.2004
 */
package net.sourceforge.fullsync.ui;

import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.fullsync.Task;
import net.sourceforge.fullsync.TaskGenerationListener;
import net.sourceforge.fullsync.TaskTree;
import net.sourceforge.fullsync.fs.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class SystemTrayItem implements TaskGenerationListener
{
    private GuiController guiController;
    private Tray tray;
    private TrayItem trayItem;
    private Menu menu;
    
    private Image[] imageList;
    private int imageActive;
    
    private Timer timer;
    private boolean isBusy;

    public SystemTrayItem( GuiController gui )
    {
        this.guiController = gui;
        this.tray = guiController.getDisplay().getSystemTray();
        this.trayItem = new TrayItem( tray, SWT.NULL );
        
        imageList = new Image[2];
        imageList[0] = new Image( null, "images/Tray_Active_01.gif" );
        imageList[1] = new Image( null, "images/Tray_Active_02.gif" );
        imageActive = 0;
        
        // initialize trayItem
        trayItem.setImage( imageList[0] );
    	trayItem.setToolTipText( "FullSync" );
    	trayItem.addListener( SWT.DefaultSelection, new Listener() {
    	    public void handleEvent(Event arg0) 
    	    {
    	        guiController.setMainShellVisible( true );
    	    }
    	} );
    	trayItem.addListener( SWT.MenuDetect, new Listener() {
    		public void handleEvent(Event evt) 
    		{
    		    menu.setVisible( true );
			}
    	} );
    	
    	// initialize popup menu
    	menu = new Menu( guiController.getMainShell(), SWT.POP_UP );
		MenuItem item;
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Open FullSync" );
		item.addListener(SWT.Selection, new Listener() {
		    public void handleEvent( Event arg0 )
            {
		    	guiController.setMainShellVisible( true );
            }
		} );
		
		item = new MenuItem( menu, SWT.NULL );
		item.setText( "Exit" );
		item.addListener( SWT.Selection, new Listener() {
		    
		    public void handleEvent( Event event )
            {
		        guiController.closeGui();
            }
		} );
		
		guiController.getSynchronizer().getProcessor().addTaskGenerationListener( this );
    }
    public void setVisible( boolean visible )
    {
        trayItem.setVisible( visible );
    }
    public boolean isDisposed()
    {
        return trayItem.isDisposed();
    }
    public void dispose()
    {
        trayItem.dispose();
        menu.dispose();
        for( int i = 0; i < imageList.length; i++ )
            imageList[i].dispose();
    }
    
    public void taskGenerationStarted(File source,File destination)
    {
        
    }
    public void taskGenerationFinished(Task task)
    {
        
    }
    public synchronized void taskTreeStarted(TaskTree tree)
    {
        if( !isBusy )
        {
            this.timer = new Timer();
            isBusy = true;
            timer.schedule( new TimerTask() {
                public void run()
                {
                    imageActive++;
                    if( imageActive >= imageList.length )
                        imageActive = 0;
                    trayItem.getDisplay().asyncExec( new Runnable() {
                        public void run()
                        {
                            trayItem.setImage( imageList[imageActive] );
                        }
                    } );
                }
            }, 0, 500 );
        }
    }
    public synchronized void taskTreeFinished(TaskTree tree)
    {
        if( isBusy )
        {
            isBusy = false;
            timer.cancel();
            timer = null;
            
            imageActive = 0;
            trayItem.getDisplay().asyncExec( new Runnable() {
                public void run()
                {
                    trayItem.setImage( imageList[imageActive] );
                }
            } );
        }
    }
}