package interf;

import java.awt.*;
import java.util.List;

/**
 * Classe que modela o mapa no qual precisamos encontrar uma solução, incluindo seu tamanho e os obstáculos.
 */
public interface IUIConfiguration {
    /**
     * Retorna a largura do mapa.
     * @return a largura do mapa.
     */
    int getWidth();

    /**
     * Altera a largura do mapa.
     * @param width o novo valor
     */
    void setWidth(int width);

    /**
     * Retorna a altura do mapa.
     * @return a altura do mapa
     */
    int getHeight();

    /**
     * Altera a altura do mapa.
     * @param height o novo valor
     */
    void setHeight(int height);

    /**
     * Retorna uma lista dos obstáculos que existem no mapa. Cada obstáculo é representado por um {@link Rectangle}.
     * @return uma lista dos obstáculos que existem no mapa
     */
    List<Rectangle> getObstacles();

    /**
     * Altera a lista de obstáculos que existem no mapa.
     * @param obstacles a nova lista de obstáculos que existem no mapa
     */
    void setObstacles(List<Rectangle> obstacles);

    /**
     * Retorna o ponto inicial para este problema específico
     * @return o ponto inicial
     */
    public IPoint getStart();

    /**
     * Altera o ponto inicial para este problema específico
     * @param start o novo ponto inicial
     */
    public void setStart(IPoint start);

    /**
     * Retorna o ponto final para este problema específico
     * @return o ponto final
     */
    public IPoint getEnd();

    /**
     * Altera o ponto final para este problema específico
     * @param end o novo ponto final
     */
    public void setEnd(IPoint end);
}
