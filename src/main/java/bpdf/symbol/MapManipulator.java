// MapManipulator.java
package bpdf.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A class to manipulate expression power maps
 * and numerics
 */
public class MapManipulator
{
    /** 
     * Merges two power maps into a single one (Multiplication)
     * @param map1 The first map.
     * @param map2 The second map.
     * @return Returns the merged map
     */
    public static HashMap<String, Integer> addMaps(
        HashMap<String, Integer> map1, HashMap<String, Integer> map2)
    {
        HashMap<String, Integer> resMap = new HashMap<String, Integer>(map1);
        Iterator<Map.Entry<String, Integer>> iMap = map2.entrySet().iterator();

        while (iMap.hasNext()) 
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            if (resMap.containsKey(key)) 
            {
                int current = resMap.get(key);
                resMap.put(key, current + value);
            } 
            else 
            {
                resMap.put(key, value);
            }
        }
        return resMap;
    }

    /** 
     * Finds the Greater Common Divisor (GCD) of two maps
     * @param map1 The first map
     * @param map2 The second map
     * @return The GCD of the two maps
     */
    public static HashMap<String, Integer> gcdMaps(
        HashMap<String, Integer> map1, HashMap<String, Integer> map2)
    {
        HashMap<String, Integer> resMap = new HashMap<String, Integer>();
        Iterator<Map.Entry<String, Integer>> iMap = 
            map1.entrySet().iterator();
        // Numerical part of each expression
        int tmpNum1 = 1;
        int tmpNum2 = 1;
        // Get the numerical part of the first map
        // Find common values with the second map
        while (iMap.hasNext()) 
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            try 
            {
                tmpNum1 = (int) (tmpNum1 * java.lang.Math.pow
                    (Integer.parseInt(key), value));
            } 
            catch (NumberFormatException e) 
            {
                if (map2.containsKey(key)) 
                {
                    // If common entries keep the smaller value
                    resMap.put(key, java.lang.Math.min(value, map2.get(key)));
                }
            }
        }
        // Get the numerical part of the second map
        iMap = map2.entrySet().iterator();
        while (iMap.hasNext())
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            try 
            {
                tmpNum2 = (int) (tmpNum2 * java.lang.Math.pow
                    (Integer.parseInt(key), value));
            } 
            catch (NumberFormatException e) 
            {
                // Do nothing
            }
        }
        // GCD of the numerical parts
        int finNum = gcdNum(tmpNum1,tmpNum2);
        resMap.put(((Integer) finNum).toString(), 1);
        return resMap;
    }

    /** 
     * Subtracts map2 from map1 (Division)
     * @param map1 The map to subtract from
     * @param map2 the map to be subtracted
     * @return The resulting map
     */
    public static HashMap<String, Integer> subMaps(
        HashMap<String, Integer> map1, HashMap<String, Integer> map2)
    {
        // If the second map is unity
        // It is like dividing by 1
        if (map2.isEmpty()) return map1;
        HashMap<String,Integer> resMap = new HashMap<String, Integer>();
        Iterator<Map.Entry<String, Integer>> iMap = map1.entrySet().iterator();
        // Numerical part for each expression
        int tmpNum1 = 1;
        int tmpNum2 = 1;
        // Get the numerical part of first map
        // If common entries subtract powers
        // If not common do nothing
        while(iMap.hasNext())
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            try 
            {
                tmpNum1 = (int) (tmpNum1 * java.lang.Math.pow
                    (Integer.parseInt(key), value));
            } 
            catch (NumberFormatException e) 
            {
                if (map2.containsKey(key))
                {
                    int newValue = value - map2.get(key);
                    if (newValue < 0)
                    { 
                        throw new RuntimeException("Negative Subtraction!");
                    } 
                    else if (newValue > 0)
                    {
                        resMap.put(key,newValue);
                    }
                } 
                else 
                {
                    resMap.put(key,value);
                }
            }
        }
        // Get the numerical part of the second map
        iMap = map2.entrySet().iterator();
        while (iMap.hasNext())
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            try 
            {
                tmpNum2 = (int) (tmpNum2 * java.lang.Math.pow
                    (Integer.parseInt(key), value));
            } 
            catch (NumberFormatException e) 
            {
                // Do nothing
            }
        }
        // Divide the numerical parts
        int finNum = tmpNum1/tmpNum2;
        resMap.put(((Integer) finNum).toString(), 1);
        return resMap;
    }

    /** 
     * Greater Common Divisor for two numerical values (Recursive)
     * @param a The first numerical value
     * @param b The second numerical value
     * @return Their GCD
     */
    public static int gcdNum(int a, int b) 
    {
        if (b==0) return a;
        return gcdNum(b,a%b);
    }

    /**
     * Prints the keys and the values of a hashmap. Auxiliary, used for 
     * debugging.
     * @param The map to be print
     */
    public static void printMap(HashMap<String,Integer> map)
    {

        Iterator<Map.Entry<String, Integer>> iMap = map.entrySet().iterator();
        
        while(iMap.hasNext())
        {
            Map.Entry<String, Integer> tempEntry = iMap.next();
            String key = tempEntry.getKey();
            int value = tempEntry.getValue();
            System.out.print("Key: " + key + ", ");
            System.out.println("Value: " + value);
        }
    }
}