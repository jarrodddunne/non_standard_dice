import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class factor {

	static final int[][] specialFactors = { {}, { 0, 1 }, { 1, 1 }, {},
			{ 1, 0, 1 }, {}, {}, {}, { 1, 0, 0, 0, 1 },
			{ 1, 0, 0, 1, 0, 0, 1 }, { 1, 0, 1, 0, 1, 0, 1, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, -1, 0, 1 } };
	static int sides = 0;
	static final int[] factorCombination = { 1, 1, 2, 4, 10, 26, 71, 197, 554,
			1570, 00 };
	static Polynomial[][] factorSums;
	static HashMap<Integer, ArrayList<Polynomial>> factorsHashMap = new HashMap<Integer, ArrayList<Polynomial>>();
	static int[] properSums;

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the number of sides: ");
		int input = Integer.parseInt(sc.next());
		sides = input;
		sc.close();
		int[] properDie = createStandardDie(sides);
		properSums = rollDie(properDie, properDie);
		int[] propSums = properSums;
		ArrayList<Integer> list = getAllFactors(input);
		if (!isPrime(input))
			list.add(input);
		Iterator it = list.iterator();
		// printArrayList(list);
		ArrayList<Polynomial> specialFactors = new ArrayList<Polynomial>();
		int size = 0;
		boolean done = false;
		while (it.hasNext() && !done) {
			Object asdf = it.next();
			if (asdf == null)
				done = true;
			Polynomial q = findSpecialFactor((int) asdf);
			specialFactors.add(q);
			size += 1;
		}
		Polynomial[] fctrs = new Polynomial[size];
		int num = 0;
		it = specialFactors.iterator();
		while (it.hasNext()) {
			fctrs[num] = (Polynomial) it.next();
			num += 1;
		}
		printPolyFactors(fctrs);
		int[] ones = new int[fctrs.length];
		for (int i = 0; i < fctrs.length; i++) {
			ones[i] = Polynomial.plugInOne(fctrs[i]);
		}
		printSums(ones);
		int prod = calcPossibleDice(fctrs, 2);
		// System.out.println("Possible combinations: " +
		// Integer.toString(prod));
		// if (prod == 1)
		// System.exit(0);
		System.out.println("Grouped Factors: " + factorsHashMap.toString());
		ArrayList<ArrayList<Polynomial[]>> alalp = new ArrayList<ArrayList<Polynomial[]>>();
		for (int i = 1; i < input + 1; i++) {
			if (factorsHashMap.containsKey(i)) {
				alalp.add(calcPossibleCombinations(factorsHashMap.get(i),
						(i == 1)));
			}
		}
		Iterator alalpIt = alalp.iterator();
		ArrayList<Polynomial[]> combinedFactors = new ArrayList<Polynomial[]>();
		if (alalp.size() > 1) {
			combinedFactors = combineFactors(alalp.get(0), alalp.get(1));
			alalpIt.next();
			alalpIt.next();
			while (alalpIt.hasNext()) {
				combinedFactors = combineFactors(combinedFactors,
						(ArrayList<Polynomial[]>) alalpIt.next());
			}
		} else {
			combinedFactors = alalp.get(0);
		}
		ArrayList<Polynomial[]> finalAlp = new ArrayList<Polynomial[]>();
		Iterator lastIt = combinedFactors.iterator();
		while (lastIt.hasNext()) {
			Polynomial[] p = (Polynomial[]) lastIt.next();
			Polynomial[] pInverse = { p[1], p[0] };
			if (!arrayListContains(p, finalAlp)) {
				finalAlp.add(p);
			} else {
//				System.out.println("Repeat: ");
//				System.out.println(p[0].toString() + ":" + p[1].toString());
			}
		}
		ArrayList<int[][]> diceAlp = new ArrayList<int[][]>();
		Iterator itDice = finalAlp.iterator();
		while (itDice.hasNext()) {
			Polynomial[] pq = (Polynomial[]) itDice.next();
			if (!containsNegative(pq)) {
				int[][] i = polynomialListToDice(pq);
				if (equalSums(properSums, rollDie(i[0], i[1]))) {
					diceAlp.add(i);
				} else {
//					System.out.println("Unequal Sums: ");
//					printDice(i);
				}
			}
		}
		int[][] propDicePair = {properDie,properDie};
		if(!diceArrayListContains(diceAlp, propDicePair)) diceAlp.add(propDicePair);
		printDice(diceAlp);
		System.out.println(diceAlp.size());
	}
	
	public static void printDice(int[][] dice){
		String die1 = "[";
		for(int i = 0;i<dice[0].length;i++){
			die1+=dice[0][i] + ",";
		}
		die1 += "]:";
		String die2 = "[";
		for(int b = 0;b<dice[1].length;b++){
			die2+=dice[1][b] + ",";
		}
		die2 += "]";
		System.out.println(die1+die2);
	}
	
	public static boolean diceArrayListContains(ArrayList<int[][]> diceAlp, int[][] i){
		for(int b = 0;b<diceAlp.size();b++){
			int[][] test = diceAlp.get(b);
			for(int c = 0;c<test.length;c++){
				for(int d = 0;d<i.length;d++){
					if(equalDice(test[c],i[d])) return true;
				}
			}
		}
		return false;
	}
	
	public static boolean equalDice(int[] die1, int[] die2){
		for(int i = 0;i<die1.length;i++){
			if(die1[i] != die2[i]) return false;
		}
		return true;
	}

	public static void printDice(ArrayList<int[][]> dice) {
		System.out.println("Possible Dice: ");
		Iterator it = dice.iterator();
		while (it.hasNext()) {
			int[][] die = (int[][]) it.next();
			System.out.println("  " + Arrays.toString(die[0]) + " : "
					+ Arrays.toString(die[1]));
		}

	}

	public static boolean containsNegative(Polynomial[] p) {
		for (int i = 0; i < p.length; i++) {
			int[] coef = p[i].getCoefficients();
			for (int j = 0; j < coef.length; j++) {
				if (coef[j] < 0)
					return true;
			}
		}
		return false;
	}

	public static int[][] polynomialListToDice(Polynomial[] p) {
		int[] a = polynomialToDie(p[0]);
		int[] b = polynomialToDie(p[1]);
		int[][] answer = { a, b };
		return answer;
	}

	public static int[] polynomialToDie(Polynomial p) {
		int[] a = p.getCoefficients();
		int[] die = new int[sides];
		int i = 0;
		for (int j = 0; j < a.length; j++) {
			if (a[j] != 0) {
				for (int b = 0; b < a[j]; b++) {
					die[i + b] = j + 1;
				}
				i += a[j];
			}
		}
		return die;
	}

	public static boolean arrayListContains(Polynomial[] p,
			ArrayList<Polynomial[]> q) {
		Iterator it = q.iterator();
		while (it.hasNext()) {
			Polynomial[] test = (Polynomial[]) it.next();
			if (samePolynomialLists(p, test))
				return true;
		}
		return false;
	}

	public static boolean samePolynomialLists(Polynomial[] a, Polynomial[] b) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				if (samePolynomial(a[i], b[j]))
					return true;
			}
		}
		return false;
	}

	public static boolean samePolynomial(Polynomial a, Polynomial b) {
		int[] ac = a.getCoefficients();
		int[] bc = b.getCoefficients();
		if(ac.length != bc.length) return false;
		for (int i = 0; i < ac.length; i++) {
			if (ac[i] < 0 || bc[i] < 0)
				return true;
			if (ac[i] != bc[i])
				return false;
		}
		return true;
	}

	public static ArrayList<Polynomial[]> combineFactors(
			ArrayList<Polynomial[]> alp1, ArrayList<Polynomial[]> alp2) {
		ArrayList<Polynomial[]> answer = new ArrayList<Polynomial[]>();
		Iterator it1 = alp1.iterator();
		boolean d1 = false;
		boolean d2 = false;
		while (it1.hasNext() && !d1) {
			Polynomial[] p1 = (Polynomial[]) it1.next();
			if (p1 == null)
				d1 = true;
			Iterator it2 = alp2.iterator();
			while (it2.hasNext() && !d2) {
				Polynomial[] p2 = (Polynomial[]) it2.next();
				if (p2 == null)
					d2 = true;
				for (int i = 0; i < p1.length; i++) {
					for (int j = 0; j < p2.length; j++) {
						Polynomial p = p1[i].multiply(p2[j]);
						Polynomial q = p1[1 - i].multiply(p2[1 - j]);
						Polynomial[] b = { p, q };
						answer.add(b);
					}
				}
			}
		}
		return answer;

	}

	public static ArrayList<Polynomial[]> calcPossibleCombinations(
			ArrayList<Polynomial> alp, boolean one) {
		ArrayList<Polynomial[]> list = new ArrayList<Polynomial[]>();
		int posse = 0;
		int size = alp.size();
		if (one && size == 1) {
			int[] i = { 1 };
			Polynomial id = new Polynomial(i);
			Polynomial p = alp.get(0);
			Polynomial asd = p.multiply(p);
			Polynomial[] q = { p, p };
			Polynomial[] b = { asd, id };
			list.add(q);
			list.add(b);
			return list;
		}
		String s = Integer.toString(size, 3);
		int num = 0;
		if (one) {
			for (int i = 0; i < Math.pow(3, size); i++) {
				String concat = "%0" + size + "d"; 
				char[] a = Integer.toString(i, 3).toCharArray();
				if(a.length < alp.size()){
					if(a.length == 1){
						String sb = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = sb.toCharArray();
					}else if(a.length == 2){
						String d = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = d.toCharArray();
					}else{
						String ab = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = ab.toCharArray();
					}
				}
				int[] asd = { 1 };
				Polynomial p = new Polynomial(asd);
				Polynomial p2 = new Polynomial(asd);
				for (int k = 0; k < a.length; k++) {
					int val = Character.getNumericValue(a[k]);
					if (val == 0) {
						p = p.multiply(alp.get(k));
						p = p.multiply(alp.get(k));
					} else if (val == 1) {
						p = p.multiply(alp.get(k));
						p2 = p2.multiply(alp.get(k));
					} else {
						p2 = p2.multiply(alp.get(k));
						p2 = p2.multiply(alp.get(k));
					}
					if (Character.getNumericValue(a[0]) == 2) {
						int intint = 0;
					}
				}
				Polynomial[] q = { p, p2 };
				list.add(q);
			}
		} else {
			for (int i = 0; i < Math.pow(3, size); i++) {
				String concat = "%0" + size + "d"; 
				char[] a = Integer.toString(i, 3).toCharArray();
				if(a.length < alp.size()){
					if(a.length == 1){
						String sb = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = sb.toCharArray();
					}else if(a.length == 2){
						String d = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = d.toCharArray();
					}else{
						String ab = String.format(concat,Integer.parseInt(Integer.toString(i,3)));
						a = ab.toCharArray();
					}
				}
				int sum = 0;
				for (int j = 0; j < a.length; j++) {
					int asdf = Character.getNumericValue(a[j]);
					if (asdf == 0)
						sum -= 1;
					if (asdf == 2)
						sum += 1;
				}
				if (sum == 0) {
					posse += 1;
					int[] asd = { 1 };
					Polynomial p = new Polynomial(asd);
					Polynomial p2 = new Polynomial(asd);
					for (int k = 0; k < a.length; k++) {
						int val = Character.getNumericValue(a[k]);
						if (val == 0) {
							p = p.multiply(alp.get(k));
							p = p.multiply(alp.get(k));
						} else if (val == 1) {
							p = p.multiply(alp.get(k));
							p2 = p2.multiply(alp.get(k));
						} else {
							p2 = p2.multiply(alp.get(k));
							p2 = p2.multiply(alp.get(k));
						}
						if (Character.getNumericValue(a[0]) == 2) {
							int intint = 0;
						}
					}
					Polynomial[] q = { p, p2 };
					list.add(q);
				}
			}
		}
		return list;
	}

	public static int calcPossibleDice(Polynomial[] p, int rolls) {
		int[] nums = new int[p.length * 150];
		for (int b = 0; b < p.length; b++) {
			for (int i = 0; i < 1000; i++) {
				if ((Polynomial.plugInOne(p[b])) == i) {
					nums[i] += 1;
					HashMap<Integer, ArrayList<Polynomial>> asdfasdf = factorsHashMap;
					if (factorsHashMap.containsKey(i) == false) {
						ArrayList<Polynomial> asdf = new ArrayList<Polynomial>();
						asdf.add(p[b]);
						factorsHashMap.put(i, asdf);
					} else {
						ArrayList<Polynomial> asdf = factorsHashMap.get(i);
						factorsHashMap.remove(i);
						asdf.add(p[b]);
						factorsHashMap.put(i, asdf);
					}
				}
			}
		}
		int product = 1;
		if (nums[1] != 0) {
			product *= Math.pow(rolls, nums[1]);
		}
		for (int i = 2; i < nums.length; i++) {
			if (nums[i] != 0) {
				product *= factorCombination[nums[i]];
			}
		}
		return product;
	}

	public static ArrayList<Integer> getAllFactors(int num) {
		ArrayList<Integer> factors = new ArrayList<Integer>();
		if (isPrime(num))
			factors.add(num);
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				factors.add(i);
			}
		}
		return factors;
	}

	public static int[] createStandardDie(int sides) {
		int[] die = new int[sides];
		for (int i = 0; i < sides; i++) {
			die[i] = i + 1;
		}
		return die;
	}

	public static int[] rollDie(int[] die1, int[] die2) {
		int[] answer = new int[die1.length + die2.length + 1];
		for (int i = 0; i < die1.length; i++) {
			for (int j = 0; j < die2.length; j++) {
				answer[die1[i] + die2[j]] += 1;
			}
		}
		return answer;
	}

	public static boolean equalSums(int[] sum1, int[] sum2) {
		for (int x = 0; x < sum1.length; x++) {
			if (sum1[x] != sum2[x]) {
				return false;
			}
		}
		return true;
	}

	public static void printSums(int[] sums) {
		for (int i = 0; i < sums.length; i++) {
			System.out.println("Factor Sum: " + Integer.toString(sums[i]));
		}
	}

	public static void printPolyFactors(Polynomial[] p) {
		for (int i = 0; i < p.length; i++) {
			System.out.println("Factor: " + p[i].toString());
		}
	}

	public static void printArrayList(ArrayList<Integer> list) {
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()) {
			System.out.println("List element: " + it.next());
		}
	}

	public static Polynomial findSpecialFactor(int factor) {
		ArrayList<Integer> list = getAllFactors(factor);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			if (factor == 2) {
				int[] coef = { 1, 1 };
				Polynomial p = new Polynomial(coef);
				return p;
			} else if (isPowerOfTwo(factor)) {
				int[] coef = new int[(factor / 2 + 1)];
				coef[0] = 1;
				coef[coef.length - 1] = 1;
				Polynomial answer = new Polynomial(coef);
				return answer;
			} else if (isPrime(factor)) {
				int[] coef = new int[factor];
				Arrays.fill(coef, 1);
				Polynomial answer = new Polynomial(coef);
				return answer;
			} else if (isPrime(factor / 2) && factor % 2 == 0) {
				int[] coef = new int[factor / 2];
				Arrays.fill(coef, 1);
				for (int i = 1; i < factor / 2; i += 2) {
					coef[i] = -1;
				}
				Polynomial p2 = new Polynomial(coef);
				return p2;
			} else if (factor < 13) {
				Polynomial answer = new Polynomial(specialFactors[factor]);
				return answer;
			} else {
				ArrayList<Integer> ls = getAllFactors(factor);
				int[] coef = new int[factor];
				Arrays.fill(coef, 1);
				Polynomial p = new Polynomial(coef);
				Iterator it1 = ls.iterator();
				while (it1.hasNext() && it1.next() != null) {
					int i = (int) it.next();
					Polynomial poly = findSpecialFactor(i);
					p = p.quotient(poly);
				}
				return p;
			}
		}
		return null;
	}

	public static boolean isPrime(int num) {
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPowerOfTwo(int number) {
		for (int i = 0; i < 1000; i++) {
			if (number == Math.pow(2, i)) {
				return true;
			}
		}
		return false;
	}
}
