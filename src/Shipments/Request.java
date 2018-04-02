package Shipments;

import java.io.Serializable;

public enum Request implements Serializable {
    DOWNLOAD_ALL,
    DOWNLOAD_NEW,
    NEW_MESSAGES,
}
