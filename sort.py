import math
import time

def _swap(arr, i, j):
    temp = arr[i]
    arr[i] = arr[j]
    arr[j] = temp

def selection_sort(data):
    data_len = len(data)
    i = 0
    while i < data_len:
        index_of_min = i
        j = i + 1
        while j < data_len:
            if data[j] < data[index_of_min]:
                index_of_min = j
            j += 1
        _swap(data, i, index_of_min)
        i += 1

    return data

def insertion_sort(data):
    data_len = len(data)
    i = 1
    while i < data_len:
        j = i - 1
        while j >= 0 and data[i] < data[j]:
            j -= 1

        if j != i - 1:
            temp = data[i]
            k = i
            while k > j + 1:
                data[k] = data[k - 1]
                k -= 1
            data[j + 1] = temp
        i += 1

    return data

def _merge(data, start, mid, end):
    if mid >= end:
        return

    left_last = mid - 1
    right_last = end - 1
    left_index = start
    right_index = mid
    merged = []

    while left_index <= left_last and right_index <= right_last:
        if data[left_index] <= data[right_index]:
            merged.append(data[left_index])
            left_index += 1

            if left_index > left_last:
                while right_index <= right_last:
                    merged.append(data[right_index])
                    right_index += 1
        else:
            merged.append(data[right_index])
            right_index += 1

            if right_index > right_last:
                while left_index <= left_last:
                    merged.append(data[left_index])
                    left_index += 1

    i = start
    while i < end:
        data[i] = merged[i - start]
        i += 1

def merge_sort(data):
    i = 2
    data_len = len(data)
    while i / 2 < data_len:
        j = 0
        while i * j < data_len:
            start = i * j
            mid = start + int(i / 2)
            end = min(i * (j + 1), data_len)
            _merge(data, start, mid, end)
            j += 1
        i *= 2

    return data

def _divide(data, min_index, max_index):
    data_len = max_index - min_index + 1
    mid = math.floor(data_len / 2) + min_index
    lo = min_index
    hi = max_index

    while lo < hi:
        while data[lo] <= data[mid] and lo < mid:
            lo += 1
        while data[hi] >= data[mid] and hi > mid:
            hi -= 1

        _swap(data, lo, hi)
        if lo == mid:
            mid = hi
        elif hi == mid:
            mid = lo

    return mid

def quick_sort(data):
    def _sort(data, min_index, max_index):
        if min_index >= max_index:
            return

        mid = _divide(data, min_index, max_index)
        _sort(data, min_index, mid - 1)
        _sort(data, mid + 1, max_index)

    _sort(data, 0, len(data) - 1)
    return data

def run_time(fn, data):
    start = time.time()
    result = fn(data)
    end = time.time()
    print('[Python] {} 10_000个数: {:.3f} 秒'.format(fn.__name__, round((end - start) * 1000) / 1000))

    return result

def main():
  def get_data(file_name):
    data = open('{}.txt'.format(file_name), 'r')
    data = list(map(lambda el: int(el), data.read().split(',')))
    return data

  data = get_data('10_000个乱序数')
  sorted = get_data('10_000个顺序数_测试用')

  selection_sort_result = run_time(selection_sort, data[:])
  insertion_sort_result = run_time(insertion_sort, data[:])
  merge_sort_result = run_time(merge_sort, data[:])
  quick_sort_result = run_time(quick_sort, data[:])

  try:
    assert data[:] == sorted
  except:
    assert selection_sort_result == sorted
    assert insertion_sort_result == sorted
    assert merge_sort_result == sorted
    assert quick_sort_result == sorted

main()