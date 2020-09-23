import os
import argparse
import numpy as np
import matplotlib.pyplot as plt
from tqdm import tqdm
import multiprocessing

MAX_NUM_PROC = multiprocessing.cpu_count()
R_MIN = 2
R_MAX = 13
R_STEP = 2
MPIRUN = ("mpirun -n {num_process} main -r={r} -x=2 --test")


def getMeanTime():
    time_arr = None
    with open('time.txt') as f:
        time_arr = np.array([float(x[:-1]) for x in f])
    os.remove("time.txt")
    return np.mean(time_arr)


def test(num_process, r, iterations):
    cmd = MPIRUN.format(num_process=num_process, r=r)
    for i in range(iterations):
        os.system(cmd)
    return getMeanTime()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-n', type=int, default=1000,
                        help="Number of iterations")

    args = parser.parse_args()
    x = np.array([i for i in range(R_MIN, R_MAX, R_STEP)])
    for p in tqdm(range(MAX_NUM_PROC)):
        y = np.array([])
        for r in tqdm(range(R_MIN, R_MAX, R_STEP)):
            y = np.append(y, test(p + 1, r, args.n))
        plt.plot(x, y, label="num_process = {}".format(p + 1))

    plt.xlabel('r')
    plt.ylabel('time (s)')
    plt.legend()
    plt.savefig('test.png')
