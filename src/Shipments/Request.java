package Shipments;

import java.io.Serializable;

/**
 * A request that can be put into a Parcel when the client wishes to download Parcels stored on a Harbor.
 */
public enum Request implements Serializable {
    DOWNLOAD_ALL,
    DOWNLOAD_NEW,
}
