# -*-  coding:utf8 -*-
# n = 12928016222583163285621599577461443538921550432522968254134024525052961389976575215720661492239277563694030199496398953014500031012932214275965402552478971
# e = 65537

import libnum
import gmpy2
from Crypto.Util.number import *

def get_prime(seed1, seed2):
    while(1):
        prime = getRandomRange(seed1, seed2)
        if isPrime(prime):
            break
    
    return prime

p = q = 0
while(p == q):
    seed1 = getRandomNBitInteger(256)
    seed2 = seed1 + getRandomNBitInteger(10)
    p = get_prime(seed1, seed2)
    q = get_prime(seed1, seed2)
n = p * q
e = 65537
d = gmpy2.invert(e, (p - 1) * (q - 1))

flag_file = open("flag.bmp.txt", "r")
cipher_file = open("cipher.txt", "w")
for line in flag_file.readlines():
    flag_line = libnum.s2n(line)
    cipher = pow(flag_line, e, n)
    cipher_file.write(str(int(cipher))+'\n')

flag_file.close()
cipher_file.close()