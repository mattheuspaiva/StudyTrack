package br.com.studytrack.dao;

import br.com.studytrack.model.*;
import br.com.studytrack.util.RocksDBUtil;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.RocksDBException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessaoEstudoDAO {

    private RocksDB banco;

    public SessaoEstudoDAO() {
        this.banco = RocksDBUtil.getBanco();
    }

    // Cadastrar sessão de estudo
    public void cadastrar(SessaoEstudo sessao) {

        try {
            String chave = "sessao:" + sessao.getId();

            byte[] valor = converterParaBytes(sessao);

            banco.put(chave.getBytes(StandardCharsets.UTF_8), valor);

            System.out.println("Sessão cadastrada com sucesso!");

        } catch (RocksDBException | IOException e) {
            throw new RuntimeException("Erro ao cadastrar sessão: " + e.getMessage(), e);
        }
    }

    // Listar todas as sessões
    public List<SessaoEstudo> listarTodos() {

        List<SessaoEstudo> sessoes = new ArrayList<>();

        RocksIterator iterator = banco.newIterator();

        try {
            iterator.seekToFirst();

            while (iterator.isValid()) {

                String chave = new String(
                        iterator.key(), StandardCharsets.UTF_8
                );

                if (chave.startsWith("sessao:")) {

                    SessaoEstudo sessao = converterParaObjeto(iterator.value());

                    sessoes.add(sessao);
                }

                iterator.next();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar sessões: " + e.getMessage(), e);

        } finally {
            iterator.close();
        }

        sessoes.sort((a, b) -> b.getData().compareTo(a.getData()));

        return sessoes;
    }

    // Buscar sessão pelo ID
    public Optional<SessaoEstudo> buscarPorId(int id) {

        try {
            String chave = "sessao:" + id;

            byte[] valor = banco.get(chave.getBytes(StandardCharsets.UTF_8));

            if (valor != null) {

                SessaoEstudo sessao = converterParaObjeto(valor);

                return Optional.of(sessao);
            }

        } catch (RocksDBException | IOException e) {
            throw new RuntimeException("Erro ao buscar sessão: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    // Atualizar sessão
    public boolean atualizar(SessaoEstudo sessao) {

        try {
            String chave = "sessao:" + sessao.getId();

            byte[] chaveBytes = chave.getBytes(StandardCharsets.UTF_8);

            byte[] valorAtual = banco.get(chaveBytes);

            if (valorAtual == null) {
                return false;
            }

            byte[] novoValor = converterParaBytes(sessao);

            banco.put(chaveBytes, novoValor);

            return true;

        } catch (RocksDBException | IOException e) {
            throw new RuntimeException("Erro ao atualizar sessão: " + e.getMessage(), e);
        }
    }

    // Excluir sessão
    public boolean excluir(int id) {

        try {
            String chave = "sessao:" + id;

            byte[] chaveBytes = chave.getBytes(StandardCharsets.UTF_8);

            byte[] valor = banco.get(chaveBytes);

            if (valor == null) {
                return false;
            }

            banco.delete(chaveBytes);

            return true;

        } catch (RocksDBException e) {
            throw new RuntimeException("Erro ao excluir sessão: " + e.getMessage(), e);
        }
    }

    // Converter objeto Java para bytes
    private byte[] converterParaBytes(SessaoEstudo sessao) throws IOException {

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        DataOutputStream dados = new DataOutputStream(saida);

        dados.writeInt(sessao.getId());

        dados.writeInt(sessao.getEstudante().getId());
        dados.writeUTF(sessao.getEstudante().getNome());

        dados.writeInt(sessao.getDisciplina().getId());
        dados.writeUTF(sessao.getDisciplina().getNome());

        dados.writeInt(sessao.getConteudo().getId());
        dados.writeUTF(sessao.getConteudo().getNome());

        dados.writeUTF(sessao.getData().toString());

        dados.writeInt(sessao.getDuracaoMinutos());

        dados.writeInt(sessao.getExerciciosResolvidos());

        dados.writeUTF(sessao.getStatus());

        dados.close();

        return saida.toByteArray();
    }

    // Converter bytes para objeto Java
    private SessaoEstudo converterParaObjeto(byte[] valor) throws IOException {

        ByteArrayInputStream entrada = new ByteArrayInputStream(valor);

        DataInputStream dados = new DataInputStream(entrada);

        int id = dados.readInt();

        Estudante estudante = new Estudante(
                dados.readInt(),
                dados.readUTF()
        );

        Disciplina disciplina = new Disciplina(
                dados.readInt(),
                dados.readUTF()
        );

        Conteudo conteudo = new Conteudo(
                dados.readInt(),
                dados.readUTF()
        );

        LocalDate data = LocalDate.parse(dados.readUTF());

        int duracao = dados.readInt();

        int exercicios = dados.readInt();

        String status = dados.readUTF();

        dados.close();

        return new SessaoEstudo(
                id,
                estudante,
                disciplina,
                conteudo,
                data,
                duracao,
                exercicios,
                status
        );
    }
}
