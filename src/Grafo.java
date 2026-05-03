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
}


