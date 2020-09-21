# Лабораторная работа 6

## Вариант 2

Дана последовательность символов ![eq1](https://latex.codecogs.com/gif.latex?C&space;=&space;{c_{0},&space;...,&space;c_{n-1}}) . Дан набор из N
пар кодирующих символов ![eq2](<https://latex.codecogs.com/gif.latex?(a_i,&space;b_i)>) . Создать OpenMP-приложение, кодирующее
строку C следующим образом: поток 0 заменяет в строке C все символы ![eq3](https://latex.codecogs.com/gif.latex?a_0) на
символы ![eq4](https://latex.codecogs.com/gif.latex?b_0) , поток 1 заменяет в строке C все символы ![eq5](https://latex.codecogs.com/gif.latex?a_1)
на символы ![eq6](https://latex.codecogs.com/gif.latex?b_1) , и т.д. Количество потоков является входным параметром программы, количество символов в строке может быть не кратно количеству потоков.
