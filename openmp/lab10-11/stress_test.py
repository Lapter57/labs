import os
import argparse
import numpy as np

MPIRUN = ("mpirun -n {num_process} main")


def print_stats(num_process, num_iter):
    time_arr = None
    with open('time.txt') as f:
        time_arr = np.array([float(x[:-1]) for x in f])
    os.remove("time.txt")
    print("mean   = ", np.mean(time_arr))
    print("median = ", np.median(time_arr))
    print("std    = ", np.std(time_arr))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-c', "--custom", action="store_true",
                        help="Using a custom barrier")
    parser.add_argument('-p', "--processors", type=int, default=4,
                        help="Number of processors")
    parser.add_argument('-n', type=int, default=1000,
                        help="Number of iterations")

    args = parser.parse_args()
    num_process = args.processors
    cmd = MPIRUN.format(num_process=num_process)
    if (args.custom):
        cmd += " --custom"
    for i in range(args.n):
        os.system(cmd)
    print_stats(num_process, args.n)
