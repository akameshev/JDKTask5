import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Philosopher extends Thread {
    private final Lock leftFork;
    private final Lock rightFork;
    private final String name;

    public Philosopher(String name, Lock leftFork, Lock rightFork) {
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void think() throws InterruptedException {
        System.out.println(name + " размышляет.");
        Thread.sleep((int) (Math.random() * 100));
    }

    private void eat() throws InterruptedException {
        System.out.println(name + " ест.");
        Thread.sleep((int) (Math.random() * 100));
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 3; i++) {
                think();

                // Берём вилки
                leftFork.lock();
                rightFork.lock();

                try {
                    eat();
                } finally {
                    // Возвращаем вилки
                    leftFork.unlock();
                    rightFork.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(name + " был прерван.");
        }
    }

    public static void main(String[] args) {
        Lock[] forks = new ReentrantLock[5];
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }

        Philosopher[] philosophers = new Philosopher[5];
        for (int i = 0; i < philosophers.length; i++) {
            Lock leftFork = forks[i];
            Lock rightFork = forks[(i + 1) % forks.length];

            // Важно, чтобы последний философ взял сначала правую, а потом левую вилку
            if (i == philosophers.length - 1) {
                philosophers[i] = new Philosopher("Философ " + (i + 1), rightFork, leftFork);
            } else {
                philosophers[i] = new Philosopher("Философ " + (i + 1), leftFork, rightFork);
            }

            philosophers[i].start();
        }

        // Ждем завершения всех философов
        for (Philosopher philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Все философы завершили трапезу.");
    }
}
