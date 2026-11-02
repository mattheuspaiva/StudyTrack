package br.com.studytrack.dao;

import br.com.studytrack.model.*;
import br.com.studytrack.util.MemuraiUtil;

import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class SessaoEstudoDAO {

    public int cadastrar(SessaoEstudo sessao) {

        Jedis banco = MemuraiUtil.getJedis();

        try {
            Estudante estudante = buscarOuCriarEstudante(sessao.getEstudante().getNome(), banco);

            Disciplina disciplina = buscarOuCriarDisciplina(sessao.getDisciplina().getNome(), banco);

            Conteudo conteudo = buscarOuCriarConteudo(sessao.getConteudo().getNome(), disciplina.getId(), banco);

            sessao.setEstudante(estudante);
            sessao.setDisciplina(disciplina);
            sessao.setConteudo(conteudo);

            int maiorId = obterMaiorIdSessao(banco);

            String contador = "studytrack:sessao:contador";
            String valorAtual = banco.get(contador);

            if (valorAtual == null || Long.parseLong(valorAtual) < maiorId) {
                banco.set(contador, String.valueOf(maiorId));
            }

            int id = Math.toIntExact(banco.incr(contador));

            sessao.setId(id);

            String chave = "sessao:" + id;

            byte[] valor = converterParaBytes(sessao);

            String valorTexto = Base64.getEncoder().encodeToString(valor);

            banco.set(chave, valorTexto);

            return id;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao cadastrar sessão: " + e.getMessage(), e);
        }
    }

    private Estudante buscarOuCriarEstudante(String nome, Jedis banco) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do estudante é obrigatório.");
        }

        nome = nome.trim();

        String nomeNormalizado = nome.toLowerCase(Locale.ROOT);

        String nomeCodificado = Base64.getUrlEncoder().withoutPadding().encodeToString(nomeNormalizado.getBytes(StandardCharsets.UTF_8));

        String chaveNome = "estudante:nome:" + nomeCodificado;

        String idExistente = banco.get(chaveNome);

        if (idExistente != null) {
            return new Estudante(Integer.parseInt(idExistente), nome);
        }

        int id = Math.toIntExact(banco.incr("studytrack:estudante:contador"));

        banco.set("estudante:" + id, nome);
        banco.set(chaveNome, String.valueOf(id));

        return new Estudante(id, nome);
    }

    private Disciplina buscarOuCriarDisciplina(String nome, Jedis banco) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da disciplina é obrigatório.");
        }

        nome = nome.trim();

        String nomeNormalizado = nome.toLowerCase(Locale.ROOT);

        String nomeCodificado = Base64.getUrlEncoder().withoutPadding().encodeToString(nomeNormalizado.getBytes(StandardCharsets.UTF_8));

        String chaveNome = "disciplina:nome:" + nomeCodificado;

        String idExistente = banco.get(chaveNome);

        if (idExistente != null) {
            return new Disciplina(Integer.parseInt(idExistente), nome);
        }

        int id = Math.toIntExact(banco.incr("studytrack:disciplina:contador"));

        banco.set("disciplina:" + id, nome);
        banco.set(chaveNome, String.valueOf(id));

        return new Disciplina(id, nome);
    }

    private Conteudo buscarOuCriarConteudo(String nome, int disciplinaId, Jedis banco) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do conteúdo é obrigatório.");
        }

        if (disciplinaId <= 0) {
            throw new IllegalArgumentException("O ID da disciplina é inválido.");
        }

        nome = nome.trim();

        String nomeNormalizado = nome.toLowerCase(Locale.ROOT);

        String identificador = disciplinaId + ":" + nomeNormalizado;

        String nomeCodificado = Base64.getUrlEncoder().withoutPadding().encodeToString(identificador.getBytes(StandardCharsets.UTF_8));

        String chaveNome = "conteudo:nome:" + nomeCodificado;

        String idExistente = banco.get(chaveNome);

        if (idExistente != null) {
            return new Conteudo(Integer.parseInt(idExistente), nome);
        }

        int id = Math.toIntExact(banco.incr("studytrack:conteudo:contador"));

        banco.set("conteudo:" + id, nome);
        banco.set("conteudo:" + id + ":disciplina", String.valueOf(disciplinaId));
        banco.set(chaveNome, String.valueOf(id));

        return new Conteudo(id, nome);
    }

    private int obterMaiorIdSessao(Jedis banco) {

        int maiorId = 0;

        Set<String> chaves = banco.keys("sessao:*");

        for (String chave : chaves) {

            try {
                int id = Integer.parseInt(
                        chave.substring("sessao:".length())
                );

                if (id > maiorId) {
                    maiorId = id;
                }

            } catch (NumberFormatException e) {
            }
        }

        return maiorId;
    }

    public List<SessaoEstudo> listarTodos() {

        Jedis banco = MemuraiUtil.getJedis();

        List<SessaoEstudo> sessoes = new ArrayList<>();

        Set<String> chaves = banco.keys("sessao:*");

        try {
            for (String chave : chaves) {

                try {
                    Integer.parseInt(chave.substring("sessao:".length()));

                    String valorTexto = banco.get(chave);

                    if (valorTexto != null) {
                        byte[] valor = Base64.getDecoder().decode(valorTexto);

                        sessoes.add(converterParaObjeto(valor));
                    }

                } catch (NumberFormatException e) {
                }
            }

            sessoes.sort(Comparator.comparing(SessaoEstudo::getData).thenComparingInt(SessaoEstudo::getId).reversed());

            return sessoes;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar sessões: " + e.getMessage(), e);
        }
    }

    public Optional<SessaoEstudo> buscarPorId(int id) {

        Jedis banco = MemuraiUtil.getJedis();

        try {
            String valorTexto = banco.get("sessao:" + id);

            if (valorTexto != null) {
                byte[] valor = Base64.getDecoder().decode(valorTexto);

                return Optional.of(converterParaObjeto(valor));
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar sessão: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean atualizar(SessaoEstudo sessao) {

        Jedis banco = MemuraiUtil.getJedis();

        try {
            String chave = "sessao:" + sessao.getId();

            if (!banco.exists(chave)) {
                return false;
            }

            byte[] valor = converterParaBytes(sessao);

            String valorTexto = Base64.getEncoder().encodeToString(valor);

            banco.set(chave, valorTexto);

            return true;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar sessão: " + e.getMessage(), e);
        }
    }

    public boolean excluir(int id) {

        Jedis banco = MemuraiUtil.getJedis();

        long resultado = banco.del("sessao:" + id);

        return resultado > 0;
    }

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

    private SessaoEstudo converterParaObjeto(byte[] valor) throws IOException {

        ByteArrayInputStream entrada = new ByteArrayInputStream(valor);
        DataInputStream dados = new DataInputStream(entrada);

        int id = dados.readInt();

        Estudante estudante = new Estudante(dados.readInt(), dados.readUTF());

        Disciplina disciplina = new Disciplina(dados.readInt(), dados.readUTF());

        Conteudo conteudo = new Conteudo(dados.readInt(), dados.readUTF());

        LocalDate data = LocalDate.parse(dados.readUTF());

        int duracao = dados.readInt();
        int exercicios = dados.readInt();

        String status = dados.readUTF();

        dados.close();

        return new SessaoEstudo(id, estudante, disciplina, conteudo, data, duracao, exercicios, status);
    }
}
