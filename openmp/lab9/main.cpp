#include <mpi.h>

#include <algorithm>
#include <iostream>
#include <vector>

using namespace std;

const int MSG_TAG = 0;
const int MIN_CASH = 100;
const int MAX_CASH = 100000;

enum Currency { CHINESE_YUAN, US_DOLLAR, BRITISH_POUND, CURRENCY_COUNT };
enum Bank { FIRST_BANK, SECOND_BANK, THIRD_BANK, BANK_COUNT };

string bankToString(Bank bank) {
    switch (bank) {
        case FIRST_BANK:
            return "First bank";
        case SECOND_BANK:
            return "Second bank";
        case THIRD_BANK:
            return "Third bank";
        default:
            return "unknown bank";
    }
}

string currencyToString(Currency currency) {
    switch (currency) {
        case CHINESE_YUAN:
            return "chinese yuan";
        case US_DOLLAR:
            return "us dollars";
        case BRITISH_POUND:
            return "british pound";
        default:
            return "unknown currency";
    }
}

int randomNumber(int min, int max) {
    return min + (rand() % static_cast<int>(max - min + 1));
}

int randomCash() { return randomNumber(MIN_CASH, MAX_CASH); }

vector<int> generateCash() {
    vector<int> cash(CURRENCY_COUNT);
    for (int currency = CHINESE_YUAN; currency != BRITISH_POUND; currency++) {
        generate(cash.begin(), cash.end(), randomCash);
    }
    return cash;
}

vector<pair<Bank, vector<int>>> generateDebt(Bank bank, vector<int> &cash) {
    vector<pair<Bank, vector<int>>> debtsInfo;
    for (int b = FIRST_BANK; b < BANK_COUNT; b++) {
        if (b != bank) {
            vector<int> debts(CURRENCY_COUNT);
            for (int currency = CHINESE_YUAN; currency < CURRENCY_COUNT;
                 currency++) {
                debts[currency] = randomNumber(0, cash[currency]);
                cash[currency] -= debts[currency];
            }
            debtsInfo.push_back(make_pair(static_cast<Bank>(b), debts));
        }
    }
    return debtsInfo;
}

void printCash(const vector<int> cash) {
    for (int currency = CHINESE_YUAN; currency < CURRENCY_COUNT; currency++) {
        cout << cash[currency] << " "
             << currencyToString(static_cast<Currency>(currency)) << "  ";
    }
    cout << endl;
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

    auto cash = generateCash();
    cout << "Before Friday, cash of " << bankToString(static_cast<Bank>(rank))
         << " is ";
    printCash(cash);
    auto debtsInfo = generateDebt(static_cast<Bank>(rank), cash);
    for (auto debtInfo : debtsInfo) {
        cout << bankToString(static_cast<Bank>(rank)) << " send to "
             << bankToString(debtInfo.first) << " ";
        printCash(debtInfo.second);
        MPI_Send(&debtInfo.second[0], CURRENCY_COUNT, MPI_INT, debtInfo.first,
                 MSG_TAG, MPI_COMM_WORLD);
    }

    vector<int> loan(CURRENCY_COUNT);
    MPI_Status status;
    for (int b = FIRST_BANK; b < BANK_COUNT; b++) {
        if (b != rank) {
            MPI_Recv(&loan[0], CURRENCY_COUNT, MPI_INT, b, MSG_TAG,
                     MPI_COMM_WORLD, &status);
            cout << bankToString(static_cast<Bank>(rank)) << " receive from "
                 << bankToString(static_cast<Bank>(b)) << " ";
            printCash(loan);
            transform(cash.begin(), cash.end(), loan.begin(), cash.begin(),
                      plus<int>());
        }
    }

    cout << "After Friday, cash of " << bankToString(static_cast<Bank>(rank))
         << " is ";
    printCash(cash);

    MPI_Finalize();
    return 0;
}