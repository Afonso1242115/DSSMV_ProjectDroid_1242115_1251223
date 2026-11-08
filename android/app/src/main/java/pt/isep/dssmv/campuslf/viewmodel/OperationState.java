package pt.isep.dssmv.campuslf.viewmodel;

// guarda o estado e a mensagem de cada operação do ecrã
public final class OperationState {
    public enum Status { IDLE, RUNNING, SUCCESS, ERROR }
    public final Status status;
    public final String message;

    private OperationState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isRunning() { return status == Status.RUNNING; }
    public static OperationState idle(String message) { return new OperationState(Status.IDLE, message); }
    public static OperationState running(String message) { return new OperationState(Status.RUNNING, message); }
    public static OperationState success(String message) { return new OperationState(Status.SUCCESS, message); }
    public static OperationState error(String message) { return new OperationState(Status.ERROR, message); }
}
