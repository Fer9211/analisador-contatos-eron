import java.io.*;
import java.util.*;

/**
 * Esta classe é o "coração" do nosso projeto.
 * Ela transforma arquivos de texto em um Grafo de e-mails.
 */
public class Analisador {

    // O Grafo: A chave é o e-mail da pessoa.
    // O valor é a lista de contatos para quem ela enviou mensagens.
    private Map<String, List<Conexao>> rede;

    public Analisador() {
        this.rede = new HashMap<>();
    }

    // --- BLOCO: MONTAGEM DO GRAFO ---

    /**
     * Adiciona uma flecha (aresta) entre o remetente e o destinatário.
     */
    public void adicionarInteracao(String remetente, String destinatario) {

        // Limpeza: Se o e-mail estiver em branco, a gente ignora.
        if (remetente == null || remetente.trim().isEmpty() ||
                destinatario == null || destinatario.trim().isEmpty()) {
            return;
        }

        // Se a pessoa nunca apareceu, a gente cria um "nó" novo para ela no mapa.
        rede.putIfAbsent(remetente, new ArrayList<>());

        List<Conexao> contatos = rede.get(remetente);
        boolean jaExiste = false;

        // Se o remetente já mandou e-mail para essa mesma pessoa antes,
        // a gente só aumenta o peso (frequência).
        for (Conexao c : contatos) {
            if (c.destino.equals(destinatario)) {
                c.quantidade++;
                jaExiste = true;
                break;
            }
        }

        // Se é a primeira vez que eles se falam, cria uma conexão nova.
        if (!jaExiste) {
            contatos.add(new Conexao(destinatario));
        }

        // Importante: O destinatário também precisa existir como um nó no grafo.
        rede.putIfAbsent(destinatario, new ArrayList<>());
    }

    /**
     * entra em todas as pastas e subpastas da Enron.
     */
    public void lerArquivosDaPasta(File pasta) {
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    // Se achou uma pasta, entra nela (Recursividade).
                    lerArquivosDaPasta(arquivo);
                } else {
                    // Se achou um arquivo, manda ler o conteúdo.
                    processarArquivoEmail(arquivo);
                }
            }
        }
    }

    /**
     * Lê o arquivo de texto e "caça" as linhas de From e To.[cite: 1, 2]
     */
    private void processarArquivoEmail(File arquivoEmail) {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoEmail))) {
            String linha, remetente = null;
            List<String> destinatarios = new ArrayList<>();

            while ((linha = leitor.readLine()) != null) {
                // Pega quem enviou.[cite: 1]
                if (linha.startsWith("From: ")) {
                    remetente = linha.replace("From: ", "").trim();
                }
                // Pega quem recebeu (pode ser mais de uma pessoa separada por vírgula).
                else if (linha.startsWith("To: ")) {
                    String[] lista = linha.replace("To: ", "").split(",");
                    for (String email : lista) destinatarios.add(email.trim());
                }
                // Para de ler ao chegar no fim do cabeçalho (linha vazia).
                if (linha.isEmpty()) break;
            }

            // Se achou os dados, registra a fofoca no grafo.
            if (remetente != null) {
                for (String para : destinatarios) adicionarInteracao(remetente, para);
            }
        } catch (Exception e) {
            // Ignora arquivos zifados ou corrompidos.
        }
    }

    // --- BLOCO: ESTATÍSTICAS (O TRABALHO DO INTEGRANTE A) ---

    // Conta quantas pessoas únicas existem no sistema.
    public int calcularTotalDeVertices() { return rede.size(); }

    // Conta o total de flechas (conexões) criadas.
    public int calcularTotalDeArestas() {
        int total = 0;
        for (List<Conexao> lista : rede.values()) total += lista.size();
        return total;
    }

    /**
     * Grau de Saída: Quem enviou e-mails para mais pessoas diferentes.
     */
    public void mostrarTop20Saida() {
        List<String> emails = new ArrayList<>(rede.keySet());
        // Ordena do maior para o menor.
        emails.sort((a, b) -> Integer.compare(rede.get(b).size(), rede.get(a).size()));

        System.out.println("\n--- TOP 20 GRAU DE SAÍDA ---");
        for (int i = 0; i < Math.min(20, emails.size()); i++) {
            System.out.println((i+1) + ". " + emails.get(i) + " (" + rede.get(emails.get(i)).size() + ")");
        }
    }

    /**
     * Grau de Entrada: Quem apareceu em mais listas de contatos alheias.
     */
    public void mostrarTop20Entrada() {
        Map<String, Integer> entradas = new HashMap<>();
        // Começa todo mundo com zero.
        rede.keySet().forEach(e -> entradas.put(e, 0));

        // Varre todas as listas do grafo para contar as menções.
        for (List<Conexao> lista : rede.values()) {
            for (Conexao c : lista) {
                entradas.put(c.destino, entradas.getOrDefault(c.destino, 0) + 1);
            }
        }

        // Transforma em lista para ordenar os resultados.
        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(entradas.entrySet());
        ranking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n--- TOP 20 GRAU DE ENTRADA ---");
        for (int i = 0; i < Math.min(20, ranking.size()); i++) {
            System.out.println((i+1) + ". " + ranking.get(i).getKey() + " (" + ranking.get(i).getValue() + ")");
        }
    }
}