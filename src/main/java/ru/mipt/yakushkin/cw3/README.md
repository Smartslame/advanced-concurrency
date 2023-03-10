## Реализация

Реализовано конкуррентное бинарное дерево без балансировки из статей:

https://ctlab.itmo.ru/~vaksenov/papers/2017-bst.pdf

https://hal.inria.fr/tel-01887505/document (глава 5)

## Верификация корректности

Корректность проверена следующими методами:

- повторение операций на последовательном BST с глобальным локом и сравнение получившегося дерева
- совпадение inorder traversal BstSeq и BstPar при одинаковых операциях
- inorder traversal BstPar является отсортированным по возрастанию

## Пропускная способность

Пропускные способности (ops в секунду) при следующей нагрузке:

- K = {1,...,10^5}
- prepopulate() -> взять каждый элемент с вероятностью 1/2
- каждый поток:

```
while time < 5s:
    key = random(K)
    p = random()
    switch:
        p < x: insert(key)
        x <= p < 2x: remove(key)
        2x <= p <= 100: contains(key)
```

|  X / THREAD_NUM | 1          | 2          | 3          | 4          | 4 / 1 |
|-----------------|------------|------------|------------|------------|--------|
| 0               | 1028017,80 | 2147644,47 | 3077501,50 | 3688271,55 | 3,59   |
| 10              | 1005888,22 | 1977884,22 | 2669150,97 | 3205614,68 | 3,19   |
| 50              | 927435,11  | 1655430,31 | 2151454,91 | 2679897,42 | 2,89   |

## Вывод программы

```
Threads count: 1
X: 0	OPS: 1028017,80
X: 10	OPS: 1005888,22
X: 50	OPS: 927435,11

Threads count: 2
X: 0	OPS: 2147644,47
X: 10	OPS: 1977884,22
X: 50	OPS: 1655430,31

Threads count: 3
X: 0	OPS: 3077501,50
X: 10	OPS: 2669150,97
X: 50	OPS: 2151454,91

Threads count: 4
X: 0	OPS: 3688271,55
X: 10	OPS: 3205614,68
X: 50	OPS: 2679897,42
