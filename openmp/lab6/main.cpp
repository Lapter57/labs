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

const vector<pair<char, char>> encoding = {
    make_pair('a', '#'), make_pair('b', '*'), make_pair('c', '$'),
    make_pair('d', '&'), make_pair('e', '%'), make_pair('f', '!'),
    make_pair('g', '^'), make_pair('h', '@')};

int main(int argc, char** argv) {
    if (argc != 2) {
        return 1;
    }

    omp_set_num_threads(stoi(argv[1]));
    printEncoding(encoding);
    string str = generateRandomString(LEN_STR);
    cout << "before: " << str << endl;

/* Опция "schedule" с параметром type = static позволяет использовать блочно-
   циклическое распределение итераций цикла. Параметр chunk = 1 приводит к тому,
   что каждый блок состоит из одной итерации, первый блок выполняет 0-ой поток,
   второй блок -- 2-ой поток и т.д. Итерирование происходит по массиву пар,
   задающих инъективное отображение элементов множества A в элементы множества
   B. Во время итерации поток работает с парой (a', b') и заменяет в строке все
   символы a' на b'. Поскольку отображение инъективно, то каждый поток не влияет
   на изменения, совершаемые другими потоками.*/
#pragma omp parallel for schedule(static, 1)
    for (unsigned int i = 0; i < encoding.size(); i++) {
        replace(str.begin(), str.end(), encoding[i].first, encoding[i].second);
    }

    cout << "after : " << str << endl;
    return 0;
}
