use std::fs;
use std::io;
use std::cmp;
use std::time::Instant;
use std::env;

fn readable(num: usize) -> String {
  let mut num = num.to_string().chars().rev().collect::<String>();
  let mut i = 3;
  while i < num.len() {
    num.insert(i, '_');
    i += 4;
  }
  num = num.chars().rev().collect();
  num
}

fn gen_file_name(num: usize) -> String {
  let file_name = readable(num) + "个乱序数.txt";
  file_name
}

fn get_data(file_name: String) -> Result<Vec<u32>, io::Error> {
  let content = fs::read_to_string(file_name)?;
  let data = content.split(',')
    .map(|x| x.parse::<u32>().unwrap())
    .collect::<Vec<u32>>();
  Ok(data)
}

fn swap(vec: &mut [u32], i: usize, j: usize) {
  let temp = vec[i];
  vec[i] = vec[j];
  vec[j] = temp;
}

fn selection_sort(mut data: Vec<u32>) -> Vec<u32> {
  let len = data.len();
  for i in 0..len {
    let mut index_min = i;
    for j in i + 1..len {
      if data[j] < data[index_min] {
        index_min = j
      }
    }
    swap(&mut data, i, index_min);
  }
  data
}

fn insertion_sort(mut data: Vec<u32>) -> Vec<u32> {
  let len = data.len();
  for i in 1..len {
    let temp = data[i];
    let mut insert_position = 0;
    for j in (0..i).rev() {
      if data[i] >= data[j] {
        insert_position = j + 1;
        break;
      }
    }
    for k in (insert_position..=i).rev() {
      data[k] = if k == insert_position {
        temp
      } else {
        data[k - 1]
      }
    }
  }
  data
}

fn merge(data: &mut [u32], start: usize, mid: usize, end: usize) {
  if mid >= end {
    return;
  }

  let left_last = mid - 1;
  let right_last = end - 1;
  let mut left_index = start;
  let mut right_index = mid;
  let mut merged = vec![0; end - start];
  let mut merged_index = 0;
  while left_index <= left_last && right_index <= right_last {
    if data[left_index] <= data[right_index] {
      merged[merged_index] = data[left_index];
      merged_index += 1;
      left_index += 1;

      if left_index > left_last {
        while right_index <= right_last {
          merged[merged_index] = data[right_index];
          merged_index += 1;
          right_index += 1;
        }
      }
    }
    else {
      merged[merged_index] = data[right_index];
      merged_index += 1;
      right_index += 1;

      if right_index > right_last {
        while left_index <= left_last {
          merged[merged_index] = data[left_index];
          merged_index += 1;
          left_index += 1;
        }
      }
    }
  }
  for i in start..end {
    data[i] = merged[i - start];
  }
}

fn merge_sort(mut data: Vec<u32>) -> Vec<u32> {
  let len = data.len();
  let mut i = 2;
  while i / 2 < len {
    let mut j = 0;
    while i * j < len {
      let start = i * j;
      let mid = start + i / 2;
      let end = cmp::min(i * (j + 1), len);
      merge(&mut data, start, mid, end);
      j += 1;
    }
    i *= 2;
  }
  data
}

fn divide(data: &mut [u32], lo: usize, hi: usize) {
  if lo >= hi {
    return;
  }

  let mut mid = (hi - lo + 1) / 2 + lo;
  let mut lo_cursor = lo;
  let mut hi_cursor = hi;
  while lo_cursor < hi_cursor {
    while data[lo_cursor] <= data[mid] && lo_cursor < mid {
        lo_cursor += 1;
    }
    while data[hi_cursor] >= data[mid] && hi_cursor > mid {
        hi_cursor -= 1;
    }
    let temp = data[lo_cursor];
    data[lo_cursor] = data[hi_cursor];
    data[hi_cursor] = temp;
    if mid == lo_cursor {
      mid = hi_cursor;
    } else if mid == hi_cursor {
      mid = lo_cursor;
    }
  }
  let left_hi = if mid == 0 {
    0
  } else {
    mid - 1
  };
  let right_lo = mid + 1;
  divide(data, lo, left_hi);
  divide(data, right_lo, hi);
}

fn quick_sort(mut data: Vec<u32>) -> Vec<u32> {
  let lo = 0;
  let hi = data.len() - 1;
  divide(&mut data, lo, hi);
  data
}

fn run_time(f: fn(Vec<u32>) -> Vec<u32>, fn_name: &str, data: Vec<u32>, print: bool) -> Vec<u32> {
  let len = data.len();
  let now = Instant::now();
  let result = f(data);
  let elapsed = now.elapsed().as_millis();

  if print {
    let num = readable(len);
    let elapsed_sec = elapsed as f64 / 1000 as f64;
    println!("[Rust] {fn_name} 排序 {num} 个数耗时: {:.3} 秒", elapsed_sec);
  }

  result
}

fn test() {
  let data = get_data(gen_file_name(10000)).unwrap();
  let sorted = get_data("10_000个顺序数_测试用.txt".to_string()).unwrap();

  let selection_sort_result = run_time(selection_sort, "selection_sort", data.clone(), false);
  let insertion_sort_result = run_time(insertion_sort, "insertion_sort", data.clone(), false);
  let merge_sort_result = run_time(merge_sort, "merge_sort", data.clone(), false);
  let quick_sort_result = run_time(quick_sort, "quick_sort", data.clone(), false);

  assert_eq!(selection_sort_result, sorted);
  assert_eq!(insertion_sort_result, sorted);
  assert_eq!(merge_sort_result, sorted);
  assert_eq!(quick_sort_result, sorted);
}

fn main() {
  test();

  let range = match env::args().collect::<Vec<String>>().get(1) {
    Some(arg) => arg.parse::<usize>().unwrap(),
    None => 10000,
  };
  let data = get_data(gen_file_name(range)).unwrap();

  if range < 100000 {
    run_time(selection_sort, "selection_sort", data.clone(), true);
    run_time(insertion_sort, "insertion_sort", data.clone(), true);
  }
  run_time(merge_sort, "merge_sort", data.clone(), true);
  run_time(quick_sort, "quick_sort", data.clone(), true);
}