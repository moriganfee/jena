/*
 * (c) Copyright 2010 Talis Information Ltd
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.util;

/** Parse an xsd:dateTime or xsd:date lexical form */  

public class DateTimeStruct
{
    // V2
    // Does gregorian stuff
    
    // "2001Z"      gYear
    // "2001-01Z"   gYearMonth
    // "--01Z"      gMonth
    // "--01-30Z"   gMonthDay
    // "---30Z"     gDay

    
//    private static final int idxNeg = 0 ;
//    private static final int idxYear = 1 ;
//    private static final int idxMonth = 2 ;
//    private static final int idxDay = 3 ;
//
//    private static final int idxHour = 4 ;
//    private static final int idxMinute = 5 ;
//    private static final int idxSecond = 6 ;
//    private static final int idxTimezone = 7 ;
//    
//    private String data[] = new String[8] ;
    
    public boolean xsdDateTime  ;
    public String neg = null ;         // Null if none. 
    public String year = null ;
    public String month = null ;
    public String day = null ;
    public String hour = null ;
    public String minute = null ;
    public String second = null ;      // Inc. fractional parts
    public String timezone = null ;    // Null if none.

    private DateTimeStruct() {}
    
    public static class DateTimeParseException extends RuntimeException
    {
        public DateTimeParseException(String msg) { super(msg) ; } 
    }
    
    @Override
    public String toString()
    { 
        String ySep = "-" ;
        String tSep = ":" ;
        String x = year+ySep+month+ySep+day ;
        if ( xsdDateTime )
            x = x + "T"+hour+tSep+minute+tSep+second ;
        if ( neg != null )
            x = neg+x ;
        if ( timezone != null )
            x = x+timezone ;
        return x ; 
    }
    
    public static DateTimeStruct parseDateTime(String str)
    { return _parseYMD(str, true, true, true) ; }

    public static DateTimeStruct parseDate(String str)
    { return _parseYMD(str, true, true, false) ; } 
    
    public static DateTimeStruct parseGYear(String str)
    { return _parseYMD(str, false, false, false) ; } 

    public static DateTimeStruct parseGYearMonth(String str)
    { return _parseYMD(str, true, false, false) ; } 
    
    public static DateTimeStruct parseGMonth(String str)
    { return _parseMD(str, true, false) ; }
    
    public static DateTimeStruct parseGMonthDay(String str)    
    { return _parseMD(str, true, true) ; }
    
    public static DateTimeStruct parseGDay(String str)      
    { return _parseMD(str, false, true) ; }
    
    // Date with year: date, dateTime, gYear, gYearMonth but not gMonth, gMonthDay, 
    private static DateTimeStruct _parseYMD(String str, boolean month, boolean day, boolean includeTime) 
    { 
        DateTimeStruct struct = new DateTimeStruct() ;
        int idx = 0 ; // if whitespace fact processing -- skipWhitespace(str, 0) ;
        boolean negYear = false ;

        if ( str.charAt(idx) == '-' )
        {
            struct.neg = "-" ;
            idx ++ ;
        }
        
        struct.year = getDigits(str, idx) ;
        if ( struct.year.length() < 4 )
            throw new DateTimeParseException("Year too short (must be 4 or more digits)") ;
        
        idx += struct.year.length() ;
        
        if ( month )
        {
            check(str, idx, '-') ;
            idx += 1 ;
            struct.month = getDigits(2, str, idx) ;
            idx += 2 ;
        }        

        if ( day )
        {
            check(str, idx, '-') ;
            idx += 1 ;
            struct.day = getDigits(2, str, idx) ;
            idx += 2 ;
        }

        if ( includeTime )
        {        
            struct.xsdDateTime = true ;
            // ---- 
            check(str, idx, 'T') ;
            idx += 1 ;

            // ---- 
            // Hour-minute-seconds
            struct.hour = getDigits(2, str, idx) ;
            idx += 2 ;
            check(str, idx, ':') ;
            idx += 1 ;

            struct.minute = getDigits(2, str, idx) ;
            idx += 2 ;
            check(str, idx, ':') ;
            idx += 1 ;

            // seconds
            struct.second = getDigits(2, str, idx) ;
            idx += 2 ;
            if ( idx < str.length() && str.charAt(idx) == '.' )
            {
                idx += 1 ;
                int idx2 = idx ;
                for ( ; idx2 < str.length() ; idx2++ )
                {
                    char ch = str.charAt(idx2) ;
                    if ( ! Character.isDigit(ch) )
                        break ;
                }
                if ( idx == idx2 )
                    throw new DateTimeParseException("Bad time part") ;
                struct.second = struct.second+'.'+str.substring(idx, idx2) ;
                idx = idx2 ;
            }
        }
        
        // Timezone
        idx = _parseTimezone(struct, str, idx) ;
        
        idx = skipWhitespace(str, idx) ;
        
        if ( idx != str.length() )
            throw new DateTimeParseException("Trailing characters after date/time") ;
        return struct ; 
    }
    
    // No year: gMonth, gMonthDay, gDay
    private static DateTimeStruct _parseMD(String str, boolean month, boolean day)
    {
        DateTimeStruct struct = new DateTimeStruct() ;
        int idx = 0 ;

        check(str, idx, '-') ;
        idx += 1 ;

        check(str, idx, '-') ;
        idx += 1 ;
        
        if ( month )
        {
            struct.month = getDigits(2, str, idx) ;
            idx += 2 ; 
        }
        
        if ( day )
        {
            check(str, idx, '-') ;
            idx += 1 ;
            struct.day = getDigits(2, str, idx) ;
            idx += 2 ; 
        }
        
        // Timezone
        idx = _parseTimezone(struct, str, idx) ;
        
        if ( idx != str.length() )
            throw new DateTimeParseException("Unexpected trailing characters in string") ;
        return struct ; 
    }
    
    
    
    private static int _parseTimezone(DateTimeStruct struct, String str, int idx)
    {
        if ( idx >= str.length() )
        {
            struct.timezone = null ;
            return idx ;
        }
        
        if ( str.charAt(idx) == 'Z' )
        {
            struct.timezone = "Z" ;
            idx += 1 ;
        }
        else
        {
            StringBuilder sb = new StringBuilder() ;

            if ( str.charAt(idx) == '+' )
                sb.append('+') ;
            else if ( str.charAt(idx) == '-' )
                sb.append('-') ;
            else
                throw new DateTimeParseException("Bad timezone") ;
            idx += 1 ;

            sb.append(getDigits(2, str, idx)) ;
            idx += 2 ;

            check(str, idx, ':') ;
            sb.append(':') ;
            idx += 1 ;

            sb.append(getDigits(2, str, idx)) ;
            idx += 2 ;
            struct.timezone = sb.toString() ;
        }
        return idx ;
    }


//    // DateTime or Date - not gregorian
//    // Replace with generic code.
//    private static DateTimeStruct _parse(String str, boolean includeTime)
//    {
//        // -? YYYY-MM-DD T hh:mm:ss.ss TZ
//        DateTimeStruct struct = new DateTimeStruct() ;
//        int idx = 0 ;
//
//        if ( str.startsWith("-") )
//        {
//            struct.neg = "-" ;
//            idx = 1 ;
//        }
//
//        // ---- Year-Month-Day
//        struct.year = getDigits(4, str, idx) ;
//        idx += 4 ;
//        check(str, idx, '-') ;
//        idx += 1 ;
//
//        struct.month = getDigits(2, str, idx) ;
//        idx += 2 ;
//        check(str, idx, '-') ;
//        idx += 1 ;
//
//        struct.day = getDigits(2, str, idx) ;
//        idx += 2 ;
//
//        struct.xsdDateTime = false ;
//
//        if ( includeTime )
//        {        
//            struct.xsdDateTime = true ;
//            // ---- 
//            check(str, idx, 'T') ;
//            idx += 1 ;
//
//            // ---- 
//            // Hour-minute-seconds
//            struct.hour = getDigits(2, str, idx) ;
//            idx += 2 ;
//            check(str, idx, ':') ;
//            idx += 1 ;
//
//            struct.minute = getDigits(2, str, idx) ;
//            idx += 2 ;
//            check(str, idx, ':') ;
//            idx += 1 ;
//
//            // seconds
//            struct.second = getDigits(2, str, idx) ;
//            idx += 2 ;
//            if ( idx < str.length() && str.charAt(idx) == '.' )
//            {
//                idx += 1 ;
//                int idx2 = idx ;
//                for ( ; idx2 < str.length() ; idx2++ )
//                {
//                    char ch = str.charAt(idx2) ;
//                    if ( ! Character.isDigit(ch) )
//                        break ;
//                }
//                if ( idx == idx2 )
//                    throw new DateTimeParseException() ;
//                struct.second = struct.second+'.'+str.substring(idx, idx2) ;
//                idx = idx2 ;
//            }
//        }
//        else
//        {
//            struct.hour =  null ;
//            struct.minute = null ;
//            struct.second = null ;
//
//        }
//        // timezone. Z or +/- 00:00
//
//        if ( idx < str.length() )
//        {
//            if ( str.charAt(idx) == 'Z' )
//            {
//                struct.timezone = "Z" ;
//                idx += 1 ;
//            }
//            else
//            {
//                StringBuilder sb = new StringBuilder() ;
//
//                if ( str.charAt(idx) == '+' )
//                    sb.append('+') ;
//                else if ( str.charAt(idx) == '-' )
//                    sb.append('-') ;
//                else
//                    throw new DateTimeParseException() ;
//                idx += 1 ;
//
//                sb.append(getDigits(2, str, idx)) ;
//                idx += 2 ;
//
//                check(str, idx, ':') ;
//                sb.append(':') ;
//                idx += 1 ;
//
//
//                sb.append(getDigits(2, str, idx)) ;
//                idx += 2 ;
//
//                struct.timezone = sb.toString() ;
//            }
//        }
//    
//        if ( idx != str.length() )
//            throw new DateTimeParseException() ;
//        return struct ;
//    }

    private static String getDigits(int num, String string, int start)
    {
        for ( int i = start ; i < (start+num) ; i++ )
        {
            char ch = string.charAt(i) ;
            // Only ASCII digits
            if ( ch < '0' || ch > '9' )
                throw new DateTimeParseException("Bad number (expected "+num+" digits)") ;
            continue ;
        }
        return string.substring(start, start+num) ;
    }
    
    private static String getDigits(String string, int start)
    {
        int i = start ;
        for ( ;; i++ )
        {
            if ( i >= string.length() )
                break ;
            char ch = string.charAt(i) ;
            // Only ASCII digits
            if ( ch < '0' || ch > '9' )
                break ;
            continue ;
        }
        return string.substring(start, i) ;
    }
    
    private static int skipWhitespace(String string, int idx)
    {
        while ( idx < string.length() )
        {
            char ch = string.charAt(idx) ;
            if ( ! Character.isWhitespace(ch) )
                return idx ;
            idx++ ;
        }
        return idx ;
    }
    
    private static void check(String string, int idx, char x)
    {
        if ( string.length() <= idx || string.charAt(idx) != x ) 
            throw new DateTimeParseException("Expected: "+x+" at index "+idx) ;
    }
}

/*
 * (c) Copyright 2010 Talis Information Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */