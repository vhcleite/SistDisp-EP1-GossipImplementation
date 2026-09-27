# Gossip Implementation

An educational implementation of a *gossip* protocol for the first programming assignment of UFABC's Distributed Systems course. Each Java process represents a **peer**: it watches a local directory, turns that view into metadata, and disseminates it to other peers through UDP datagrams. When a peer receives another peer's metadata, it stores it temporarily and randomly forwards it.

The goal is to explore epidemic information dissemination and connectionless communication. It is neither a file synchronization system nor a production-ready implementation.

For a conceptual walkthrough of gossip and how this implementation applies it, see [GOSSIP.md](GOSSIP.md).

## Disseminated data

The project does not transmit file contents. Every second, each peer traverses `~/gossip_test_folder` and creates a `Metadata` object containing:

- the **names** of all regular files found (without their path, contents, size, or hash);
- the creation timestamp of that snapshot.

This object, together with the originating peer's address, is serialized as JSON using Gson and sent in a UDP packet. Consequently, files with the same name in different subdirectories cannot be distinguished.

## Architecture

```text
~/gossip_test_folder
          |
          v
MetaDataBuilderThread ── updates ──> Local peer
                                      |        |
                              every 6 s|       | UDP/JSON
                                      v        v
                           random peer     PeerListenerThread
                                               |
                                               v
                                       peer records
                                               |
                         every 3 s: random forwarding
                                               |
                                               v
                                           another peer
```

At startup, `PeerClient` creates five threads:

| Thread | Responsibility | Interval |
| --- | --- | --- |
| `MetaDataBuilderThread` | Reads `~/gossip_test_folder` and rebuilds local metadata. | 1 s |
| `PeerListenerThread` | Listens for UDP messages, deserializes JSON, and incorporates received records. | blocks on `receive()` |
| `MyMetaDataSenderThread` | Sends local peer metadata to a randomly selected neighbor. | 6 s |
| `NeighborsMetaDataSenderThread` | Selects a known record and forwards it to another neighbor. | 3 s |
| `MetaDataVerifierThread` | Removes records received more than 10 seconds ago. | 3 s |

The received record replaces the previous one when it is the first known version or when its metadata `creationDate` is more recent. Messages representing the local peer are ignored.

## Prerequisites

- JDK 8 or later (`pom.xml` compiles with `source` and `target` set to 8);
- Apache Maven 3;
- UDP access to the ports used by the peers — `localhost` is sufficient on one machine; open the ports in the firewall between machines.

Runtime dependency: [Gson 2.8.5](https://github.com/google/gson), downloaded by Maven.

## Running the project

1. Create the watched directory and add a few files to it:

   ```bash
   mkdir -p ~/gossip_test_folder
   touch ~/gossip_test_folder/file-a.txt
   ```

2. Compile the project:

   ```bash
   mvn compile
   ```

3. In one terminal per peer, run the main class, supplying its UDP port and a comma-separated list of neighbors in the `ip:port` format:

   ```bash
   mvn exec:java -Dexec.args="9001 127.0.0.1:9002,127.0.0.1:9003"
   ```

   The default `exec-maven-plugin` configuration in `pom.xml` is equivalent to `9000 127.0.0.1:9001`, but passing `-Dexec.args` makes each instance explicit.

### Three-peer local example

Open three terminals in the project directory and run:

```bash
mvn exec:java -Dexec.args="9001 127.0.0.1:9002,127.0.0.1:9003"
```

```bash
mvn exec:java -Dexec.args="9002 127.0.0.1:9001,127.0.0.1:9003"
```

```bash
mvn exec:java -Dexec.args="9003 127.0.0.1:9001,127.0.0.1:9002"
```

After a few seconds, the logs should show the Portuguese messages `Metadados enviados`, `Recebido`, `Atualizado registro`, and the `PEER RECORDS` table. Creating, removing, or renaming a file in the watched directory creates a new metadata version, which will be propagated in subsequent rounds. Stop each process with `Ctrl+C`.

To test across a network, replace `127.0.0.1` with reachable host addresses and keep ports consistent. The address included in a message is automatically obtained through `InetAddress.getLocalHost()`; using that same IP in the neighbor configuration reduces duplicate records.

## Command-line interface

```text
java services.PeerClient <local-port> <ip1:port1,ip2:port2,...>
```

Both arguments are required. The neighbor list must contain at least one valid address; entries are separated by commas and use `:` between the IP/host and port. The code does not validate arguments defensively, so empty entries, invalid ports, or a list with no peers cause the application to fail or leave it without a destination to select.

## Message format

A message is JSON matching the `Peer` model. A conceptual example is:

```json
{
  "address": { "ip": "192.168.0.10", "port": 9001 },
  "metadata": {
    "creationDate": "Jul 11, 2019 12:30:01 AM",
    "folderContent": ["file-a.txt", "report.pdf"]
  }
}
```

The exact date format is produced and accepted by Gson. The receiver buffer holds up to 65,535 bytes per datagram; this does not remove the actual UDP datagram size limits imposed by the network.

## Code structure

```text
src/main/java/
├── model/
│   ├── Address.java       # IP address and port
│   ├── Metadata.java      # directory snapshot and creation time
│   ├── Peer.java          # peer identity and metadata
│   └── PeerRecord.java    # known peer and receipt time
├── services/
│   ├── PeerClient.java    # entry point and thread creation
│   ├── MessageHandler.java
│   ├── MetadataSenderService.java
│   └── LotteryService.java
└── threads/               # collection, sending, reception, and expiry
```

`PeerService.java` exists as an empty class and does not participate in the current flow. The repository contains no automated tests.

## Known limitations

- UDP does not guarantee delivery or ordering and does not prevent duplicates; the algorithm relies on further gossip rounds to compensate for packet loss.
- There is no authentication, encryption, or source/format validation beyond Gson's lenient deserialization. Do not expose this program to untrusted networks.
- The neighbor list is static and supplied at startup. There is no membership discovery, health check, or re-entry for an expired record unless a new message arrives.
- Records are stored in `ArrayList`s shared by threads without synchronization; concurrent execution can cause race conditions or `ConcurrentModificationException`.
- The neighbor forwarder can busy-wait while no record has metadata, and random selection requires a non-empty neighbor list.
- Send-error recovery recursively retries after 10 ms without a limit. Persistent failures can exhaust the stack.
- The watched directory and intervals are hard-coded; there is no configuration file in use (`application.properties` is empty).
- `Address` overloads `equals(Address)` instead of implementing `equals(Object)`/`hashCode`; it should not be used as a key in hash-based collections.

## Technologies

- Java 8 (Maven-declared compatibility)
- Maven
- UDP (`DatagramSocket`/`DatagramPacket`)
- Gson 2.8.5
