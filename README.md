# SMessage
A simple Java program for sending quick and versatile messages and commands between computers and servers. Designed to be as simple as possible, while still remaining useful for integrating in projects.

The server class is called Receiver.Harbor and has methods to load settings and previously received messages, subclasses of Shipments.Parcels.Parcel, from disk. After running, the Harbor waits for a connection, creates a new runnable Receiver.Handler which receives Parcels and sends them to different methods for handling. The class Main.CHarbor is a program using a command line interface for controlling a Harbor.

The Parcels available for sending lie in the package Shipments.Parcels, and are sent using the class Sender.Post. It also has methods for downloading either all or only new Parcels from a Harbor. Main.CPost lets users try sending Parcels to a Harbor, also using a command line interface.

To send more types of Parcels, extend the class Parcel using the content field for any kind of data. Then edit the methods handle(Parcel parcel) and/or handleServerParcel(Parcel parcel) and handle them however you want. For more information, read the inline documentation.
