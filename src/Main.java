import java.io.*;
import java.nio.file.*;
import java.util.*; // Importa Scanner, List, ArrayList e HashSet de uma vez

public class Main {
    public static void main(String[] args) {
        // 1. Instancia as classes
        File pasta = new File("data/Amostra Enron");
        if (!pasta.exists()) {
            System.out.println("ERRO: Pasta não encontrada. Verifique o caminho.");
            return;
        }

        Grafo meuGrafo = new Grafo(100000);
        GerenciadorIndices indices = new GerenciadorIndices();

        Path caminhoBase = pasta.toPath();

        System.out.println("Lendo arquivos... isso pode demorar um pouco.");

        try {
            // 2. Files.walk percorre todas as subpastas
            Files.walk(caminhoBase)
                    .filter(Files::isRegularFile)
                    .forEach(arquivo -> {
                        processarConteudo(arquivo.toFile(), meuGrafo, indices);
                    });
        } catch (IOException e) {
            System.out.println("Erro ao percorrer arquivos: " + e.getMessage());
        }

        // 3. Configura rótulos e mostra estatísticas (Requisitos 1 e 2)
        meuGrafo.setRotulos(indices.getListaDeRotulos());

        // Se quiser ver todas as conexões, descomente a linha abaixo:
        // meuGrafo.imprime_adjacencias();

        System.out.println("---------------------------------------------");
        System.out.println("N. de Vertices: " + meuGrafo.getNumVertices());
        System.out.println("N. de Arestas: " + meuGrafo.getNumArestas());

        meuGrafo.imprimirTop20Saida();
        meuGrafo.imprimirTop20Entrada();

//ESSE É PRA TESTAR O CAMINHO
        Scanner leitor = new Scanner(System.in);
        System.out.println("O caminho entre um e outro");
        System.out.print("Digite o e-mail de origem (X): ");
        String emailX = leitor.nextLine().trim().toLowerCase();
        System.out.print("Digite o e-mail de destino (Y): ");
        String emailY = leitor.nextLine().trim().toLowerCase();

        List<String> caminhoEncontrado = meuGrafo.buscaProfundidade(emailX, emailY, indices);

        if (caminhoEncontrado != null) {
            System.out.println("\nX " + emailX + " consegue alcançar Y" + emailY);
            System.out.println("Caminho percorrido:");
            System.out.println(String.join(" -> ", caminhoEncontrado));
        } else {
            System.out.println(" Não foi encontrado um caminho entre os e-mails informados.");
        }

        leitor.close();
    }

    private static void processarConteudo(File arquivo, Grafo g, GerenciadorIndices idx) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha, de = null;

            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("From: ")) {
                    de = limparEmail(linha.substring(6));
                }
                else if (linha.startsWith("To: ")) {
                    if (de == null || !de.contains("@")) continue;

                    String conteudoTo = linha.substring(4);
                    String[] partes = conteudoTo.split("[,;]");

                    for (String p : partes) {
                        String paraLimpo = limparEmail(p);
                        if (!paraLimpo.isEmpty() && paraLimpo.contains("@")) {
                            g.cria_ou_atualiza_adjacencia(idx.getId(de), idx.getId(paraLimpo));
                        }
                    }
                }
                if (linha.trim().isEmpty()) break;
            }
        } catch (Exception e) {
            // Ignora erros
        }
    }

    private static String limparEmail(String texto) {
        if (texto == null) return "";
        texto = texto.trim().toLowerCase();
        if (texto.contains("<") && texto.contains(">")) {
            texto = texto.substring(texto.indexOf("<") + 1, texto.indexOf(">"));
        }
        texto = texto.replaceAll("[;>,\\s\\[\\]]", "").trim();
        return texto.contains("@") ? texto : "";
    }
}