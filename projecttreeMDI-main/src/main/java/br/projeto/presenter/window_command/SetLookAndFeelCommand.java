package br.projeto.presenter.window_command;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class SetLookAndFeelCommand implements WindowCommand {

    @Override
    public void execute() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Erro ao definir o FlatLaf. Usando Look and Feel padrão.");
        }
    }
}
