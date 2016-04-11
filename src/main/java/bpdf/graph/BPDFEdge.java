// BPDFEdge.java

package bpdf.graph;

import bpdf.symbol.*;

/**
 * The basic BPDF edge. It holds its input / output actors along with their
 * rates. The guard enabling / disabling the edge is also captured along with 
 * the number of tokens currently stored on the edge.
 * @author Vagelis Bebelis
 */
public class BPDFEdge 
{
/******************************************************************************
 ** PRIVATE PARAMETERS
 ******************************************************************************/

    /**
     * Edge name
     */
    private String name;

    /**
     * Producer actor
     */
    private BPDFActor producer;

    /**
     * Consumer actor
     */
    private BPDFActor consumer;

    /**
     * Edge input rate
     */
    private Product rateIn;

    /**
     * Edge output rate
     */
    private Product rateOut;

    /**
     * Number of stored tokens on edge
     */
    private Product tokens = new Product(0);

    /**
     * Boolean guard
     */
    private String guard = "";
    
/******************************************************************************
 ** CONSTRUCTORS
 ******************************************************************************/

    /**
     * Constructor setting up the edge's parameters using Strings
     * @param prod The producing actor
     * @param rateProd The name of the producing port
     * @param cons The consuming actor
     * @param rateCons The name of the consuming port
     */
    public BPDFEdge(BPDFActor prod, String rateProd,
        BPDFActor cons, String rateCons)
    {
        producer = prod;
        consumer = cons;
        rateIn = new Product(rateProd);
        rateOut = new Product(rateCons);
        name = prod.getName() 
            + "(" + rateProd + ") -> (" + rateCons + ")"
            + cons.getName();   
    }

    /**
     * Constructor setting up the edge's parameters using Products
     * @param prod The producing actor
     * @param rateProd The name of the producing port
     * @param cons The consuming actor
     * @param rateCons The name of the consuming port
     */
    public BPDFEdge(BPDFActor prod, Product rateProd,
        BPDFActor cons, Product rateCons)
    {
        producer = prod;
        consumer = cons;
        rateIn = rateProd;
        rateOut = rateCons;
        name = prod.getName() 
            + "(" + rateProd.getString() + ") -> (" + rateCons.getString() + ")"
            + cons.getName();   
    }

/******************************************************************************
 ** ADD / REMOVE TOKENS
 ******************************************************************************/

    /**
     * Adds t tokens to the ones stored in the edge
     * @param t The amount of tokens to be added
     */
    public void addTokens(String t)
    {
        addTokens(new Product(t));
    }

    /**
     * Adds t tokens to the ones stored in the edge
     * @param t The amount of tokens to be added
     */
    public void addTokens(Product t)
    {
        Expression res = tokens.add(t);
        if (res.isProduct())
            tokens = res.getProduct();
        else
            System.out.println ("Polynomial not a product!!");
    }

    /**
     * Subtracts t tokens to the ones stored in the edge
     * @param t The amount of tokens to be removed
     */
    public void removeTokens(String t)
    {
        removeTokens(new Product(t));
    }

    /**
     * Subtracts t tokens to the ones stored in the edge
     * @param t The amount of tokens to be removed
     */
    public void removeTokens(Product t)
    {
        Product tNeg = new Product("-" + t.getString());
        Product zero = new Product(0);
        Expression res = tokens.add(tNeg);
        if (res.isProduct())
        {
            if (zero.isGreaterThan(res.getProduct()))
                System.out.println("Not enough tokens");
            else
                tokens = res.getProduct();
        }
        else
            System.out.println ("Polynomial not a product!!");
    }

/******************************************************************************
 ** SETTERS
 ******************************************************************************/

    /**
     * Sets the tokens stored in the edge
     * @param t The amount of tokens to be stored
     */
    public void setTokens(String t)
    {
        setTokens(new Product(t));
    }

    /**
     * Sets the tokens stored in the edge
     * @param t The amount of tokens to be stored
     */
    public void setTokens(Product t)
    {
        tokens = t;
    }

    public void setTokens(int i)
    {
        setTokens(new Product(i));
    }

    /**
     * Sets the guard of the edge
     * @param g The guard value
     */
    public void setGuard(String g)
    {
        guard = g;
    }

/******************************************************************************
 ** GETTERS
 ******************************************************************************/

    /**
     * Returns the name of the edge
     * @return The name of the edge
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the consumer of the edge
     * @return The consumer of the edge
     */
    public BPDFActor getConsumer()
    {
        return consumer;
    }

    /**
     * Returns the producer of the edge
     * @return The producer of the edge
     */
    public BPDFActor getProducer()
    {
        return producer;
    }

    /**
     * Returns the input rate of the edge
     * @return The input rate of the edge
     */
    public Product getRateIn()
    {
        return rateIn;
    }

    /**
     * Returns the output rate of the edge
     * @return The output rate of the edge
     */
    public Product getRateOut()
    {
        return rateOut;
    }

    /**
     * Returns the amount of stored tokens
     * @return The amount of stored tokens
     */
    public Product getTokens()
    {
        return tokens;
    }

    /**
     * Returns the edge's boolean guard
     * @return The edge's boolean guard
     */
    public String getGuard()
    {
        return guard;
    }
}