import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        // 1. Instancia as classes
        // Verifique se o caminho da pasta está correto no seu computador
        File pasta = new File("data/Amostra Enron");
        if (!pasta.exists()) {
            System.out.println("ERRO: Pasta não encontrada. Verifique o caminho.");
            return;
        }

        Grafo meuGrafo = new Grafo(100000);
        GerenciadorIndices indices = new GerenciadorIndices();

        Path caminhoBase = pasta.toPath();

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

        // 3. Passa os rótulos coletados para o grafo e imprime
        meuGrafo.setRotulos(indices.getListaDeRotulos());

        meuGrafo.imprime_adjacencias();

    }

    private static void processarConteudo(File arquivo, Grafo g, GerenciadorIndices idx) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha, de = null;

            while ((linha = br.readLine()) != null) {
                // Captura o Remetente
                if (linha.startsWith("From: ")) {
                    de = limparEmail(linha.substring(6));
                }
                // Captura o(s) Destinatário(s)
                else if (linha.startsWith("To: ")) {
                    // Só processa destinatários se já tivermos um remetente válido
                    if (de == null || !de.contains("@")) continue;

                    String conteudoTo = linha.substring(4);
                    // Divide por vírgula ou ponto e vírgula para múltiplos e-mails
                    String[] partes = conteudoTo.split("[,;]");

                    for (String p : partes) {
                        String paraLimpo = limparEmail(p);
                        // Validação crucial: só cria aresta se ambos forem e-mails (tiverem @)
                        if (!paraLimpo.isEmpty() && paraLimpo.contains("@")) {
                            g.cria_ou_atualiza_adjacencia(idx.getId(de), idx.getId(paraLimpo));
                        }
                    }
                }

                // Otimização: Se encontrar uma linha vazia, os cabeçalhos acabaram
                if (linha.trim().isEmpty()) break;
            }
        } catch (Exception e) {
            // Ignora erros de arquivos corrompidos para não travar a execução
        }
    }

    private static String limparEmail(String texto) {
        if (texto == null) return "";

        texto = texto.trim().toLowerCase();

        // 1. Remove data/horário (on 10/11/2000...)
        if (texto.contains(" on ")) {
            texto = texto.split(" on ")[0];
        }

        // 2. Trata formato Nome <email@dominio.com>
        if (texto.contains("<") && texto.contains(">")) {
            texto = texto.substring(texto.indexOf("<") + 1, texto.indexOf(">"));
        }

        // 3. Trata formato [mailto:email@dominio.com]
        if (texto.contains("[mailto:")) {
            int inicio = texto.indexOf("[mailto:") + 8;
            int fim = texto.indexOf("]", inicio);
            if (fim > inicio) {
                texto = texto.substring(inicio, fim);
            }
        }

        // 4. Se ainda houver espaços (ex: "nome email@dominio.com"), pega a última parte
        if (texto.contains(" ")) {
            String[] partes = texto.split(" ");
            texto = partes[partes.length - 1];
        }

        // 5. Limpeza final de caracteres residuais
        texto = texto.replaceAll("[;>,\\s\\[\\]]", "").trim();

        // Só retorna se parecer um e-mail (tiver @), caso contrário retorna vazio
        return texto.contains("@") ? texto : "";
    }
}