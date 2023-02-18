public final class myTransOne {
    private static final int keykey = -1640531527;

    private final int MX(int sum, int y, int z, int p, int e, int[] k) {
        return (((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z));
    }

    public static final byte[] encrypt(byte[] data, byte[] key) {
        byte[] it = toByteArray(encrypt(toIntArray(data, true), toIntArray(fixKey(key), false)), false);
        if (it != null) {
            return it;
        }
        return data;
    }

    public static final byte[] encrypt(String data, byte[] key) {
        Object obj;
        try {
            byte[] bytes = data.getBytes();
            obj = encrypt(bytes, key);
        } catch (Throwable th) {
            obj = null;
        }
        return (byte[]) obj;
    }

    public static final byte[] encrypt(byte[] data, String key) {
        Object obj;
        try {
            byte[] bytes = key.getBytes();
            obj = encrypt(data, bytes);
        } catch (Throwable th) {
            obj = null;
        }
        return (byte[]) obj;
    }

    public static final byte[] encrypt(String data, String key) {
        Object obj;
        try {
            byte[] bytes = data.getBytes();
            byte[] bytes2 = key.getBytes();
            obj = encrypt(bytes, bytes2);
        } catch (Throwable th) {
            obj = null;
        }
        return (byte[]) obj;
    }

    public static final byte[] decrypt(byte[] data, byte[] key) {
        byte[] it = toByteArray(decrypt(toIntArray(data, false), toIntArray(fixKey(key), false)), true);
        if (it != null) {
            return it;
        }
        return data;
    }

    public static final byte[] decrypt(byte[] data, String key) {
        Object obj;
        try {
            byte[] bytes = key.getBytes();
            obj = decrypt(data, bytes);
        } catch (Throwable th) {
            obj = null;
        }
        return (byte[]) obj;
    }

    public static final String decryptToString(byte[] data, byte[] key) {
        Object obj;
        try {
            obj = new String(decrypt(data, key));
        } catch (Throwable th) {
            obj = null;
        }
        return (String) obj;
    }

    public static final String decryptToString(byte[] data, String key) {
        Object obj;
        String str = null;
        try {
            byte[] decrypt = decrypt(data, key);
            obj = decrypt == null ? null : new String(decrypt);
        } catch (Throwable th) {
            obj = null;
        }
        str = (String) obj;
        return str;
    }

    private static final int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        }
        int q = (52 / (n + 1)) + 6;
        int z = v[n];
        int sum = 0;
        while (true) {
            int q2 = q - 1;
            if (q > 0) {
                sum += keykey;
                int e = (sum >>> 2) & 3;
                int p = 0;
                while (p < n) {
                    int y = v[p + 1];
                    v[p] = v[p] + ((((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z)));
                    z = v[p];
                    p++;
                }
                int y2 = v[0];
                v[n] = v[n] + ((((z >>> 5) ^ (y2 << 2)) + ((y2 >>> 3) ^ (z << 4))) ^ ((sum ^ y2) + (k[(p & 3) ^ e] ^ z)));
                z = v[n];
                q = q2;
            } else {
                return v;
            }
        }
    }

    private static final int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;
        if (n < 1) {
            return v;
        }
        int q = (52 / (n + 1)) + 6;
        int y = v[0];
        for (int sum = q * keykey; sum != 0; sum -= keykey) {
            int e = (sum >>> 2) & 3;
            int p = n;
            while (p > 0) {
                int z = v[p - 1];
                v[p] = v[p] - ((((z >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z)));
                y = v[p];
                p--;
            }
            int z2 = v[n];
            v[0] = v[0] - ((((z2 >>> 5) ^ (y << 2)) + ((y >>> 3) ^ (z2 << 4))) ^ ((sum ^ y) + (k[(p & 3) ^ e] ^ z2)));
            y = v[0];
        }
        return v;
    }

    public static final byte[] copyInto(byte[] copyInto, byte[] destination, int destinationOffset, int startIndex, int endIndex) {
        System.arraycopy(copyInto, startIndex, destination, destinationOffset, endIndex - startIndex);
        return destination;
    }

    public static byte[] copyInto$default(byte[] objArr, byte[] objArr2, int i, int i2, int i3, int i4, Object obj) {
        if ((i4 & 2) != 0) {
            i = 0;
        }
        if ((i4 & 4) != 0) {
            i2 = 0;
        }
        if ((i4 & 8) != 0) {
            i3 = objArr.length;
        }
        return copyInto(objArr, objArr2, i, i2, i3);
    }

    private static final byte[] fixKey(byte[] $this$fixKey) {
        if ($this$fixKey.length == 16) {
            return $this$fixKey;
        }
        byte[] fixedKey = new byte[16];
        if ($this$fixKey.length < 16) {
            copyInto$default($this$fixKey, fixedKey, 0, 0, 0, 14, (Object) null);
        } else {
            copyInto$default($this$fixKey, fixedKey, 0, 0, 16, 6, (Object) null);
        }
        return fixedKey;
    }

    private static final int[] toIntArray(byte[] $this$toIntArray, boolean includeLength) {
        int n;
        int[] result;
        if (($this$toIntArray.length & 3) == 0) {
            n = $this$toIntArray.length >>> 2;
        } else {
            n = ($this$toIntArray.length >>> 2) + 1;
        }
        if (includeLength) {
            result = new int[n + 1];
            result[n] = $this$toIntArray.length;
        } else {
            result = new int[n];
        }
        int n2 = $this$toIntArray.length;
        int i = 0;
        if (n2 > 0) {
            do {
                int i2 = i;
                i++;
                result[i2 >>> 2] = result[i2 >>> 2] | (($this$toIntArray[i2] & 255) << ((i2 & 3) << 3));
            } while (i < n2);
            return result;
        }
        return result;
    }

    private static final byte[] toByteArray(int[] $this$toByteArray, boolean includeLength) {
        int n = $this$toByteArray.length << 2;
        if (includeLength) {
            int m = $this$toByteArray[$this$toByteArray.length - 1];
            int n2 = n - 4;
            if (m < n2 - 3 || m > n2) {
                return null;
            }
            n = m;
        }
        byte[] result = new byte[n];
        int i = 0;
        if (n > 0) {
            do {
                int i2 = i;
                i++;
                result[i2] = (byte) ($this$toByteArray[i2 >>> 2] >>> ((i2 & 3) << 3));
            } while (i < n);
            return result;
        }
        return result;
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Start to run solution script");
        byte[] bingo = {-111, 30, 53, 84, 122, 88, -67, Byte.MAX_VALUE, 100, 3, -1, 50, -6, 63, -86, -15, 57, -66, 119, 77, -29, 97, 122, 67, 92, -90, -18, -15, 118, 113, -18, -48, 20, -15, 13, 118, -26, 38, -98, 49, 43, 51, -104, -16, 41, 83, 33, -83};
        String k = "1919810";
        byte[] encryptFirst = decrypt(bingo, k);
        for (byte i : encryptFirst) {
            if (i >= 48 && i <= 57) {
                int u = ((i - 48 + 10 - 7) % 10 + 2) % 10;
                // System.out.print("DIGIT: ");
                System.out.print(u);
            } else if (i >= 97) {
                int u = (((i - 97 + 26 - 7) % 26 + 7) % 26);
                // System.out.print("CHAR: ");
                System.out.print((char)(u+97));
            } else {
                int u = (((i - 65 + 26 - 7) % 26 + 13) % 26);
                // System.out.print("CHAR: ");
                System.out.print((char)(u+65));
            }
        }
        System.out.println();
    }
}