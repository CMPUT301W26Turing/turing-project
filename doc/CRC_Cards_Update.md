# CRC Design Update - WaitingList

## Feedback Addressed
TA feedback asked us to reconsider whether `WaitingList` should exist as its own class, or whether it is only acting as a list.

## Design Decision
`WaitingList` will **not** be treated as a standalone class in the updated design.

## Reason
If a class only exists to create or store a list, then it does not have enough independent responsibility to justify being its own class.

After reviewing the current implementation, waitlist functionality is better represented through:
- `Event`, which stores event-related waitlist data such as waitlist capacity
- `EventRepository`, which manages Firestore waitlist data and updates
- related ViewModel/UI logic, which handles join, leave, display, and management actions

A separate `WaitingList` class would only make sense if it had more substantial responsibilities such as:
- maintaining entrant order
- preventing duplicate entries
- tracking entrant positions
- promoting entrants into participants

Since the current implementation does not use `WaitingList` as a behavior-rich object, keeping it as a standalone class would add unnecessary design complexity.

## Updated CRC Responsibility Mapping

### Class: Event
**Responsibilities**
- store event information
- store waitlist-related data such as waitlist capacity
- represent an event that entrants can join or leave

**Collaborators**
- `EventRepository`
- `EventViewModel`
- related event views

### Class: EventRepository
**Responsibilities**
- read and write event data in Firestore
- manage waitlist subcollection data
- add entrants to the waitlist
- remove entrants from the waitlist
- retrieve waitlist and participant data

**Collaborators**
- `Event`
- `EventViewModel`
- Firestore

## Result
The CRC design is updated so that waitlist responsibilities are represented under `Event` and `EventRepository` instead of as a separate `WaitingList` class.
