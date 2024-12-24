package battlefieldanaly;

import robocode.*;
import java.awt.geom.*;
import java.io.IOException;
import java.util.Random;

public class BattlefieldAnalyzerRobot extends AdvancedRobot {

    private static final int GRID_SIZE = 10; // Tamanho da célula da grade (10x10 pixels)
    private static final int MAP_WIDTH = 800; // Largura do mapa em pixels
    private static final int MAP_HEIGHT = 600; // Altura do mapa em pixels

    private RobocodeFileOutputStream fw; // Para escrever dados no arquivo CSV
    private int currentGridX, currentGridY; // Posição atual na grade
    private long lastScanTime = 0; // Para controlar a frequência de varredura
    private Random random = new Random(); // Para adicionar aleatoriedade

    private enum LocationRating { GOOD, BAD } // "BOA" ou "RUIM"
    private LocationRating currentLocationRating = LocationRating.GOOD; // Começa otimista

    @Override
    public void run() {
        try {
            fw = new RobocodeFileOutputStream(getDataFile("battle_data.csv").getAbsolutePath(), true); // true para append
            fw.write("grid_x,grid_y,distancia_relativa,angulo_relativo,energia_robo,energia_inimigo,velocidade_inimigo,distancia_absoluta,tipo_inimigo,num_inimigos_visiveis,distancia_parede_mais_proxima,angulo_parede_mais_proxima,location_rating\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean[][] visitedGrid = new boolean[MAP_WIDTH / GRID_SIZE][MAP_HEIGHT / GRID_SIZE];
        currentGridX = 0;
        currentGridY = 0;

        while (true) {
            visitedGrid[currentGridX][currentGridY] = true;

            do {
                currentGridX = (currentGridX + 1) % (MAP_WIDTH / GRID_SIZE);
                if (currentGridX == 0) {
                    currentGridY = (currentGridY + 1) % (MAP_HEIGHT / GRID_SIZE);
                }
            } while (visitedGrid[currentGridX][currentGridY]);

            double newX = (currentGridX + 0.5) * GRID_SIZE;
            double newY = (currentGridY + 0.5) * GRID_SIZE;
            goTo(newX, newY);

            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (getTime() - lastScanTime < 10) {
            return;
        }
        lastScanTime = getTime();

        double angle = Math.toRadians((getHeading() + event.getBearing()) % 360);
        double enemyX = getX() + Math.sin(angle) * event.getDistance();
        double enemyY = getY() + Math.cos(angle) * event.getDistance();

        double relativeDistance = event.getDistance() / Math.sqrt(MAP_WIDTH * MAP_WIDTH + MAP_HEIGHT * MAP_HEIGHT);
        double relativeAngle = normalizeBearing(event.getBearing() + getHeading() - getGunHeading());

        int numEnemiesVisible = getOthers();
        double distanceToClosestWall = Math.min(Math.min(getX(), getY()), Math.min(MAP_WIDTH - getX(), MAP_HEIGHT - getY()));
        double angleToClosestWall = getAngleToClosestWall();

        updateLocationRating(event, numEnemiesVisible, distanceToClosestWall);

        String data = String.format("%d,%d,%f,%f,%f,%f,%f,%f,%s,%d,%f,%f,%s\n",
            currentGridX, currentGridY, relativeDistance, relativeAngle, getEnergy(), event.getEnergy(), event.getVelocity(),
            event.getDistance(), event.getName(), numEnemiesVisible, distanceToClosestWall, angleToClosestWall,
            currentLocationRating.toString());

        try {
            fw.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLocationRating(ScannedRobotEvent event, int numEnemiesVisible, double distanceToClosestWall) {
        if (event.getDistance() < 100 || numEnemiesVisible > 1 || distanceToClosestWall < 50) {
            currentLocationRating = LocationRating.BAD; 
        } else {
            currentLocationRating = LocationRating.GOOD; 
        }

        if (random.nextInt(100) < 5) { 
            currentLocationRating = (currentLocationRating == LocationRating.GOOD) ? LocationRating.BAD : LocationRating.GOOD;
        }
    }

    private double getAngleToClosestWall() {
        double angle = 0;
        double x = getX();
        double y = getY();

        if (x <= y && x <= MAP_WIDTH - x && x <= MAP_HEIGHT - y) { // Parede esquerda
            angle = 90;
        } else if (y <= x && y <= MAP_WIDTH - x && y <= MAP_HEIGHT - y) { // Parede superior
            angle = 0;
        } else if (MAP_WIDTH - x <= x && MAP_WIDTH - x <= y && MAP_WIDTH - x <= MAP_HEIGHT - y) { // Parede direita
            angle = 270;
        } else { // Parede inferior
            angle = 180;
        }
        return angle;
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goTo(double x, double y) {
        double dx = x - getX();
        double dy = y - getY();
        double angle = Math.toDegrees(Math.atan2(dx, dy));
        double distance = Point2D.distance(getX(), getY(), x, y);

        turnRight(normalizeBearing(angle - getHeading()));
        ahead(distance);
    }

    private double normalizeBearing(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }
}
