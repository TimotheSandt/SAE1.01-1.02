public class Complex {

    private final double re;   // Real part
    private final double im;   // Imaginary part

    // Constructor to create a complex number
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    // Return real part
    public double re() {
        return re;
    }

    // Return imaginary part
    public double im() {
        return im;
    }

    // Return the modulus (magnitude) of the complex number
    public double abs() {
        return Math.sqrt(re * re + im * im);
    }

    // Add this complex number to another complex number
    public Complex plus(Complex b) {
        double real = this.re + b.re;
        double imag = this.im + b.im;
        return new Complex(real, imag);
    }

    // Subtract another complex number from this one
    public Complex minus(Complex b) {
        double real = this.re - b.re;
        double imag = this.im - b.im;
        return new Complex(real, imag);
    }

    // Multiply this complex number by another complex number
    public Complex times(Complex b) {
        double real = this.re * b.re - this.im * b.im;
        double imag = this.re * b.im + this.im * b.re;
        return new Complex(real, imag);
    }

    // Scale this complex number by a real factor
    public Complex scale(double alpha) {
        return new Complex(alpha * this.re, alpha * this.im);
    }

    // Take the conjugate of this complex number
    public Complex conjugate() {
        return new Complex(this.re, -this.im);
    }

    // Return the complex number as a string in the form a + bi
    public String toString() {
        if (im == 0) {
            return re + "";
        } else if (re == 0) {
            return im + "i";
        } else {
            return re + " + " + im + "i";
        }
    }
}
