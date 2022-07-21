import gmpy2
import libnum
from Crypto.Util.number import *

# n = 12928016222583163285621599577461443538921550432522968254134024525052961389976575215720661492239277563694030199496398953014500031012932214275965402552478971
# e = 65537
# d = 1766291671224035950065700331363806177388399874464968761882845653559427747468578334624297389288081148192811346008992354282683301619956371502784830383326817

p = 113701434566953302296018327231919974281008636789018283600308658084922136632147
q = 113701434566953302296018327231919974281008636789018283600308658084922136631993
e = 65537

n = p * q
d = gmpy2.invert(e, (p - 1) * (q - 1))

print(n)
print(d)


with open('cipher.txt', 'r') as f:
    lines = f.readlines()
    fp_bmp = open('flag.bmp', 'wb')
    fp_txt = open('flag.bmp.txt', 'w')
    flag_bytes = []
    for l in lines:
        c = int(l.strip())
        m = pow(c, d, n)
        flag_line = libnum.n2s(int(m))
        flag_line_str = bytes.decode(flag_line.strip())
        fp_txt.write('%s\n'%flag_line_str)
        for b in flag_line_str.split(' '):
            fp_bmp.write(bytes([int(b, 16)]))
        
    fp_bmp.close()
    fp_txt.close()