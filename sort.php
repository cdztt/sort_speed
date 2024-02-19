<?php
function _swap(array &$arr, int $i, int $j): void {
    $temp = $arr[$i];
    $arr[$i] = $arr[$j];
    $arr[$j] = $temp;
}

function selection_sort(array &$data): array {
    $len = count($data);
    for ($i = 0; $i < $len; $i ++) {
        $index_of_min = $i;
        for ($j = $i + 1; $j < $len; $j ++) {
            if ($data[$j] < $data[$index_of_min]) {
                $index_of_min = $j;
            }
        }

        _swap($data, $i, $index_of_min);
    }

    return $data;
}

function insertion_sort(array &$data): array {
    for ($i = 1, $len = count($data); $i < $len; $i ++) {
        $j = $i - 1;
        while ($j >= 0 && $data[$i] < $data[$j]) {
            $j --;
        }

        if ($j !== $i - 1) {
            $temp = $data[$i];
            for ($k = $i; $k > $j + 1; $k --) {
                $data[$k] = $data[$k - 1];
            }
            $data[$j + 1] = $temp;
        }
    }

    return $data;
}

function _merge(array &$data, int $start, int $mid, int $end): void {
    if ($mid >= $end) {
        return;
    }

    $left_last = $mid - 1;
    $right_last = $end - 1;
    $left_index = $start;
    $right_index = $mid;
    $merged = [];

    while ($left_index <= $left_last && $right_index <= $right_last) {
        if ($data[$left_index] <= $data[$right_index]) {
            $merged[] = $data[$left_index];
            $left_index ++;

            if ($left_index > $left_last) {
                while ($right_index <= $right_last) {
                    $merged[] = $data[$right_index];
                    $right_index ++;
                }
            }
        }
        else {
            $merged[] = $data[$right_index];
            $right_index ++;

            if ($right_index > $right_last) {
                while ($left_index <= $left_last) {
                    $merged[] = $data[$left_index];
                    $left_index ++;
                }
            }
        }
    }

    for ($i = $start; $i < $end; $i ++) {
        $data[$i] = $merged[$i - $start];
    }
}

function merge_sort(array &$data): array {
    for ($i = 2, $len = count($data); $i / 2 < $len; $i *= 2) {
        for ($j = 0; $i * $j < $len; $j ++) {
            $start = $i * $j;
            $mid = $start + $i / 2;
            $end = min($i * ($j + 1), $len);
            _merge($data, $start, $mid, $end);
        }
    }

    return $data;
}

function _divide(array &$data, int $min_index, int $max_index): int {
    $len = $max_index - $min_index + 1;
    $mid = (int) floor($len / 2) + $min_index;
    $lo = $min_index;
    $hi = $max_index;

    while ($lo < $hi) {
        while ($data[$lo] <= $data[$mid] && $lo < $mid) {
            $lo ++;
        }
        while ($data[$hi] >= $data[$mid] && $hi > $mid) {
            $hi --;
        }

        _swap($data, $lo, $hi);
        if ($lo === $mid) {
            $mid = $hi;
        }
        else if ($hi === $mid) {
            $mid = $lo;
        }
    }

    return $mid;
}

function quick_sort(array &$data): array {
    function _sort(array &$data, int $min_index, int $max_index): void {
        if ($min_index >= $max_index) {
            return;
        }

        $mid = _divide($data, $min_index, $max_index);
        _sort($data, $min_index, $mid - 1);
        _sort($data, $mid + 1, $max_index);
    }

    _sort($data, 0, array_key_last($data));
    return $data;
}

function run_time($fn, $data) {
    $start = microtime(true);
    $result = $fn($data);
    $end = microtime(true);
    $run_time = number_format(round(($end - $start) * 1000) / 1000, 3);
    var_dump("[Php] $fn 10_000个数: $run_time" . ' 秒');

    return $result;
}

function main() {
  function get_data(string $file_name): array {
    $data_str = file_get_contents(__DIR__ . "/$file_name.txt");
    $data = explode(',', $data_str);
    $data = array_map(function ($el) {
        return intval($el);
    }, $data);

    return $data;
  }

  $data = get_data('10_000个乱序数');
  $sorted = get_data('10_000个顺序数_测试用');

  $selection_sort_result = run_time('selection_sort', array_slice($data, 0));
  $insertion_sort_result = run_time('insertion_sort', array_slice($data, 0));
  $merge_sort_result = run_time('merge_sort', array_slice($data, 0));
  $quick_sort_result = run_time('quick_sort', array_slice($data, 0));

  try {
    $assert_result = assert(array_slice($data, 0) === $sorted);
  } catch (AssertionError $e) {
    assert($selection_sort_result === $sorted);
    assert($insertion_sort_result === $sorted);
    assert($merge_sort_result === $sorted);
    assert($quick_sort_result === $sorted);
  }
}

main();
?>