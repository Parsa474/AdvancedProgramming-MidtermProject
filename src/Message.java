public class Message {
    // Fields:
    private String message;
    private String[] receiver;

    // Constructors:
    public Message(String message, String[] receiver) {
        this.message = message;
        this.receiver = receiver;
    }

    // Methods:
    // Getter Methods:
    public String getMessage() {
        return message;
    }

    public String[] getReceiver() {
        return receiver;
    }

    // Setter Methods:
    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String[] receiver) {
        this.receiver = receiver;
    }
}
