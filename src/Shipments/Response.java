package Shipments;

import java.io.Serializable;

public enum Response implements Serializable {
    CONNECT_ERROR,
    SEND_ERROR,
    SUCCESS,
    DISCONNECT_ERROR,
    NOSUCHSERVER_ERROR,
    INVALID_FORMAT_ERROR,
}
