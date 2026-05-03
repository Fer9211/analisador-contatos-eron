import java.util.*;

public class Grafo {
    private Aresta[] listaDeAdjacencias;
    private String[] rotulosVertices;
    public int totalVertices;

    public Grafo(int totalVerticies){
        this.totalVertices = totalVerticies;
        this.listaDeAdjacencias = new Aresta[totalVerticies];
        this.rotulosVertices = new String[totalVerticies];
    }

    public void cria_ou_atualiza_adjacencia(int verticeOrigem, int verticeDestino) {
        if (verticeOrigem < 0 || verticeOrigem >= totalVertices || verticeDestino < 0 || verticeDestino >= totalVertices) return;

        Aresta atual = listaDeAdjacencias[verticeOrigem];
        while (atual != null) {
            if (atual.verticeDestino == verticeDestino) {
                atual.peso += 1.0;
                return;
            }
            atual = atual.proximaAresta;
        }
        Aresta novaAresta = new Aresta(verticeDestino, 1.0);
        novaAresta.proximaAresta = listaDeAdjacencias[verticeOrigem];
        listaDeAdjacencias[verticeOrigem] = novaAresta;
    }
    public void imprime_adjacencias() {
        for (int vertice = 0; vertice < totalVertices; vertice++) {
            if (rotulosVertices[vertice] == null) continue;

            System.out.print(rotulosVertices[vertice] + " -> ");
            Aresta arestaAtual = listaDeAdjacencias[vertice];
            while (arestaAtual != null) {
                String nomeDestino = rotulosVertices[arestaAtual.verticeDestino];
                System.out.print("[" + nomeDestino + ", peso: " + arestaAtual.peso + "] ");
                arestaAtual = arestaAtual.proximaAresta;
            }
            System.out.println();
        }
    }
    public void setRotulos(String[] nomesColetados) {
        this.rotulosVertices = nomesColetados;
    }

    // --- MÉTODOS DO ALUNO A ---

    /**
     * Retorna o número de e-mails (vértices) que foram realmente preenchidos.
     */
    public int getNumVertices() {
        int contador = 0;
        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] != null) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Conta todas as conexões (arestas) percorrendo as listas encadeadas.
     */
    public int getNumArestas() {
        int total = 0;
        for (int i = 0; i < totalVertices; i++) {
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                total++;
                atual = atual.proximaAresta;
            }
        }
        return total;
    }

    /**
     * Calcula quem mais enviou e-mails (Grau de Saída).
     */
    public void imprimirTop20Saida() {
        List<Map.Entry<String, Integer>> listaRanking = new ArrayList<>();

        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] == null) continue;

            int grau = 0;
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                grau++;
                atual = atual.proximaAresta;
            }
            listaRanking.add(new AbstractMap.SimpleEntry<>(rotulosVertices[i], grau));
        }

        // Ordena do maior para o menor[cite: 1]
        listaRanking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n--- TOP 20 GRAU DE SAÍDA ---");
        for (int i = 0; i < Math.min(20, listaRanking.size()); i++) {
            System.out.println((i + 1) + ". " + listaRanking.get(i).getKey() + " | Valor: " + listaRanking.get(i).getValue());
        }
    }

    /**
     * Calcula quem mais recebeu e-mails (Grau de Entrada)[cite: 1].
     */
    public void imprimirTop20Entrada() {
        int[] contagemEntrada = new int[totalVertices];

        // Varre o grafo inteiro contando quem é o destino das flechas[cite: 1]
        for (int i = 0; i < totalVertices; i++) {
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                contagemEntrada[atual.verticeDestino]++;
                atual = atual.proximaAresta;
            }
        }

        List<Map.Entry<String, Integer>> listaRanking = new ArrayList<>();
        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] == null) continue;
            listaRanking.add(new AbstractMap.SimpleEntry<>(rotulosVertices[i], contagemEntrada[i]));
        }

        listaRanking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n--- TOP 20 GRAU DE ENTRADA ---");
        for (int i = 0; i < Math.min(20, listaRanking.size()); i++) {
            System.out.println((i + 1) + ". " + listaRanking.get(i).getKey() + " | Valor: " + listaRanking.get(i).getValue());
        }
    }
}



