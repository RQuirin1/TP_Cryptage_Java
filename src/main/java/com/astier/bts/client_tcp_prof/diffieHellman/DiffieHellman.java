package com.astier.bts.client_tcp_prof.diffieHellman;

import com.astier.bts.client_tcp_prof.tcp.TCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class DiffieHellman {
    TCP tcp;
    int taille;
    BigInteger p;
    BigInteger g;
    BigInteger a;
    BigInteger A;
    BigInteger B;
    BigInteger K;

    public DiffieHellman(TCP tcp, int taille) {
        this.tcp = tcp;
        this.taille = taille;
        a = new BigInteger(taille, new SecureRandom());
        p = BigInteger.probablePrime(taille, new SecureRandom());
        do {
            g = new BigInteger(taille, new SecureRandom());
        } while (g.compareTo(p) > 0);
        A = g.modPow(a, p);
    }


    public byte[] getParameters() throws IOException, InterruptedException {
        byte[] byte_B = new byte[65535];
        tcp.out.write(p.toByteArray());
        Thread.sleep(100);
        tcp.out.write(g.toByteArray());
        Thread.sleep(100);
        tcp.out.write(A.toByteArray());

        int taille2 = tcp.in.read(byte_B);
        B = new BigInteger(Arrays.copyOfRange(byte_B, 0, taille2));
        K = B.modPow(a, p);
        return K.toByteArray();
    }

}
