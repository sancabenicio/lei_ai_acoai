package battlefieldanaly;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;  
import java.util.List; 


public class shootingRobo extends AdvancedRobot {
    private PrintWriter writer;
    private boolean movingForward;
    private int currentShotIndex = 0;

    // Estrutura para armazenar dados temporários antes de registrar no CSV
    private class ShotData {
        double meuX;
        double meuY;
        double meuAngulo;
        double minhaVelocidade;
        double distanciaInimigo;
        double anguloInimigo;
        double energiaInimigo;
        double potenciaTiro;
        int acerto;  // 0 = Erro, 1 = Acerto

        ShotData(double meuX, double meuY, double meuAngulo, double minhaVelocidade, double distanciaInimigo, double anguloInimigo, double energiaInimigo, double potenciaTiro, int acerto) {
            this.meuX = meuX;
            this.meuY = meuY;
            this.meuAngulo = meuAngulo;
            this.minhaVelocidade = minhaVelocidade;
            this.distanciaInimigo = distanciaInimigo;
            this.anguloInimigo = anguloInimigo;
            this.energiaInimigo = energiaInimigo;
            this.potenciaTiro = potenciaTiro;
            this.acerto = acerto;
        }
    }

    // Lista para armazenar os dados dos disparos
    private List<ShotData> shotDataList = new ArrayList<>();

    public void run() {
        setColors(Color.RED, Color.BLUE, Color.GREEN);
        movingForward = true;

        try {
            writer = new PrintWriter(new RobocodeFileOutputStream(getDataFile("shooting_robo.csv")));
            writer.println("robo_x,robo_y,robo_angulo,robo_velocidade,distancia_inimigo,angulo_inimigo,energia_inimigo,potencia_tiro,acerto");
        } catch (IOException e) {
            e.printStackTrace();
        }

        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        turnRadarRight(Double.POSITIVE_INFINITY);

        while (true) {
            if (movingForward) {
                setAhead(100);
            } else {
                setBack(100);
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double meuX = getX();
        double meuY = getY();
        double meuAngulo = getHeading();
        double minhaVelocidade = getVelocity();
        double distanciaInimigo = e.getDistance();
        double anguloInimigo = getHeading() + e.getBearing();
        double energiaInimigo = e.getEnergy();
        double potenciaTiro = Math.min(400 / distanciaInimigo, 3);

        turnGunRight(Utils.normalRelativeAngleDegrees(anguloInimigo - getGunHeading()));
        fire(potenciaTiro);

        // Armazena os dados do disparo atual
        shotDataList.add(new ShotData(meuX, meuY, meuAngulo, minhaVelocidade, distanciaInimigo, anguloInimigo, energiaInimigo, potenciaTiro, 0));
        currentShotIndex++;
    }

    public void onBulletHit(BulletHitEvent e) {
        // Atualiza o valor de acerto no último disparo registrado
        if (currentShotIndex > 0 && currentShotIndex <= shotDataList.size()) {
            shotDataList.get(currentShotIndex - 1).acerto = 1;
        }
    }

    public void onBulletMissed(BulletMissedEvent e) {
        // Já está registrado como erro (0) inicialmente, não precisa fazer nada aqui
    }

    public void onHitByBullet(HitByBulletEvent e) {
        if (movingForward) {
            setBack(100);
            movingForward = false;
        } else {
            setAhead(100);
            movingForward = true;
        }
    }

    public void onHitWall(HitWallEvent e) {
        if (movingForward) {
            setBack(150);
            movingForward = false;
        } else {
            setAhead(100);
            movingForward = true;
        }
    }

    public void onDeath(DeathEvent e) {
        saveData();
    }

    public void onWin(WinEvent e) {
        saveData();
    }

    private void saveData() {
        try {
            for (ShotData data : shotDataList) {
                writer.println(data.meuX + "," + data.meuY + "," + data.meuAngulo + "," + data.minhaVelocidade + "," + data.distanciaInimigo + "," + data.anguloInimigo + "," + data.energiaInimigo + "," + data.potenciaTiro + "," + data.acerto);
            }
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
