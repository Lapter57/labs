#include <omp.h>

#include <algorithm>
#include <ctime>
#include <iostream>
#include <vector>

#define EPS 1E-9

using namespace std;

int randomNumber() { return rand() % 10; }

vector<vector<double>> generateMatrix(const int size) {
    vector<vector<double>> matrix(size, vector<double>(size));
#pragma omp parallel for
    for (int i = 0; i < size; i++) {
        generate(matrix[i].begin(), matrix[i].end(), randomNumber);
    }
    return matrix;
}

void printMatrix(const vector<vector<double>> matrix) {
    for (auto vec : matrix) {
        for (auto el : vec) {
            cout << el << " ";
        }
        cout << endl;
    }
}

/**
 *  Вычисление определителя матрицы методом Гаусса
 */
double determinant(vector<vector<double>> matrix, const int size) {
    /**
     * Каждая i + 1 итерация этого цикла зависит от результатов i итерации,
     * поэтому данный цикл распараллелить не получится из-за зависимости данных.
     */
    for (int i = 0; i < size; i++) {
        /**
         * Поиск максимального элемента в столбце i. Если максимальный
         * элемент равен 0, то матрица вырожденная. а значит определитель
         * равен 0. Если максимальный элемент не равен нулю, то происходит
         * смена строки, содержащей этот элемент, с первой строкой.
         */
        int k = i;
        for (int j = i + 1; j < size; j++) {
            if (abs(matrix[j][i]) > abs(matrix[k][i])) {
                k = j;
            }
        }
        if (abs(matrix[k][i]) < EPS) {
            return 0;
        }
        swap(matrix[i], matrix[k]);
        /**
         * Преобразование матрицы в верхнюю треугольную. У данного цикла ни
         * одна из итераций не зависит от более ранних, поэтому его можно
         * распараллелить.
         */
#pragma omp parallel for
        for (int j = i + 1; j < size; j++) {
            const double ratio = matrix[j][i] / matrix[i][i];
            for (int k = 0; k < size; k++) {
                matrix[j][k] -= ratio * matrix[i][k];
            }
        }
    }
    /**
     * Перемножив элементы, расположенные на диагонали, получим определитель
     * матрицы. Данный цикл можно распараллелить с применением опции
     * "reduction", за счет чего каждый поток умножил свою копию переменной det
     * на один из элементов главной диагонали матрицы, а по завершению
     * параллельной секции все приватные переменные det были перемножены.
     */
    double det = 1;
#pragma omp parallel for reduction(* : det)
    for (int i = 0; i < size; i++) {
        det *= matrix[i][i];
    }
    return det;
}

int main(int argc, char** argv) {
    srand(unsigned(time(0)));
    if (argc < 2) {
        return 1;
    }
    const int size = stoi(argv[1]);
    if (argc == 3) {
        omp_set_num_threads(stoi(argv[2]));
    }
    const auto matrix = generateMatrix(size);
    // printMatrix(matrix);
    // cout << endl;
    cout << determinant(matrix, size) << endl;
    return 0;
}
