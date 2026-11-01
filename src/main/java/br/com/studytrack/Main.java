package br.com.studytrack;

import br.com.studytrack.dao.SessaoEstudoDAO;
import br.com.studytrack.service.SessaoEstudoService;
import br.com.studytrack.service.RelatorioService;
import br.com.studytrack.util.RocksDBUtil;
import br.com.studytrack.view.MenuView;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            RocksDBUtil.conectar();

            SessaoEstudoDAO dao = new SessaoEstudoDAO();

            SessaoEstudoService service = new SessaoEstudoService(dao);

            RelatorioService relatorioService = new RelatorioService(dao);

            MenuView menu = new MenuView(
                    scanner,
                    service,
                    relatorioService
            );

            menu.exibirMenu();

        } catch (Exception e) {
            System.out.println("Erro ao executar o StudyTrack: "
                    + e.getMessage());

        } finally {
            RocksDBUtil.desconectar();

            scanner.close();
        }
    }
}
