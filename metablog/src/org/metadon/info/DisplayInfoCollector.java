/*
 * DisplayInfoCollector.java
 *
 * Created on 31. Oktober 2007, 16:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.info;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.metadon.control.Controller;

/**
 *
 * @author Hannes
 */
public class DisplayInfoCollector extends InfoCollector {

    private static Canvas canvas = null;
    private static int[] canvasDimension = new int[2];
    
    public DisplayInfoCollector(){
    	super();
    }
    
    public void collectInfos(Controller controller, Display display) {      
        
        boolean isColor = display.isColor();
        int numColors = display.numColors();

        if ( ! isColor ) {
            this.addInfo( "Display Type: ", "Grayscale");
            this.addInfo( "Levels: ", Integer.toString(numColors) );
        } else {
            this.addInfo( "Display Type: ", "Color" );
            this.addInfo( "Colors: ", Integer.toString(numColors) );
        }
          
        this.addInfo( "Canvas Width: ", Integer.toString(getCanvasDimension()[0]));
        this.addInfo( "Canvas Height: ", Integer.toString(getCanvasDimension()[1]));
        this.addInfo( "Canvas (DoubleBuffered): ", "" + canvas.isDoubleBuffered() );

        Font font = Font.getDefaultFont();
        this.addInfo("Default-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL );
        this.addInfo("Small-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM );
        this.addInfo("Medium-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE );
        this.addInfo("Large-Font-Height: ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL );
        this.addInfo("Small-Font-Height (bold): ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
        this.addInfo("Medium-Font-Height (bold): ", "" + font.getHeight() );
        font = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE );
        this.addInfo("Large-Font-Height (bold): ", "" + font.getHeight() );
    }
    
    public static int[] getCanvasDimension() {
        initCanvas();
        canvasDimension[0] = canvas.getWidth();
        canvasDimension[1] = canvas.getHeight();
        return canvasDimension;
    }
    
    private static void initCanvas() {
        if(canvas == null) {
            canvas = new Canvas() {  
                public void paint(Graphics g){
                // do nothing
                }
            };
        }
    }
        
}