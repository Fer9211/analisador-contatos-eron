import java.io.*;
import java.util.*;

public class Analisador {
    // Grafo: Chave é o e-mail, Valor é a lista de conexões (Lista de Adjacência)[cite: 2]
    private Map<String, List<Conexao>> rede;

    public Analisador() {
        this.rede = new HashMap<>();
    }

    // --- BLOCO: MONTAGEM DO GRAFO ---

    public void adicionarInteracao(String remetente, String destinatario) {
        // NOVA TRAVA: Se o nome do remetente ou destinatário estiver vazio, a gente ignora
        if (remetente == null || remetente.trim().isEmpty() ||
                destinatario == null || destinatario.trim().isEmpty()) {
            return;
        }

        rede.putIfAbsent(remetente, new ArrayList<>());

        List<Conexao> contatos = rede.get(remetente);
        boolean jaExiste = false;

        for (Conexao c : contatos) {
            if (c.destino.equals(destinatario)) {
                c.quantidade++;
                jaExiste = true;
                break;
            }
        }

        if (!jaExiste) {
            contatos.add(new Conexao(destinatario));
        }

        rede.putIfAbsent(destinatario, new ArrayList<>());
    }

    public void lerArquivosDaPasta(File pasta) {
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    lerArquivosDaPasta(arquivo); // Recursividade para subpastas[cite: 2]
                } else {
                    processarArquivoEmail(arquivo);
                }
            }
        }
    }

    private void processarArquivoEmail(File arquivoEmail) {
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoEmail))) {
            String linha, remetente = null;
            List<String> destinatarios = new ArrayList<>();

            while ((linha = leitor.readLine()) != null) {
                if (linha.startsWith("From: ")) {
                    remetente = linha.replace("From: ", "").trim();
                } else if (linha.startsWith("To: ")) {
                    String[] lista = linha.replace("To: ", "").split(",");
                    for (String email : lista) destinatarios.add(email.trim());
                }
                if (linha.isEmpty()) break;
            }

            if (remetente != null) {
                for (String para : destinatarios) adicionarInteracao(remetente, para);
            }
        } catch (Exception e) { /* Ignora erros de leitura */ }
    }

    // --- BLOCO: ESTATÍSTICAS (INTEGRANTE A) ---

    public int calcularTotalDeVertices() { return rede.size(); }

    public int calcularTotalDeArestas() {
        int total = 0;
        for (List<Conexao> lista : rede.values()) total += lista.size();
        return total;
    }

    public void mostrarTop20Saida() {
        List<String> emails = new ArrayList<>(rede.keySet());
        emails.sort((a, b) -> Integer.compare(rede.get(b).size(), rede.get(a).size()));

        System.out.println("\n--- TOP 20 GRAU DE SAÍDA ---");
        for (int i = 0; i < Math.min(20, emails.size()); i++) {
            System.out.println((i+1) + ". " + emails.get(i) + " (" + rede.get(emails.get(i)).size() + ")");
        }
    }

    public void mostrarTop20Entrada() {
        Map<String, Integer> entradas = new HashMap<>();
        rede.keySet().forEach(e -> entradas.put(e, 0));

        for (List<Conexao> lista : rede.values()) {
            for (Conexao c : lista) {
                entradas.put(c.destino, entradas.getOrDefault(c.destino, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(entradas.entrySet());
        ranking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n--- TOP 20 GRAU DE ENTRADA ---");
        for (int i = 0; i < Math.min(20, ranking.size()); i++) {
            System.out.println((i+1) + ". " + ranking.get(i).getKey() + " (" + ranking.get(i).getValue() + ")");
        }
    }
}