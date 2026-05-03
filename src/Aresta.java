public class Aresta {
    public int verticeDestino; // índice do vértice para onde esta aresta aponta
    public double peso; // custo/peso da aresta
    public Aresta proximaAresta; // próxima aresta na lista encadeada

    public Aresta(int verticeDestino, double peso) {
        this.verticeDestino = verticeDestino;
        this.peso = peso;
        this.proximaAresta = null;
    }
}