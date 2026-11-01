import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KnapsackGA {
    static final int NUM_ITEMS = 20;
    static final int MAX_WEIGHT = 250;
    static final int POP_SIZE = 100;
    static final int GENERATIONS = 300;
    static final double CROSSOVER_RATE = 0.8;
    static final double MUTATION_RATE = 0.05;
    static final int TOURNAMENT_SIZE = 5;

    static int[] weights = new int[NUM_ITEMS];
    static int[] values = new int[NUM_ITEMS];
    static Random rand = new Random(42);

    static class Individual implements Comparable<Individual> {
        int[] genes = new int[NUM_ITEMS];
        int fitness;
        int totalWeight;

        Individual() {}

        Individual(int[] genes) {
            this.genes = Arrays.copyOf(genes, genes.length);
            calculateFitness();
        }

        void calculateFitness() {
            int w = 0;
            int v = 0;
            for (int i = 0; i < NUM_ITEMS; i++) {
                if (genes[i] == 1) {
                    w += weights[i];
                    v += values[i];
                }
            }
            this.totalWeight = w;
            if (w <= MAX_WEIGHT) {
                this.fitness = v;
            } else {
                this.fitness = 0;
            }
        }

        @Override
        public int compareTo(Individual o) {
            return Integer.compare(o.fitness, this.fitness);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_ITEMS; i++) {
            weights[i] = rand.nextInt(99) + 1;
            values[i] = rand.nextInt(99) + 1;
        }

        System.out.println("Generated Items (Weight gm, Value $):");
        for (int i = 0; i < NUM_ITEMS; i++) {
            System.out.printf("Item %2d: %2d gm, $%2d\n", (i + 1), weights[i], values[i]);
        }
        System.out.println("----------------------------------------");

        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < POP_SIZE; i++) {
            int[] genes = new int[NUM_ITEMS];
            for (int j = 0; j < NUM_ITEMS; j++) {
                genes[j] = rand.nextInt(2);
            }
            population.add(new Individual(genes));
        }

        Individual bestEver = population.get(0);

        for (int gen = 0; gen < GENERATIONS; gen++) {
            population.sort(null);
            if (population.get(0).fitness > bestEver.fitness) {
                bestEver = new Individual(population.get(0).genes);
            }

            List<Individual> nextGen = new ArrayList<>();
            nextGen.add(new Individual(population.get(0).genes));
            nextGen.add(new Individual(population.get(1).genes));

            while (nextGen.size() < POP_SIZE) {
                Individual parent1 = tournamentSelect(population);
                Individual parent2 = tournamentSelect(population);

                Individual child1 = new Individual(parent1.genes);
                Individual child2 = new Individual(parent2.genes);

                if (rand.nextDouble() < CROSSOVER_RATE) {
                    int point = rand.nextInt(NUM_ITEMS - 1) + 1;
                    for (int i = point; i < NUM_ITEMS; i++) {
                        child1.genes[i] = parent2.genes[i];
                        child2.genes[i] = parent1.genes[i];
                    }
                }

                mutate(child1);
                mutate(child2);

                child1.calculateFitness();
                child2.calculateFitness();

                nextGen.add(child1);
                if (nextGen.size() < POP_SIZE) {
                    nextGen.add(child2);
                }
            }
            population = nextGen;
        }

        population.sort(null);
        if (population.get(0).fitness > bestEver.fitness) {
            bestEver = new Individual(population.get(0).genes);
        }

        System.out.println("Best Solution Found:");
        System.out.println("Total Value: $" + bestEver.fitness);
        System.out.println("Total Weight: " + bestEver.totalWeight + " gm / " + MAX_WEIGHT + " gm");
        System.out.print("Selected Items: ");
        for (int i = 0; i < NUM_ITEMS; i++) {
            if (bestEver.genes[i] == 1) {
                System.out.print((i + 1) + " ");
            }
        }
        System.out.println();
    }

    static Individual tournamentSelect(List<Individual> population) {
        Individual best = population.get(rand.nextInt(population.size()));
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            Individual ind = population.get(rand.nextInt(population.size()));
            if (ind.fitness > best.fitness) {
                best = ind;
            }
        }
        return best;
    }

    static void mutate(Individual ind) {
        for (int i = 0; i < NUM_ITEMS; i++) {
            if (rand.nextDouble() < MUTATION_RATE) {
                ind.genes[i] = 1 - ind.genes[i];
            }
        }
    }
}