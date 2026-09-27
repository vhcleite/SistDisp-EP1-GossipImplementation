# Gossip Protocol Explained

This document explains the idea behind the protocol used in this project and relates it to the code. It is intended as a companion to the main [README](README.md).

## The core idea

A gossip protocol spreads information the same way a rumor spreads through a group of people. A peer does not need to contact every other peer directly. Instead, it periodically chooses one neighbor, shares what it knows, and that neighbor can later share the information with someone else.

```text
Peer A knows a fact
       |
       v
Peer B learns it ──> Peer C learns it ──> Peer D learns it
```

The chosen paths are random. Some sends can fail, be duplicated, or reach peers that already know the information. The protocol tolerates this by repeating the exchange over time. With a connected network and enough rounds, information is likely to reach all peers.

This property is called **eventual dissemination**: the system does not promise that every peer sees an update immediately, but it aims for peers to converge after a while.

## What a peer knows in this project

Each peer owns a local snapshot of `~/gossip_test_folder`. The snapshot contains file names and a creation timestamp. It does not contain the files themselves.

The peer also keeps a list of `PeerRecord` objects. A record stores:

- the remote peer address;
- the most recent `Metadata` known for that peer;
- the local time at which that record was received.

In other words, a peer has a small, local cache of what it knows about other peers' folders.

## How one update travels

Suppose peer A has a new snapshot and its configured neighbors include peer B. The flow is:

1. `MetaDataBuilderThread` scans A's watched folder and creates a new `Metadata` instance.
2. `MyMetaDataSenderThread` chooses a configured neighbor at random and sends A's `Peer` object as JSON over UDP. It does this every 6 seconds.
3. B's `PeerListenerThread` receives the packet, turns the JSON back into a `Peer`, and saves or updates A's `PeerRecord`.
4. B's `NeighborsMetaDataSenderThread` can later choose A's record and forward it to another configured neighbor, such as C. This happens every 3 seconds.
5. C stores the record and may forward it again. A did not need a direct connection to C.

```text
watched folder on A
        |
        v
  A creates metadata
        |
        | direct gossip, every 6 s
        v
  B stores A's record
        |
        | forwarded gossip, every 3 s
        v
  C stores A's record
```

The local sender spreads a peer's own information. The neighbor sender spreads information learned from other peers. Together, they turn direct announcements into multi-hop dissemination.

## Freshness and conflict resolution

Every `Metadata` object has a `creationDate`. When a peer receives a record for an address it already knows, it keeps the received version only when one of the following is true:

- it has never received a record for that address before;
- the received record has no metadata; or
- the received metadata timestamp is newer than the stored metadata timestamp.

This is a simple **last-writer-wins** rule based on wall-clock time. It prevents an older snapshot from overwriting a newer one in the normal case.

One important detail of this implementation: the metadata builder creates a fresh timestamp on every scan, even when the folder contents have not changed. Therefore, every one-second scan is considered a newer version by the protocol.

## Why UDP is used

UDP sends independent datagrams without creating a connection. That makes the example compact and fits the idea that gossip protocols should continue despite occasional lost messages.

The trade-off is that UDP does not guarantee:

- delivery;
- ordering;
- uniqueness; or
- retransmission.

The application does not implement acknowledgements. Instead, it relies on repeated random sends: a lost packet may be sent again in a later gossip round.

## Membership and expiration

The initial neighbors are passed on the command line. They provide the first paths through which the network can exchange data.

For records received from the network, `MetaDataVerifierThread` removes entries that have not been refreshed for more than 10 seconds. This is a simple form of soft state: information disappears unless it continues to arrive.

It is not a full peer-failure detector. A missing record can mean packet loss, a peer that stopped sending, or simply a delay in gossip propagation.

## What this is and is not

This project demonstrates the basic **push gossip** pattern:

```text
periodically choose a random neighbor
send either my state or a known neighbor's state
repeat
```

It is not a full implementation of a modern membership protocol such as SWIM. In particular, it has no acknowledgements, failure suspicions, anti-entropy synchronization, pull requests, security, or reliable membership management.

Those omissions are useful to keep in mind when presenting the project: its value is showing how a small number of periodic, randomized exchanges can spread state through a distributed network.
