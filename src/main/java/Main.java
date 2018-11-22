import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Exchanger;

public class Main {
    public static void main(String[] args) {
        System.out.println(String.format("Starting main thread ID = %s, Name = %s",
                Thread.currentThread().getId(), Thread.currentThread().getName()));

        var exchanger = new Exchanger<ArrayList<Integer>>();
        final int BUFFER_SIZE = 10;

        var producer = new Thread() {
            Random rnd = new Random();
            ArrayList<Integer> buffer = new ArrayList<>(BUFFER_SIZE);

            @Override
            public void run() {
                System.out.println(String.format("Starting producer thread ID = %s, Name = %s",
                        currentThread().getId(), currentThread().getName()));

                while (true) {
                    try {
                        var workTime = (rnd.nextInt(10) + 1) * 1000;
                        System.out.println(String.format("Starting producing numbers for %d seconds",
                                workTime / 1000));

                        Thread.sleep(workTime);
                        for (int i = 0; i < BUFFER_SIZE; i++) {
                            buffer.add(rnd.nextInt());
                        }
                        System.out.println("Generated numbers: " + buffer);

                        System.out.println("Producer is waiting to exchange the data...");
                        buffer = exchanger.exchange(buffer);
                        System.out.println("Producer received buffer: " + buffer);

                    } catch (InterruptedException e) {
                        System.out.println("Producer interrupted!");
//                        e.printStackTrace();
                        break;
                    }
                }
            }
        };

        var consumer = new Thread() {
            ArrayList<Integer> buffer = new ArrayList<>(BUFFER_SIZE);

            @Override
            public void run() {
                System.out.println(String.format("Starting consumer thread ID = %s, Name = %s",
                        currentThread().getId(), currentThread().getName()));

                while (true) {
                    try {
                        System.out.println("Consumer is waiting for the data...");
                        buffer = exchanger.exchange(buffer);

                        System.out.println("Consumer received: " + buffer);
                        buffer.clear();
                    } catch (InterruptedException e) {
                        System.out.println("Consumer interrupted!");
                        break;
                    }
                }
            }
        };

        producer.start();
        consumer.start();

        try {
            Thread.currentThread().sleep(30 * 1000);
            System.out.println("Interrupting Producer...");
            producer.interrupt();
            System.out.println("Interrupting Consumer...");
            consumer.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
