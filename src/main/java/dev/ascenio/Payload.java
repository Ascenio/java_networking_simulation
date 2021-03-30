package dev.ascenio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

enum PayloadType {
    integer, string
}

public class Payload implements Serializable {
    private static final long serialVersionUID = -8546992686786852762L;
    private PayloadType type;
    private String message;
    private int originalSenderID;

    public Payload(String message, int originalSenderID) {
        this.type = PayloadType.string;
        this.message = message;
        this.originalSenderID = originalSenderID;
    }

    public Payload(int message, int originalSenderID) {
        this.type = PayloadType.integer;
        this.message = String.valueOf(message);
        this.originalSenderID = originalSenderID;
    }

    public void append(int message) {
        if (!isInteger()) {
            throw new IllegalStateException();
        }
        this.message = String.valueOf(asInteger() + message);
    }

    public void append(String message) {
        if (!isString()) {
            throw new IllegalStateException();
        }
        this.message += message;
    }

    public boolean isInteger() {
        return type == PayloadType.integer;
    }

    public boolean isString() {
        return type == PayloadType.string;
    }

    public String asString() {
        return message;
    }

    public int asInteger() {
        return Integer.valueOf(message);
    }

    @Override
    public String toString() {
        if (isString()) {
            return "'" + message + "'";
        }
        return message;
    }

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        type = Enum.valueOf(PayloadType.class, inputStream.readUTF());
        message = inputStream.readUTF();
        originalSenderID = inputStream.readInt();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeUTF(type.toString());
        outputStream.writeUTF(message.toString());
        outputStream.writeInt(originalSenderID);
    }

    public boolean wasSentBy(int senderID) {
        return originalSenderID == senderID;
    }
}
