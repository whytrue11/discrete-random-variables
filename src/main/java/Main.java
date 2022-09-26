import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
  private static final Workbook WORKBOOK = new HSSFWorkbook();

  public static void main(String[] args) {
    int n = 10000;

    Scanner scanner = new Scanner(System.in);
    System.out.println("1: Uniform distribution");
    System.out.println("2: Binomial distribution");
    System.out.println("3: Geometric distribution 1");
    System.out.println("4: Geometric distribution 2");
    System.out.println("5: Geometric distribution 3");
    System.out.println("6: Poisson distribution 1");
    System.out.println("7: Poisson distribution 2");
    System.out.println("8: Logarithmic distribution");
    System.out.println("\nInput number of distribution: ");
    int my_case = scanner.nextInt();
    switch (my_case) {
      case 1 -> distribution(n, "Uniform distribution");
      case 2 -> distribution(n, "Binomial distribution");
      case 3 -> distribution(n, "Geometric distribution 1");
      case 4 -> distribution(n, "Geometric distribution 2");
      case 5 -> distribution(n, "Geometric distribution 3");
      case 6 -> distribution(n, "Poisson distribution 1");
      case 7 -> distribution(n, "Poisson distribution 2");
      case 8 -> distribution(n, "Logarithmic distribution");
      default -> System.out.println("Incorrect number");
    }

    try {
      WORKBOOK.write(new FileOutputStream("k.xls"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void distribution(int n, String distribution) {
    List<Integer> randomNumbers = new ArrayList<>(n);
    int rUp = 10;
    switch (distribution) {
      case "Uniform distribution": {
        int lLow = 1;
        rUp = 100;
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNUNI(lLow, rUp));
        }
        break;
      }
      case "Binomial distribution": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNBIN(10, 0.5));
        }
        break;
      }
      case "Geometric distribution 1": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNGEO_1(0.5));
        }
        break;
      }
      case "Geometric distribution 2": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNGEO_2(0.5));
        }
        break;
      }
      case "Geometric distribution 3": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNGEO_3(0.5));
        }
        break;
      }
      case "Poisson distribution 1": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNPOI(10));
        }
        break;
      }
      case "Poisson distribution 2": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNPSN(10));
        }
        break;
      }
      case "Logarithmic distribution": {
        for (int i = 0; i < n; i++) {
          randomNumbers.add(IRNLOG(0.5));
        }
        break;
      }
    }

    rUp = randomNumbers.stream().max(Integer::compareTo).get();
    System.out.println("\n" + distribution);
    Sheet sheet = WORKBOOK.createSheet(distribution);
    graphics(rUp, n, sheet, randomNumbers);
    evaluation(n, randomNumbers);
  }

  private static void graphics(int rUp, int n, Sheet sheet, List<Integer> randomNumbers) {
    List<Double> probability = new ArrayList<>(10);
    List<Double> probability2 = new ArrayList<>(10);

    int k = 10;
    int h = rUp / 10;
    if (h == 1) {
      k = rUp;
    }
    else if (rUp % 10 > 0) {
      k += rUp % 10 / h + ((rUp % 10 % h > 0) ? 1 : 0);
    }

    for (int i = 0; i < k; i++) {
      probability.add((double) 0);
      probability2.add((double) 0);
    }
    for (int i = 0; i < n; i++) {
      probability2.set(randomNumbers.get(i) == rUp ? (randomNumbers.get(i) / h - 1) : (randomNumbers.get(i) / h), probability2.get(randomNumbers.get(i) == rUp ? (randomNumbers.get(i) / h - 1) : (randomNumbers.get(i) / h)) + 1);
      for (int j = randomNumbers.get(i) == rUp ? (randomNumbers.get(i) / h - 1) : (randomNumbers.get(i) / h); j < k; j++) {
        probability.set(j, probability.get(j) + 1);
      }
    }

    for (int i = 0; i < k; i++) {
      probability.set(i, probability.get(i) / n);
      probability2.set(i, probability2.get(i) / n);
    }

    List<Cell> cells = new ArrayList<>(2);
    for (int i = 0; i < k; i++) {
      Row row = sheet.createRow(i);
      cells.add(row.createCell(0));
      cells.add(row.createCell(1));
      cells.get(0).setCellValue(probability.get(i));
      cells.get(1).setCellValue(probability2.get(i));
      cells.clear();
    }
  }

  private static void evaluation(int n, List<Integer> randomNumbers) {
    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += randomNumbers.get(i);
    }
    double matheExpectation = sum / n;

    double dispersion = 0;
    for (int i = 0; i < n; i++) {
      dispersion += Math.pow(randomNumbers.get(i) - matheExpectation, 2);
    }
    dispersion /= n;

    System.out.println("n: " + n +
        "\nMath expectation: " + matheExpectation +
        "\nDispersion: " + dispersion);
  }

  private static int IRNUNI(int ILOW, int IUP) {
    IUP -= ILOW;
    return (int) (Math.random() * ++IUP) + ILOW;
  }

  private static int IRNBIN(int n, double p) {
    double a = Math.random();
    double p_r = Math.pow(1 - p, n);
    int m = 0;
    while (a - p_r >= 0) {
      a = a - p_r;
      p_r = p_r * ((p * (n - m)) / ((m + 1) * (1 - p)));
      m++;
    }
    return m;
  }

  private static int IRNGEO_1(double p) {
    double a = Math.random();
    double p_r = p;
    int m = 0;
    while (a - p_r >= 0) {
      a -= p_r;
      p_r *= (1 - p);
      m++;
    }
    return m;
  }

  private static int IRNGEO_2(double p) {
    double a = Math.random();
    int k = 0;
    while (a > p) {
      a = Math.random();
      k++;
    }
    return k;
  }

  private static int IRNGEO_3(double p) {
    double a = Math.random();
    return (int) (Math.round(Math.log(a) / Math.log(1 - p)) + 1);
  }

  private static int IRNPOI(double mu) {
    double a = Math.random();
    double p_r = Math.exp(-mu);
    int m = 1;
    while (a - p_r >= 0) {
      a -= p_r;
      p_r *= mu / m;
      m++;
    }
    return m;
  }

  private static int IRNPSN(double mu) {
    double a = Math.random();
    double p_r = a;
    int m = 1;
    while (p_r >= Math.exp(-mu)) {
      a = Math.random();
      p_r *= a;
      m++;
    }
    return m;
  }

  private static int IRNLOG(double q) {
    double a = Math.random();
    double p_r = -(1 * 1.0 / Math.log(q)) * (1 - q);
    int m = 1;
    while (a - p_r >= 0) {
      a -= p_r;
      p_r *= (m * 1.0 / (m + 1)) * (1 - q);
      m++;
    }
    return m;
  }
}
