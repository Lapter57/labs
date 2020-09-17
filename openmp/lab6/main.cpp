#include <omp.h>

#include <algorithm>
#include <iostream>
#include <random>
#include <vector>

#define LEN_STR 100

using namespace std;

string generateRandomString(const unsigned int len) {
    string str(
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");

    random_device rd;
    mt19937 generator(rd());

    shuffle(str.begin(), str.end(), generator);

    return str.substr(0, len);
}

void printEncoding(const vector<pair<char, char>> encoding) {
    for (auto el : encoding) {
        cout << el.first << " -> " << el.second << endl;
    }
}

vector<pair<char, char>> encoding = {make_pair('a', '#'), make_pair('b', '*'),
                                     make_pair('c', '$'), make_pair('d', '&'),
                                     make_pair('e', '%'), make_pair('f', '!'),
                                     make_pair('g', '^'), make_pair('h', '@')};

int main(int argc, char** argv) {
    if (argc != 2) {
        return 1;
    }

    const int nThreads = stoi(argv[1]);
    printEncoding(encoding);
    string str = generateRandomString(LEN_STR);
    cout << "before: " << str << endl;

#pragma omp parallel for schedule(static, 1) num_threads(nThreads)
    for (unsigned int i = 0; i < encoding.size(); i++) {
        replace(str.begin(), str.end(), encoding[i].first, encoding[i].second);
    }

    cout << "after : " << str << endl;
    return 0;
}