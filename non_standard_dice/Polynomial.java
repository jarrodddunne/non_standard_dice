public class Polynomial {

	private int[] coefficients;
	private int actualdegree;

	public Polynomial(int a, int b) {

		this(b);
		this.setCoefficient(b, a);

		updateDegree(); // called whenever coefficients is changed

	}
	
	public int[] getCoefficients(){
		return coefficients;
	}

	public Polynomial(int deg) {

		if (deg < 0) {
			System.out.println("Polynomial degree must not be negative");
			System.exit(1);
		}

		coefficients = new int[deg + 1];

		updateDegree();

	}

	public Polynomial(int[] coeffs) {

		coefficients = (int[]) coeffs.clone();

		updateDegree();

	}

	public int Degree() {
		return coefficients.length - 1;
	}

	public int ActualDegree() {

		return actualdegree;
	}

	private void updateDegree() {

		for (int i = coefficients.length - 1; i >= 0; i--)
			if (coefficients[i] != 0) {
				actualdegree = i;
				return;
			}

		actualdegree = 0;

	}

	/**
	 * Retrieves a specific coefficient of the polynomial.
	 * 
	 * @param r
	 *            The degree of the coefficient to return. If this is out of the
	 *            range, zero is returned.
	 * @return The coefficient for the degree specified.
	 */
	public int Coefficient(int r) {

		if (r > Degree() || r < 0)
			return 0;

		return coefficients[r];

	}

	public void setCoefficient(int r, int value) {
		if (r < 0) {
			System.out.println("Invalid degree for setCoefficient");
			return;
		}
		if (r > Degree()) {

			int newlength;

			newlength = Math.max(coefficients.length, r + 1);

			int[] newcoeffs = new int[newlength];

			for (int i = 0; i < coefficients.length; i++)
				newcoeffs[i] = coefficients[i];

			newcoeffs[r] = value;
			coefficients = newcoeffs;
		} else
			coefficients[r] = value;

		updateDegree();

	}

	public double evaluate(double x) {

		double sum = 0;

		for (int i = 0; i < coefficients.length; i++)
			sum += (double) coefficients[i] * Math.pow(x, i);

		return sum;

	}

	public Polynomial evaluate(Polynomial other) {

		Polynomial output;

		output = new Polynomial(other.Degree() * this.Degree());

		for (int a = 0; a <= other.ActualDegree(); a++)
			output = output
					.add(this.powerOf(a).scalarmul(other.Coefficient(a)));

		return output;
	}

	public Polynomial clonePoly() {

		return new Polynomial(coefficients);

	}

	public Polynomial add(Polynomial other) {

		Polynomial large, small, output;

		if (other.ActualDegree() > ActualDegree()) {
			large = other;
			small = this;
		} else {
			large = this;
			small = other;
		}

		output = large.clonePoly();

		for (int i = 0; i <= small.ActualDegree(); i++)
			output.setCoefficient(i,
					output.Coefficient(i) + small.Coefficient(i));

		return output;

	}

	/**
	 * Subtracts a polynomial from this polynomial.
	 * 
	 * @param other
	 *            A polynomial to subtract from this polynomial.
	 * @return A new polynomial which is this polynomial subtract other.
	 */
	public Polynomial subtract(Polynomial other) {

		return this.add(other.scalarmul(-1));

	}

	/**
	 * Multiplies a polynomial by this polynomial.
	 * 
	 * @param other
	 *            A polynomial to multiply by this.
	 * @return A new polynomial which is the multiple of this and other.
	 */
	public Polynomial multiply(Polynomial other) {

		Polynomial output;

		output = new Polynomial(other.Degree() + this.Degree());

		for (int a = 0; a <= this.ActualDegree(); a++){
			for (int b = 0; b <= other.ActualDegree(); b++)
				output.setCoefficient(
						a + b,
						output.Coefficient(a + b) + other.Coefficient(b)
								* this.Coefficient(a));
		}

		return output;

	}

	public Polynomial scalarmul(int k) {

		Polynomial output;

		output = new Polynomial(Degree());

		for (int i = 0; i <= ActualDegree(); i++)
			output.setCoefficient(i, k * Coefficient(i));

		return output;

	}

	public Polynomial powerOf(int pow) {

		if (pow < 0) {
			System.out.println("Negative powers are not allowed");
			return null;
		}

		int[] one = { 1 };
		Polynomial output = new Polynomial(one); // this takes care of pow==0
													// case

		for (int i = 1; i <= pow; i++)
			output = output.multiply(this);

		return output;
	}

	public Polynomial quotient(Polynomial other) {

		return this.divide(other, true);

	}

	public Polynomial remainder(Polynomial other) {

		return this.divide(other, false);

	}

	private Polynomial divide(Polynomial div, boolean returnquotient) {
		int maxdeg = div.Degree();
		if (maxdeg == 0) {
			System.out.println("Polynomial divide by zero error");
			return null;
		}
		Polynomial rem = this.clonePoly();
		Polynomial running = new Polynomial(/*this.Degree() - */maxdeg);
		for (int i = this.Degree(); i >= maxdeg; i--) {
			running.setCoefficient(i - maxdeg, rem.Coefficient(i));
			rem = this.subtract(div.multiply(running));
		}

		if (returnquotient)
			return running;
		else
			return rem;

	}

	public Polynomial derivative() {

		Polynomial output = new Polynomial(this.Degree() - 1);

		for (int i = 1; i <= ActualDegree(); i++)
			output.setCoefficient(i - 1, this.Coefficient(i) * i);

		return output;

	}
	
	public static int plugInOne(Polynomial a){
		int sum = 0;
		int[] b = a.coefficients;
		for(int i =0;i<b.length;i++){
			sum += b[i];
		}
		return sum;
	}

	public String toString() {
		if (this.Degree() == 0)
			return "" + coefficients[0];
		if (this.Degree() == 1)
			return coefficients[1]
					+ "x "
					+ ((coefficients[0] > 0) ? "+ " + coefficients[0]
							: (coefficients[0] == 0) ? "" : "- "
									+ -coefficients[0]);
		String s = coefficients[this.Degree()] > 0 ? coefficients[this.Degree()]
				+ "x^" + this.Degree()
				: "";
		for (int i = this.Degree() - 1; i >= 0; i--) {
			if (coefficients[i] == 0)
				continue;
			else if (coefficients[i] > 0)
				s = s.equals("") ? s + (coefficients[i]) : s + " + "
						+ (coefficients[i]);
			else if (coefficients[i] < 0)
				s = s.equals("") ? s + (coefficients[i]) : s + " - "
						+ (-coefficients[i]);
			if (i == 1)
				s = s + "x";
			else if (i > 1)
				s = s + "x^" + i;
		}
		return s;
	}

	public static void main(String[] args) {
		System.out.println("NOT A RUNNABLE CLASS");
	}
}