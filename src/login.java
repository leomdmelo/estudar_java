import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;

public class login {

    public static void main(String[] args) {
        // Criando o JFrame (janela principal)
        JFrame frame = new JFrame("Tela de Login");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);  // Usando layout nulo para posicionar manualmente

        // Verificar a conexão com o banco de dados ao iniciar
        String dbStatus = checkDatabaseConnection();
        JLabel dbStatusLabel = new JLabel(dbStatus);
        dbStatusLabel.setBounds(50, 10, 250, 25);
        frame.add(dbStatusLabel);

        // Criando rótulos (JLabels)
        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setBounds(50, 50, 80, 25);
        frame.add(userLabel);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setBounds(50, 90, 80, 25);
        frame.add(passwordLabel);

        // Criando campos de texto (JTextFields)
        JTextField userText = new JTextField();
        userText.setBounds(130, 50, 150, 25);
        frame.add(userText);

        // Criando o campo de senha (JPasswordField)
        JPasswordField passwordText = new JPasswordField();
        passwordText.setBounds(130, 90, 150, 25);
        frame.add(passwordText);

        // Criando o botão de login
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(130, 130, 150, 25);
        frame.add(loginButton);

        // Adicionando ação ao botão de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());
                String hashedPassword = generateSHA1Hash(password); // Gerar o hash SHA-1 da senha

                if (authenticateUser(username, hashedPassword)) {
                    JOptionPane.showMessageDialog(frame, "Login bem-sucedido!");
                    // Fechar a tela de login e abrir a nova tela
                    frame.dispose();  // Fecha a tela de login
                    new Janela(); // Abre a próxima tela
                } else {
                    JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!");
                }
            }
        });

        // Tornando a janela visível
        frame.setVisible(true);
    }

    // Metodo para autenticar o usuário usando JDBC
    private static boolean authenticateUser(String username, String hashedPassword) {
        boolean isAuthenticated = false;

        // Dados da conexão com o banco de dados
        String url = "jdbc:mysql://localhost:3306/sistema_erp";
        String user = "root"; // Substitua pelo seu usuário do MySQL
        String pass = "root"; // Substitua pela sua senha do MySQL

        try {
            // Conectando ao banco de dados
            Connection conn = DriverManager.getConnection(url, user, pass);

            // Preparando a consulta SQL
            String query = "SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?) AND senha = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);

            // Executando a consulta
            ResultSet resultSet = preparedStatement.executeQuery();

            // Verificando se o usuário existe
            if (resultSet.next()) {
                isAuthenticated = true;
            }

            // Fechando a conexão
            resultSet.close();
            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return isAuthenticated;
    }

    // Metodo para gerar o hash SHA-1
    private static String generateSHA1Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(input.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // Adiciona um zero à esquerda se necessário
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Metodo para verificar a conexão com o banco de dados
    private static String checkDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/sistema_erp";
        String user = "root"; // Substitua pelo seu usuário do MySQL
        String pass = "root"; // Substitua pela sua senha do MySQL
        String status;

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            status = "Conexão com o banco: Sucesso";
            conn.close();
        } catch (Exception e) {
            status = "Conexão com o banco: Falhou!";
            e.printStackTrace();
        }

        return status;
    }
}
