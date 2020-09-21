#include "lab9.h"

/**
 * Моделирует обмен деньами в пятницу вечером.
 *
 * @param bank банк, отправляющий деньги
 * @param isVerbose если true, то будет выведен подробный отчет об обмене
 * деньгами
 * @param withPack если true, то при отправке будет использовать явная упаковка
 * данных
 * @param isTest если true, то время работы программы будет записано в конец
 * файлa time.txt
 */
void simulateFriday(Bank bank, bool isVerbose, bool withPack, bool isTest) {
    auto cash = generateCash();
    if (isVerbose) {
        cout << "Before Friday, cash of " << bankToString(bank) << " is ";
        printCash(cash);
    }
    auto debtsInfo = generateDebt(bank, cash);
    const double timestamp = MPI_Wtime();
    for (auto debtInfo : debtsInfo) {
        if (isVerbose) {
            cout << bankToString(bank) << " send to "
                 << bankToString(debtInfo.first) << " ";
            printCash(debtInfo.second);
        }
        if (withPack) {
            int bufSize;
            MPI_Pack_size(CURRENCY_COUNT, MPI_INT, MPI_COMM_WORLD, &bufSize);
            char buf[bufSize];
            int position = 0;
            MPI_Pack(&debtInfo.second[0], CURRENCY_COUNT, MPI_INT, buf, bufSize,
                     &position, MPI_COMM_WORLD);
            MPI_Send(buf, position, MPI_PACKED, debtInfo.first, MSG_TAG,
                     MPI_COMM_WORLD);

        } else {
            MPI_Send(&debtInfo.second[0], CURRENCY_COUNT, MPI_INT,
                     debtInfo.first, MSG_TAG, MPI_COMM_WORLD);
        }
    }

    vector<int> loan(CURRENCY_COUNT);
    for (int b = FIRST_BANK; b < BANK_COUNT; b++) {
        if (b != bank) {
            MPI_Recv(&loan[0], CURRENCY_COUNT, MPI_INT, b, MSG_TAG,
                     MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            if (isVerbose) {
                cout << bankToString(bank) << " receive from "
                     << bankToString(static_cast<Bank>(b)) << " ";
                printCash(loan);
            }
            transform(cash.begin(), cash.end(), loan.begin(), cash.begin(),
                      plus<int>());
        }
    }
    const double timeSpent = MPI_Wtime() - timestamp;
    if (isTest) {
        saveTimeSpent(timeSpent);
    }
    if (isVerbose) {
        cout << "After Friday, cash of " << bankToString(bank) << " is ";
        printCash(cash);
    }
}

int main(int argc, char **argv) {
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (size != 3) {
        cout << "3 processors are required to execute the program" << endl;
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    srand(unsigned(time(0) + rank));

    bool isTest = isCmdOptionExist(argc, argv, "--test");
    bool isVerbose = isCmdOptionExist(argc, argv, "--verbose");
    bool withPack = isCmdOptionExist(argc, argv, "--pack");

    simulateFriday(static_cast<Bank>(rank), isVerbose, withPack, isTest);

    MPI_Finalize();
    return 0;
}