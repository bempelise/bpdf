// SymbolTest.java
package bpdf.symbol;

import java.util.ArrayList;
import java.lang.Math;

public class SymbolTest {

    /**
     * @param args
     */
    public static void main(String[] args) {


        boolean testProduct = false;
        boolean testFraction = false;
        boolean testPolynomial = false;
        boolean testComposite = false;
        boolean testBoolean = true;
        
        // Testing Product class
        if (testProduct)
            testProduct();
        

        // Testing Polynomial class
        if (testPolynomial)
            testPolynomial();

        // testing Composite expansion
        if (testComposite)
            testComposite();

        if (testBoolean)
            testBoolean();
        
    }

    static void testProduct()
    {
        // Constructors & boolean properties
        Product zeroProd = new Product("0");
        Expression zeroExpr = new Product(0);
        Product unitProd = new Product("1");
        Expression unitExpr = new Product(1);
        Product unit2Prod = new Product(unitProd);

        // System.out.println(zeroProd.getString());
        // System.out.println(zeroExpr.getString());
        // System.out.println(unitProd.getString());
        // System.out.println(unitExpr.getString());
        // System.out.println(unit2Prod.getString());

        assert zeroProd.isZero();
        assert zeroProd.isNumber();
        assert zeroProd.isProduct();
        assert zeroProd.isFraction();
        assert zeroExpr.isZero();
        assert zeroProd.isEqualTo(zeroExpr);

        assert unitProd.isUnit();
        assert unitProd.isNumber();
        assert unitProd.isProduct();
        assert unitProd.isFraction();
        assert unitExpr.isUnit();
        assert unitProd.isEqualTo(unitExpr);

        assert unit2Prod.isUnit();
        assert unit2Prod.isEqualTo(unitProd);
        assert unit2Prod.isEqualTo(unitExpr);

        /* Base products */
        Expression prod1 = new Product("6*p");
        Expression prod2 = new Product("3");
        Expression prod3 = new Product("p");
        Expression prod4 = new Product("3*q^3");
        Expression prod5 = new Product("2*p^2");

        // System.out.println(prod1.getString());
        // System.out.println(prod2.getString());
        // System.out.println(prod3.getString());
        // System.out.println(prod4.getString());
        // System.out.println(prod5.getString());
        // System.out.println("");

        /* Parametric part only */
        Expression prod1param = prod1.getParam();
        Expression prod2param = prod2.getParam();
        Expression prod3param = prod3.getParam();
        Expression prod4param = prod4.getParam();
        Expression prod5param = prod5.getParam();

        // System.out.println(prod1param.getString());
        // System.out.println(prod2param.getString());
        // System.out.println(prod3param.getString());
        // System.out.println(prod4param.getString());
        // System.out.println(prod5param.getString());
        // System.out.println("");

        /* Multiplication */
        Expression resProd13 = new Product("6*p^2");
        Expression resProd34 = new Product("3*p*q^3");
        Expression resProd25 = new Product("6*p^2");
        Expression resProd14 = new Product("18*p*q^3");

        Expression prod13 = prod1.multiply(prod3);
        Expression prod34 = prod3.multiply(prod4);
        Expression prod25 = prod2.multiply(prod5);
        Expression prod14 = prod1.multiply(prod4);
        
        assert prod13.isEqualTo(prod25);
        assert prod13.isEqualTo(resProd13);
        assert prod34.isEqualTo(resProd34);
        assert prod25.isEqualTo(resProd25);
        assert prod14.isEqualTo(resProd14);

        // System.out.println(prod13.getString());
        // System.out.println(prod34.getString());
        // System.out.println(prod25.getString());
        // System.out.println(prod14.getString());
        // System.out.println("");

        /* Division */
        Expression quot13 = prod1.divide(prod3);
        Expression quot42 = prod4.divide(prod2);
        Expression quot24 = prod2.divide(prod4);
        Expression quot35 = prod3.divide(prod5);
        Expression quot43 = prod4.divide(prod3);
 
        Expression resQuot13 = new Fraction(new Product("6"));
        Expression resQuot42 = new Fraction(new Product("q^3"));
        Expression resQuot24 = new Fraction(unitProd,new Product("q^3"));
        Expression resQuot35 = new Fraction(unitProd,new Product("2*p"));
        Expression resQuot43 = new Fraction(prod4,prod3);

        assert quot13.isEqualTo(resQuot13);
        assert quot42.isEqualTo(resQuot42);
        assert quot24.isEqualTo(resQuot24);
        assert quot35.isEqualTo(resQuot35);
        assert quot43.isEqualTo(resQuot43);

        // System.out.println(quot13.getString());
        // System.out.println(quot42.getString());
        // System.out.println(quot24.getString());
        // System.out.println(quot35.getString());
        // System.out.println(quot43.getString());
        // System.out.println("");


        /* Negative Products */
        Product prod1n = new Product("-6*p");
        Product prod2n = new Product("-3");
        Product prod3n = new Product("-p");
        Product prod4n = new Product("-3*q^3");
        Product prod5n = new Product("-2*p^2");

        // System.out.println(prod1n.getString());
        // System.out.println(prod2n.getString());
        // System.out.println(prod3n.getString());
        // System.out.println(prod4n.getString());
        // System.out.println(prod5n.getString());
        // System.out.println("");

        Product prodZeron = new Product(-0);
        Product prodUnitn = new Product(-1);
        assert (prodZeron.isZero());
        assert (prodUnitn.isUnit());

        /* Multiplication */
        Expression prod1by1n = prod1.multiply(prod1n);
        Expression prod2by2n = prod2.multiply(prod2n);
        Expression prod3by3n = prod3.multiply(prod3n);
        Expression prod4by4n = prod4.multiply(prod4n);
        Expression prod5by5n = prod5.multiply(prod5n);

        // System.out.println(prod1by1n.getString());
        // System.out.println(prod2by2n.getString());
        // System.out.println(prod3by3n.getString());
        // System.out.println(prod4by4n.getString());
        // System.out.println(prod5by5n.getString());
        // System.out.println("");

        /* Division */
        Expression prod1to1n = prod1.divide(prod1n);
        Expression prod2to2n = prod2.divide(prod2n);
        Expression prod3to3n = prod3.divide(prod3n);
        Expression prod4to4n = prod4.divide(prod4n);
        Expression prod5to5n = prod5.divide(prod5n);

        // System.out.println(prod1to1n.getString());
        // System.out.println(prod2to2n.getString());
        // System.out.println(prod3to3n.getString());
        // System.out.println(prod4to4n.getString());
        // System.out.println(prod5to5n.getString());
        // System.out.println("");

        /* Subtraction */
        Expression prod1add1n = prod1.add(prod1n);
        Expression prod2add2n = prod2.add(prod2n);
        Expression prod3add3n = prod3.add(prod3n);
        Expression prod4add4n = prod4.add(prod4n);
        Expression prod5add5n = prod5.add(prod5n);

        // System.out.println(prod1add1n.getString());
        // System.out.println(prod2add2n.getString());
        // System.out.println(prod3add3n.getString());
        // System.out.println(prod4add4n.getString());
        // System.out.println(prod5add5n.getString());
        // System.out.println("");

        // System.out.println(prod1add1n.isZero());
        // System.out.println(prod2add2n.isZero());
        // System.out.println(prod3add3n.isZero());
        // System.out.println(prod4add4n.isZero()); 
        // System.out.println(prod5add5n.isZero());

        /* Comparison */
        Expression prod1comp = new Product("4*p");
        Expression prod2comp = new Product("2*p");
        Expression prod3comp = new Product("4*p^2");
        Expression prod4comp = new Product("4*q");
        Expression prod5comp = new Product(4);
        Expression prod6comp = new Product(3);

        System.out.println(prod1comp.getString()
            + " > " + prod2comp.getString()
            + " - " + prod1comp.isGreaterThan(prod2comp));
        System.out.println(prod5comp.getString()
            + " > " + prod6comp.getString()
            + " - " + prod5comp.isGreaterThan(prod6comp));
        System.out.println(prod3comp.getString()
            + " > " + prod2comp.getString()
            + " - " + prod3comp.isGreaterThan(prod2comp));

        assert prod1comp.isGreaterThan(prod2comp);
        assert prod5comp.isGreaterThan(prod6comp);
        assert prod3comp.isGreaterThan(prod2comp);

        System.out.println(prod2comp.getString()
            + " > " + prod1comp.getString()
            + " - " + prod2comp.isGreaterThan(prod1comp));
        System.out.println(prod6comp.getString()
            + " > " + prod5comp.getString()
            + " - " + prod6comp.isGreaterThan(prod5comp));
        System.out.println(prod2comp.getString() 
            + " > " + prod3comp.getString()
            + " - " + prod2comp.isGreaterThan(prod3comp));

        /* Throws exception */
        // prod4comp.isGreaterThan(prod1comp);
    }

    static void testPolynomial()
    {
        Fraction frac1 = new Fraction(new Product(0),new Product(3));
        assert frac1.isZero();
        Fraction frac2 = new Fraction(new Product("6"),new Product(2));
        assert frac2.isNumber();

        Fraction frac3 = new Fraction(new Product("3*x"),new Product("2*y"));
        Fraction frac4 = new Fraction(new Product("x"),new Product("3*y"));
        Fraction frac5 = new Fraction(new Product("2*x"),new Product("y"));

        ArrayList<Expression> fracList1 = new ArrayList<Expression>();
        ArrayList<Expression> fracList2 = new ArrayList<Expression>();
        ArrayList<Expression> fracList3 = new ArrayList<Expression>();

        fracList1.add(new Fraction(new Product("d"),new Product(5)));
        fracList1.add(frac1);

        fracList2.add(new Fraction(new Product("3*d"),new Product("q")));
        fracList2.add(new Fraction(new Product("2*p"),new Product(9)));
        fracList2.add(frac2);

        fracList3.add(frac3);
        fracList3.add(frac4);
        fracList3.add(frac5);

        Polynomial poly1 = new Polynomial();
        Polynomial poly2 = new Polynomial(
            new Fraction(new Product("q"),new Product(3)));
        Polynomial poly3 = new Polynomial(fracList1);
        Polynomial poly4 = new Polynomial(fracList2);
        Polynomial poly5 = new Polynomial(fracList3);

        // System.out.println(poly1.getString());
        // System.out.println(poly2.getString());
        // System.out.println(poly3.getString());
        // System.out.println(poly4.getString());
        // System.out.println(poly5.getString());

        /* Base products */
        Expression prod1 = new Product("6*p");
        Expression prod2 = new Product("3");
        Expression prod3 = new Product("p");
        Expression prod4 = new Product("-p");
        Expression prod5 = new Product("-2");

        Polynomial poly6 = new Polynomial(prod1);
        poly6 = poly6.add(prod2).getPolynomial();

        Polynomial poly7 = new Polynomial(prod1);
        poly7 = poly7.add(prod3).getPolynomial();
        
        Polynomial poly8 = new Polynomial(prod3);
        poly8 = poly8.add(prod4).getPolynomial();
        
        Polynomial poly9 = new Polynomial(prod4);
        poly9 = poly9.add(prod1).getPolynomial();
        
        Polynomial poly10 = new Polynomial(prod2);
        poly10 = poly10.add(prod5).getPolynomial();
        
        Polynomial poly11 = poly9.add(prod4).getPolynomial();

        System.out.println(poly6.getString());
        System.out.println(poly7.getString());
        System.out.println(poly8.getString());
        System.out.println(poly9.getString());
        System.out.println(poly10.getString());
        System.out.println(poly11.getString());
    }

    static void testComposite()
    {
        Product uSol = new Product("5*p*q");
        Product mSol = new Product("5*p");
        Product rPeriod = new Product("p*q");
        Product wPeriod = new Product("p");
        Product negWPeriod = new Product("-p");

        Expression aux = new Fraction(new Product("i"),rPeriod);

        Expression f = wPeriod.multiply(
                        aux.ceiling()).add(new Product (1)).add(negWPeriod);

        // System.out.println(f.getClass().getName());
        // f = f.add(new Product (1));
        // System.out.println(f.getClass().getName());
        // f = f.add(negWPeriod);
        // System.out.println(f.getClass().getName());


        Expression feval = f.evaluate("p",1);
        System.out.println(feval.getString());
        feval = feval.evaluate("q",2);
        System.out.println(feval.getString());
        feval = feval.evaluate("i",1);
        System.out.println(feval.getString());

    }

    static void testBoolean()
    {
        String expr1 = "(a&b)|c&!a";
        String expr2 = "(!a&b)";
        String expr3 = "!a&b";
        String expr4 = "(a&b|!c)|(a&b)|c";
        String expr5 = "(a&b)|c";
        String expr6 = "(a&b|!c)";

        // System.out.println(expr1);
        // System.out.println(expr2);
        // System.out.println(expr3);
        // System.out.println(expr4);
        // System.out.println(expr5);
        // System.out.println(expr6);

        BooleanComposite test1 = new BooleanComposite(expr1);
        BooleanComposite test2 = new BooleanComposite(expr2);
        BooleanComposite test3 = new BooleanComposite(expr3);
        BooleanComposite test4 = new BooleanComposite(expr4);
        BooleanComposite test5 = new BooleanComposite(expr5);
        BooleanComposite test6 = new BooleanComposite(expr6);

        // test.print();
        // test2.print();
        // test3.print();
        // test4.print();
        // test5.print();
        // test6.print();



        test1.setValue("a",true);
        test1.setValue("b",false);
        test1.setValue("c",true);

        test2.setValue("a",false);
        test2.setValue("b",true);

        test3.setValue("a",false);
        test3.setValue("b",true);

        test4.setValue("a",true);
        test4.setValue("b",true);
        test4.setValue("c",true);



        System.out.println(test1.getValue());
        // System.out.println(test2.getValue());
        // System.out.println(test3.getValue());
        // System.out.println(test4.getValue());
        // System.out.println(test5.getValue());
        // System.out.println(test6.getValue());



        }
}