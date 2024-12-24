package utils;

import robocode.AdvancedRobot;
import robocode.Robot;
import java.awt.geom.*;
import java.util.GregorianCalendar;

/**
 * Classe utilitária para tarefas comuns em Robocode.
 * 
 * Esta classe fornece métodos para calcular distâncias, obter coordenadas de inimigos, 
 * movimentar robôs e formatar a data atual.
 * 
 * @author Benício Deco Sanca
 * @version 1.0
 */

 public final class Utils {

    // Construtor privado para evitar instanciação da classe
    private Utils() {}

    /**
     * Calcula a distância entre um robô e um ponto (x, y).
     * 
     * @param robot O robô cuja posição será usada como referência.
     * @param x A coordenada x do ponto de destino.
     * @param y A coordenada y do ponto de destino.
     * @return A distância entre o robô e o ponto (x, y).
     */
    public static double getDistance(Robot robot, double x, double y) {
        x -= robot.getX();
        y -= robot.getY();
        return Math.hypot(x, y);
    }

    /**
     * Calcula as coordenadas de um inimigo a partir do ângulo e da distância.
     * 
     * @param robot O robô que detectou o inimigo.
     * @param bearing O ângulo em graus para o inimigo, relativo à direção do robô.
     * @param distance A distância até o inimigo.
     * @return As coordenadas (x, y) do inimigo.
     */
    public static Point2D.Double getEnemyCoordinates(Robot robot, double bearing, double distance) {
        double angle = Math.toRadians((robot.getHeading() + bearing) % 360);
        return new Point2D.Double((robot.getX() + Math.sin(angle) * distance), (robot.getY() + Math.cos(angle) * distance));
    }

    /**
     * Faz o robô (Robot) se mover em linha reta a partir de um ponto (x, y) por uma determinada distância.
     * 
     * @param robot O robô que será movido.
     * @param x A coordenada x do ponto de partida.
     * @param y A coordenada y do ponto de partida.
     * @param distance A distância que o robô deve percorrer.
     */
    public static void runLineFrom(Robot robot, double x, double y, double distance) {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.turnRight(Math.toDegrees(turnAngle));

        if (targetAngle == turnAngle) {
            robot.back(distance);
        } else {
            robot.ahead(distance);
        }
    }

    /**
     * Faz o robô (Robot) ir para um ponto específico (x, y).
     * 
     * @param robot O robô que será movido.
     * @param x A coordenada x do ponto de destino.
     * @param y A coordenada y do ponto de destino.
     */
    public static void robotGoTo(Robot robot, double x, double y)
    {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.turnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.ahead(distance);
        else
            robot.back(distance);
    }

    /**
     * Faz o robô (AdvancedRobot) ir para um ponto específico (x, y).
     * 
     * @param robot O robô que será movido.
     * @param x A coordenada x do ponto de destino.
     * @param y A coordenada y do ponto de destino.
     */
    public static void advancedRobotGoTo(AdvancedRobot robot, double x, double y)
    {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.setTurnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.setAhead(distance);
        else
            robot.setBack(distance);
        robot.execute();
    }

    /**
     * Obtém a data e hora atual formatada como "yyyy-MM-dd HH:mm:ss".
     * 
     * @return A data e hora atual formatada.
     */
    public static String dataAtualFormatada()
    {
        GregorianCalendar gc = new GregorianCalendar();
        String mes = gc.get(GregorianCalendar.MONTH) < 10 ? "0"+gc.get(GregorianCalendar.MONTH) : ""+gc.get(GregorianCalendar.MONTH);
        String dia = gc.get(GregorianCalendar.DAY_OF_MONTH) < 10 ? "0"+gc.get(GregorianCalendar.DAY_OF_MONTH) : ""+gc.get(GregorianCalendar.DAY_OF_MONTH);
        String hora = gc.get(GregorianCalendar.HOUR_OF_DAY) < 10 ? "0"+gc.get(GregorianCalendar.HOUR_OF_DAY) : ""+gc.get(GregorianCalendar.HOUR_OF_DAY);
        String minuto = gc.get(GregorianCalendar.MINUTE) < 10 ? "0"+gc.get(GregorianCalendar.MINUTE) : ""+gc.get(GregorianCalendar.MINUTE);
        String segundo = gc.get(GregorianCalendar.SECOND) < 10 ? "0"+gc.get(GregorianCalendar.SECOND) : ""+gc.get(GregorianCalendar.SECOND);

        return gc.get(GregorianCalendar.YEAR)+"-"+mes+"-"+dia+" "+hora+":"+minuto+":"+segundo;
    }
}