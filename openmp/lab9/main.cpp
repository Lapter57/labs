#include "lab9.h"

/**
 * Моделирует обмен деньами в пятницу вечером.
 *
 * @param bank банк, отправляющий деньги
 * @param isVerbose если true, то будет выведен подробный отчет об обмене
 * деньгами
 * @param isTest если true, то время работы программы будет записано в конец
 * файлa time.txt
 */
void simulateFriday(Bank bank, bool isVerbose, bool isTest) {
    vector<vector<int>> banksCash(BANK_COUNT);
    generate(banksCash.begin(), banksCash.end(), generateCash);

    if (isVerbose) {
        for (int bank = FIRST_BANK; bank < BANK_COUNT; bank++) {
            auto cash = banksCash[bank];
            cout << "Before Friday, cash of "
                 << bankToString(static_cast<Bank>(bank)) << " is ";
            printCash(cash);
        }
    }

    vector<vector<pair<Bank, vector<int>>>> banksDebt(BANK_COUNT);
    for (int bank = FIRST_BANK; bank < BANK_COUNT; bank++) {
        banksDebt[bank] =
            generateDebt(static_cast<Bank>(bank), banksCash[bank]);
    }

    double timestamp = MPI_Wtime();
    for (int bank = FIRST_BANK; bank < BANK_COUNT; bank++) {
        auto debt = banksDebt[bank];
        for (auto debtInfo : debt) {
            if (isVerbose) {
                cout << bankToString(static_cast<Bank>(bank)) << " send to "
                     << bankToString(debtInfo.first) << " ";
                printCash(debtInfo.second);
            }
            transform(banksCash[debtInfo.first].begin(),
                      banksCash[debtInfo.first].end(), debtInfo.second.begin(),
                      banksCash[debtInfo.first].begin(), plus<int>());
        }
    }
    double timeSpent = MPI_Wtime() - timestamp;
    if (isTest) {
        saveTimeSpent(timeSpent * 1000);
    }
    if (isVerbose) {
        for (int bank = FIRST_BANK; bank < BANK_COUNT; bank++) {
            auto cash = banksCash[bank];
            cout << "After Friday, cash of "
                 << bankToString(static_cast<Bank>(bank)) << " is ";
            printCash(cash);
        }
    }
}

int main(int argc, char **argv) {
    int rank, size;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (size != 1) {
        cout << "1 processor is required to execute the program" << endl;
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    srand(unsigned(time(0) + rank));

    bool isTest = isCmdOptionExist(argc, argv, "--test");
    bool isVerbose = isCmdOptionExist(argc, argv, "--verbose");

    simulateFriday(static_cast<Bank>(rank), isVerbose, isTest);

    MPI_Finalize();
    return 0;
}