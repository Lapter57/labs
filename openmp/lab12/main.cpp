#include <mpi.h>

#include <algorithm>
#include <cmath>
#include <iostream>
#include <tuple>
#include <vector>

using namespace std;

const int A_MIN = 0;
const int A_MAX = 10;

int randomA() { return A_MIN + (rand() % static_cast<int>(A_MAX - A_MIN + 1)); }

/**
 * Вычисляет q_i(x) = a_k + ... + a_{k+r-1} * x^r, k = (i - 1) * r + 1
 */
double qi(const int i, const int r, const int x, const vector<int> a) {
    int k = (i - 1) * r + 1;
    double value = a[k++];
    for (int j = 0; j < r - 1; j++) {
        value += a[k++] * pow(x, (j + 2));
    }
    return value;
}

void printInfo(const int s, const int r, const int x, const vector<int> a) {
    cout << "s = " << s << "  r = " << r << "  x = " << x << endl;
    cout << "a: ";
    for (auto el : a) {
        cout << el << " ";
    }
    cout << endl;
}

int main(int argc, char **argv) {
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (argc != 3) {
        cout << "Values of r and x are required" << endl;
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    srand(unsigned(time(0)));

    // Инициализируем константы
    const int r = stoi(argv[1]);
    const int x = stoi(argv[2]);
    const int s = 1 << r;
    vector<int> a(s * r + 1);
    if (rank == 0) {
        generate(a.begin(), a.end(), randomA);
        printInfo(s, r, x, a);
    }
    MPI_Bcast(&a[0], s * r + 1, MPI_INT, 0, MPI_COMM_WORLD);

    // Распределение вычислений x^{i * r} * q_i(x) по процессам с помощью
    // цикличной слоистой схемы
    double localSum = 0;
    for (int i = rank + 1; i <= s; i += size) {
        localSum += (i == 1 ? 1 : pow(x, (i - 1) * r)) * qi(i, r, x, a);
    }

    // Вычисление q(x)
    double totalSum = 0;
    MPI_Reduce(&localSum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
    totalSum += a[0];

    if (rank == 0) {
        cout << "q(x) = " << totalSum << endl;
    }

    MPI_Finalize();
    return 0;
}