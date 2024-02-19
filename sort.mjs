import assert from 'node:assert';
import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';

/*
交换数组里的元素
*/
function _swap(arr, i, j) {
  [arr[i], arr[j]] = [arr[j], arr[i]];
}

/*
让文件名里的数字可读
 */
function readable(num) {
  num = num.toString().split('').reverse();
  for (let i = 3; i < num.length; i += 4) {
    num.splice(i, 0, '_');
  }
  num = num.reverse().join('');

  return num;
}

/*
生成文件名
 */
export function genFileName(num) {
  return `${readable(num)}个乱序数.txt`;
}

/*
读取文件得到随机数用来排序
 */
async function getData(fileName) {
  return await readFile(fileName, { encoding: 'utf8' }).then((data) =>
    data.split(',').map((el) => parseInt(el))
  );
}

/*
选择排序
*/
function selectionSort(data) {
  const len = data.length;
  for (let i = 0; i < len; i++) {
    let indexOfMin = i;
    for (let j = i + 1; j < len; j++) {
      if (data[j] < data[indexOfMin]) {
        indexOfMin = j;
      }
    }

    _swap(data, i, indexOfMin);
  }

  return data;
}

/*
插入排序
*/
function insertionSort(data) {
  for (let i = 1; i < data.length; i++) {
    let j = i - 1;
    while (j >= 0 && data[i] < data[j]) {
      j--;
    }

    if (j !== i - 1) {
      const temp = data[i];
      for (let k = i; k > j + 1; k--) {
        data[k] = data[k - 1];
      }
      data[j + 1] = temp;
    }
  }

  return data;
}

/*
归并排序
*/
function _merge(data, start, mid, end) {
  if (mid >= end) {
    return;
  }

  const leftLast = mid - 1;
  const rightLast = end - 1;
  let leftIndex = start;
  let rightIndex = mid;
  const merged = [];

  while (leftIndex <= leftLast && rightIndex <= rightLast) {
    if (data[leftIndex] <= data[rightIndex]) {
      merged.push(data[leftIndex]);
      leftIndex++;

      if (leftIndex > leftLast) {
        while (rightIndex <= rightLast) {
          merged.push(data[rightIndex]);
          rightIndex++;
        }
      }
    } else {
      merged.push(data[rightIndex]);
      rightIndex++;

      if (rightIndex > rightLast) {
        while (leftIndex <= leftLast) {
          merged.push(data[leftIndex]);
          leftIndex++;
        }
      }
    }
  }

  for (let i = start; i < end; i++) {
    data[i] = merged[i - start];
  }
}

function mergeSort(data) {
  const len = data.length;
  for (let i = 2; i / 2 < len; i *= 2) {
    for (let j = 0; i * j < len; j++) {
      const start = i * j;
      const mid = start + i / 2;
      const end = Math.min(i * (j + 1), len);
      _merge(data, start, mid, end);
    }
  }
  return data;
}

/*
快速排序
*/
function _divide(data, minIndex, maxIndex) {
  const len = maxIndex - minIndex + 1;
  let mid = Math.floor(len / 2) + minIndex;
  let lo = minIndex;
  let hi = maxIndex;

  while (lo < hi) {
    while (data[lo] <= data[mid] && lo < mid) {
      lo++;
    }
    while (data[hi] >= data[mid] && hi > mid) {
      hi--;
    }

    _swap(data, lo, hi);
    if (lo === mid) {
      mid = hi;
    } else if (hi === mid) {
      mid = lo;
    }
  }

  return mid;
}

function quickSort(data) {
  function _sort(data, minIndex = 0, maxIndex = data.length - 1) {
    if (minIndex >= maxIndex) {
      return;
    }
    const mid = _divide(data, minIndex, maxIndex);
    _sort(data, minIndex, mid - 1);
    _sort(data, mid + 1, maxIndex);
  }
  _sort(data);

  return data;
}

/*
排序所用的时间
*/
function runTime(fn, data, print = true) {
  const start = Date.now();
  const result = fn(data);
  const now = Date.now();

  if (print) {
    console.log(
      `[NodeJS] ${fn.name} 排序 ${readable(data.length)} 个数耗时: ${(
        (now - start) /
        1000
      ).toFixed(3)} 秒`
    );
  }

  return result;
}

/*
测试4种排序的正确性
*/
async function test() {
  const data = await getData(genFileName(10000), {
    encoding: 'utf8',
  });
  const sorted = await getData('10_000个顺序数_测试用.txt', {
    encoding: 'utf8',
  });

  const selectionSortResult = runTime(selectionSort, data.slice(), false);
  const insertionSortResult = runTime(insertionSort, data.slice(), false);
  const mergeSortResult = runTime(mergeSort, data.slice(), false);
  const quickSortResult = runTime(quickSort, data.slice(), false);

  assert.strictEqual(selectionSortResult.toString(), sorted.toString());
  assert.strictEqual(insertionSortResult.toString(), sorted.toString());
  assert.strictEqual(mergeSortResult.toString(), sorted.toString());
  assert.strictEqual(quickSortResult.toString(), sorted.toString());
}

/*
主函数
 */
async function main() {
  await test();

  const range = process.argv[2] ?? 10000;
  const data = await getData(genFileName(range), {
    encoding: 'utf8',
  });

  if (range < 1000000) {
    runTime(selectionSort, data.slice());
    runTime(insertionSort, data.slice());
  }
  runTime(mergeSort, data.slice());
  runTime(quickSort, data.slice());
}

/*
执行主函数
 */
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
