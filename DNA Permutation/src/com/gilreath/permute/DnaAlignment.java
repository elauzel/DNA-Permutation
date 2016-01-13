package com.gilreath.permute;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DnaAlignment {
	private static Integer	highestScore;
	private static Integer	lowestScore;
	private static String	originalDNA;
	private static int		originalDNALength;
	private static char[]	mutatedDNA;
	private static int		mutatedDNALength;
	private static String	bestPermutation;
	private static String	worstPermutation;
	private static int		permutations	= 0;

	public static void main(String[] args) {
		final long startTime = System.nanoTime();
		try (BufferedReader br = new BufferedReader(new FileReader("./doc/input/DnaAlignment.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				performPermutations(line.split(" | "));
			}
			System.out.println("Processed " + permutations + " permutations in " + ((System.nanoTime() - startTime) / 1000000) + " milliseconds");
			System.out.println("Original:\t" + originalDNA);
			System.out.println("Best Match:\t" + bestPermutation + "\tScore: " + highestScore);
			System.out.println("Worst Match:\t" + worstPermutation + "\tScore: " + lowestScore);
		} catch (FileNotFoundException fnfe) {
			System.err.println(fnfe.getMessage());
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	private static void performPermutations(String[] pairs) {
		final String pairOne = pairs[0].trim();
		final String pairTwo = pairs[2].trim();
		final int pairOneLength = pairOne.length();
		final int pairTwoLength = pairTwo.length();
		if (pairOneLength == pairTwoLength) {
			originalDNA = pairOne;
			originalDNALength = pairOneLength;
			score(pairTwo);
		} else if (pairOneLength < pairTwoLength) {
			originalDNA = pairTwo;
			originalDNALength = pairTwoLength;
			mutatedDNA = pairOne.toCharArray();
			mutatedDNALength = pairOneLength;
		} else {
			originalDNA = pairOne;
			originalDNALength = pairOneLength;
			mutatedDNA = pairTwo.toCharArray();
			mutatedDNALength = pairTwoLength;
		}
		permuteAndScore(0, Math.abs(pairOneLength - pairTwoLength), "");
	}

	private static void permuteAndScore(int nextChar, int hyphensRemaining, String permutation) {
		if (originalDNALength == permutation.length()) {
			score(permutation);
		} else {
			if (nextChar != mutatedDNALength) {
				permuteAndScore(nextChar + 1, hyphensRemaining, permutation + mutatedDNA[nextChar]);
			}
			if (hyphensRemaining > 0) {
				permuteAndScore(nextChar, hyphensRemaining - 1, permutation + "-");
			}
		}
	}

	private static void scoreAndPrint(final String pairWithIndels) {
		System.out.println(pairWithIndels);
		score(pairWithIndels);
	}

	private static void score(final String pairWithIndels) {
		permutations++;
		int score = 0;
		boolean indel = false;
		for (int i = 0; i < pairWithIndels.length(); i++) {
			if (pairWithIndels.charAt(i) == originalDNA.charAt(i)) {
				score += 3;// Match: +3
				indel = false;
			} else if (pairWithIndels.charAt(i) == '-') {
				if (indel) {
					score -= 1;// Indel extension: -1
				} else {
					score -= 8;// Indel start: -8
					indel = true;
				}
			} else {
				score -= 3;// Mismatch: -3
				indel = false;
			}
		}
		if (highestScore == null || highestScore < score) {
			highestScore = score;
			bestPermutation = pairWithIndels;
		}
		if (lowestScore == null || score < lowestScore) {
			lowestScore = score;
			worstPermutation = pairWithIndels;
		}
	}
}
