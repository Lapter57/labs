#include <mpi.h>

#include <algorithm>
#include <cmath>
#include <iostream>
#include <tuple>
#include <vector>

using namespace std;

const int A_MIN = 0;
const int A_MAX = 100;

int randomA() { return A_MIN + (rand() % static_cast<int>(A_MAX - A_MIN + 1)); }

/**
 * Вычисляет q_i(x) = a_k + ... + a_{k+r-1} * x^r, k = (i - 1) * r + 1
 */
double qi(const int i, const int r, const vector<double> xPows,
          const vector<double> a) {
    int k = (i - 1) * r + 1;
    double value = a[k++];
    for (int j = 0; j < r - 1; j++) {
        value += a[k++] * xPows[j];
    }
    return value;
}

tuple<vector<int>, vector<int>> getRevCountsAndDispls(const int s,
                                                      const int numProcess) {
    vector<int> revcounts;
    vector<int> displs;

    const int maxLayer = ceil((double)s / numProcess);
    for (int i = 0; i < numProcess; i++) {
        revcounts.push_back(i < s % numProcess ? maxLayer : maxLayer - 1);
        displs.push_back(i == 0 ? 0 : displs[i - 1] + revcounts[i - 1]);
    }

    return {revcounts, displs};
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

    const int r = stoi(argv[1]);
    const int x = stoi(argv[2]);
    const int s = r << 2;
    vector<double> a(s * r + 1);
    if (rank = 0) {
        generate(a.begin(), a.end(), randomA);
    }
    MPI_Bcast(&a[0], s * r + 1, MPI_INT, 0, MPI_COMM_WORLD);

    vector<double> xPows(r - 1);

    // Вычисление x^2, x^3 ... x^r
    for (int i = 0; i < r - 1; i++) {
        xPows.push_back(pow(x, i + 2));
    }

    // Распределение вычислений x^{i * r} * q_i(x) по процессам с помощью
    // цикличной слоистой схемы
    double localSum = 0;
    for (int i = rank + 1; i <= s; i += size) {
        localSum += (i == 1 ? 1 : pow(x, (i - 1) * r)) * qi(i, r, xPows, a);
    }

    double totalSum = a[0];
    MPI_Reduce(&localSum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);

    if (rank == 0) {
        cout << "x = " << x << endl;
        cout << "q(x) = " << totalSum;
    }

    MPI_Finalize();
    return 0;
}