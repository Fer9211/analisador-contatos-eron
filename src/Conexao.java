public class Conexao {
    String destino;
    int quantidade;

    public Conexao(String destino) {
        this.destino = destino;
        this.quantidade = 1; // Começa com 1 e-mail enviado
    }
}