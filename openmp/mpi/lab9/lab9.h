#include <mpi.h>

#include <algorithm>
#include <fstream>
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

bool isCmdOptionExist(int argc, char **argv, const string option) {
    for (int i = 0; i < argc; i++) {
        string arg = argv[i];
        if (0 == arg.find(option)) {
            return true;
        }
    }
    return false;
}

void saveTimeSpent(double timeSpent) {
    ofstream file;
    file.open("time.txt", ios::app);
    file << timeSpent << endl;
    file.close();
}
