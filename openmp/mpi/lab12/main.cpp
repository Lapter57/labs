#include <mpi.h>

#include <algorithm>
#include <cmath>
#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

const int A_MIN = 0;
const int A_MAX = 10;

void saveTimeSpent(double timeSpent) {
    ofstream file;
    file.open("time.txt", ios::app);
    file << timeSpent << endl;
    file.close();
}

bool isCmdOptionExist(int argc, char **argv, const string option) {
    for (int i = 0; i < argc; i++) {
        string arg = argv[i];
        if (0 == arg.find(option)) {
            return true;
        }
    }
    return false;
}

string getCmdOption(int argc, char **argv, const string &option) {
    string cmd;
    for (int i = 0; i < argc; ++i) {
        string arg = argv[i];
        if (0 == arg.find(option)) {
            size_t found = arg.find_last_of(option);
            cmd = arg.substr(found + 1);
            return cmd;
        }
    }
    return cmd;
}

int randomA() { return A_MIN + (rand() % static_cast<int>(A_MAX - A_MIN + 1)); }

/**
 * Вычисляет q_i(x) = a_k + ... + a_{k+r-1} * x^r, k = (i - 1) * r + 1
 */
double qi(const int i, const int r, const vector<double> xPows,
          const vector<int> a) {
    int k = (i - 1) * r + 1;
    double value = a[k++];
    for (int j = 0; j < r - 1; j++) {
        value += a[k++] * xPows[j];
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

    srand(unsigned(time(0)));

    // Инициализация констант
    bool isTest = isCmdOptionExist(argc, argv, "--test");
    const int r = stoi(getCmdOption(argc, argv, "-r="));
    const int x = stoi(getCmdOption(argc, argv, "-x="));
    const int s = 1 << r;
    vector<int> a(s * r + 1);
    generate(a.begin(), a.end(), randomA);

    const double timestamp = MPI_Wtime();

    // Вычисление x^2, x^3 ... x^r
    vector<double> xPows;
    for (int i = 0; i < r - 1; i++) {
        xPows.push_back(pow(x, i + 2));
    }

    // Распределение вычислений x^{i * r} * q_i(x) по процессам с помощью
    // цикличной слоистой схемы и подсчет промежуточной суммы
    double localSum = 0;
    int i;
    for (i = rank + 1; i <= s; i += size) {
        localSum += (i == 1 ? 1 : pow(x, (i - 1) * r)) * qi(i, r, xPows, a);
    }

    // Вычисление q(x)
    double totalSum = 0;
    MPI_Reduce(&localSum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
    totalSum += a[0];

    double timeSpent = MPI_Wtime() - timestamp;
    if (rank == 0) {
        if (isTest) {
            saveTimeSpent(timeSpent);
        } else {
            printInfo(s, r, x, a);
            cout << "q(x) = " << totalSum << endl;
        }
    }

    MPI_Finalize();
    return 0;
}