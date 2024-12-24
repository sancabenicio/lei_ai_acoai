package maps;

import interf.IUIConfiguration;
import viewer.PathViewer;
import impl.Point;
import interf.IPoint;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PathDrawingSample {

    public static IUIConfiguration conf;

    public static void main(String args[]) throws InterruptedException, Exception {
        Random rand = new Random();
        int map_id = 1;

        conf = Maps.getMap(map_id);
        
        // Configurações do Algoritmo Genético
        int populationSize = 100;
        int generations = 1000;
        double mutationRate = 0.05;

        // Gerar a população inicial
        List<List<IPoint>> population = generateInitialPopulation(populationSize, conf, rand);

        List<IPoint> bestSolution = null;
        double bestFitness = Double.MAX_VALUE;

        for (int generation = 0; generation < generations; generation++) {
            // Avaliar a população
            List<Double> fitnessScores = evaluatePopulation(population, conf);

            // Encontrar a melhor solução da geração
            for (int i = 0; i < populationSize; i++) {
                if (fitnessScores.get(i) < bestFitness) {
                    bestFitness = fitnessScores.get(i);
                    bestSolution = population.get(i);
                }
            }

            // Selecionar a próxima geração
            List<List<IPoint>> newPopulation = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                List<IPoint> parent1 = selectParent(population, fitnessScores, rand);
                List<IPoint> parent2 = selectParent(population, fitnessScores, rand);
                List<IPoint> offspring = crossover(parent1, parent2, rand);
                if (rand.nextDouble() < mutationRate) {
                    mutate(offspring, conf, rand);
                }
                newPopulation.add(offspring);
            }
            population = newPopulation;

            System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
        }

        // Verificar se a melhor solução é válida
        if (isValidSolution(bestSolution, conf)) {
            System.out.println("Solução válida!");
        } else {
            System.out.println("Solução inválida!");
        }

        // Visualizar a melhor solução encontrada
        PathViewer pv = new PathViewer(conf);
        pv.setFitness(bestFitness);
        pv.paintPath(bestSolution);
    }

    // Gerar população inicial
    public static List<List<IPoint>> generateInitialPopulation(int populationSize, IUIConfiguration conf, Random rand) {
        List<List<IPoint>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<IPoint> path = new ArrayList<>();
            path.add(conf.getStart());
            int size = rand.nextInt(5); // cria um caminho aleatório com no máximo 5 nós intermediários (exceto início e fim)
            for (int j = 0; j < size; j++) {
                path.add(new Point(rand.nextInt(conf.getWidth()), rand.nextInt(conf.getHeight())));
            }
            path.add(conf.getEnd());
            population.add(path);
        }
        return population;
    }
    

    // Avaliar a população
    public static List<Double> evaluatePopulation(List<List<IPoint>> population, IUIConfiguration conf) {
        List<Double> fitnessScores = new ArrayList<>();
        for (List<IPoint> path : population) {
            double fitness = calculateFitness(path, conf);
            fitnessScores.add(fitness);
        }
        return fitnessScores;
    }

    // Calcular o fitness de um caminho
    public static double calculateFitness(List<IPoint> path, IUIConfiguration conf) {
        double totalDistance = 0;
        int obstacleIntersections = 0;
    
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D.Double p1 = new Point2D.Double(path.get(i).getX(), path.get(i).getY());
            Point2D.Double p2 = new Point2D.Double(path.get(i + 1).getX(), path.get(i + 1).getY());
            Line2D.Double line = new Line2D.Double(p1, p2);
            totalDistance += p1.distance(p2);
    
            for (int j = 0; j < conf.getObstacles().size(); j++) {
                if (conf.getObstacles().get(j).intersectsLine(line)) {
                    obstacleIntersections++;
                }
            }
        }
    
        return totalDistance + (obstacleIntersections * 1000); // Penaliza interseções com obstáculos
    }

    // Selecionar um pai usando torneio
    public static List<IPoint> selectParent(List<List<IPoint>> population, List<Double> fitnessScores, Random rand) {
        int tournamentSize = 5;
        List<IPoint> best = null;
        double bestFitness = Double.MAX_VALUE;
    
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = rand.nextInt(population.size());
            if (fitnessScores.get(randomIndex) < bestFitness) {
                bestFitness = fitnessScores.get(randomIndex);
                best = population.get(randomIndex);
            }
        }
    
        return best;
    }
    

    // Crossover de dois caminhos
    public static List<IPoint> crossover(List<IPoint> parent1, List<IPoint> parent2, Random rand) {
        int crossoverPoint = rand.nextInt(Math.min(parent1.size(), parent2.size()));
        List<IPoint> offspring = new ArrayList<>();
        for (int i = 0; i < crossoverPoint; i++) {
            offspring.add(parent1.get(i));
        }
        for (int i = crossoverPoint; i < parent2.size(); i++) {
            offspring.add(parent2.get(i));
        }
        return offspring;
    }

    // Mutação de um caminho
    public static void mutate(List<IPoint> path, IUIConfiguration conf, Random rand) {
        int mutationPoint = rand.nextInt(path.size());
        path.set(mutationPoint, new Point(rand.nextInt(conf.getWidth()), rand.nextInt(conf.getHeight())));
    }
    // Verificar se uma solução é válida
    public static boolean isValidSolution(List<IPoint> path, IUIConfiguration conf) {
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D.Double p1 = new Point2D.Double(path.get(i).getX(), path.get(i).getY());
            Point2D.Double p2 = new Point2D.Double(path.get(i + 1).getX(), path.get(i).getY());
            Line2D.Double line = new Line2D.Double(p1, p2);
            for (int j = 0; j < conf.getObstacles().size(); j++) {
                if (conf.getObstacles().get(j).intersectsLine(line)) {
                    return false;
                }
            }
        }
        return true;
    }
    
}
