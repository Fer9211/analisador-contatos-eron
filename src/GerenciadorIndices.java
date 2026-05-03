import java.util.HashMap;

public class GerenciadorIndices {
    private HashMap<String, Integer> emailParaId = new HashMap<>();
    // Crie um array para guardar os nomes (rótulos)
    private String[] idParaEmail = new String[100000];
    private int contador = 0;

    public int getId(String email) {
        if (!emailParaId.containsKey(email)) {
            emailParaId.put(email, contador);
            // Salva o e-mail no índice atual antes de incrementar o contador
            idParaEmail[contador] = email;
            contador++;
        }
        return emailParaId.get(email);
    }

    // Método para você pegar a lista de nomes e passar para o Grafo
    public String[] getListaDeRotulos() {
        return idParaEmail;
    }

    public int getTotalEncontrado() {
        return contador;
    }
}