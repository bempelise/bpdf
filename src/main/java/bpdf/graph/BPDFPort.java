// BPDFPort.java

package bpdf.graph;

import bpdf.symbol.*;

public class BPDFPort 
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /** 
     * The port name
     */
    private String name;

    /** 
     * The port rate
     */
    private Product rate;

    /** 
     * The port status, false for input, true for output
     */
    private boolean status;


/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /**
     * Constructor that sets the port's name
     * @param name The port's name
     */
    public BPDFPort(String n)
    {
        name = n;
    }

    /**
     * Constructor that sets the port's name
     * it's rate based on a string
     * and its input / output status
     * @param n The port's name
     * @param r The port's rate
     * @param st True for output, false for input
     */
    public BPDFPort(String n, String r, boolean st)
    {
        name = n;
        rate = new Product(r);
        status = st;
    }

    /**
     * Constructor that sets the port's name
     * it's rate based on a product
     * and its input / output status
     * @param n The port's name
     * @param r The port's rate
     * @param st True for output, false for input
     */
    public BPDFPort(String n, Product r, boolean st)
    {
        name = n;
        rate = r;
        status = st;
    }

/******************************************************************************
 ** SETTERS
 ******************************************************************************/

    /**
     * Sets the port's rate based on a product
     * @param The given product
     */
    public void setRate(Product r)
    {
        rate = r;
    }

    /**
     * Sets the port's rate based on a string
     * @param The given string
     */
    public void setRate(String r)
    {
        setRate(new Product(r));
    }

    /**
     * Sets the port's rate based on a string
     * @param The given string
     */
    public void setStatus(boolean st)
    {
        status = st;
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    /**
     * Returns the port's name
     * @return The port's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the port's rate
     * @return The port's rate
     */
    public Product getRate()
    {
        return rate;
    }

    /**
     * Returns whether the port is an output or not
     * @return True if it is an output
     */
    public boolean isOutput()
    {
        return status;
    }

    /**
     * Returns whether the port is an input or not
     * @return True if it is an input
     */
    public boolean isInput()
    {
        return !status;
    }
}