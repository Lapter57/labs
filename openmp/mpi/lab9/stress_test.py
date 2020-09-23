import os
import argparse
import numpy as np

MPIRUN = ("mpirun -n {num_process} {program} --test")


def print_stats(num_process, num_iter):
    time_arr = None
    with open('time.txt') as f:
        time_arr = np.array([float(x[:-1]) for x in f])
    os.remove("time.txt")
    if (num_process == 3):
        time_arr = np.array_split(time_arr, num_iter)
        time_arr = [np.max(arr) for arr in time_arr]

    print("mean   = ", np.mean(time_arr))
    print("median = ", np.median(time_arr))
    print("std    = ", np.std(time_arr))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-mpi', action="store_true",
                        help="Start program with 3 processors")
    parser.add_argument('-p', '--pack', action="store_true",
                        help="Passing messages by packing and unpacking")
    parser.add_argument('-n', type=int, default=1000,
                        help="Number of iterations")

    args = parser.parse_args()
    num_process = 3 if args.mpi else 1
    cmd = MPIRUN.format(num_process=num_process,
                        program="main_mpi" if args.mpi else "main")
    if (args.mpi and args.pack):
        cmd += " --pack"
    for i in range(args.n):
        os.system(cmd)
    print_stats(num_process, args.n)
