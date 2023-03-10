package com.moh.alarmclock.String;

public class String {

    /**
     * returns the similarity of string a to string b
     * for example if a = "abcd" and b = "abcde"
     * a is 80 percent or 0.8 similar to b
     * @param a
     * @param b
     */
    public static float getSimilarity(java.lang.String a, java.lang.String b){
        double biggerSize = Math.max(a.length(), b.length());
        double similarChars = 0;
        // if either is empty return 0 percent similarity
        if(a.isEmpty() || b.isEmpty()){
            return 0f;
        }else if(a.equals(b)) {
            // they are the same
            return 1f;
        }
        char[] ac = a.toCharArray();
        char[] bc = b.toCharArray();
        int min = Math.min(a.length(),b.length());
        for(int i = 0; i < min; i++){
            if(ac[i] == bc[i]){
                // this character in a is same as b
                similarChars++;
            }else{
                // we met the first char where a and b are not similar
                break;
            }
        }
        return (float)(similarChars/biggerSize);
    }


    /**
     * returns a [text] that is [count] long
     * and ends with [endWith]
     * @param text that want to be limited
     * @param endWith that want to end with
     * @param count number of characters included
     * @return
     */
    public static java.lang.String getLimitedCount(java.lang.String text, java.lang.String endWith, int count){
        if(text.length() <= count){
            return text;
        }
        return text.substring(0,count) + endWith;
    }

    /**
     * normal version of a limited text ends with ...
     * to let the user know that there is more text than this
     * @param text
     * @param count
     * @return
     */
    public static java.lang.String getLimitedCount(java.lang.String text, int count){
        return getLimitedCount(text,"...",count);
    }


}
