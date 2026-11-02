package br.com.studytrack.dao;

import br.com.studytrack.model.*;
import br.com.studytrack.util.MemuraiUtil;

import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SessaoEstudoDAO {

    public void cadastrar(SessaoEstudo sessao) {

        try {
            Jedis banco = MemuraiUtil.getJedis();

            String chave = "sessao:" + sessao.getId();

            byte[] valor = converterParaBytes(sessao);

            String valorTexto = Base64.getEncoder().encodeToString(valor);

            banco.set(chave, valorTexto);

            System.out.println("Sessão cadastrada com sucesso!");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao cadastrar sessão: " + e.getMessage(), e);
        }
    }

    public List<SessaoEstudo> listarTodos() {

        List<SessaoEstudo> sessoes = new ArrayList<>();

        Jedis banco = MemuraiUtil.getJedis();

        Set<String> chaves = banco.keys("sessao:*");

        try {
            for (String chave : chaves) {

                String valorTexto = banco.get(chave);

                if (valorTexto != null) {

                    byte[] valor = Base64.getDecoder().decode(valorTexto);

                    SessaoEstudo sessao = converterParaObjeto(valor);

                    sessoes.add(sessao);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar sessões: " + e.getMessage(), e);
        }

        sessoes.sort((a, b) -> b.getData().compareTo(a.getData()));

        return sessoes;
    }

    public Optional<SessaoEstudo> buscarPorId(int id) {

        try {
            Jedis banco = MemuraiUtil.getJedis();

            String chave = "sessao:" + id;

            String valorTexto = banco.get(chave);

            if (valorTexto != null) {

                byte[] valor = Base64.getDecoder().decode(valorTexto);

                SessaoEstudo sessao = converterParaObjeto(valor);

                return Optional.of(sessao);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar sessão: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public boolean atualizar(SessaoEstudo sessao) {

        try {
            Jedis banco = MemuraiUtil.getJedis();

            String chave = "sessao:" + sessao.getId();

            String valorAtual = banco.get(chave);

            if (valorAtual == null) {
                return false;
            }

            byte[] novoValor = converterParaBytes(sessao);

            String novoValorTexto = Base64.getEncoder().encodeToString(novoValor);

            banco.set(chave, novoValorTexto);

            return true;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar sessão: " + e.getMessage(), e);
        }
    }

    public boolean excluir(int id) {

        Jedis banco = MemuraiUtil.getJedis();

        String chave = "sessao:" + id;

        long resultado = banco.del(chave);

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

        Estudante estudante = new Estudante(dados.readInt(), dados.readUTF()
        );

        Disciplina disciplina = new Disciplina(dados.readInt(), dados.readUTF()
        );

        Conteudo conteudo = new Conteudo(dados.readInt(), dados.readUTF()
        );

        LocalDate data = LocalDate.parse(dados.readUTF());

        int duracao = dados.readInt();

        int exercicios = dados.readInt();

        String status = dados.readUTF();

        dados.close();

        return new SessaoEstudo(id,estudante,disciplina,conteudo,data,duracao,exercicios, status
        );
    }
}
