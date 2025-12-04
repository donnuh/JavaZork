# ZorkUL – All Nighter Text Adventure

ZorkUL is a Java text adventure where you play a student trying to finish a 4000-word paper before 8:00 AM. Explore rooms, interact with items, and manage your sleep to succeed.

---
## Requirements
- Java JDK 17+
---
## Compile & Run

```bash
# Compile all classes
javac -d target/classes src/main/java/zorkul/core/*.java src/main/java/zorkul/items/*.java src/main/java/zorkul/world/*.java

# Run the game
java -cp target/classes zorkul.core.ZorkULGame

----
----
Commands

go <direction> – Move rooms

look – Examine current room

take <item> / drop <item> – Manage inventory

use <item> – Interact with items

write – Work on your paper (+100 words)

status – Show time, sleep, word count

help – List commands

repair – Fix coffee machine in Study Lounge

swipe – Unlock doors with StudentID

talk <name> – Talk to a character

submit – Submit your paper

cheat – Instantly reach 4000 words

save – Save progress

quit – Exit game

---
