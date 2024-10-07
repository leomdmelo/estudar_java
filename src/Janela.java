import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Janela {

    public Janela() {
        // Criando o JFrame (janela principal)
        JFrame janelaFrame = new JFrame("Janela");
        janelaFrame.setSize(400, 300);
        janelaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaFrame.setLayout(null);

        // Adicionando um rótulo de boas-vindas
        JLabel welcomeLabel = new JLabel("Bem-vindo à Janela!");
        welcomeLabel.setBounds(100, 100, 200, 25);
        janelaFrame.add(welcomeLabel);

        // Criando a barra de menu
        JMenuBar menuBar = new JMenuBar();

        // Criando o menu "Usuários"
        JMenu userMenu = new JMenu("Usuarios");

        // Criando o item "Listar Usuários"
        JMenuItem listUsersItem = new JMenuItem("Listar Usuarios");
        listUsersItem.addActionListener(e -> listarUsuarios());

        // Criando o item "Cadastrar Usuário"
        JMenuItem registerUserItem = new JMenuItem("Cadastrar Usuario");
        registerUserItem.addActionListener(e -> cadastrarUsuario());

        // Adicionando itens ao menu "Usuários"
        userMenu.add(listUsersItem);
        userMenu.add(registerUserItem);

        // Adicionando o menu "Usuários" à barra de menu
        menuBar.add(userMenu);

        // Definindo a barra de menu no frame
        janelaFrame.setJMenuBar(menuBar);

        // Tornando a janela visível
        janelaFrame.setVisible(true);
    }

    // Metodo para exibir a lista de usuários
    private void listarUsuarios() {
        // Criando a janela para exibir a lista de usuários
        JFrame listFrame = new JFrame("Lista de Usuarios");
        listFrame.setSize(500, 300);
        listFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        listFrame.setLayout(new BorderLayout());

        // Criando o modelo da tabela
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nome do Usuario");
        model.addColumn("Usuario");
        model.addColumn("Status");

        // Criando a tabela
        JTable userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);
        listFrame.add(scrollPane, BorderLayout.CENTER);

        // Carregando os dados do banco de dados
        carregarUsuariosDoBanco(model);

        listFrame.setVisible(true);
    }

    // Metodo para carregar dados dos usuários do banco de dados
    private void carregarUsuariosDoBanco(DefaultTableModel model) {
        // Dados de conexão com o banco de dados
        String url = "jdbc:mysql://localhost:3306/sistema_erp";
        String user = "root"; // Substitua pelo seu usuário do MySQL
        String pass = "root"; // Substitua pela sua senha do MySQL

        try {
            // Conectando ao banco de dados
            Connection conn = DriverManager.getConnection(url, user, pass);

            // Preparando a consulta SQL
            String query = "SELECT nome, email, status FROM usuarios";
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            // Executando a consulta
            ResultSet resultSet = preparedStatement.executeQuery();

            // Preenchendo o modelo da tabela com os dados do banco
            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");
                String status = resultSet.getString("status");

                model.addRow(new Object[]{nome, email, status});
            }

            // Fechando a conexão
            resultSet.close();
            preparedStatement.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados do banco de dados!");
        }
    }

    // Metodo para abrir a tela de cadastro de usuário
    private void cadastrarUsuario() {
        JFrame registerFrame = new JFrame("Cadastrar Usuario");
        registerFrame.setSize(300, 200);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setLayout(null);

        JLabel userLabel = new JLabel("Nome do Usuario:");
        userLabel.setBounds(10, 10, 120, 25);
        registerFrame.add(userLabel);

        JTextField userNameField = new JTextField();
        userNameField.setBounds(140, 10, 120, 25);
        registerFrame.add(userNameField);

        JLabel userFieldLabel = new JLabel("Email:");
        userFieldLabel.setBounds(10, 50, 120, 25);
        registerFrame.add(userFieldLabel);

        JTextField userField = new JTextField();
        userField.setBounds(140, 50, 120, 25);
        registerFrame.add(userField);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setBounds(10, 90, 120, 25);
        registerFrame.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(140, 90, 120, 25);
        registerFrame.add(passwordField);

        JButton registerButton = new JButton("Cadastrar");
        registerButton.setBounds(140, 130, 120, 25);
        registerButton.addActionListener(e -> {
            String userName = userNameField.getText();
            String user = userField.getText();
            String password = new String(passwordField.getPassword());
            cadastrarUsuarioNoBanco(userName, user, password);
            registerFrame.dispose();
        });
        registerFrame.add(registerButton);

        registerFrame.setVisible(true);
    }

    // Metodo para cadastrar o usuário no banco de dados
    private void cadastrarUsuarioNoBanco(String nome, String email, String senha) {
        // Dados de conexão com o banco de dados
        String url = "jdbc:mysql://localhost:3306/sistema_erp";
        String user = "root"; // Substitua pelo seu usuário do MySQL
        String pass = "root"; // Substitua pela sua senha do MySQL

        try {
            // Conectando ao banco de dados
            Connection conn = DriverManager.getConnection(url, user, pass);

            // Preparando a consulta SQL para inserir o novo usuário
            String query = "INSERT INTO usuarios (nome, email, senha, status) VALUES (?, ?, ?, 'Ativo')";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, senha); // Aqui pode adicionar criptografia, se necessário

            // Executando a inserção
            preparedStatement.executeUpdate();

            // Fechando a conexão
            preparedStatement.close();
            conn.close();

            JOptionPane.showMessageDialog(null, "Usuario cadastrado com sucesso!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar o usuario no banco de dados!");
        }
    }

    public static void main(String[] args) {
        new Janela();
    }
}
