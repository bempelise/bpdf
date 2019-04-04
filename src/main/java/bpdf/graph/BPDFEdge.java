package bpdf.graph;

import bpdf.symbol.Expression;
import bpdf.symbol.Product;

/**
 * The basic BPDF edge. It holds its input / output actors along with their
 * rates. The guard enabling / disabling the edge is also captured along with
 * the number of tokens currently stored on the edge.
 * @author Vagelis Bebelis
 */
public class BPDFEdge {
    /** Edge name */
    private String m_name;
    /** Producer actor */
    private BPDFActor m_producer;
    /** Consumer actor */
    private BPDFActor m_consumer;
    /** Edge input rate */
    private Product m_rateIn;
    /** Edge output rate */
    private Product m_rateOut;
    /** Number of stored tokens on edge */
    private Product m_tokens = new Product(0);
    /** Boolean guard */
    private String m_guard = "";

    /**
     * Constructor setting up the edge's parameters using Strings
     * @param prod The producing actor
     * @param rateProd The name of the producing port
     * @param cons The consuming actor
     * @param rateCons The name of the consuming port
     */
    public BPDFEdge(BPDFActor prod, String rateProd,
                    BPDFActor cons, String rateCons) {
        m_producer = prod;
        m_consumer = cons;
        m_rateIn = new Product(rateProd);
        m_rateOut = new Product(rateCons);
        m_name = prod.getName()
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
                    BPDFActor cons, Product rateCons) {
        m_producer = prod;
        m_consumer = cons;
        m_rateIn = rateProd;
        m_rateOut = rateCons;
        m_name = prod.getName()
            + "(" + rateProd.getString() + ") -> (" + rateCons.getString() + ")"
            + cons.getName();
    }

    /**
     * Adds t tokens to the ones stored in the edge
     * @param t The amount of tokens to be added
     */
    public void addTokens(String t) {
        addTokens(new Product(t));
    }

    /**
     * Adds t tokens to the ones stored in the edge
     * @param t The amount of tokens to be added
     */
    public void addTokens(Product t) {
        Expression res = m_tokens.add(t);
        if (res.isProduct()) {
            m_tokens = res.getProduct();
        } else {
            System.out.println("Polynomial not a product!!");
        }
    }

    /**
     * Subtracts t tokens to the ones stored in the edge
     * @param t The amount of tokens to be removed
     */
    public void removeTokens(String t) {
        removeTokens(new Product(t));
    }

    /**
     * Subtracts t tokens to the ones stored in the edge
     * @param t The amount of tokens to be removed
     */
    public void removeTokens(Product t) {
        Product tNeg = new Product("-" + t.getString());
        Product zero = new Product(0);
        Expression res = m_tokens.add(tNeg);
        if (res.isProduct()) {
            if (zero.isGreaterThan(res.getProduct())) {
                System.out.println("Not enough tokens");
            } else {
                m_tokens = res.getProduct();
            }
        } else {
            System.out.println("Polynomial not a product!!");
        }
    }

    /**
     * Sets the tokens stored in the edge
     * @param t The amount of tokens to be stored
     */
    public void setTokens(String t) {
        setTokens(new Product(t));
    }

    /**
     * Sets the tokens stored in the edge
     * @param t The amount of tokens to be stored
     */
    public void setTokens(Product t) {
        m_tokens = t;
    }

    public void setTokens(int i) {
        setTokens(new Product(i));
    }

    /**
     * Sets the guard of the edge
     * @param g The guard value
     */
    public void setGuard(String g) {
        m_guard = g;
    }

    /**
     * Returns the name of the edge
     * @return The name of the edge
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the consumer of the edge
     * @return The consumer of the edge
     */
    public BPDFActor getConsumer() {
        return m_consumer;
    }

    /**
     * Returns the producer of the edge
     * @return The producer of the edge
     */
    public BPDFActor getProducer() {
        return m_producer;
    }

    /**
     * Returns the input rate of the edge
     * @return The input rate of the edge
     */
    public Product getRateIn() {
        return m_rateIn;
    }

    /**
     * Returns the output rate of the edge
     * @return The output rate of the edge
     */
    public Product getRateOut() {
        return m_rateOut;
    }

    /**
     * Returns the amount of stored tokens
     * @return The amount of stored tokens
     */
    public Product getTokens() {
        return m_tokens;
    }

    /**
     * Returns the edge's boolean guard
     * @return The edge's boolean guard
     */
    public String getGuard() {
        return m_guard;
    }
}
