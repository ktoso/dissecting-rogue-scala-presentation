package pl.softwaremill.common.util.io;

import java.io.IOException;
import java.util.List;

public class KillableProcess {

    private final String shellCommand;
    private final String[] processGrepStrings;

    private Process process;

    /**
     * @param shellCommand       Command to run the process.
     * @param processGrepStrings Strings which will be used when grepping the process list to determine the process's pid.
     */
    public KillableProcess(String shellCommand, String... processGrepStrings) {
        this.shellCommand = shellCommand;
        this.processGrepStrings = processGrepStrings;
    }

    public Process start() throws IOException {
        process = new ProcessBuilder("sh",
                "-c",
                shellCommand).start();

        return process;
    }

    public void sendSigInt() throws IOException, InterruptedException {
        sendSig(1);
    }

    public void sendSigKill() throws IOException, InterruptedException {
        sendSig(9);
    }

    public void sendSigTerm() throws IOException, InterruptedException {
        sendSig(15);
    }

    public void sendSig(int sig) throws IOException, InterruptedException {
        for (String pid : readPids()) {
            System.out.println("Sending signal " + sig + " to pid " + pid);
            Shell.runShellCommand("kill -" + sig + " " + pid).waitFor();
        }

        process = null;
    }

    public List<String> readPids() throws IOException {
        return Shell.readProcessPids(processGrepStrings);
    }

    public Process getProcess() {
        return process;
    }
}
