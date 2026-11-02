
package br.com.studytrack.util;

import redis.clients.jedis.Jedis;

public class MemuraiUtil {

    private static Jedis jedis;

    public static void conectar() {

        try {
            jedis = new Jedis("localhost", 6379);

            jedis.ping();

            System.out.println("Memurai conectado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao conectar ao Memurai: " + e.getMessage());
        }
    }

    public static Jedis getJedis() {
        return jedis;
    }

    public static void desconectar() {

        if (jedis != null) {
            jedis.close();
            System.out.println("Conexão com Memurai encerrada.");
        }
    }
}
