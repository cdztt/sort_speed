import { writeFile } from 'node:fs/promises';
import { genFileName } from './sort.mjs';

/*
生成一组有序的正整数
*/
function genOrderedNum(range) {
  const data = [];
  for (let i = 0; i < range; i++) {
    data.push(i);
  }

  return data;
}

/*
生成一组乱序不重复的正整数
*/
function genUnorderedNum(range) {
  const data = genOrderedNum(range);
  const shuffled = [];
  for (let i = 0; i < range; i++) {
    const j = Math.floor(Math.random() * (range - i));
    shuffled.push(...data.splice(j, 1));
  }

  return shuffled;
}

/*
把乱序数写入文件
 */
function putUnorderedToFile(range) {
  const content = genUnorderedNum(range).toString();
  const fileName = genFileName(range);
  writeFile(fileName, content);
}

/*
 * 生成数据文件
 */
function setup() {
  putUnorderedToFile(10000);
  putUnorderedToFile(100000);
  putUnorderedToFile(1000000);
  writeFile(`10_000个顺序数_测试用.txt`, genOrderedNum(10000).toString());
}

setup();
