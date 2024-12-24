package interf;

/**
 * Representa um ponto no caminho
 */
public interface IPoint {
    /**
     * Retorna o valor X deste ponto no caminho
     * @return o valor X deste ponto
     */
    int getX();

    /**
     * Retorna o valor Y deste ponto no caminho
     * @return o valor Y deste ponto
     */
    int getY();

    /**
     * Retorna uma representação em string do Ponto
     * @return uma representação em string do Ponto
     */
    String toString();
}
