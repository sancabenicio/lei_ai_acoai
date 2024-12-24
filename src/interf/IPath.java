package interf;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface que implementa um caminho
 */
public interface IPath {
    public List<IPoint> getPoints();
    public void setPoints(List<IPoint> points);
}

