# Лабораторная работа 7_1

## Вариант 2

Устранить ошибки в программе:

```
...
    double a[N],b[N];
    a[0] = 0;
# pragma omp parallel for nowait
    for (i=1; i<N; i++)
    {
        a[i] = a[i-1]+2.0;
        b[i] = a[i] + a[i-1];
    }
    b[0]=a[N-1];
...
```