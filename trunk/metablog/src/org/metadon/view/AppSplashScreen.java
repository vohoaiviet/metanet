/*
 * AppSplashScreen.java
 *
 * Created on 30. Oktober 2007, 15:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import java.util.TimerTask;
import java.util.Timer;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.Controller;

import de.enough.polish.util.Locale;
import de.enough.polish.util.TextUtil;

public class AppSplashScreen extends Canvas implements Runnable
{
    private Controller controller;
    private Displayable nextScreen = null;
    private Timer timer = new Timer();
    
    private final static int MARGIN = 1;
    private final static int BACKGROUND_COLOR = 0xFFFFFF;
    private final static int TEXT_COLOR = 0x1E5556;
	
    private final Image logo;
    private int yOffset = 6;
    private long delay = 50;
    private long timeoutPeriod = 10;
    private final int availableHeight;
    private final int availableWidth;
    private final int contentHeight;
    private final Font textFont;
    private final String[] credits;
    private String[] loadingStatusText;
   

    public AppSplashScreen(Controller c)
    {
        Image logo = null;
                
        this.controller = c;
        this.setFullScreenMode(true);

    	try {
            logo = Image.createImage("/logo_splash.png");
	} catch (IOException e) {
            //#debug error
            System.out.println("Unable to load splash screen logo [/logo.png]" +e );
	}
        this.logo = logo;
        this.textFont = Font.getFont( Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL );
        this.availableHeight = getHeight();
        this.availableWidth = getWidth() - (2 * MARGIN);
        this.setLoadingStatusText(Locale.get("appSplashScreen.loading.application"));
        this.credits = TextUtil.wrap(Locale.get("appSplashScreen.credits"), this.textFont, this.availableWidth, this.availableHeight);
        this.contentHeight = this.credits.length * (this.textFont.getHeight() + MARGIN);
        
        new Thread(this).start();
        this.controller.show(this);
    }

    protected void keyPressed( int keyCode ){
        //dismiss();
    }
    
    public void run() {
        // check if a next screen is available
        while(this.nextScreen == null) {
            try {
               Thread.sleep(delay);
            } catch(Exception e) {}
        }
        timer.cancel();
        this.controller.show(this.nextScreen);
    }
    
    protected void paint(Graphics g){
        
        // paint logo
        g.setColor( BACKGROUND_COLOR );
        g.fillRect(0, 0, getWidth(), getHeight());
        if (this.logo != null) {
            g.drawImage(this.logo, getWidth()/2, getHeight()/4, Graphics.HCENTER | Graphics.TOP );
        }      
        
        // write credits
        int y = this.logo.getHeight() + getHeight()/4 + this.yOffset;
        g.setColor( TEXT_COLOR );
        g.setFont( this.textFont );
        int lineHeight = this.textFont.getHeight() + MARGIN;
        for (int i = 0; i < this.loadingStatusText.length; i++) {
            String text = this.loadingStatusText[i];
            g.drawString( text, getWidth()/2, y, Graphics.HCENTER | Graphics.TOP );
            y += lineHeight;
        }
        for (int i = 0; i < this.credits.length; i++) {
            String text = this.credits[i];
            g.drawString( text, getWidth()/2, y, Graphics.HCENTER | Graphics.TOP );
            y += lineHeight;
        }
    }

    // for devices with touch screen
    protected void pointerPressed( int x, int y ) {
        //dismiss();
    }

    // called automatically when the Canvas is put on screen
    protected void showNotify() {  
        // monitor start-up process
        timer.scheduleAtFixedRate(new ScreenRefresh(), this.delay, this.timeoutPeriod);
    }
    
    public void setLoadingStatusText(String loadingText) {
        this.loadingStatusText = TextUtil.wrap(loadingText+"\n\u0020", this.textFont, availableWidth, availableHeight);
    }
    
    public void setNextScreen(Displayable screen) {
        this.nextScreen = screen;
    }

    // ----------------------------------------
    private class ScreenRefresh extends TimerTask 
    {
        public void run(){
            repaint();
        }
    }
}

