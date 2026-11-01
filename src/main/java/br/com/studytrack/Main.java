package br.com.studytrack;

import br.com.studytrack.dao.SessaoEstudoDAO;
import br.com.studytrack.service.SessaoEstudoService;
import br.com.studytrack.service.RelatorioService;
import br.com.studytrack.util.RocksDBUtil;
import br.com.studytrack.view.MenuView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner leitor = new Scanner(System.in);
        try {
            // Conectar com o RocksDB
            RocksDBUtil.conectar();
            // Criar o DAO
            SessaoEstudoDAO dao = new SessaoEstudoDAO();
            // Criar o Service de sessões
            SessaoEstudoService service = new SessaoEstudoService(dao);
            // Criar o Service de relatórios
            RelatorioService relatorioService = new RelatorioService(dao);
            // Criar e iniciar o menu
            MenuView menu = new MenuView(leitor,service, relatorioService);

            menu.exibirMenu();

        } catch (Exception e) {
            System.out.println("Erro ao executar o StudyTrack: " + e.getMessage());

        } finally {
            // Fechar conexão com o RocksDB
            RocksDBUtil.desconectar();

            leitor.close();
        }
    }
}
