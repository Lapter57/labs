#include <omp.h>

#include <iostream>

#define N 20

using namespace std;

int main(int argc, char** argv) {
    double a[N], b[N];
    a[0] = 0;
#pragma omp parallel for
    for (int i = 1; i < N; i++) {
        // a[i] = a[i - 1] + 2.0;
        // b[i] = a[i] + a[i - 1];
        a[i] = i * 2.0;
        b[i] = (2 * i - 1) * 2.0;
    }
    b[0] = a[N - 1];  // also can be b[0] = (N-1) * 2.0
    for (int i = 0; i < N; i++) {
        cout << b[i] << " ";
    }
    cout << endl;
    return 0;
}
