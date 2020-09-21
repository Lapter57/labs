#include <mpi.h>

#include <fstream>
#include <iostream>

using namespace std;

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

int mod(const int a, const int b) {
    const int r = a % b;
    return r < 0 ? r + b : r;
}

/**
 * Барьер с распространением. Каждый процесс синхронизируется с другими за
 * ceil(log_2{n}) синхронизируемых раундов, где n - число процессов. Во
 * время раунда i процесс p отправляет сообщение процессу 2i + p (mod n).
 * После отправки сообщения процесс p ждет сообщения от p - 2i (mod n) процесса.
 * Эта симметричный барьер, поэтому схема работает одинаково для всех процессов.
 *
 * @param comm коммутатор
 * @msgTag тэг сообщений
 */
void disseminationBarrier(MPI_Comm comm, const int msgTag) {
    int rank, size;
    MPI_Comm_size(comm, &size);
    MPI_Comm_rank(comm, &rank);

    if (size == 1) {
        return;
    }

    for (int i = 1; i < size; i *= 2) {
        MPI_Request req;
        MPI_Isend(NULL, 0, MPI_INT, mod(rank + i, size), msgTag, comm, &req);
        MPI_Request_free(&req);
        MPI_Recv(NULL, 0, MPI_INT, mod(rank - i, size), msgTag, comm,
                 MPI_STATUS_IGNORE);
    }
}

int main(int argc, char **argv) {
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    bool customBarrier = isCmdOptionExist(argc, argv, "--custom");

    const double timestamp = MPI_Wtime();
    if (customBarrier) {
        disseminationBarrier(MPI_COMM_WORLD, 0);
    } else {
        MPI_Barrier(MPI_COMM_WORLD);
    }
    if (rank == 0) {
        saveTimeSpent(MPI_Wtime() - timestamp);
    }

    MPI_Finalize();
    return 0;
}