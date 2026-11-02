package kstu.pipelines.jenkins.service.process;

public final class ProcessUtils {
    private ProcessUtils() {}

    public static void terminate(Process process) {
        process.destroyForcibly();

        boolean interrupted = false;

        while (process.isAlive()) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
