/*
 * WaypointToolkit.java
 *
 * Created on 16. Dezember 2007, 13:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.location;

/**
 *
 * @author Hannes
 */
public class WaypointToolkit {
    
    public static final int MILE = 0;
    public static final int KILOMETER = 1;
    
    //private static final double MILE_TO_KILOMETER = 0.621371192;
    //private static final double FOOT_TO_METER = 0.3048;
    
    private static final double KILOMETER_TO_MILE = 1.609344;
    private static final double METER_TO_FOOT = 3.2808399;
    
    private static final double KNOTS_TO_MPH = 1.151;
    private static final double KNOTS_TO_KPH = 1.852;
    
    // return the distance of two gps locations in kilometers
    public static double getDistance(GPSLocation loc1, GPSLocation loc2) {
        double lat1 = (Math.PI / 180.0) * loc1.getLatitude();
        double lon1 = (Math.PI / 180.0) * loc1.getLongitude();
        double lat2 = (Math.PI / 180.0) * loc2.getLatitude();
        double lon2 = (Math.PI / 180.0) * loc2.getLongitude();
        double distance = 2 * MathUtil.asin(
                Math.sqrt(MathUtil.pow(Math.sin((lat1 - lat2) / 2), 2)
                + Math.cos(lat1)
                * Math.cos(lat2)
                * MathUtil.pow(Math.sin((lon1 - lon2) / 2), 2)));
        return distance * 6371.0;
    }
    
    // return an user friendly distance string in kilometers/miles and sub units (meters/feet) or
    // in sub units format only
    public static String getUserFriendlyDistance(double distance, int unit, boolean subUnitsOnly) {
        double length = distance;
        int fractionalDigits = 2;
        String extension;
        String result;
        
        if(subUnitsOnly) {
            if(unit == MILE) {
                length = length * METER_TO_FOOT;
                extension = "ft";
            } else 
                extension = "m";
        }
        else {
            if(unit == MILE) {
                length = length * KILOMETER_TO_MILE;
                extension = "ml";
            } else
                extension = "km";
        }
        result = Double.toString(length);
        
        // cut fractional digits
        if(length != 0.0) {
            // check number of fractional digits
            int fd = result.substring(result.indexOf(".")).length();
            if(fd >= fractionalDigits) {
               // use two fractional digits
               result = result.substring(0, result.indexOf(".")+fractionalDigits);
            } else {
               result = result.substring(0, result.indexOf(".")+fd);
            }
        }
        return result+" "+extension;
    }
    
    // return the speed in km/h or in mph
    public static String getUserFriendlySpeed(double speedInKnots, int unit) {
        double speed;
        String extension;
        String result;
        
        if(unit == MILE) {
            speed = speedInKnots * KNOTS_TO_MPH;
            extension = "mph";
        } else {
            speed = speedInKnots * KNOTS_TO_KPH;
            extension = "km/h";
        }
        result = Double.toString(speed);
        return result+" "+extension;
     }
    
   /******************************************************************************/
    
    /**
      * Utility methods for mathematical problems.
      *
      * @author Tommi Laukkanen
    */
    public static class MathUtil {

        /** Square root from 3 */
        final static public double SQRT3 = 1.732050807568877294;
        /** Log10 constant */
        final static public double LOG10 = 2.302585092994045684;
        /** ln(0.5) constant */
        final static public double LOGdiv2 = -0.6931471805599453094;

        /** Creates a new instance of MathUtil */
        private MathUtil() {
        }

        /**
         * Returns the value of the first argument raised to the
         * power of the second argument.
         *
         * @author Mario Sansone
         */
        public static int pow(int base, int exponent) {
            boolean reciprocal = false;
            if(exponent < 0){
                reciprocal = true;
            }
            int result = 1;
            while (exponent-- > 0) {
                result *= base;
            }
            return reciprocal?1/result:result;
        }

        public static double pow(double base, int exponent) {
            boolean reciprocal = false;
            if(exponent < 0){
                reciprocal = true;
            }
            double result = 1;
            while (exponent-- > 0) {
                result *= base;
            }
            return reciprocal?1/result:result;
        }


        /** Arcus cos */
        static public double acos(double x) {
            double f=asin(x);
            if(f==Double.NaN)
                return f;
            return Math.PI/2-f;
        }

        /** Arcus sin */
        static public double asin(double x) {
            if( x<-1. || x>1. ) return Double.NaN;
            if( x==-1. ) return -Math.PI/2;
            if( x==1 ) return Math.PI/2;
            return atan(x/Math.sqrt(1-x*x));
        }

        /** Arcus tan */
        static public double atan(double x) {
            boolean signChange=false;
            boolean Invert=false;
            int sp=0;
            double x2, a;
            // check up the sign change
            if(x<0.) {
                x=-x;
                signChange=true;
            }
            // check up the invertation
            if(x>1.) {
                x=1/x;
                Invert=true;
            }
            // process shrinking the domain until x<PI/12
            while(x>Math.PI/12) {
                sp++;
                a=x+SQRT3;
                a=1/a;
                x=x*SQRT3;
                x=x-1;
                x=x*a;
            }
            // calculation core
            x2=x*x;
            a=x2+1.4087812;
            a=0.55913709/a;
            a=a+0.60310579;
            a=a-(x2*0.05160454);
            a=a*x;
            // process until sp=0
            while(sp>0) {
                a=a+Math.PI/6;
                sp--;
            }
            // invertation took place
            if(Invert) a=Math.PI/2-a;
            // sign change took place
            if(signChange) a=-a;
            //
            return a;
        }    
    }// end MathUtil
    
}// end WaypointToolkit
