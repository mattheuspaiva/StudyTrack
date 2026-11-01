
package br.com.studytrack.util;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RocksDBUtil {

    private static RocksDB banco;
    private static Options opcoes;

    // Abrir conexão com o banco
    public static void conectar() {

        try {
            RocksDB.loadLibrary();

            // Criar a pasta dados, caso não exista
            Files.createDirectories(Path.of("dados"));

            opcoes = new Options();
            opcoes.setCreateIfMissing(true);

            banco = RocksDB.open(opcoes, "dados/studytrack");

            System.out.println("RocksDB conectado com sucesso!");

        } catch (RocksDBException | IOException e) {
            throw new RuntimeException(
                    "Erro ao conectar com RocksDB: " + e.getMessage(), e
            );
        }
    }

    // Retornar conexão
    public static RocksDB getBanco() {

        if (banco == null) {
            throw new IllegalStateException(
                    "O RocksDB não está conectado."
            );
        }

        return banco;
    }

    // Fechar conexão
    public static void desconectar() {

        if (banco != null) {
            banco.close();
            banco = null;
        }

        if (opcoes != null) {
            opcoes.close();
            opcoes = null;
        }

        System.out.println("Conexão com RocksDB encerrada!");
    }
}
