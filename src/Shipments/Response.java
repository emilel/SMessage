package Shipments;

import java.io.Serializable;

public enum Response implements Serializable {
    CONNECT_ERROR,
    SEND_ERROR,
    SUCCESS,
    OPEN_INPUTSTREAM_ERROR,
    UNKNOWN_SHIPMENT_ERROR,
    DISCONNECT_ERROR,
    NOSUCHSERVER_ERROR,
    NEW_PARCELS,
    NO_NEW_PARCELS,
    INVALID_FORMAT_ERROR,
    IO_ERROR,
    READ_ERROR,
}
