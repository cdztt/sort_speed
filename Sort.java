import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;

public class Sort {
  static Function<int[], int[]> selectionSort = SelectionSort::selectionSort;
  static Function<int[], int[]> insertionSort = InsertionSort::insertionSort;
  static Function<int[], int[]> mergeSort = MergeSort::mergeSort;
  static Function<int[], int[]> quickSort = QuickSort::quickSort;

  public static void main(String[] args) throws IOException{
    test();

    int range = args.length > 0 ? Integer.valueOf(args[0]) : 10000;
    Path fileName = GetData.genFileName(range);
    int[] data = GetData.getData(fileName);

    if (range < 1000000) {
      RunTime.runTime(selectionSort, "selectionSort", Arrays.copyOf(data, data.length), true);
      RunTime.runTime(insertionSort, "insertionSort", Arrays.copyOf(data, data.length), true);
    }
    RunTime.runTime(mergeSort, "mergeSort", Arrays.copyOf(data, data.length), true);
    RunTime.runTime(quickSort, "quickSort", Arrays.copyOf(data, data.length), true);
  }

    static void test() throws IOException {
      int[] data = GetData.getData(GetData.genFileName(10000));
      int[] sorted = GetData.getData(Path.of("10_000个顺序数_测试用.txt"));

      int[] selectionSortResult = RunTime.runTime(selectionSort, "selectionSort", Arrays.copyOf(data, data.length), false);
      int[] insertionSortResult = RunTime.runTime(insertionSort, "insertionSort", Arrays.copyOf(data, data.length), false);
      int[] mergeSortResult = RunTime.runTime(mergeSort, "mergeSort", Arrays.copyOf(data, data.length), false);
      int[] quickSortResult = RunTime.runTime(quickSort, "quickSort", Arrays.copyOf(data, data.length), false);

      assert Arrays.equals(selectionSortResult, sorted);
      assert Arrays.equals(insertionSortResult, sorted);
      assert Arrays.equals(mergeSortResult, sorted);
      assert Arrays.equals(quickSortResult, sorted);
    }
}

class GetData {
    static String readable(int num) {
      StringBuilder sb = new StringBuilder(Integer.toString(num)).reverse();
      for (int i = 3; i < sb.length(); i += 4) {
        sb.insert(i, '_');
      }
      String str = sb.reverse().toString();

      return str;
    }

    static Path genFileName(int num) {
      Path fileName = Path.of(readable(num) + "个乱序数.txt");
      return fileName;
    }

    static int[] getData(Path fileName) throws IOException {
        String content = Files.readString(fileName);

        int[] data = Arrays.stream(content.split(","))
            .mapToInt(Integer::parseInt)
            .toArray();

        return data;
    }
}

class Swap {
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}

class SelectionSort {
    static int[] selectionSort(int[] data) {
        for (int i = 0, len = data.length; i < len; i ++) {
            int indexOfMin = i;
            for (int j = i + 1; j < len; j ++) {
                if (data[j] < data[indexOfMin]) {
                    indexOfMin = j;
                }
            }

            Swap.swap(data, i, indexOfMin);
        }

        return data;
    }
}

class InsertionSort {
    static int[] insertionSort(int[] data) {
        for (int i = 1, len = data.length; i < len; i ++) {
            int j = i - 1;
            while (j >= 0 && data[i] < data[j]) {
                j --;
            }

            if (j != i - 1) {
                int temp = data[i];
                for (int k = i; k > j + 1; k --) {
                    data[k] = data[k - 1];
                }
                data[j + 1] = temp;
            }
        }

        return data;
    }
}

class MergeSort {
    static void _merge(int[] data, int start, int mid, int end) {
        if (mid >= end) {
            return;
        }

        int leftLast = mid - 1;
        int rightLast = end - 1;
        int leftIndex = start;
        int rightIndex = mid;
        int[] merged = new int[end - start];
        int indexOfMerged = 0;

        while (leftIndex <= leftLast && rightIndex <= rightLast) {
            if (data[leftIndex] <= data[rightIndex]) {
                merged[indexOfMerged ++] = data[leftIndex];
                leftIndex ++;

                if (leftIndex > leftLast) {
                    while (rightIndex <= rightLast) {
                        merged[indexOfMerged ++] = data[rightIndex];
                        rightIndex ++;
                    }
                }
            }
            else {
                merged[indexOfMerged ++] = data[rightIndex];
                rightIndex ++;

                if (rightIndex > rightLast) {
                    while (leftIndex <= leftLast) {
                        merged[indexOfMerged ++] = data[leftIndex];
                        leftIndex ++;
                    }
                }
            }
        }

        for (int i = start; i < end; i ++) {
            data[i] = merged[i - start];
        }
    }

    static int[] mergeSort(int[] data) {
        for (int i = 2, len = data.length; i / 2 < len; i *= 2) {
            for (int j = 0; i * j < len; j ++) {
                int start = i * j;
                int mid = start + i / 2;
                int end = Math.min(i * (j + 1), len);
                _merge(data, start, mid, end);
            }
        }

        return data;
    }
}

class QuickSort {
    static int _divide(int[] data, int minIndex, int maxIndex) {
        int len = maxIndex - minIndex + 1;
        int mid = (int) Math.floor(len / 2) + minIndex;
        int lo = minIndex;
        int hi = maxIndex;

        while (lo < hi) {
            while (data[lo] <= data[mid] && lo < mid) {
                lo ++;
            }
            while (data[hi] >= data[mid] && hi > mid) {
                hi --;
            }

            Swap.swap(data, lo, hi);
            if (lo == mid) {
                mid = hi;
            }
            else if (hi == mid) {
                mid = lo;
            }
        }

        return mid;
    }

    static void _sort(int[] data, int minIndex, int maxIndex) {
        if (minIndex >= maxIndex) {
            return;
        }

        int mid = _divide(data, minIndex, maxIndex);
        _sort(data, minIndex, mid - 1);
        _sort(data, mid + 1, maxIndex);
    }

    static int[] quickSort(int[] data) {
        _sort(data, 0, data.length - 1);
        return data;
    }
}

class RunTime {
    static int[] runTime(Function<int[], int[]> fn, String fnName, int[] data, boolean print) {
        long start = Instant.now().toEpochMilli();
        int[] result = fn.apply(data);
        long end = Instant.now().toEpochMilli();

        BigDecimal elapsed = new BigDecimal((end - start) / 1000F);
        if (print) {
          System.out.println("[Java] " + fnName + " 排序 " + GetData.readable(data.length) + " 个数耗时: " + elapsed.setScale(3, RoundingMode.HALF_UP) + " 秒");
        }

        return result;
    }
}